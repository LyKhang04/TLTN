package com.sanh.chungcu.service;

import com.sanh.chungcu.entity.*;
import com.sanh.chungcu.enums.ApartmentStatus;
import com.sanh.chungcu.enums.InvoiceStatus;
import com.sanh.chungcu.enums.UtilityType;
import com.sanh.chungcu.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sinh hóa đơn dịch vụ hàng tháng một cách tự động.
 *
 * Quy tắc tính (dựa trên dữ liệu đã có trong hệ thống):
 *  1. Phí quản lý  = diện tích căn hộ (m2) x đơn giá "Phi quan ly" trong service_price_configs
 *  2. Tiền điện    = chỉ số điện của kỳ (utility_readings) x đơn giá "Tien dien"
 *  3. Tiền nước    = chỉ số nước của kỳ (utility_readings) x đơn giá "Tien nuoc"
 *  4. Phí gửi xe   = số phương tiện của căn hộ x đơn giá "Phi gui xe"
 *
 * Đơn giá luôn lấy bản ghi có effective_date muộn nhất nhưng không vượt quá
 * ngày cuối của kỳ tính — nhờ vậy khi Ban quản lý điều chỉnh biểu giá,
 * các hóa đơn của kỳ cũ vẫn giữ nguyên đơn giá tại thời điểm đó.
 */
@Service
public class InvoiceGenerationService {

    private final ApartmentRepository apartmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ServicePriceConfigRepository priceConfigRepository;
    private final UtilityReadingRepository utilityReadingRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public InvoiceGenerationService(ApartmentRepository apartmentRepository,
                                    InvoiceRepository invoiceRepository,
                                    InvoiceItemRepository invoiceItemRepository,
                                    ServicePriceConfigRepository priceConfigRepository,
                                    UtilityReadingRepository utilityReadingRepository,
                                    VehicleRepository vehicleRepository,
                                    UserRepository userRepository) {
        this.apartmentRepository = apartmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.priceConfigRepository = priceConfigRepository;
        this.utilityReadingRepository = utilityReadingRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    /** Tên dịch vụ trong bảng service_price_configs. */
    private static final String SV_QUAN_LY = "Phi quan ly";
    private static final String SV_DIEN = "Tien dien";
    private static final String SV_NUOC = "Tien nuoc";
    private static final String SV_GUI_XE = "Phi gui xe";

    /**
     * Sinh hóa đơn cho toàn bộ căn hộ đang có người ở trong một kỳ.
     * Căn hộ đã có hóa đơn của kỳ đó sẽ được bỏ qua (không tạo trùng).
     *
     * @param month     tháng của kỳ tính (1-12)
     * @param year      năm của kỳ tính
     * @param issuedById id người phát hành (nhân sự Ban quản lý), có thể null
     */
    @Transactional
    public Map<String, Object> generateForPeriod(int month, int year, Integer issuedById) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Tháng phải nằm trong khoảng 1-12.");
        }

