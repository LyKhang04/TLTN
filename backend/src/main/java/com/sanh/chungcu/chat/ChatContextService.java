package com.sanh.chungcu.chat;

import com.sanh.chungcu.entity.*;
import com.sanh.chungcu.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Gom dữ liệu thật của cư dân (căn hộ, hóa đơn, sự cố, thông báo) thành một đoạn
 * văn bản ngắn gọn, đưa vào system prompt để AI trả lời có căn cứ, không bịa số liệu.
 */
@Service
public class ChatContextService {

    private final UserRepository userRepository;
    private final ApartmentResidentRepository apartmentResidentRepository;
    private final InvoiceRepository invoiceRepository;
    private final IncidentRepository incidentRepository;
    private final NotificationRepository notificationRepository;

    public ChatContextService(UserRepository userRepository,
                               ApartmentResidentRepository apartmentResidentRepository,
                               InvoiceRepository invoiceRepository,
                               IncidentRepository incidentRepository,
                               NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.apartmentResidentRepository = apartmentResidentRepository;
        this.invoiceRepository = invoiceRepository;
        this.incidentRepository = incidentRepository;
        this.notificationRepository = notificationRepository;
    }

    public String buildContext(Integer residentId) {
        if (residentId == null) {
            return "Không xác định được cư dân đang hỏi. Trả lời chung chung, đề nghị họ đăng nhập lại nếu cần tra cứu thông tin cá nhân.";
        }

        User user = userRepository.findById(residentId).orElse(null);
        if (user == null) {
            return "Không tìm thấy thông tin người dùng trong hệ thống.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Cư dân đang hỏi: ").append(user.getFullName())
                .append(" (username: ").append(user.getUsername()).append(")\n");

        List<ApartmentResident> links = apartmentResidentRepository.findByUser_Id(residentId);
        if (links.isEmpty()) {
            sb.append("Cư dân này chưa được gán vào căn hộ nào trong hệ thống.\n");
        } else {
            for (ApartmentResident link : links) {
                Apartment apt = link.getApartment();
                if (apt == null) continue;
                sb.append("- Căn hộ: ").append(apt.getCode())
                        .append(" (tòa ").append(apt.getBuilding() != null ? apt.getBuilding().getName() : "?")
                        .append(", tầng ").append(apt.getFloor())
                        .append(", ").append(apt.getArea()).append(" m2), vai trò: ")
                        .append(link.getRelationType()).append("\n");

                List<Invoice> invoices = invoiceRepository.findByApartment_Id(apt.getId());
                if (invoices.isEmpty()) {
                    sb.append("  Chưa có hóa đơn nào cho căn hộ này.\n");
                } else {
                    String invoiceLines = invoices.stream()
                            .map(inv -> String.format("kỳ %d/%d: %,.0fđ (%s)",
                                    inv.getPeriodMonth(), inv.getPeriodYear(),
                                    inv.getTotalAmount() == null ? 0 : inv.getTotalAmount(), inv.getStatus()))
                            .collect(Collectors.joining("; "));
                    sb.append("  Hóa đơn: ").append(invoiceLines).append("\n");
                }
            }
        }

        List<Incident> incidents = incidentRepository.findByReporter_Id(residentId);
        if (!incidents.isEmpty()) {
            String incidentLines = incidents.stream()
                    .map(i -> String.format("[#%d] %s - %s (%s)", i.getId(), i.getCategory(), i.getDescription(), i.getStatus()))
                    .collect(Collectors.joining("; "));
            sb.append("Sự cố đã báo cáo: ").append(incidentLines).append("\n");
        }

        List<Notification> recentNotifications = notificationRepository.findAll();
        if (!recentNotifications.isEmpty()) {
            String notiLines = recentNotifications.stream()
                    .limit(5)
                    .map(n -> String.format("\"%s\": %s", n.getTitle(), n.getContent()))
                    .collect(Collectors.joining(" | "));
            sb.append("Thông báo gần đây từ ban quản lý: ").append(notiLines).append("\n");
        }

        return sb.toString();
    }
}
