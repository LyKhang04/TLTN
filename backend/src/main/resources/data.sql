-- ===== Du lieu lo i =====
INSERT INTO roles (name, description) VALUES
                                          ('ADMIN', 'Ban quan ly toa nha'),
                                          ('GUARD', 'Bao ve'),
                                          ('RESIDENT', 'Cu dan'),
                                          ('TECHNICIAN', 'Nhan vien ky thuat');

INSERT INTO users (role_id, username, password_hash, full_name, phone, email, avatar_url, status, created_at) VALUES
                                                                                                                  (1, 'admin', '$2b$10$eQAZDZ4SN3Z5Ts/RCo//A.XcNZMgjBQj.3dZ849i9cAeEnXiVAMKG', 'Ban quan ly',      '0900000001', 'admin@sanh.vn', NULL, 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                                                  (2, 'guard1', '$2b$10$eQAZDZ4SN3Z5Ts/RCo//A.XcNZMgjBQj.3dZ849i9cAeEnXiVAMKG', 'Tran Van Bao',      '0900000002', 'guard1@sanh.vn', NULL, 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                                                  (3, 'lan.nguyen', '$2b$10$eQAZDZ4SN3Z5Ts/RCo//A.XcNZMgjBQj.3dZ849i9cAeEnXiVAMKG', 'Nguyen Thi Lan', '0912345678', 'lan.nguyen@gmail.com', NULL, 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                                                  (3, 'hung.tran',  '$2b$10$eQAZDZ4SN3Z5Ts/RCo//A.XcNZMgjBQj.3dZ849i9cAeEnXiVAMKG', 'Tran Van Hung',  '0987654321', 'hung.tran@gmail.com', NULL, 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                                                  (3, 'anh.le',     '$2b$10$eQAZDZ4SN3Z5Ts/RCo//A.XcNZMgjBQj.3dZ849i9cAeEnXiVAMKG', 'Le Minh Anh',    '0901222333', 'anh.le@gmail.com', NULL, 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                                                  (4, 'kythuat1',   '$2b$10$eQAZDZ4SN3Z5Ts/RCo//A.XcNZMgjBQj.3dZ849i9cAeEnXiVAMKG', 'Pham Quoc Dat',  '0903444555', 'kythuat1@sanh.vn', NULL, 'ACTIVE', CURRENT_TIMESTAMP);

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
                                                                                                                                        (4, 5, 'Dien nuoc', 'Ro ri nuoc tran nha ve sinh', 'IN_PROGRESS', 1, 2, '2026-07-28 10:00:00', NULL),
                                                                                                                                        (1, 3, 'Thang may', 'Thang may keu to bat thuong', 'NEW', NULL, NULL, '2026-07-29 09:00:00', NULL),
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

-- ===== Bang gia dich vu (dung cho chuc nang tu dong tinh hoa don) =====
-- Ten dich vu phai khop voi hang so trong InvoiceGenerationService.
INSERT INTO service_price_configs (service_name, unit, unit_price, effective_date) VALUES
                                                                                       ('Phi quan ly', 'm2',   8000,  '2026-01-01'),
                                                                                       ('Tien dien',   'kWh',  2800,  '2026-01-01'),
                                                                                       ('Tien nuoc',   'm3',   15000, '2026-01-01'),
                                                                                       ('Phi gui xe',  'xe',   120000,'2026-01-01');

-- ===== Phuong tien dang ky theo can ho (dung tinh phi gui xe) =====
INSERT INTO vehicles (user_id, apartment_id, plate_number, vehicle_type, status, registered_at) VALUES
                                                                                                    (3, 1, '59A1-123.45', 'CAR',       'ACTIVE', CURRENT_TIMESTAMP),
                                                                                                    (3, 1, '59X1-678.90', 'MOTORBIKE', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                                    (4, 2, '51H1-222.33', 'MOTORBIKE', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                                    (5, 4, '59D1-444.55', 'MOTORBIKE', 'ACTIVE', CURRENT_TIMESTAMP);

-- ===== Chi so dien nuoc ky 8/2026 (de demo tu dong tinh hoa don) =====
INSERT INTO utility_readings (apartment_id, type, reading_value, reading_date) VALUES
                                                                                   (1, 'ELECTRICITY', 215.0, '2026-08-31'),
                                                                                   (1, 'WATER',        14.5, '2026-08-31'),
                                                                                   (2, 'ELECTRICITY', 340.0, '2026-08-31'),
                                                                                   (2, 'WATER',        22.0, '2026-08-31'),
                                                                                   (4, 'ELECTRICITY', 180.0, '2026-08-31'),
                                                                                   (4, 'WATER',        11.0, '2026-08-31');

-- ===== Phieu bao tri =====
-- Phieu 1: sinh tu su co #1 (ro ri nuoc can ho A-0501), dang thi cong
-- Phieu 2: bao tri dinh ky khu vuc chung, da hoan thanh, co chi phi
-- Phieu 3: ke hoach bao tri PCCC, cho phan cong
INSERT INTO maintenance_tickets
(incident_id, apartment_id, assigned_to, created_by, title, description, category, status, scheduled_date, completed_at, cost, created_at) VALUES
                                                                                                                                               (1, 4, 6, 1, 'Xu ly ro ri duong ong nuoc', 'Thay van va doan ong bi ro tai nha ve sinh',
                                                                                                                                                'Cap thoat nuoc', 'IN_PROGRESS', '2026-08-10', NULL, NULL, CURRENT_TIMESTAMP),
                                                                                                                                               (NULL, NULL, 6, 1, 'Bao tri dinh ky thang may Toa A', 'Kiem tra cap tai, boi tron, hieu chinh cua',
                                                                                                                                                'Thang may', 'DONE', '2026-07-15', '2026-07-15 16:30:00', 4500000, CURRENT_TIMESTAMP),
                                                                                                                                               (NULL, NULL, NULL, 1, 'Kiem tra he thong PCCC Toa B', 'Kiem tra binh chua chay va dau bao khoi cac tang',
                                                                                                                                                'PCCC', 'PENDING', '2026-09-01', NULL, NULL, CURRENT_TIMESTAMP);
