package hr.fina.student.projekt.service.impl;
import hr.fina.student.projekt.enums.VerificationType;
import hr.fina.student.projekt.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    
    //send asynchronously so user doesn't have to wait for email to be sent
    @Override
    @Async
    public void sendEmail(String firstName, String userEmail,  String verificationUrl,
     String activationCode, String verificationType) throws MessagingException {
        String templateName = null;

        switch (verificationType) {
            case "activateAccount":
                templateName = "ActivateAccountEmail";
                break;
            case "resetPassword":
                templateName = "ResetPasswordEmail";
                break;
            case "verifyAccount":
                templateName = "VerifyAccountEmail";
                break;
            default:
                break;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
            Map<String, Object> properties = new HashMap<>();
            properties.put("firstName", firstName);
            properties.put("verificationUrl", verificationUrl);
            properties.put("activationCode", activationCode);

            Context context = new Context();
            context.setVariables(properties);

            helper.setFrom("raguzf91@gmail.com");
            helper.setTo(userEmail);

            String template = templateEngine.process(templateName, context);

            helper.setText(template, true);

            mailSender.send(mimeMessage);
            log.info("Email sent to: " + userEmail);
        } catch (Exception e) {
            log.error("Error sending email: " + e.getCause().toString());
            throw new MessagingException();
        }
        
    }

}

