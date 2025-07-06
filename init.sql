-- Tạo cơ sở dữ liệu
CREATE DATABASE hiv_medical;
GO
USE hiv_medical;
GO

-- Xóa và khởi tạo dữ liệu cho bảng users
DELETE FROM users;
INSERT INTO users (username, password_hash, email, full_name, registration_date, last_login_date, profile_picture_url, role, enabled) VALUES
                                                                                                                                          (N'nguyenvana', '$2a$10$exampleHashedPassword1', 'nguyenvana@gmail.com', N'Nguyễn Văn A', '2025-06-30 10:00:00', NULL, NULL, 'PATIENT', 1),
                                                                                                                                          (N'tranb', '$2a$10$exampleHashedPassword2', 'tranb@gmail.com', N'Trần Thị B', '2025-06-30 10:30:00', '2025-06-30 12:00:00', NULL, 'PATIENT', 1),
                                                                                                                                          (N'admin', '$2a$10$exampleHashedPassword3', 'admin@hivmedical.vn', N'Quản Trị Viên', '2025-06-30 09:00:00', '2025-06-30 15:00:00', NULL, 'ADMIN', 1);

-- Xóa và khởi tạo dữ liệu cho bảng doctor
DELETE FROM doctor;
INSERT INTO dbo.doctor (email, full_name, phone_number, qualification, specialization, working_schedule, image_url) VALUES
                                                                                                                        ('nguyen.an@hivclinic.vn', N'Bác sĩ Nguyễn Văn An', '0912345678', N'Bác sĩ CKI', N'HIV/AIDS', N'Thứ 2 - Thứ 6, 08:00-17:00', 'https://img.lovepik.com/free-png/20211215/lovepik-male-doctor-image-png-image_401633157_wh1200.png'),
                                                                                                                        ('tran.mai@hivclinic.vn', N'Bác sĩ Trần Thị Mai', '0987654321', N'Thạc sĩ Y học', N'HIV/AIDS', N'Thứ 2 - Thứ 6, 07:30-16:30', 'https://honghunghospital.com.vn/wp-content/uploads/2020/05/18.-Nguy%E1%BB%85n-Thanh-Ph%C6%B0%C6%A1ng-scaled.jpg'),
                                                                                                                        ('le.huy@hivclinic.vn', N'Bác sĩ Lê Quang Huy', '0909123456', N'Bác sĩ Đa khoa', N'HIV/AIDS', N'Thứ 3 - Thứ 7, 08:00-17:00', 'https://img.lovepik.com/free-png/20211215/lovepik-male-doctor-image-png-image_401633174_wh1200.png'),
                                                                                                                        ('do.lan@hivclinic.vn', N'Tiến sĩ Đỗ Thị Lan', '0933666888', N'Tiến sĩ Y học', N'HIV/AIDS', N'Thứ 2 - Thứ 6, 09:00-18:00', 'https://img.lovepik.com/element/40094/5848.png_860.png'),
                                                                                                                        ('pham.tuan@hivclinic.vn', N'Bác sĩ Phạm Minh Tuấn', '0977333444', N'Bác sĩ CKII', N'HIV/AIDS', N'Thứ 2 - Thứ 6, 08:30-17:30', 'https://honghunghospital.com.vn/wp-content/uploads/2020/09/26.-L%C3%AA-Tu%E1%BA%A5n-Anh-1-scaled.jpg');

