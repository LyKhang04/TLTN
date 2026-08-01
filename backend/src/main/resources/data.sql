-- ===== Du lieu lo i =====
INSERT INTO roles (name, description) VALUES
  ('ADMIN', 'Ban quan ly toa nha'),
  ('GUARD', 'Bao ve'),
  ('RESIDENT', 'Cu dan');

INSERT INTO users (role_id, username, password_hash, full_name, phone, email, avatar_url, status, created_at) VALUES
  (1, 'admin', '$2b$10$eQAZDZ4SN3Z5Ts/RCo//A.XcNZMgjBQj.3dZ849i9cAeEnXiVAMKG', 'Ban quan ly',      '0900000001', 'admin@sanh.vn', NULL, 'ACTIVE', CURRENT_TIMESTAMP),
  (2, 'guard1', '$2b$10$eQAZDZ4SN3Z5Ts/RCo//A.XcNZMgjBQj.3dZ849i9cAeEnXiVAMKG', 'Tran Van Bao',      '0900000002', 'guard1@sanh.vn', NULL, 'ACTIVE', CURRENT_TIMESTAMP),
  (3, 'lan.nguyen', '$2b$10$eQAZDZ4SN3Z5Ts/RCo//A.XcNZMgjBQj.3dZ849i9cAeEnXiVAMKG', 'Nguyen Thi Lan', '0912345678', 'lan.nguyen@gmail.com', NULL, 'ACTIVE', CURRENT_TIMESTAMP),
  (3, 'hung.tran',  '$2b$10$eQAZDZ4SN3Z5Ts/RCo//A.XcNZMgjBQj.3dZ849i9cAeEnXiVAMKG', 'Tran Van Hung',  '0987654321', 'hung.tran@gmail.com', NULL, 'ACTIVE', CURRENT_TIMESTAMP),
  (3, 'anh.le',     '$2b$10$eQAZDZ4SN3Z5Ts/RCo//A.XcNZMgjBQj.3dZ849i9cAeEnXiVAMKG', 'Le Minh Anh',    '0901222333', 'anh.le@gmail.com', NULL, 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO buildings (name, address) VALUES
  ('Toa A', '12 Nguyen Van Linh, Quan 7, TP.HCM'),
  ('Toa B', '12 Nguyen Van Linh, Quan 7, TP.HCM');

INSERT INTO apartments (building_id, code, floor, area, status) VALUES
  (1, 'A-0501', 5, 68.5, 'OCCUPIED'),
  (1, 'A-0702', 7, 92.0, 'OCCUPIED'),
  (1, 'A-1203', 12, 54.2, 'VACANT'),
  (2, 'B-0301', 3, 76.8, 'OCCUPIED'),
  (2, 'B-0904', 9, 110.4, 'MAINTENANCE');

INSERT INTO apartment_residents (apartment_id, user_id, relation_type, is_primary, moved_in_at, moved_out_at) VALUES
  (1, 3, 'OWNER', true, '2023-01-15', NULL),
  (2, 4, 'OWNER', true, '2022-06-01', NULL),
  (4, 5, 'TENANT', true, '2024-03-01', NULL);

INSERT INTO amenities (name, capacity, open_time, close_time) VALUES
  ('Ho boi ngoai troi', 30, '06:00:00', '21:00:00'),
  ('Phong gym', 20, '05:00:00', '22:00:00'),
  ('Phong sinh hoat cong dong', 50, '08:00:00', '20:00:00'),
  ('San BBQ tang thuong', 15, '16:00:00', '22:00:00');

INSERT INTO invoices (apartment_id, period_month, period_year, total_amount, status, issued_at, issued_by) VALUES
  (1, 7, 2026, 2450000, 'UNPAID', '2026-07-01 08:00:00', 1),
  (2, 7, 2026, 3120000, 'PAID',   '2026-07-01 08:00:00', 1),
  (4, 7, 2026, 1980000, 'OVERDUE','2026-07-01 08:00:00', 1);

INSERT INTO invoice_items (invoice_id, item_name, quantity, unit_price) VALUES
  (1, 'Phi quan ly', 68.5, 8000),
  (1, 'Tien nuoc', 12, 15000),
  (1, 'Tien dien', 210, 2800),
  (1, 'Phi gui xe may', 1, 80000);

INSERT INTO incidents (apartment_id, reporter_id, category, description, status, approved_by, assigned_to, created_at, resolved_at) VALUES
  (5, 5, 'Dien nuoc', 'Ro ri nuoc tran nha ve sinh', 'IN_PROGRESS', 1, 2, '2026-07-28 10:00:00', NULL),
  (3, 3, 'Thang may', 'Thang may tang 12 keu to bat thuong', 'NEW', NULL, NULL, '2026-07-29 09:00:00', NULL),
  (1, 3, 'Ve sinh', 'Khu vuc rac tang 5 chua duoc don', 'RESOLVED', 1, 2, '2026-07-24 08:00:00', '2026-07-24 15:00:00');

INSERT INTO notifications (title, content, target_scope, created_by) VALUES
  ('Bao tri thang may Toa A', 'Thang may so 2 tam ngung 09:00-11:00 ngay 02/08 de bao tri dinh ky.', 'BUILDING', 1),
  ('Nhac thanh toan phi thang 7', 'Vui long thanh toan hoa don thang 7 truoc ngay 10/08 de tranh phi tre han.', 'ALL', 1);

INSERT INTO notification_recipients (notification_id, user_id, read_at) VALUES
  (1, 3, NULL),
  (1, 4, '2026-07-30 09:00:00'),
  (2, 3, NULL),
  (2, 4, NULL),
  (2, 5, NULL);
