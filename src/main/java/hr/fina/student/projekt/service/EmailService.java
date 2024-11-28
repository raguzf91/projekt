package hr.fina.student.projekt.service;

import hr.fina.student.projekt.enums.VerificationType;

public interface EmailService {
    void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType verificationType);
}