-- Xóa và khởi tạo dữ liệu cho bảng services
DELETE FROM services;
INSERT INTO dbo.services (created_at, description, name, price, updated_at, type) VALUES
                                                                                      ('2025-06-30 16:23:07.426667', N'["Bảo mật & Hiệu quả.", "Sử dụng thuốc để phòng tránh lây nhiễm HIV trước khi có nguy cơ tiếp xúc.", "Được tư vấn và theo dõi bởi đội ngũ y tế giàu kinh nghiệm."]', N'Dự phòng trước phơi nhiễm HIV – PrEP', N'500000', '2025-06-30 16:23:07.426667', N'FIRST_VISIT'),
                                                                                      ('2025-06-30 16:23:07.426667', N'["Ít tác dụng phụ.", "Điều trị khẩn cấp sau khi có nguy cơ phơi nhiễm HIV.", "Hiệu quả nếu thực hiện trong vòng 72 giờ."]', N'Dự phòng sau phơi nhiễm HIV – PEP', N'300000', '2025-06-30 16:23:07.426667', N'FIRST_VISIT'),
                                                                                      ('2025-06-30 16:23:07.426667', N'["Bảo mật & Uy tín.", "Điều trị HIV lâu dài giúp kiểm soát virus.", "Nâng cao chất lượng cuộc sống và giảm nguy cơ lây nhiễm."]', N'Điều trị HIV – ARV', N'Tùy phác đồ', '2025-06-30 16:23:07.426667', N'FIRST_VISIT'),
                                                                                      ('2025-06-30 16:23:07.426667', N'["Theo lịch hẹn.", "Tư vấn khám chữa bệnh qua điện thoại, ứng dụng.", "Tiện lợi và bảo mật tối đa."]', N'Dịch vụ tại nhà', N'Liên hệ để biết giá', '2025-06-30 16:23:07.426667', N'FOLLOW_UP'),
                                                                                      ('2025-06-30 16:23:07.426667', N'["Hỗ trợ tinh thần.", "Tư vấn tâm lý cho người sống chung với HIV.", "Giảm căng thẳng, lo lắng, nâng cao tinh thần."]', N'Tư vấn tâm lý', N'Liên hệ để biết giá', '2025-06-30 16:23:07.426667', N'FOLLOW_UP'),
                                                                                      ('2025-06-30 16:43:09.769112', N'["Hỗ trợ chế độ ăn uống.", "Tăng cường sức khỏe."]', N'Tư vấn dinh dưỡng', N'200000', '2025-06-30 16:43:09.769112', N'FIRST_VISIT');

-- Xóa và khởi tạo dữ liệu cho bảng schedule
DELETE FROM schedule;
INSERT INTO dbo.schedule (date, time_slots, doctor_id) VALUES
                                                           ('2025-06-26', N'["08:00-09:00", "09:00-10:00"]', 1),
                                                           ('2025-06-26', N'["07:30-08:30", "08:30-09:30"]', 2),
                                                           ('2025-06-26', N'["08:00-09:00", "09:00-10:00"]', 3),
                                                           ('2025-06-26', N'["09:00-10:00", "10:00-11:00"]', 4),
                                                           ('2025-06-26', N'["08:30-09:30", "09:30-10:30"]', 5);

-- Xóa và khởi tạo dữ liệu cho bảng appointments
DELETE FROM appointments;
INSERT INTO appointments (user_id, doctor_id, date, time, type, is_anonymous, status, reference_code) VALUES
                                                                                                          (1, 1, '2025-07-01', '08:00:00', N'Tư vấn HIV', 0, N'CONFIRMED', N'APPT001'),
                                                                                                          (2, 2, '2025-07-01', '09:00:00', N'Xét nghiệm HIV', 1, N'PENDING', N'APPT002'),
                                                                                                          (NULL, 1, '2025-07-02', '09:00:00', N'Tư vấn HIV', 1, N'PENDING', N'APPT003');

-- Xóa và khởi tạo dữ liệu cho bảng verification_tokens
DELETE FROM verification_tokens;
INSERT INTO verification_tokens (token, email, user_info, expiry_date, type) VALUES
                                                                                 ('token123456', 'nguyenvana@gmail.com', N'{"username": "nguyenvana", "fullName": "Nguyễn Văn A"}', '2025-07-01 10:00:00', N'EMAIL_VERIFICATION'),
                                                                                 ('token789012', 'tranb@gmail.com', N'{"username": "tranb", "fullName": "Trần Thị B"}', '2025-07-01 10:30:00', N'EMAIL_VERIFICATION');

-- Lặp lại cho từng bác sĩ và từng ngày
DECLARE @doctor_id INT, @date DATE, @start_time TIME, @end_time TIME, @i INT, @j INT;

