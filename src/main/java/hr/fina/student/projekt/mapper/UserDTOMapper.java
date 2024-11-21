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
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO); //!!! potencijalni problem
        userDTO.setRoleName(role.getName());
        userDTO.setPermissions(role.getPermission());
        return userDTO;
    }

    public static User toUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }
}
