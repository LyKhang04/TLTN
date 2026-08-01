# Sảnh — Nền tảng quản lý chung cư

Dự án full-stack dựng từ ERD "Quản lý chung cư" (25 bảng): backend Spring Boot (Java 17)
và frontend React (Vite). Backend dùng H2 in-memory kèm dữ liệu mẫu để chạy demo ngay,
không cần cài đặt MySQL.

## Cấu trúc

```
project/
├── backend/     Spring Boot 3 + Spring Data JPA + H2 (demo) / MySQL (production)
└── frontend/    React 18 + Vite, gọi REST API của backend
```

## Chạy backend

Yêu cầu: JDK 17+, Maven (hoặc dùng `./mvnw` nếu bạn tự thêm wrapper).

```bash
cd backend
mvn spring-boot:run
```

- API chạy ở `http://localhost:8080/api/...`
- Console H2 (xem dữ liệu trực tiếp): `http://localhost:8080/h2-console`
  (JDBC URL: `jdbc:h2:mem:chungcu`, user `sa`, không mật khẩu)
- Dữ liệu mẫu được nạp tự động từ `data.sql` mỗi lần khởi động (schema tạo lại từ đầu).

### Chuyển sang MySQL khi triển khai thật

1. Thêm dependency `mysql-connector-j` vào `pom.xml`.
2. Trong `application.yml`, comment phần H2 và bật khối MySQL đã có sẵn ở cuối file.
3. Đổi `ddl-auto` thành `validate` và tự quản lý migration bằng Flyway/Liquibase
   (khuyến nghị dùng file `.architect`/SQL đã tạo trước đó làm điểm xuất phát).

## Chạy frontend

Yêu cầu: Node.js 18+.

```bash
cd frontend
npm install
npm run dev
```

Mở `http://localhost:5173`. Đăng nhập bằng tài khoản demo:

| Tài khoản     | Mật khẩu      | Vai trò        |
|---------------|---------------|----------------|
| `admin`       | `password123` | Ban quản lý    |
| `lan.nguyen`  | `password123` | Cư dân         |
| `hung.tran`   | `password123` | Cư dân         |

## Bật chatbot AI hỗ trợ cư dân (Sảnh AI)

Trước khi chạy backend, đặt biến môi trường `ANTHROPIC_API_KEY` (lấy tại
https://console.anthropic.com):

```bash
# macOS / Linux
export ANTHROPIC_API_KEY=sk-ant-xxxxx
cd backend && mvn spring-boot:run

# Windows PowerShell
$env:ANTHROPIC_API_KEY="sk-ant-xxxxx"
cd backend; mvn spring-boot:run
```

Nếu không đặt biến này, backend vẫn chạy bình thường — chatbot chỉ trả lời một
thông báo nhắc cấu hình thay vì lỗi 500.

Cách hoạt động:
- Nút chat nổi (góc dưới bên phải) chỉ hiển thị cho vai trò **Cư dân**.
- Mỗi câu hỏi được gửi kèm `residentId` lên `POST /api/chat`.
- Backend (`ChatContextService`) tự truy vấn CSDL lấy đúng dữ liệu của cư dân đó
  (căn hộ, hóa đơn, sự cố đã báo, thông báo gần đây) rồi đưa vào system prompt,
  để AI trả lời có căn cứ thay vì bịa số liệu.
- Model mặc định: `claude-sonnet-5` — đổi ở `anthropic.model` trong `application.yml`.
- Có thể mở rộng: giới hạn tốc độ gọi (rate limit) theo user, lưu lịch sử chat vào DB,
  hoặc cho AI gọi thêm "tool" để tạo hộ yêu cầu (ví dụ tự tạo `incident` khi cư dân
  mô tả sự cố qua chat).

## Những gì đã hoàn thiện

- **25 Entity JPA** ánh xạ đầy đủ 25 bảng trong ERD, kể cả các FK "ẩn" trong `incidents`
  (`reporter`, `approvedBy`, `assignedTo` đều trỏ về `users`).
- **25 REST Controller CRUD** (`/api/{ten-bang}` — GET danh sách, GET theo id, POST, PUT, DELETE).
- **Đăng nhập** với mật khẩu băm BCrypt (`/api/auth/login`).
- **Dashboard tổng hợp** (`/api/dashboard/summary`) tính số căn hộ, hóa đơn chưa thu, sự cố đang mở.
- **Giao diện React** dùng lại thiết kế "Sảnh" (đổi vai trò Quản lý / Cư dân), 12 màn hình
  kết nối API thật: Tổng quan, Căn hộ, Cư dân, Hóa đơn, Sự cố, Thông báo (Admin) và
  Trang chủ, Hóa đơn của tôi (có nút thanh toán), Đăng ký khách, Báo sự cố, Đặt tiện ích,
  Thông báo (Cư dân).
- **Chatbot AI "Sảnh AI"** hỗ trợ cư dân — widget chat nổi gọi thẳng Claude API, trả lời
  dựa trên dữ liệu thật của từng cư dân (xem mục cấu hình bên dưới).

## Việc nên làm tiếp khi lên production

- Thay xác thực đơn giản hiện tại bằng **Spring Security + JWT**, thêm phân quyền theo `role`.
- Bổ sung **DTO + Bean Validation** cho từng Controller thay vì nhận thẳng Entity
  (hiện tại để đơn giản hoá, Controller CRUD nhận/trả Entity trực tiếp).
- Thêm **phân trang** (`Pageable`) cho các danh sách lớn (invoice, incident, system_logs...).
- Bổ sung **index** cho các cột hay lọc: `invoices(period_month, period_year)`,
  `incidents(status)`, như đã nêu trong phần phân tích ERD.
- Thêm FK từ `invoice_items` về `service_price_configs` để tính giá tự động.
