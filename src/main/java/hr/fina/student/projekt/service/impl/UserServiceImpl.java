package hr.fina.student.projekt.service.impl;

import hr.fina.student.projekt.dao.TokenDao;
import hr.fina.student.projekt.dao.RoleDao;
import hr.fina.student.projekt.dao.UserDao;
import hr.fina.student.projekt.entity.Token;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.exceptions.ApiException;
import hr.fina.student.projekt.exceptions.key.AccountAlreadyConfirmedException;
import hr.fina.student.projekt.exceptions.key.InvalidKeyException;
import hr.fina.student.projekt.exceptions.key.KeyExpiredException;
import hr.fina.student.projekt.dto.UserDTO;
import static hr.fina.student.projekt.mapper.UserDTOMapper.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import hr.fina.student.projekt.service.EmailService;
import hr.fina.student.projekt.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao<User> userRepository;
    private final TokenDao tokenRepository;
    private final TokenDao activationTokenRepository;
    private final EmailService emailService;
    private final String ACTIVATION_URL = "http://localhost:8080/api/auth/account-verification";

    @Override
    public User createUser(User user) {
        return userRepository.create(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findUserById(Integer id) {
        return userRepository.findById(id);
    }
    
    public void activateAccount(String key) throws MessagingException {
        Token savedToken;

        
        try {

           savedToken = tokenRepository.findByKey(key);

        } catch (Exception e) {
            log.error("Error finding token");
            throw new InvalidKeyException("Key is incorrect. Please enter a valid key");
        }

        if(savedToken.getConfirmedAt() != null) {
            log.error("account already confirmed");
            throw new AccountAlreadyConfirmedException("Account is already confirmed");
        }

        User user = findUserById(savedToken.getUser().getId());
        
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
               log.error("Token expired");
               sendEmail(user, ACTIVATION_URL, "activateAccount");
               throw new KeyExpiredException("Key has expired. Please check your email for a new key");
            }
        user.setEnabled(true);
        user.setAccountLocked(false);
        savedToken.setConfirmedAt(LocalDateTime.now());
        if(userRepository.updateUser(user)) {
            savedToken.setConfirmedAt(LocalDateTime.now());
            tokenRepository.updateActivationToken(savedToken);
            log.info("User account activated");
        }
    }

     public void sendEmail(User user, String url, String emailType ) throws MessagingException {
        String activationKey = generateAndSaveActivationToken(user);
        emailService.sendEmail(user.getFirstName(), user.getEmail(), url, activationKey, emailType);
    }

    private String generateAndSaveActivationToken(User user) {
         //generate key
         String generatedKey = generateActivationKey(6);
         Token activationToken = Token.builder()
                 .key(generatedKey)
                 .createdAt(LocalDateTime.now())
                 .expiresAt(LocalDateTime.now().plusMinutes(15))
                 .user(user)
                 .build();
        activationTokenRepository.saveActivationToken(activationToken);
        return generatedKey;
    }

    private String generateActivationKey(int length) {
        String characters = "0123456789";
         StringBuilder keyBuilder = new StringBuilder();
         SecureRandom secureRandom = new SecureRandom();
         for (int i = 0; i < length; i++) {
             int randomIndex = secureRandom.nextInt(characters.length()); //od 0 do 9
             keyBuilder.append(characters.charAt(randomIndex)); // u secureRandom dodamo broj na tom random indexu

         }

         return keyBuilder.toString();
    }

    @Override
    public User verifyCode(String email, String code) throws InvalidKeyException {
        try {
            Token token = tokenRepository.findByKey(code);
            if(token == null || token.getConfirmedAt() != null) {
                throw new InvalidKeyException("Key is incorrect. Please enter a valid key");

            }

            User userByEmail = findUserByEmail(email);
            User userByCode = tokenRepository.findUserByKey(code);
            
            if(userByCode.getEmail().equalsIgnoreCase(userByEmail.getEmail())) {
                if(LocalDateTime.now().isAfter(token.getExpiresAt())) {
                    sendEmail(userByCode, code, email);
                } else {
                    token.setConfirmedAt(LocalDateTime.now());
                    tokenRepository.updateActivationToken(token);
                    return userByCode;
                }
    
            } else {
                throw new InvalidKeyException("Key is incorrect. Please enter a valid key");
            }
            
        } catch (Exception e) {
            log.error("Error verifying code" + e.getCause().toString());
            throw new InvalidKeyException("Key is incorrect. Please enter a valid key");
        }
        return null;
    }

    
}
