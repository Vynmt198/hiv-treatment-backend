-- Script migration đơn giản - chỉ tạo bảng mới và migrate dữ liệu cơ bản
-- Chạy từng bước theo thứ tự

USE hiv_medical;
GO

PRINT '=== BẮT ĐẦU MIGRATION ĐƠN GIẢN ===';

-- Bước 1: Kiểm tra dữ liệu hiện tại
PRINT '=== KIỂM TRA DỮ LIỆU HIỆN TẠI ===';
SELECT 'Users count:' as info, COUNT(*) as count FROM users;
SELECT 'Users with NULL full_name:' as info, COUNT(*) as count FROM users WHERE full_name IS NULL;
SELECT 'Users by role:' as info, role, COUNT(*) as count FROM users GROUP BY role;

-- Bước 2: Tạo bảng accounts mới
PRINT '=== TẠO BẢNG ACCOUNTS ===';
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'accounts')
BEGIN
    CREATE TABLE accounts (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(255) NOT NULL UNIQUE,
        password_hash NVARCHAR(255) NOT NULL,
        email NVARCHAR(255) NOT NULL UNIQUE,
        role NVARCHAR(50) NOT NULL,
        enabled BIT NOT NULL DEFAULT 1,
        registration_date DATETIME2 NOT NULL,
        last_login_date DATETIME2 NULL,
        profile_picture_url NVARCHAR(255) NULL,
        created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
    );
    PRINT 'Bảng accounts đã được tạo';
END
ELSE
BEGIN
    PRINT 'Bảng accounts đã tồn tại';
END

-- Bước 3: Tạo bảng patient_profiles
PRINT '=== TẠO BẢNG PATIENT_PROFILES ===';
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'patient_profiles')
BEGIN
    CREATE TABLE patient_profiles (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        account_id BIGINT NOT NULL,
        full_name NVARCHAR(255) NOT NULL,
        phone NVARCHAR(50) NULL,
        gender NVARCHAR(10) NULL,
        address NVARCHAR(255) NULL,
        birth_date DATE NOT NULL,
        hiv_status NVARCHAR(255) NULL,
        treatment_start_date DATE NOT NULL,
        created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
    );
    PRINT 'Bảng patient_profiles đã được tạo';
END
ELSE
BEGIN
    PRINT 'Bảng patient_profiles đã tồn tại';
END

-- Bước 4: Tạo bảng doctor_profiles
PRINT '=== TẠO BẢNG DOCTOR_PROFILES ===';
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'doctor_profiles')
BEGIN
    CREATE TABLE doctor_profiles (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        account_id BIGINT NOT NULL,
        full_name NVARCHAR(255) NOT NULL,
        specialization NVARCHAR(255) NULL,
        qualification NVARCHAR(255) NULL,
        phone_number NVARCHAR(50) NULL,
        working_schedule NVARCHAR(255) NULL,
        image_url NVARCHAR(255) NULL,
        created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
    );
    PRINT 'Bảng doctor_profiles đã được tạo';
END
ELSE
BEGIN
    PRINT 'Bảng doctor_profiles đã tồn tại';
END

-- Bước 5: Tạo bảng admin_profiles
PRINT '=== TẠO BẢNG ADMIN_PROFILES ===';
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'admin_profiles')
BEGIN
    CREATE TABLE admin_profiles (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        account_id BIGINT NOT NULL,
        full_name NVARCHAR(255) NOT NULL,
        phone NVARCHAR(50) NULL,
        department NVARCHAR(255) NULL,
        created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
    );
    PRINT 'Bảng admin_profiles đã được tạo';
END
ELSE
BEGIN
    PRINT 'Bảng admin_profiles đã tồn tại';
END

-- Bước 6: Migrate dữ liệu từ users sang accounts
PRINT '=== MIGRATE DỮ LIỆU TỪ USERS SANG ACCOUNTS ===';
IF NOT EXISTS (SELECT * FROM accounts)
BEGIN
    INSERT INTO accounts (username, password_hash, email, role, enabled, registration_date, last_login_date, profile_picture_url)
    SELECT 
        username, 
        password_hash, 
        email, 
        role, 
        enabled, 
        registration_date, 
        last_login_date, 
        profile_picture_url
    FROM users;
    
    DECLARE @accounts_inserted INT = @@ROWCOUNT;
    PRINT 'Đã migrate ' + CAST(@accounts_inserted AS VARCHAR) + ' accounts';
END
ELSE
BEGIN
    PRINT 'Bảng accounts đã có dữ liệu, bỏ qua migration';
END

-- Bước 7: Migrate thông tin patient sang patient_profiles
PRINT '=== MIGRATE PATIENT PROFILES ===';
IF NOT EXISTS (SELECT * FROM patient_profiles)
BEGIN
    INSERT INTO patient_profiles (account_id, full_name, phone, gender, address, birth_date, hiv_status, treatment_start_date)
    SELECT 
        a.id, 
        CASE 
            WHEN u.full_name IS NULL OR u.full_name = '' THEN a.username
            ELSE u.full_name 
        END as full_name, 
        u.phone, 
        u.gender, 
        u.address, 
        CASE 
            WHEN u.birth_date IS NULL THEN '2000-01-01'
            ELSE u.birth_date 
        END as birth_date, 
        u.hiv_status, 
        CASE 
            WHEN u.treatment_start_date IS NULL THEN '2000-01-01'
            ELSE u.treatment_start_date 
        END as treatment_start_date
    FROM users u
    INNER JOIN accounts a ON u.username = a.username
    WHERE u.role = 'PATIENT';
    
    DECLARE @patients_inserted INT = @@ROWCOUNT;
    PRINT 'Đã migrate ' + CAST(@patients_inserted AS VARCHAR) + ' patient profiles';
