-- Script khởi tạo dữ liệu mẫu cho hệ thống ARV
USE hiv_medical;
GO

PRINT '=== KHỞI TẠO DỮ LIỆU ARV PROTOCOL ===';

-- Xóa dữ liệu cũ nếu có
DELETE FROM prescriptions;
DELETE FROM arv_protocols;
DELETE FROM medications;

-- Khởi tạo dữ liệu thuốc ARV
INSERT INTO medications (name, generic_name, brand_name, description, drug_class, mechanism, dosage_forms, standard_dosage, side_effects, contraindications, drug_interactions, is_active, created_at, updated_at) VALUES
('Tenofovir Disoproxil Fumarate', 'TDF', 'Viread', N'Thuốc ức chế men sao chép ngược nucleoside (NRTI)', 'NRTI', N'Ức chế men sao chép ngược của HIV', N'Viên nén 300mg', N'300mg mỗi ngày', N'Buồn nôn, tiêu chảy, đau đầu, mệt mỏi', N'Quá mẫn với thành phần thuốc, suy thận nặng', N'Tương tác với các thuốc ảnh hưởng đến thận', 1, GETDATE(), GETDATE()),

('Lamivudine', '3TC', 'Epivir', N'Thuốc ức chế men sao chép ngược nucleoside (NRTI)', 'NRTI', N'Ức chế men sao chép ngược của HIV', N'Viên nén 300mg', N'300mg mỗi ngày', N'Buồn nôn, đau đầu, mệt mỏi', N'Quá mẫn với thành phần thuốc', N'Tương tác với các thuốc ảnh hưởng đến gan', 1, GETDATE(), GETDATE()),

('Dolutegravir', 'DTG', 'Tivicay', N'Thuốc ức chế men tích hợp (INSTI)', 'INSTI', N'Ức chế men tích hợp của HIV', N'Viên nén 50mg', N'50mg mỗi ngày', N'Đau đầu, mất ngủ, tăng men gan', N'Quá mẫn với thành phần thuốc', N'Tương tác với các thuốc ảnh hưởng đến chuyển hóa', 1, GETDATE(), GETDATE()),

('Emtricitabine', 'FTC', 'Emtriva', N'Thuốc ức chế men sao chép ngược nucleoside (NRTI)', 'NRTI', N'Ức chế men sao chép ngược của HIV', N'Viên nén 200mg', N'200mg mỗi ngày', N'Buồn nôn, đau đầu, mệt mỏi', N'Quá mẫn với thành phần thuốc', N'Tương tác với các thuốc ảnh hưởng đến gan', 1, GETDATE(), GETDATE()),

('Efavirenz', 'EFV', 'Sustiva', N'Thuốc ức chế men sao chép ngược không nucleoside (NNRTI)', 'NNRTI', N'Ức chế men sao chép ngược của HIV', N'Viên nén 600mg', N'600mg mỗi ngày', N'Chóng mặt, mất ngủ, ảo giác', N'Quá mẫn với thành phần thuốc, rối loạn tâm thần', N'Tương tác với nhiều thuốc khác', 1, GETDATE(), GETDATE()),

('Ritonavir', 'RTV', 'Norvir', N'Thuốc ức chế protease (PI)', 'PI', N'Ức chế protease của HIV', N'Viên nén 100mg', N'100mg mỗi ngày', N'Buồn nôn, tiêu chảy, tăng mỡ máu', N'Quá mẫn với thành phần thuốc', N'Tương tác với nhiều thuốc khác', 1, GETDATE(), GETDATE()),

('Lopinavir', 'LPV', 'Kaletra', N'Thuốc ức chế protease (PI)', 'PI', N'Ức chế protease của HIV', N'Viên nén 200mg/50mg', N'400mg/100mg mỗi ngày', N'Buồn nôn, tiêu chảy, tăng mỡ máu', N'Quá mẫn với thành phần thuốc', N'Tương tác với nhiều thuốc khác', 1, GETDATE(), GETDATE()),

('Raltegravir', 'RAL', 'Isentress', N'Thuốc ức chế men tích hợp (INSTI)', 'INSTI', N'Ức chế men tích hợp của HIV', N'Viên nén 400mg', N'400mg 2 lần mỗi ngày', N'Đau đầu, mệt mỏi, tăng men gan', N'Quá mẫn với thành phần thuốc', N'Tương tác với các thuốc ảnh hưởng đến chuyển hóa', 1, GETDATE(), GETDATE());

PRINT '=== KHỞI TẠO DỮ LIỆU ARV PROTOCOL ===';

