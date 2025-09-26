package com.hoangdung.movie_booking.service;

import com.hoangdung.movie_booking.dto.EmailDTO;
import java.util.Map;

/**
 * Service interface for sending emails in various formats:
 * <ul>
 *     <li>Plain text emails (simple messages, OTP, password reset).</li>
 *     <li>HTML emails with templates (Thymeleaf/Freemarker).</li>
 *     <li>Support for single or multiple recipients.</li>
 *     <li>Support for synchronous and asynchronous sending.</li>
 * </ul>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * // Send simple email
 * emailService.sendSimpleEmail("user@example.com", "OTP Code", "Your OTP is 123456");
 *
 * // Send template email
 * Map<String, Object> data = Map.of("username", "Dũng", "link", resetLink);
 * emailService.sendTemplateEmail("user@example.com", "Reset Password", "reset_password.html", data);
 *
 * // Send async
 * emailService.sendSimpleEmailAsync(new String[]{"u1@example.com", "u2@example.com"}, "News", "Hello all!");
 * }</pre>
 *
 * @author
 *   Hoàng Đình Dũng
 * @since 1.0
 */
public interface EmailService {

    /**
     * Send email based on a DTO containing full information.
     *
     * @param emailDTO DTO including from, to, subject, body, attachments, etc.
     */
    void sendEmail(EmailDTO emailDTO);

    /**
     * Send an HTML email using a template (Thymeleaf/Freemarker).
     *
     * @param to           Recipient email address
     * @param subject      Subject line
     * @param templateName Template file name (e.g. welcome.html)
     * @param variables    Map of variables to inject into the template
     */
    void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables);

    /**
     * Send a simple plain text email (no template).
     * <p>Useful for OTPs, password reset, quick notifications.</p>
     *
     * @param to      Recipient email address
     * @param subject Subject line
     * @param content Plain text content
     */
    void sendSimpleEmail(String to, String subject, String content);

    /**
     * Send an HTML email using a template (to multiple recipients).
     *
     * @param to           Array of recipient emails
     * @param subject      Subject line
     * @param templateName Template file name (e.g. welcome.html)
     * @param variables    Map of variables to inject into the template
     */
    void sendTemplateEmail(String[] to, String subject, String templateName, Map<String, Object> variables);

    /**
     * Send a simple plain text email (to multiple recipients).
     *
     * @param to      Array of recipient emails
     * @param subject Subject line
     * @param content Plain text content
     */
    void sendSimpleEmail(String[] to, String subject, String content);

    //========== ASYNC METHODS ==========//

    /**
     * Send email asynchronously based on a DTO.
     *
     * @param emailDTO DTO including from, to, subject, body, attachments, etc.
     */
    void sendEmailAsync(EmailDTO emailDTO);

    /**
     * Send an HTML email asynchronously using a template.
     *
     * @param to           Recipient email address
     * @param subject      Subject line
     * @param templateName Template file name
     * @param variables    Map of variables to inject into the template
     */
    void sendTemplateEmailAsync(String to, String subject, String templateName, Map<String, Object> variables);

    /**
     * Send an HTML email asynchronously using a template (to multiple recipients).
     *
     * @param to           Array of recipient emails
     * @param subject      Subject line
     * @param templateName Template file name
     * @param variables    Map of variables to inject into the template
     */
    void sendTemplateEmailAsync(String[] to, String subject, String templateName, Map<String, Object> variables);

    /**
     * Send a plain text email asynchronously.
     *
     * @param to      Recipient email address
     * @param subject Subject line
     * @param content Plain text content
     */
    void sendSimpleEmailAsync(String to, String subject, String content);

    /**
     * Send a plain text email asynchronously (to multiple recipients).
     *
     * @param to      Array of recipient emails
     * @param subject Subject line
     * @param content Plain text content
     */
    void sendSimpleEmailAsync(String[] to, String subject, String content);
}
