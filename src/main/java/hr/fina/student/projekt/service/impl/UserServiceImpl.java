package hr.fina.student.projekt.service.impl;

import hr.fina.student.projekt.dao.ActivationTokenDao;
import hr.fina.student.projekt.dao.RoleDao;
import hr.fina.student.projekt.dao.UserDao;
import hr.fina.student.projekt.entity.ActivationToken;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.exceptions.ApiException;
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
    private final ActivationTokenDao tokenRepository;
    private final ActivationTokenDao activationTokenRepository;
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
        ActivationToken savedToken;
        try {
           savedToken = tokenRepository.findByKey(key);
        } catch (Exception e) {
            log.error("Error finding token");
            throw new ApiException("Invalid key");
        }

        User user = findUserById(savedToken.getUser().getId());
        
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
               log.error("Token expired");
               sendVerificationEmail(user);
               throw new ApiException("Token expired. a new one has been sent to your email");
            }
        user.setEnabled(true);
        user.setAccountLocked(false);
        savedToken.setConfirmedAt(LocalDateTime.now());
        if(userRepository.updateUser(user)) {
            savedToken.setConfirmedAt(LocalDateTime.now());
            tokenRepository.updateActivationToken(savedToken);
        }
    }

     public void sendVerificationEmail(User user) throws MessagingException {
        String activationKey = generateAndSaveActivationToken(user);
        emailService.sendVerificationEmail(user.getFirstName(), user.getEmail(), ACTIVATION_URL, activationKey, "activateAccount");
    }

    private String generateAndSaveActivationToken(User user) {
         //generate key
         String generatedKey = generateActivationKey(6);
         ActivationToken activationToken = ActivationToken.builder()
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

    
}
