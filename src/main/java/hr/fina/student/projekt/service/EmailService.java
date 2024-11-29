package hr.fina.student.projekt.service;

import hr.fina.student.projekt.enums.VerificationType;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendVerificationEmail(String firstName, String email,  String verificationUrl, String activationCode, String verificationType) throws MessagingException;  
} 