-- Khởi tạo dữ liệu phác đồ ARV
INSERT INTO arv_protocols (name, description, target_group, medications, dosage, contraindications, side_effects, monitoring, is_active, created_at, updated_at) VALUES
('TDF + 3TC + DTG', N'Phác đồ điều trị HIV hàng đầu cho người lớn', 'Adults', N'["Tenofovir Disoproxil Fumarate", "Lamivudine", "Dolutegravir"]', N'TDF 300mg + 3TC 300mg + DTG 50mg mỗi ngày', N'Phụ nữ mang thai trong 3 tháng đầu, suy thận nặng', N'Buồn nôn, đau đầu, mệt mỏi, tăng men gan', N'Theo dõi chức năng thận, men gan, tải lượng virus', 1, GETDATE(), GETDATE()),

('TDF + FTC + DTG', N'Phác đồ điều trị HIV cho người lớn', 'Adults', N'["Tenofovir Disoproxil Fumarate", "Emtricitabine", "Dolutegravir"]', N'TDF 300mg + FTC 200mg + DTG 50mg mỗi ngày', N'Phụ nữ mang thai trong 3 tháng đầu, suy thận nặng', N'Buồn nôn, đau đầu, mệt mỏi', N'Theo dõi chức năng thận, tải lượng virus', 1, GETDATE(), GETDATE()),

('TDF + 3TC + EFV', N'Phác đồ điều trị HIV cho người lớn', 'Adults', N'["Tenofovir Disoproxil Fumarate", "Lamivudine", "Efavirenz"]', N'TDF 300mg + 3TC 300mg + EFV 600mg mỗi ngày', N'Phụ nữ mang thai, rối loạn tâm thần', N'Chóng mặt, mất ngủ, ảo giác', N'Theo dõi chức năng thận, tâm thần, tải lượng virus', 1, GETDATE(), GETDATE()),

('LPV/r + TDF + 3TC', N'Phác đồ điều trị HIV cho người lớn', 'Adults', N'["Lopinavir", "Ritonavir", "Tenofovir Disoproxil Fumarate", "Lamivudine"]', N'LPV 400mg + RTV 100mg + TDF 300mg + 3TC 300mg mỗi ngày', N'Suy thận nặng, rối loạn chuyển hóa lipid', N'Buồn nôn, tiêu chảy, tăng mỡ máu', N'Theo dõi chức năng thận, lipid máu, tải lượng virus', 1, GETDATE(), GETDATE()),

('TDF + 3TC + DTG (Pregnant Women)', N'Phác đồ điều trị HIV cho phụ nữ mang thai', 'Pregnant women', N'["Tenofovir Disoproxil Fumarate", "Lamivudine", "Dolutegravir"]', N'TDF 300mg + 3TC 300mg + DTG 50mg mỗi ngày', N'Suy thận nặng', N'Buồn nôn, đau đầu, mệt mỏi', N'Theo dõi chức năng thận, men gan, tải lượng virus, thai kỳ', 1, GETDATE(), GETDATE()),

('ABC + 3TC + DTG', N'Phác đồ điều trị HIV cho người lớn (thay thế TDF)', 'Adults', N'["Abacavir", "Lamivudine", "Dolutegravir"]', N'ABC 600mg + 3TC 300mg + DTG 50mg mỗi ngày', N'Dị ứng với Abacavir, suy tim', N'Buồn nôn, đau đầu, mệt mỏi, phản ứng dị ứng', N'Theo dõi chức năng tim, tải lượng virus', 1, GETDATE(), GETDATE()),

('TDF + 3TC + RAL', N'Phác đồ điều trị HIV cho người lớn', 'Adults', N'["Tenofovir Disoproxil Fumarate", "Lamivudine", "Raltegravir"]', N'TDF 300mg + 3TC 300mg + RAL 400mg 2 lần mỗi ngày', N'Suy thận nặng', N'Buồn nôn, đau đầu, mệt mỏi', N'Theo dõi chức năng thận, tải lượng virus', 1, GETDATE(), GETDATE()),

('ABC + 3TC + EFV (Children)', N'Phác đồ điều trị HIV cho trẻ em', 'Children', N'["Abacavir", "Lamivudine", "Efavirenz"]', N'ABC 300mg + 3TC 150mg + EFV 200mg mỗi ngày', N'Dị ứng với Abacavir, rối loạn tâm thần', N'Buồn nôn, đau đầu, mệt mỏi, phản ứng dị ứng', N'Theo dõi chức năng tim, tâm thần, tải lượng virus', 1, GETDATE(), GETDATE());

PRINT '=== KHỞI TẠO DỮ LIỆU PRESCRIPTION MẪU ===';

-- Khởi tạo một số đơn thuốc mẫu (nếu có bệnh nhân và bác sĩ trong hệ thống)
-- Lưu ý: Cần có dữ liệu bệnh nhân và bác sĩ trước khi chạy phần này

PRINT '=== HOÀN THÀNH KHỞI TẠO DỮ LIỆU ARV ===';
PRINT 'Đã tạo:';
PRINT '- 8 loại thuốc ARV';
PRINT '- 8 phác đồ điều trị ARV';
PRINT 'Dữ liệu sẵn sàng để sử dụng!'; 