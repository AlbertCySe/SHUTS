package com.highway.tolling.service;

import com.highway.tolling.model.Bill;
import com.highway.tolling.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Email Service
 * Sends email notifications to users
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send monthly bill notification email to user
     * 
     * @param user The user to send email to
     * @param bill The bill details
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendBillNotification(User user, Bill bill) {
        // Check if email is enabled
        if (!emailEnabled) {
            logger.info("Email is disabled. Skipping email notification for user: {}", user.getUserId());
            return false;
        }

        try {
            // Create email message
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Monthly Highway Toll Bill - " + bill.getBillMonth());
            message.setText(createBillEmailBody(user, bill));

            // Send email
            mailSender.send(message);

            logger.info("Bill notification email sent to: {}", user.getEmail());
            return true;

        } catch (Exception e) {
            logger.error("Failed to send bill notification email to {}: {}",
                    user.getEmail(), e.getMessage());
            return false;
        }
    }

    /**
     * Create email body content for bill notification
     * 
     * @param user The user
     * @param bill The bill
     * @return Email body text
     */
    private String createBillEmailBody(User user, Bill bill) {
        StringBuilder body = new StringBuilder();

        body.append("Dear ").append(user.getName()).append(",\n\n");
        body.append("Your monthly highway toll bill is ready.\n\n");
        body.append("Bill Details:\n");
        body.append("----------------------------------------\n");
        body.append("Bill Month: ").append(bill.getBillMonth()).append("\n");
        body.append("Total Distance: ").append(String.format("%.2f", bill.getTotalDistance())).append(" km\n");
        body.append("Total Amount: ₹").append(String.format("%.2f", bill.getTotalAmount())).append("\n");
        body.append("Due Date: ").append(bill.getDueDate()).append("\n");
        body.append("Status: ").append(bill.getStatus()).append("\n");
        body.append("----------------------------------------\n\n");
        body.append("Please ensure payment is made before the due date to avoid penalties.\n\n");
        body.append("Thank you for using Smart Highway Tolling System.\n\n");
        body.append("Best regards,\n");
        body.append("Highway Tolling Team\n");

        return body.toString();
    }

    /**
     * Send welcome email to new user
     * 
     * @param user The new user
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendWelcomeEmail(User user) {
        if (!emailEnabled) {
            logger.info("Email is disabled. Skipping welcome email for user: {}", user.getUserId());
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Welcome to Smart Highway Tolling System");
            message.setText(createWelcomeEmailBody(user));

            mailSender.send(message);
            logger.info("Welcome email sent to: {}", user.getEmail());
            return true;

        } catch (Exception e) {
            logger.error("Failed to send welcome email to {}: {}",
                    user.getEmail(), e.getMessage());
            return false;
        }
    }

    /**
     * Create welcome email body
     * 
     * @param user The user
     * @return Email body text
     */
    private String createWelcomeEmailBody(User user) {
        return "Dear " + user.getName() + ",\n\n" +
                "Welcome to Smart Highway Tolling System!\n\n" +
                "Your account has been successfully created.\n" +
                "You can now register your vehicles and start using our highway network.\n\n" +
                "Account Details:\n" +
                "Name: " + user.getName() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Phone: " + user.getPhoneNumber() + "\n\n" +
                "Thank you for choosing us.\n\n" +
                "Best regards,\n" +
                "Highway Tolling Team";
    }

    /**
     * Send payment reminder email
     * 
     * @param user The user
     * @param bill The overdue bill
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendPaymentReminder(User user, Bill bill) {
        if (!emailEnabled) {
            logger.info("Email is disabled. Skipping payment reminder for user: {}", user.getUserId());
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Payment Reminder - Highway Toll Bill Overdue");
            message.setText(createReminderEmailBody(user, bill));

            mailSender.send(message);
            logger.info("Payment reminder sent to: {}", user.getEmail());
            return true;

        } catch (Exception e) {
            logger.error("Failed to send payment reminder to {}: {}",
                    user.getEmail(), e.getMessage());
            return false;
        }
    }

    /**
     * Create payment reminder email body
     * 
     * @param user The user
     * @param bill The bill
     * @return Email body text
     */
    private String createReminderEmailBody(User user, Bill bill) {
        return "Dear " + user.getName() + ",\n\n" +
                "This is a reminder that your highway toll bill is overdue.\n\n" +
                "Bill Details:\n" +
                "Bill Month: " + bill.getBillMonth() + "\n" +
                "Amount Due: ₹" + String.format("%.2f", bill.getTotalAmount()) + "\n" +
                "Due Date: " + bill.getDueDate() + "\n\n" +
                "Please make the payment at your earliest convenience to avoid additional charges.\n\n" +
                "Best regards,\n" +
                "Highway Tolling Team";
    }
}
