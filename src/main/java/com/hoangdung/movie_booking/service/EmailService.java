package com.hoangdung.movie_booking.service;

import com.hoangdung.movie_booking.dto.EmailDTO;
import java.util.Map;

public interface EmailService {

    /**
     * Gửi email dựa trên một DTO chứa đầy đủ thông tin
     * @param emailDTO (from, to, subject, body, attachments,…).
     */
    void sendEmail(EmailDTO emailDTO);

    /**
     * Gửi email HTML theo template (Thymeleaf/Freemarker).
     * templateName: tên file template (ví dụ welcome.html).
     * variables: dữ liệu map để inject vào template.
     * to: một người nhận.
     */
    void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables);

    /**
     * Gửi mail thuần (không dùng Template)
     * Sử dụng send OTP, Reset password
     * to: một người
     */
    void sendSimpleEmail(String to, String subject, String content);

    //========== SEND MANY PEOPLE ==========//
    /**
     *
     * Gửi email HTML theo template (Thymeleaf/Freemarker).
     * templateName: tên file template (ví dụ welcome.html).
     * variables: dữ liệu map để inject vào template.
     * to: nhiều người nhận.
     */
    void sendTemplateEmail(String[] to, String subject, String templateName, Map<String, Object> variables);

    /**
     * Gửi mail thuần (không dùng Template)
     * Sử dụng send OTP, Reset password
     * to: nhiều người
     */
    void sendSimpleEmail(String[] to, String subject, String content);

    //========== SEND ASYNC =========//
    /**
     * Gửi email dựa trên một DTO chứa đầy đủ thông tin
     *
     * @param emailDTO (from, to, subject, body, attachments,…).
     *                 Async
     */
    void sendEmailAsync(EmailDTO emailDTO);

    /**
     * Gửi email HTML theo template (Thymeleaf/Freemarker).
     * templateName: tên file template (ví dụ welcome.html).
     * variables: dữ liệu map để inject vào template.
     * to: một người nhận.
     * Async
     */
    void sendTemplateEmailAsync(String to, String subject, String templateName, Map<String, Object> variables);

    /**
     * Gửi email HTML theo template (Thymeleaf/Freemarker).
     * templateName: tên file template (ví dụ welcome.html).
     * variables: dữ liệu map để inject vào template.
     * to: nhiều người nhận.
     * Async
     */
    void sendTemplateEmailAsync(String[] to, String subject, String templateName, Map<String, Object> variables);

    /**
     * Gửi mail thuần (không dùng Template)
     * Sử dụng send OTP, Reset password
     * to: một người
     * Async
     */
    void sendSimpleEmailAsync(String to, String subject, String content);

    /**
     * Gửi mail thuần (không dùng Template)
     * Sử dụng send OTP, Reset password
     * to: nhiều người
     * Async
     */
    void sendSimpleEmailAsync(String[] to, String subject, String content);
}
