package hr.fina.student.projekt.service.impl;
import hr.fina.student.projekt.enums.VerificationType;
import hr.fina.student.projekt.service.AllArgsContructor;
import hr.fina.student.projekt.service.EmailService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;


@Service
@AllArgsContructor
@Slf4j
public class EmailServiceImpl implements EmailService{@Override
    public void sendVerificationEmail(String firstName, String email, String verificationUrl,
            VerificationType verificationType) {
       
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("filip");
            message.setTo(email);
            message.setText(getEmailMessage(firstName, verificationUrl, verificationType));
        } catch (Exception e) {
            log.error("Failed to send verification email", e);
        }
    }

    private String getEmailMessage(String firstName, String verificationUrl, VerificationType verificationType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEmailMessage'");
    }
}

