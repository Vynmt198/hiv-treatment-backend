-- Script kiểm tra cấu trúc database hiện tại
USE hiv_medical;
GO

PRINT '=== KIỂM TRA CẤU TRÚC DATABASE ===';

-- Kiểm tra các bảng hiện có
PRINT '--- CÁC BẢNG HIỆN CÓ ---';
SELECT TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;

-- Kiểm tra cấu trúc bảng users
PRINT '--- CẤU TRÚC BẢNG USERS ---';
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'users'
ORDER BY ORDINAL_POSITION;

-- Kiểm tra cấu trúc bảng appointments
PRINT '--- CẤU TRÚC BẢNG APPOINTMENTS ---';
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'appointments'
ORDER BY ORDINAL_POSITION;

-- Kiểm tra cấu trúc bảng doctor
PRINT '--- CẤU TRÚC BẢNG DOCTOR ---';
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'Doctor'
ORDER BY ORDINAL_POSITION;

-- Kiểm tra foreign key constraints
PRINT '--- FOREIGN KEY CONSTRAINTS ---';
SELECT 
    fk.name as constraint_name,
    OBJECT_NAME(fk.parent_object_id) as table_name,
    COL_NAME(fkc.parent_object_id, fkc.parent_column_id) as column_name,
    OBJECT_NAME(fk.referenced_object_id) as referenced_table_name,
    COL_NAME(fkc.referenced_object_id, fkc.referenced_column_id) as referenced_column_name
FROM sys.foreign_keys fk
INNER JOIN sys.foreign_key_columns fkc ON fk.object_id = fkc.constraint_object_id
ORDER BY table_name, column_name;

-- Kiểm tra dữ liệu mẫu
PRINT '--- DỮ LIỆU MẪU ---';
SELECT 'Users count:' as info, COUNT(*) as count FROM users;
SELECT 'Appointments count:' as info, COUNT(*) as count FROM appointments;
SELECT 'Doctor count:' as info, COUNT(*) as count FROM Doctor;

-- Kiểm tra dữ liệu appointments
PRINT '--- DỮ LIỆU APPOINTMENTS ---';
SELECT TOP 5 * FROM appointments;

-- Kiểm tra dữ liệu users
PRINT '--- DỮ LIỆU USERS ---';
SELECT TOP 5 * FROM users; 