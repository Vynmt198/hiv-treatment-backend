package com.hivmedical.medical.entitty;

public enum AppointmentStatus {
    PENDING, // Chưa đến
    CHECKED_IN, // Đã đến
    IN_PROGRESS, // Đang khám
    COMPLETED, // Hoàn tất
    ABSENT, // Vắng
    ONLINE_PENDING,
    ONLINE_ANONYMOUS_PENDING,
    CANCELLED // Hủy lịch
}