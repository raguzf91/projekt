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
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .bio(user.getBio() != null ? user.getBio() : "")
                .speaksLanguages(user.getSpeaksLanguages() != null ? user.getSpeaksLanguages() : new String[0])
                .responseRate(user.getResponseRate() != null ? user.getResponseRate().doubleValue() : 0)
                .profilePhoto(user.getProfilePhoto() != null ? user.getProfilePhoto() : "")
                .averageRating(user.getAverageRating() != null ? user.getAverageRating() : 0)
                .createdAt(user.getCreatedAt().toLocalDate() != null ? user.getCreatedAt().toLocalDate() : null )
                .city(user.getCity() != null ? user.getCity() : "")
                .country(user.getCountry() != null ? user.getCountry() : "")
                .roleName(role.getName())
                .permissions(role.getPermission())
                .oauth2User(user.getOauth2User())
                .build();
    }

    public static User toUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }
}