        LocalDate periodEnd = LocalDate.of(year, month, 1)
                .withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth());

        User issuedBy = issuedById == null ? null : userRepository.findById(issuedById).orElse(null);

        BigDecimal giaQuanLy = unitPrice(SV_QUAN_LY, periodEnd);
        BigDecimal giaDien = unitPrice(SV_DIEN, periodEnd);
        BigDecimal giaNuoc = unitPrice(SV_NUOC, periodEnd);
        BigDecimal giaGuiXe = unitPrice(SV_GUI_XE, periodEnd);

        List<Invoice> existing = invoiceRepository.findAll();
        List<UtilityReading> readings = utilityReadingRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll();

        List<String> created = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Apartment apt : apartmentRepository.findAll()) {
            // Chỉ tính phí cho căn hộ đang có người ở
            if (apt.getStatus() != null && apt.getStatus() != ApartmentStatus.OCCUPIED) {
                continue;
            }

            boolean daCo = existing.stream().anyMatch(inv ->
                    inv.getApartment() != null
                            && apt.getId().equals(inv.getApartment().getId())
                            && Integer.valueOf(month).equals(inv.getPeriodMonth())
                            && Integer.valueOf(year).equals(inv.getPeriodYear()));
            if (daCo) {
                skipped.add(apt.getCode());
                continue;
            }

            Invoice invoice = new Invoice();
            invoice.setApartment(apt);
            invoice.setPeriodMonth(month);
            invoice.setPeriodYear(year);
            invoice.setStatus(InvoiceStatus.UNPAID);
            invoice.setIssuedAt(LocalDateTime.now());
            invoice.setIssuedBy(issuedBy);
            invoice.setTotalAmount(BigDecimal.ZERO);
            Invoice savedInvoice = invoiceRepository.save(invoice);

            List<InvoiceItem> items = new ArrayList<>();

            // 1) Phí quản lý theo diện tích
            if (apt.getArea() != null && giaQuanLy != null) {
                items.add(item(savedInvoice, SV_QUAN_LY, apt.getArea(), giaQuanLy));
            }

            // 2) Tiền điện theo chỉ số của kỳ
            BigDecimal soDien = readingOf(readings, apt.getId(), UtilityType.ELECTRICITY, month, year);
            if (soDien != null && giaDien != null) {
                items.add(item(savedInvoice, SV_DIEN, soDien, giaDien));
            }

            // 3) Tiền nước theo chỉ số của kỳ
            BigDecimal soNuoc = readingOf(readings, apt.getId(), UtilityType.WATER, month, year);
            if (soNuoc != null && giaNuoc != null) {
                items.add(item(savedInvoice, SV_NUOC, soNuoc, giaNuoc));
            }

            // 4) Phí gửi xe theo số phương tiện đã đăng ký
            long soXe = vehicles.stream()
                    .filter(v -> v.getApartment() != null && apt.getId().equals(v.getApartment().getId()))
                    .count();
            if (soXe > 0 && giaGuiXe != null) {
                items.add(item(savedInvoice, SV_GUI_XE, BigDecimal.valueOf(soXe), giaGuiXe));
            }

            invoiceItemRepository.saveAll(items);

            BigDecimal total = items.stream()
                    .map(it -> it.getQuantity().multiply(it.getUnitPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP);

            savedInvoice.setTotalAmount(total);
            invoiceRepository.save(savedInvoice);

            grandTotal = grandTotal.add(total);
            created.add(apt.getCode());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("period", month + "/" + year);
        result.put("createdCount", created.size());
        result.put("createdApartments", created);
        result.put("skippedCount", skipped.size());
        result.put("skippedApartments", skipped);
        result.put("grandTotal", grandTotal);
        result.put("message", created.isEmpty()
                ? "Không có hóa đơn nào được tạo (có thể kỳ này đã phát hành trước đó)."
                : "Đã phát hành " + created.size() + " hóa đơn cho kỳ " + month + "/" + year + ".");
        return result;
    }

    /** Lấy đơn giá còn hiệu lực tại thời điểm cuối kỳ. */
    private BigDecimal unitPrice(String serviceName, LocalDate periodEnd) {
        return priceConfigRepository.findAll().stream()
                .filter(c -> serviceName.equalsIgnoreCase(c.getServiceName()))
                .filter(c -> c.getEffectiveDate() == null || !c.getEffectiveDate().isAfter(periodEnd))
                .max(Comparator.comparing(ServicePriceConfig::getEffectiveDate,
                        Comparator.nullsFirst(Comparator.naturalOrder())))
                .map(ServicePriceConfig::getUnitPrice)
                .orElse(null);
    }

    /** Lấy chỉ số điện/nước đã ghi trong kỳ của một căn hộ. */
    private BigDecimal readingOf(List<UtilityReading> readings, Integer apartmentId,
                                 UtilityType type, int month, int year) {
        return readings.stream()
                .filter(r -> r.getApartment() != null && apartmentId.equals(r.getApartment().getId()))
                .filter(r -> r.getType() == type)
                .filter(r -> r.getReadingDate() != null
                        && r.getReadingDate().getMonthValue() == month
                        && r.getReadingDate().getYear() == year)
                .max(Comparator.comparing(UtilityReading::getReadingDate))
                .map(UtilityReading::getReadingValue)
                .orElse(null);
    }

    private InvoiceItem item(Invoice invoice, String name, BigDecimal qty, BigDecimal price) {
        InvoiceItem it = new InvoiceItem();
        it.setInvoice(invoice);
        it.setItemName(name);
        it.setQuantity(qty);
        it.setUnitPrice(price);
        return it;
    }
}
