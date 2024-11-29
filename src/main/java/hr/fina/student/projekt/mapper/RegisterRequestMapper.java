package hr.fina.student.projekt.mapper;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.request.RegisterRequest;

public class RegisterRequestMapper {
    public static User registerRequestToUser(RegisterRequest request) {
        return User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .build();
    }
}
