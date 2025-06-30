BEGIN
    CREATE DATABASE hiv_medical;
END;
GO
USE hiv_medical

-- Khởi tạo dữ liệu cho bảng users
-- INSERT INTO users (username, password_hash, email, full_name, registration_date, last_login_date, profile_picture_url, role, enabled) VALUES
--                                                                                                                                           ('nguyenvana', '$2a$10$exampleHashedPassword1', 'nguyenvana@gmail.com', 'Nguyen Van A', '2025-06-30 10:00:00', NULL, NULL, 'PATIENT', true),
--                                                                                                                                           ('tranb', '$2a$10$exampleHashedPassword2', 'tranb@gmail.com', 'Tran Thi B', '2025-06-30 10:30:00', '2025-06-30 12:00:00', NULL, 'PATIENT', true),
--                                                                                                                                           ('admin', '$2a$10$exampleHashedPassword3', 'admin@hivmedical.vn', 'Quan Tri Vien', '2025-06-30 09:00:00', '2025-06-30 15:00:00', NULL, 'ADMIN', true);

-- Khởi tạo dữ liệu cho bảng doctors
INSERT INTO dbo.doctor (email, full_name, phone_number, qualification, specialization, working_schedule, image_url)
VALUES
    ('nguyen.an@hivclinic.vn', 'BS. Nguyen Van An', '0912345678', 'Bac si CKI', 'HIV/AIDS', 'Thu 2 - Thu 6, 08:00-17:00', 'https://img.lovepik.com/free-png/20211215/lovepik-male-doctor-image-png-image_401633157_wh1200.png'),
    ('tran.mai@hivclinic.vn', 'BS. Tran Thi Mai', '0987654321', 'Thac si Y hoc', 'HIV/AIDS', 'Thu 2 - Thu 6, 07:30-16:30', 'https://honghunghospital.com.vn/wp-content/uploads/2020/05/18.-Nguy%E1%BB%85n-Thanh-Ph%C6%B0%C6%A1ng-scaled.jpg'),
    ('le.huy@hivclinic.vn', 'BS. Le Quang Huy', '0909123456', 'Bac si Da khoa', 'HIV/AIDS', 'Thu 3 - Thu 7, 08:00-17:00', 'https://img.lovepik.com/free-png/20211215/lovepik-male-doctor-image-png-image_401633174_wh1200.png'),
    ('do.lan@hivclinic.vn', 'TS. Do Thi Lan', '0933666888', 'Tien si Y hoc', 'HIV/AIDS', 'Thu 2 - Thu 6, 09:00-18:00', 'https://img.lovepik.com/element/40094/5848.png_860.png'),
    ('pham.tuan@hivclinic.vn', 'BS. Pham Minh Tuan', '0977333444', 'Bac si CKII', 'HIV/AIDS', 'Thu 2 - Thu 6, 08:30-17:30', 'https://honghunghospital.com.vn/wp-content/uploads/2020/09/26.-L%C3%AA-Tu%E1%BA%A5n-Anh-1-scaled.jpg');


-- Khởi tạo dữ liệu cho bảng services
INSERT INTO dbo.services (created_at, description, name, price, updated_at, type)
VALUES
    ('2025-06-30 16:23:07.426667', '["Bao mat & Hieu qua.", "Su dung thuoc de phong tranh lay nhiem HIV truoc khi co nguy co tiep xuc.", "Duoc tu van va theo doi boi doi ngu y te giau kinh nghiem."]', 'Du phong truoc phoi nhiem HIV – PrEP', '500000', '2025-06-30 16:23:07.426667', 'FIRST_VISIT'),

    ('2025-06-30 16:23:07.426667', '["It tac dung phu.", "Dieu tri khan cap sau khi co nguy co phoi nhiem HIV.", "Hieu qua neu thuc hien trong vong 72 gio."]', 'Du phong sau phoi nhiem HIV – PEP', '300000', '2025-06-30 16:23:07.426667', 'FIRST_VISIT'),

    ('2025-06-30 16:23:07.426667', '["Bao mat & Uy tin.", "Dieu tri HIV lau dai giup kiem soat virus.", "Nang cao chat luong cuoc song va giam nguy co lay nhiem."]', 'Dieu tri HIV – ARV', 'Tuy phac do', '2025-06-30 16:23:07.426667', 'FOLLOW_UP'),

    ('2025-06-30 16:23:07.426667', '["Theo lich hen.", "Tu van kham chua benh qua dien thoai, ung dung.", "Tien loi va bao mat toi da."]', 'Dich vu tai nha', 'Lien he de biet gia', '2025-06-30 16:23:07.426667', 'FOLLOW_UP'),

    ('2025-06-30 16:23:07.426667', '["Ho tro tinh than.", "Tu van tam ly cho nguoi song chung voi HIV.", "Giam cang thang, lo lang, nang cao tinh than."]', 'Tu van tam ly', 'Lien he de biet gia', '2025-06-30 16:23:07.426667', 'FOLLOW_UP'),

    ('2025-06-30 16:43:09.769112', '["Ho tro che do an uong.", "Tang cuong suc khoe."]', 'Tu van dinh duong', '200000', '2025-06-30 16:43:09.769112', 'FIRST_VISIT');

-- Khởi tạo dữ liệu cho bảng schedules
INSERT INTO dbo.schedule (date, time_slots, doctor_id)
VALUES
    ('2025-06-26', '["08:00-09:00", "09:00-10:00"]', 1),
    ('2025-06-26', '["07:30-08:30", "08:30-09:30"]', 2),
    ('2025-06-26', '["08:00-09:00", "09:00-10:00"]', 3),
    ('2025-06-26', '["09:00-10:00", "10:00-11:00"]', 4),
    ('2025-06-26', '["08:30-09:30", "09:30-10:30"]', 5);

-- Khởi tạo dữ liệu cho bảng appointments
INSERT INTO appointments (user_id, doctor_id, date, time, type, is_anonymous, status, reference_code) VALUES
                                                                                                          (1, 1, '2025-07-01', '08:00:00', 'Tu van HIV', false, 'CONFIRMED', 'APPT001'),
                                                                                                          (2, 2, '2025-07-01', '09:00:00', 'Xet nghiem HIV', true, 'PENDING', 'APPT002'),
                                                                                                          (NULL, 1, '2025-07-02', '09:00:00', 'Tu van HIV', true, 'PENDING', 'APPT003');

-- Khởi tạo dữ liệu cho bảng verification_tokens
-- INSERT INTO verification_tokens (token, email, user_info, expiry_date, type) VALUES
--                                                                                  ('token123456', 'nguyenvana@gmail.com', '{"username": "nguyenvana", "fullName": "Nguyen Van A"}', '2025-07-01 10:00:00', 'EMAIL_VERIFICATION'),
--                                                                                  ('token789012', 'tranb@gmail.com', '{"username": "tranb", "fullName": "Tran Thi B"}', '2025-07-01 10:30:00', 'EMAIL_VERIFICATION');