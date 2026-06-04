package com.highway.tolling.service;

import com.highway.tolling.model.Bill;
import com.highway.tolling.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

/**
 * Email Service
 * Handles sending emails for the tolling system
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private final JavaMailSender mailSender;

    @Value("${app.email.from:noreply@highwaytolling.com}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send monthly bill email to user
     * 
     * @param user the user to send the bill to
     * @param bill the generated bill
     */
    public void sendBillEmail(User user, Bill bill) {
        if (!emailEnabled) {
            logger.info("Email service is disabled. Skipping bill email for user: {}", user.getEmail());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Monthly Highway Toll Bill - " + bill.getBillMonth());

            String content = buildBillEmailContent(user, bill);
            helper.setText(content, true);

            mailSender.send(message);
            logger.info("Bill email sent successfully to: {}", user.getEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send bill email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    /**
     * Build HTML content for the bill email
     */
    private String buildBillEmailContent(User user, Bill bill) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #2c3e50; text-align: center;'>Smart Highway Tolling System</h2>" +
                "<hr style='border: 0; border-top: 1px solid #eee;'>" +
                "<p>Dear <strong>" + user.getName() + "</strong>,</p>" +
                "<p>Your highway toll bill for the month of <strong>" + bill.getBillMonth() + "</strong> has been generated.</p>" +
                
                "<div style='background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                "<table style='width: 100%;'>" +
                "<tr><td><strong>Bill ID:</strong></td><td>#" + bill.getBillId() + "</td></tr>" +
                "<tr><td><strong>Month:</strong></td><td>" + bill.getBillMonth() + "</td></tr>" +
                "<tr><td><strong>Total Distance:</strong></td><td>" + df.format(bill.getTotalDistance()) + " km</td></tr>" +
                "<tr><td><strong>Total Amount:</strong></td><td style='color: #e74c3c; font-size: 1.2em;'><strong>₹" + df.format(bill.getTotalAmount()) + "</strong></td></tr>" +
                "<tr><td><strong>Due Date:</strong></td><td>" + bill.getDueDate() + "</td></tr>" +
                "<tr><td><strong>Status:</strong></td><td><span style='padding: 3px 8px; background: #ffeaa7; border-radius: 3px; font-size: 0.9em;'>" + bill.getStatus() + "</span></td></tr>" +
                "</table>" +
                "</div>" +

                "<p>Please log in to your dashboard to view detailed usage and clear your dues.</p>" +
                "<div style='text-align: center; margin-top: 30px;'>" +
                "<a href='#' style='background-color: #3498db; color: white; padding: 10px 25px; text-decoration: none; border-radius: 5px; font-weight: bold;'>Pay Now</a>" +
                "</div>" +
                "<p style='margin-top: 40px; font-size: 0.8em; color: #7f8c8d; text-align: center;'>" +
                "This is an automated message. Please do not reply to this email.<br>" +
                "&copy; 2026 Smart Highway Tolling System" +
                "</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
