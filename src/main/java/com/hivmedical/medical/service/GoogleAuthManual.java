package com.hivmedical.medical.service;

public class GoogleAuthManual {
    public static void main(String[] args) throws Exception {
        GoogleCalendarService service = new GoogleCalendarService();
        service.getCalendarService(); // Sẽ in ra link xác thực nếu chưa có token
        System.out.println("Xác thực thành công! Token đã được lưu vào thư mục 'tokens/'.");
        System.out.println("Token path: " + new java.io.File("tokens").getAbsolutePath());
    }
}