# Hướng dẫn sử dụng chức năng ARV Protocol

## Tổng quan
Hệ thống ARV Protocol đã được phát triển đầy đủ với các chức năng:
- Quản lý phác đồ ARV điều trị HIV
- Quản lý thuốc ARV
- Quản lý đơn thuốc cho bệnh nhân
- Hỗ trợ bác sĩ lựa chọn và customize phác đồ

## Cấu trúc dữ liệu

### 1. ARVProtocol (Phác đồ ARV)
- **name**: Tên phác đồ (VD: "TDF + 3TC + DTG")
- **description**: Mô tả phác đồ
- **targetGroup**: Nhóm đối tượng (Adults, Pregnant women, Children, Elderly)
- **medications**: Danh sách thuốc (JSON string)
- **dosage**: Hướng dẫn liều lượng
- **contraindications**: Chống chỉ định
- **sideEffects**: Tác dụng phụ
- **monitoring**: Theo dõi cần thiết

### 2. Medication (Thuốc ARV)
- **name**: Tên thuốc
- **genericName**: Tên gốc
- **brandName**: Tên thương hiệu
- **drugClass**: Nhóm thuốc (NRTI, NNRTI, PI, INSTI)
- **mechanism**: Cơ chế tác dụng
- **standardDosage**: Liều lượng chuẩn
- **sideEffects**: Tác dụng phụ
- **contraindications**: Chống chỉ định

### 3. Prescription (Đơn thuốc)
- **patient**: Bệnh nhân
- **doctor**: Bác sĩ kê đơn
- **protocol**: Phác đồ ARV được chọn
- **customInstructions**: Hướng dẫn tùy chỉnh
- **dosageAdjustments**: Điều chỉnh liều lượng
- **status**: Trạng thái (ACTIVE, COMPLETED, DISCONTINUED, SUSPENDED, MODIFIED)

## API Endpoints

### ARV Protocol APIs

#### Lấy phác đồ ARV
```http
GET /api/arv-protocols/active                    # Tất cả phác đồ đang hoạt động
GET /api/arv-protocols                           # Tất cả phác đồ (Admin/Doctor)
GET /api/arv-protocols/{id}                      # Phác đồ theo ID
GET /api/arv-protocols/target-group/{targetGroup} # Phác đồ theo nhóm đối tượng
GET /api/arv-protocols/search?keyword={keyword}  # Tìm kiếm phác đồ
GET /api/arv-protocols/target-groups             # Tất cả nhóm đối tượng
```

#### Quản lý phác đồ (Admin)
```http
POST /api/arv-protocols                          # Tạo phác đồ mới
PUT /api/arv-protocols/{id}                      # Cập nhật phác đồ
DELETE /api/arv-protocols/{id}                   # Xóa phác đồ
PATCH /api/arv-protocols/{id}/activate           # Kích hoạt phác đồ
```

### Medication APIs

#### Lấy thông tin thuốc
```http
GET /api/medications/active                      # Tất cả thuốc đang hoạt động
GET /api/medications                             # Tất cả thuốc (Admin/Doctor)
GET /api/medications/{id}                        # Thuốc theo ID
GET /api/medications/drug-class/{drugClass}      # Thuốc theo nhóm
GET /api/medications/search?keyword={keyword}    # Tìm kiếm thuốc
GET /api/medications/drug-classes                # Tất cả nhóm thuốc
GET /api/medications/name/{name}                 # Thuốc theo tên
GET /api/medications/generic-name/{genericName}  # Thuốc theo tên gốc
```

#### Quản lý thuốc (Admin)
```http
POST /api/medications                            # Tạo thuốc mới
PUT /api/medications/{id}                        # Cập nhật thuốc
DELETE /api/medications/{id}                     # Xóa thuốc
PATCH /api/medications/{id}/activate             # Kích hoạt thuốc
```

### Prescription APIs

#### Lấy đơn thuốc
```http
GET /api/prescriptions                           # Tất cả đơn thuốc (Admin/Doctor/Staff)
GET /api/prescriptions/{id}                      # Đơn thuốc theo ID
GET /api/prescriptions/patient/{patientId}       # Đơn thuốc của bệnh nhân
GET /api/prescriptions/patient/{patientId}/active # Đơn thuốc đang hoạt động của bệnh nhân
GET /api/prescriptions/doctor/{doctorId}         # Đơn thuốc của bác sĩ
GET /api/prescriptions/doctor/{doctorId}/active  # Đơn thuốc đang hoạt động của bác sĩ
GET /api/prescriptions/status/{status}           # Đơn thuốc theo trạng thái
```