END
ELSE
BEGIN
    PRINT 'Bảng patient_profiles đã có dữ liệu, bỏ qua migration';
END

-- Bước 8: Migrate thông tin doctor sang doctor_profiles
PRINT '=== MIGRATE DOCTOR PROFILES ===';
IF NOT EXISTS (SELECT * FROM doctor_profiles)
BEGIN
    INSERT INTO doctor_profiles (account_id, full_name, specialization, qualification, phone_number, working_schedule, image_url)
    SELECT 
        a.id, 
        CASE 
            WHEN u.full_name IS NULL OR u.full_name = '' THEN a.username
            ELSE u.full_name 
        END as full_name, 
        'HIV/AIDS', 
        'Bác sĩ', 
        u.phone, 
        'Thứ 2 - Thứ 6, 08:00-17:00', 
        NULL
    FROM users u
    INNER JOIN accounts a ON u.username = a.username
    WHERE u.role = 'DOCTOR';
    
    DECLARE @doctors_inserted INT = @@ROWCOUNT;
    PRINT 'Đã migrate ' + CAST(@doctors_inserted AS VARCHAR) + ' doctor profiles';
END
ELSE
BEGIN
    PRINT 'Bảng doctor_profiles đã có dữ liệu, bỏ qua migration';
END

-- Bước 9: Migrate thông tin admin sang admin_profiles
PRINT '=== MIGRATE ADMIN PROFILES ===';
IF NOT EXISTS (SELECT * FROM admin_profiles)
BEGIN
    INSERT INTO admin_profiles (account_id, full_name, phone, department)
    SELECT 
        a.id, 
        CASE 
            WHEN u.full_name IS NULL OR u.full_name = '' THEN a.username
            ELSE u.full_name 
        END as full_name, 
        u.phone, 
        'Quản trị hệ thống'
    FROM users u
    INNER JOIN accounts a ON u.username = a.username
    WHERE u.role = 'ADMIN';
    
    DECLARE @admins_inserted INT = @@ROWCOUNT;
    PRINT 'Đã migrate ' + CAST(@admins_inserted AS VARCHAR) + ' admin profiles';
END
ELSE
BEGIN
    PRINT 'Bảng admin_profiles đã có dữ liệu, bỏ qua migration';
END

-- Bước 10: Tạo indexes
PRINT '=== TẠO INDEXES ===';
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_accounts_username')
BEGIN
    CREATE INDEX IX_accounts_username ON accounts(username);
    PRINT 'Đã tạo index IX_accounts_username';
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_accounts_email')
BEGIN
    CREATE INDEX IX_accounts_email ON accounts(email);
    PRINT 'Đã tạo index IX_accounts_email';
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_patient_profiles_account_id')
BEGIN
    CREATE INDEX IX_patient_profiles_account_id ON patient_profiles(account_id);
    PRINT 'Đã tạo index IX_patient_profiles_account_id';
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_doctor_profiles_account_id')
BEGIN
    CREATE INDEX IX_doctor_profiles_account_id ON doctor_profiles(account_id);
    PRINT 'Đã tạo index IX_doctor_profiles_account_id';
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_admin_profiles_account_id')
BEGIN
    CREATE INDEX IX_admin_profiles_account_id ON admin_profiles(account_id);
    PRINT 'Đã tạo index IX_admin_profiles_account_id';
END

-- Bước 11: Kiểm tra kết quả
PRINT '=== KIỂM TRA KẾT QUẢ ===';
SELECT 'Accounts count:' as info, COUNT(*) as count FROM accounts;
SELECT 'Patient profiles count:' as info, COUNT(*) as count FROM patient_profiles;
SELECT 'Doctor profiles count:' as info, COUNT(*) as count FROM doctor_profiles;
SELECT 'Admin profiles count:' as info, COUNT(*) as count FROM admin_profiles;

-- Kiểm tra foreign key relationships
SELECT 'Patient profiles with accounts:' as info, COUNT(*) as count 
FROM patient_profiles pp 
INNER JOIN accounts a ON pp.account_id = a.id;

SELECT 'Doctor profiles with accounts:' as info, COUNT(*) as count 
FROM doctor_profiles dp 
INNER JOIN accounts a ON dp.account_id = a.id;

PRINT '=== MIGRATION CƠ BẢN HOÀN THÀNH ===';
PRINT 'Lưu ý: Bảng users cũ vẫn còn tồn tại.';
PRINT 'Bảng appointments chưa được cập nhật - sẽ cập nhật sau khi chạy ứng dụng.';
PRINT 'Bạn có thể chạy ứng dụng Spring Boot để test các chức năng mới.'; 



///////////////////////////// chạy các dòng trên xong mới chạy dòng này ///////////////
ALTER TABLE appointments ADD user_id BIGINT NULL;
-- Cập nhật dữ liệu user_id cho từng dòng (dựa vào dữ liệu cũ hoặc mapping)
-- Ví dụ: UPDATE appointments SET user_id = ... WHERE ...;
-- Sau khi chắc chắn không còn NULL:
ALTER TABLE appointments ALTER COLUMN user_id BIGINT NOT NULL;

ALTER TABLE doctor ADD account_id BIGINT NULL;
-- Cập nhật dữ liệu account_id cho từng dòng
-- Sau khi chắc chắn không còn NULL:
ALTER TABLE doctor ALTER COLUMN account_id BIGINT NOT NULL;

DELETE FROM appointments;
ALTER TABLE appointments ADD user_id BIGINT NOT NULL;

ALTER TABLE appointments
ADD CONSTRAINT FK_appointments_user_id FOREIGN KEY (user_id) REFERENCES accounts(id);

SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'appointments';
SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'doctor';