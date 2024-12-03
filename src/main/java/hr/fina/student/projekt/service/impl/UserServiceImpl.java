package hr.fina.student.projekt.service.impl;

import hr.fina.student.projekt.dao.TokenDao;
import hr.fina.student.projekt.dao.UserDao;
import hr.fina.student.projekt.entity.Token;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.exceptions.key.InvalidKeyException;



import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Set;

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
    
    public void activateAccount(String email, String key) throws MessagingException {
        
        User user = verifyCode(email, key);
        user.setEnabled(true);
        user.setAccountLocked(false);
        userRepository.updateUser(user);
        
    }

    @Override
    public void verifyAccount(String key, String email) {
        log.info("Verifying account");
        User user = verifyCode(email, key);
       
        userRepository.updateUser(user);
    }

     public void sendEmail(User user, String url, String emailType ) throws MessagingException {
        deleteExistingTokens(user.getEmail());
        String activationKey = generateAndSaveActivationToken(user, emailType);
        emailService.sendEmail(user.getFirstName(), user.getEmail(), url, activationKey, emailType);
    }

    private String generateAndSaveActivationToken(User user, String type) {
         //generate key
         String generatedKey = generateActivationKey(6);
         Token activationToken = Token.builder()
                 .key(generatedKey)
                 .createdAt(LocalDateTime.now())
                 .expiresAt(LocalDateTime.now().plusMinutes(15))
                 .user(user)
                 .type(type)
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

    

    
    private User verifyCode(String email, String code) throws InvalidKeyException {
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

    private void deleteExistingTokens(String email) {
        log.info("Deleting existing tokens");
        Set<Token> userTokens = tokenRepository.findTokensByUserId(findUserByEmail(email).getId());
        if(!userTokens.isEmpty()) {
            tokenRepository.deleteAllTokensByUserId(findUserByEmail(email).getId());
        }
    }

    
}
