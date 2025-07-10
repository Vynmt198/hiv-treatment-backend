package com.hivmedical.medical.entitty;

public enum TestStatus {
    REQUESTED, // Đã yêu cầu
    SAMPLE_RECEIVED, // Đã nhận mẫu
    IN_PROGRESS, // Đang xét nghiệm
    COMPLETED, // Đã có kết quả
    CANCELLED // Hủy
}