SET @i = 0;
WHILE @i < 5 -- 5 ngày tiếp theo
BEGIN
    SET @date = DATEADD(DAY, @i, CAST(GETDATE() AS DATE));
    SET @doctor_id = 1;
    SET @j = 0;
    WHILE @j < 4 -- 4 khung giờ mỗi ngày
    BEGIN
        SET @start_time = DATEADD(MINUTE, @j * 30, '08:00');
        SET @end_time = DATEADD(MINUTE, (@j + 1) * 30, '08:00');
        INSERT INTO schedule (doctor_id, date, start_time, end_time, is_available, created_at, updated_at, time_slots)
        VALUES (@doctor_id, @date, 
                DATEADD(MINUTE, @j * 30, CAST(@date AS DATETIME)), 
                DATEADD(MINUTE, (@j + 1) * 30, CAST(@date AS DATETIME)), 
                1, GETDATE(), GETDATE(), 
                CONCAT('["', FORMAT(@start_time, 'HH:mm'), '-', FORMAT(@end_time, 'HH:mm'), '"]'));
        SET @j = @j + 1;
    END
    -- Lặp lại cho bác sĩ thứ 2
    SET @doctor_id = 2;
    SET @j = 0;
    WHILE @j < 4
    BEGIN
        SET @start_time = DATEADD(MINUTE, @j * 30, '08:00');
        SET @end_time = DATEADD(MINUTE, (@j + 1) * 30, '08:00');
        INSERT INTO schedule (doctor_id, date, start_time, end_time, is_available, created_at, updated_at, time_slots)
        VALUES (@doctor_id, @date, 
                DATEADD(MINUTE, @j * 30, CAST(@date AS DATETIME)), 
                DATEADD(MINUTE, (@j + 1) * 30, CAST(@date AS DATETIME)), 
                1, GETDATE(), GETDATE(), 
                CONCAT('["', FORMAT(@start_time, 'HH:mm'), '-', FORMAT(@end_time, 'HH:mm'), '"]'));
        SET @j = @j + 1;
    END
    SET @i = @i + 1;
END

///////////////////////////////////////////////////
-- updated sql appoinment online
ALTER TABLE appointments ADD phone NVARCHAR(50);
ALTER TABLE appointments ADD gender NVARCHAR(10);
ALTER TABLE appointments ADD description NVARCHAR(MAX);

ALTER TABLE users ADD phone NVARCHAR(50);
ALTER TABLE users ADD gender NVARCHAR(10);

INSERT INTO services(name, type, description)
VALUES (N'Tư vấn online HIV', N'ONLINE', N'Dịch vụ tư vấn trực tuyến về HIV/AIDS');

-- Thêm slot 09:00-09:30 cho ngày 2025-07-09
INSERT INTO schedule (created_at, date, end_time, is_available, start_time, time_slots, updated_at, doctor_id)
VALUES (
  GETDATE(), 
  '2025-07-09', 
  '2025-07-09 09:30:00.000', 
  1, 
  '2025-07-09 09:00:00.000', 
  '["09:00-09:30"]', 
  GETDATE(), 
  1
);

-- Thêm slot 10:00-10:30 cho ngày 2025-07-09
INSERT INTO schedule (created_at, date, end_time, is_available, start_time, time_slots, updated_at, doctor_id)
VALUES (
  GETDATE(), 
  '2025-07-09', 
  '2025-07-09 10:30:00.000', 
  1, 
  '2025-07-09 10:00:00.000', 
  '["10:00-10:30"]', 
  GETDATE(), 
  1
);

-- Thêm slot 09:00-09:30 cho ngày 2025-07-10
INSERT INTO schedule (created_at, date, end_time, is_available, start_time, time_slots, updated_at, doctor_id)
VALUES (
  GETDATE(), 
  '2025-07-10', 
  '2025-07-10 09:30:00.000', 
  1, 
  '2025-07-10 09:00:00.000', 
  '["09:00-09:30"]', 
  GETDATE(), 
  1
);

/////////////////////////////////////////////////////////

    ALTER TABLE users
ADD
    birth_date DATE NOT NULL DEFAULT '2000-01-01',
    treatment_start_date DATE NOT NULL DEFAULT '2000-01-01';