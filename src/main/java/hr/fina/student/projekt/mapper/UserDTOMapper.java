package hr.fina.student.projekt.mapper;

import org.springframework.beans.BeanUtils;
import hr.fina.student.projekt.dto.UserDTO;
import hr.fina.student.projekt.entity.Role;
import hr.fina.student.projekt.entity.User;

public class UserDTOMapper {
     public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public static UserDTO fromUser(User user, Role role) {
        return UserDTO.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getUserGender())
                .dateOfBirth(user.getDateOfBirth())
                .bio(user.getBio())
                .languages(user.getSpeaksLanguages())
                .responseRate(user.getResponseRate().doubleValue())
                .profilePhoto(user.getProfilePhoto())
                .createdAt(user.getCreatedAt().toLocalDate())
                .build();
    }

    public static User toUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }
}