#### Quản lý đơn thuốc (Doctor/Admin)
```http
POST /api/prescriptions                          # Tạo đơn thuốc mới
PUT /api/prescriptions/{id}                      # Cập nhật đơn thuốc
PATCH /api/prescriptions/{id}/status             # Cập nhật trạng thái
PATCH /api/prescriptions/{id}/discontinue        # Dừng đơn thuốc
PATCH /api/prescriptions/{id}/suspend            # Tạm ngưng đơn thuốc
```

## Ví dụ sử dụng

### 1. Tạo phác đồ ARV mới
```json
POST /api/arv-protocols
{
  "name": "TDF + 3TC + DTG",
  "description": "Phác đồ điều trị HIV hàng đầu cho người lớn",
  "targetGroup": "Adults",
  "medications": ["Tenofovir Disoproxil Fumarate", "Lamivudine", "Dolutegravir"],
  "dosage": "TDF 300mg + 3TC 300mg + DTG 50mg mỗi ngày",
  "contraindications": "Phụ nữ mang thai trong 3 tháng đầu, suy thận nặng",
  "sideEffects": "Buồn nôn, đau đầu, mệt mỏi, tăng men gan",
  "monitoring": "Theo dõi chức năng thận, men gan, tải lượng virus",
  "isActive": true
}
```

### 2. Tạo đơn thuốc cho bệnh nhân
```json
POST /api/prescriptions
{
  "patientId": 1,
  "doctorId": 1,
  "protocolId": 1,
  "customInstructions": "Uống thuốc sau bữa ăn",
  "dosageAdjustments": "Giảm liều TDF xuống 250mg do suy thận nhẹ",
  "notes": "Bệnh nhân có tiền sử suy thận nhẹ",
  "startDate": "2025-01-15T00:00:00",
  "endDate": null
}
```

### 3. Tìm kiếm phác đồ theo nhóm đối tượng
```http
GET /api/arv-protocols/target-group/Pregnant%20women
```

### 4. Cập nhật trạng thái đơn thuốc
```http
PATCH /api/prescriptions/1/status?status=COMPLETED
```

## Phân quyền

### Guest
- Xem phác đồ ARV đang hoạt động
- Xem thông tin thuốc đang hoạt động
- Tìm kiếm phác đồ và thuốc

### Patient
- Xem phác đồ ARV đang hoạt động
- Xem thông tin thuốc
- Xem đơn thuốc của mình

### Doctor
- Tất cả quyền của Patient
- Xem tất cả phác đồ ARV
- Xem tất cả thuốc
- Tạo, cập nhật đơn thuốc
- Quản lý đơn thuốc của bệnh nhân mình phụ trách

### Staff
- Xem tất cả đơn thuốc
- Xem thông tin phác đồ và thuốc

### Admin
- Tất cả quyền
- Quản lý phác đồ ARV (CRUD)
- Quản lý thuốc (CRUD)
- Quản lý tất cả đơn thuốc

## Khởi tạo dữ liệu

Chạy script SQL để khởi tạo dữ liệu mẫu:
```sql
-- Chạy file arv_data_init.sql
```

Script này sẽ tạo:
- 8 loại thuốc ARV phổ biến
- 8 phác đồ điều trị ARV cho các nhóm đối tượng khác nhau

## Lưu ý quan trọng

1. **Bảo mật**: Tất cả API đều có phân quyền nghiêm ngặt
2. **Validation**: Dữ liệu được validate đầy đủ trước khi lưu
3. **Audit**: Tất cả thay đổi đều được ghi log với timestamp
4. **Soft Delete**: Dữ liệu không bị xóa hoàn toàn, chỉ đánh dấu inactive
5. **JSON Storage**: Danh sách thuốc được lưu dưới dạng JSON string để linh hoạt

## Tích hợp với hệ thống hiện tại

Chức năng ARV Protocol đã được tích hợp hoàn toàn với:
- Hệ thống authentication/authorization
- Hệ thống appointment
- Hệ thống test results
- Hệ thống patient management

Bác sĩ có thể:
1. Chọn phác đồ ARV từ danh sách có sẵn
2. Customize phác đồ cho từng bệnh nhân
3. Theo dõi hiệu quả điều trị
4. Điều chỉnh liều lượng khi cần thiết