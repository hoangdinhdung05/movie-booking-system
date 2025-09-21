package com.hoangdung.movie_booking.service.impl;

import com.hoangdung.movie_booking.dto.AttachmentDTO;
import com.hoangdung.movie_booking.dto.EmailDTO;
import com.hoangdung.movie_booking.exception.EmailTemplateException;
import com.hoangdung.movie_booking.exception.SendMailException;
import com.hoangdung.movie_booking.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of {@link EmailService} using Spring {@link JavaMailSender} and Thymeleaf {@link TemplateEngine}.
 * <p>
 * Supports:
 * <ul>
 *   <li>Plain text email (via {@link SimpleMailMessage}).</li>
 *   <li>HTML email with templates (Thymeleaf/Freemarker).</li>
 *   <li>Sending to single or multiple recipients.</li>
 *   <li>Attachments (inline or normal).</li>
 *   <li>Asynchronous sending using {@code @Async} with {@code mailTaskExecutor}.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Send simple text email
 * emailService.sendSimpleEmail("user@example.com", "OTP Code", "Your OTP is 123456");
 *
 * // Send template email
 * Map<String, Object> data = Map.of("username", "Dung", "link", resetLink);
 * emailService.sendTemplateEmail("user@example.com", "Reset Password", "reset_password.html", data);
 *
 * // Send async with attachments
 * EmailDTO dto = EmailDTO.builder()
 *     .to(List.of("u1@example.com"))
 *     .subject("Monthly Report")
 *     .htmlContent("<h1>Report</h1>")
 *     .attachments(List.of(new AttachmentDTO("report.pdf", pdfBytes, false)))
 *     .isHtml(true)
 *     .build();
 * emailService.sendEmailAsync(dto);
 * }</pre>
 *
 * @author
 *   Hoàng Đình Dũng
 * @since 1.0
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine mailTemplateEngine;

    public EmailServiceImpl(JavaMailSender mailSender,
                            @Qualifier("mailTemplateEngine") TemplateEngine mailTemplateEngine) {
        this.mailSender = mailSender;
        this.mailTemplateEngine = mailTemplateEngine;
    }

    /**
     * Gửi email dựa trên một DTO chứa đầy đủ thông tin
     *
     * @param emailDTO (from, to, subject, body, attachments,…).
     */
    @Override
    public void sendEmail(EmailDTO emailDTO) {
        try {
            if (emailDTO.isHtml() || emailDTO.getAttachments() != null || StringUtils.hasText(emailDTO.getTemplateName())) {
                sendMineMessage(emailDTO);
            } else {
                sendSimpleMessage(emailDTO);
            }
            log.info("Email sent to {} successfully", emailDTO.getTo());
        } catch (Exception e) {
            log.error("SendMail Fail with error={}", e.getMessage());
            throw new SendMailException("Failed to send email to " + emailDTO.getTo(), e);
        }
    }

    /**
     * Gửi email HTML theo template (Thymeleaf/Freemarker).
     * templateName: tên file template (ví dụ welcome.html).
     * variables: dữ liệu map để inject vào template.
     * to: một người nhận.
     */
    @Override
    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        sendTemplateEmail(new String[]{to}, subject, templateName, variables);
    }

    /**
     * Gửi mail thuần (không dùng Template)
     * Sử dụng send OTP, Reset password
     */
    @Override
    public void sendSimpleEmail(String to, String subject, String content) {
        sendSimpleEmail(new String[]{to}, subject, content);
    }

    //========== SEND MANY PEOPLE =========//
    /**
     * Gửi email HTML theo template (Thymeleaf/Freemarker).
     * templateName: tên file template (ví dụ welcome.html).
     * variables: dữ liệu map để inject vào template.
     * to: nhiều người nhận.
     */
    @Override
    public void sendTemplateEmail(String[] to, String subject, String templateName, Map<String, Object> variables) {
        EmailDTO emailDTO = EmailDTO.builder()
                .to(Arrays.asList(to))
                .subject(subject)
                .templateName(templateName)
                .templateVariables(variables)
                .isHtml(true)
                .build();
        sendEmail(emailDTO);
    }

    /**
     * Gửi mail thuần (không dùng Template)
     * Sử dụng send OTP, Reset password
     * to: nhiều người
     */
    @Override
    public void sendSimpleEmail(String[] to, String subject, String content) {
        EmailDTO emailDTO = EmailDTO.builder()
                .to(Arrays.asList(to))
                .subject(subject)
                .textContent(content)
                .isHtml(false)
                .build();
        sendEmail(emailDTO);
    }

    //========= SEND ASYNC ==========//
    /**
     * Gửi email dựa trên một DTO chứa đầy đủ thông tin
     *
     * @param emailDTO (from, to, subject, body, attachments,…).
     * Async
     */
    @Override
    @Async("mailTaskExecutor")
    public void sendEmailAsync(EmailDTO emailDTO) {
        CompletableFuture.runAsync(() -> sendEmail(emailDTO));
    }

    /**
     * Gửi email HTML theo template (Thymeleaf/Freemarker).
     * templateName: tên file template (ví dụ welcome.html).
     * variables: dữ liệu map để inject vào template.
     * to: một người nhận.
     * Async
     */
    @Override
    @Async("mailTaskExecutor")
    public void sendTemplateEmailAsync(String to, String subject, String templateName, Map<String, Object> variables) {
        CompletableFuture.runAsync(() -> sendTemplateEmail(to, subject, templateName, variables));
    }

    /**
     * Gửi email HTML theo template (Thymeleaf/Freemarker).
     * templateName: tên file template (ví dụ welcome.html).
     * variables: dữ liệu map để inject vào template.
     * to: nhiều người nhận.
     */
    @Override
    @Async("mailTaskExecutor")
    public void sendTemplateEmailAsync(String[] to, String subject, String templateName, Map<String, Object> variables) {
        CompletableFuture.runAsync(() -> sendTemplateEmail(to, subject, templateName, variables));
    }

    /**
     * Gửi mail thuần (không dùng Template)
     * Sử dụng send OTP, Reset password
     * to: một người
     * Async
     */
    @Override
    @Async("mailTaskExecutor")
    public void sendSimpleEmailAsync(String to, String subject, String content) {
        CompletableFuture.runAsync(() -> sendSimpleEmail(to, subject, content));
    }

    /**
     * Gửi mail thuần (không dùng Template)
     * Sử dụng send OTP, Reset password
     * to: nhiều người
     * Async
     */
    @Override
    @Async("mailTaskExecutor")
    public void sendSimpleEmailAsync(String[] to, String subject, String content) {
        CompletableFuture.runAsync(() -> sendSimpleEmail(to, subject, content));
    }

    //========== PRIVATE METHOD ==========//
    private void sendSimpleMessage(EmailDTO emailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDto.getTo().toArray(new String[0]));
        if (emailDto.getCc() != null && !emailDto.getCc().isEmpty()) {
            message.setCc(emailDto.getCc().toArray(new String[0]));
        }
        if (emailDto.getBcc() != null && !emailDto.getBcc().isEmpty()) {
            message.setBcc(emailDto.getBcc().toArray(new String[0]));
        }
        message.setSubject(emailDto.getSubject());
        message.setText(emailDto.getTextContent());

        mailSender.send(message);
    }

    private void sendMineMessage(EmailDTO emailDTO) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        Context context = new Context();

        helper.setTo(emailDTO.getTo().toArray(new String[0]));
        if (emailDTO.getCc() != null && !emailDTO.getCc().isEmpty()) {
            helper.setCc(emailDTO.getCc().toArray(new String[0]));
        }
        if (emailDTO.getBcc() != null && !emailDTO.getBcc().isEmpty()) {
            helper.setBcc(emailDTO.getBcc().toArray(new String[0]));
        }
        helper.setSubject(emailDTO.getSubject());

        String content = getMailContext(emailDTO);
        helper.setText(content, emailDTO.isHtml());


        if (emailDTO.getAttachments() != null) {
            addAttachments(helper, emailDTO.getAttachments());
        }

        mailSender.send(message);
    }

    private String getMailContext(EmailDTO emailDTO) {
        if (StringUtils.hasText(emailDTO.getTemplateName())) {
            return processTemplate(emailDTO.getTemplateName(), emailDTO.getTemplateVariables());
        } else if (emailDTO.isHtml() && StringUtils.hasText(emailDTO.getHtmlContent())) {
            return emailDTO.getHtmlContent();
        } else {
            return emailDTO.getTextContent();
        }
    }

    private String processTemplate(String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context();
            if (variables != null) {
                context.setVariables(variables);
            }
            return mailTemplateEngine.process(templateName, context);
        } catch (Exception e) {
            log.error("Failed to process email template: {}", templateName, e);
            throw new EmailTemplateException("Failed to process email template: " + templateName, e);
        }
    }

    private void addAttachments(MimeMessageHelper helper, List<AttachmentDTO> attachments) throws MessagingException {
        for (AttachmentDTO attachment : attachments) {
            if (attachment.getContent() != null) {
                ByteArrayResource resource = new ByteArrayResource(attachment.getContent());
                if (attachment.isInline() && StringUtils.hasText(attachment.getContentId())) {
                    helper.addInline(attachment.getContentId(), resource, attachment.getContentType());
                } else {
                    helper.addAttachment(attachment.getFilename(), resource);
                }
            } else if (StringUtils.hasText(attachment.getFilePath())) {
                File file = new File(attachment.getFilePath());
                if (file.exists()) {
                    FileSystemResource resource = new FileSystemResource(file);
                    if (attachment.isInline() && StringUtils.hasText(attachment.getContentId())) {
                        helper.addInline(attachment.getContentId(), resource);
                    } else {
                        helper.addAttachment(attachment.getFilename(), resource);
                    }
                }
            }
        }
    }
}
