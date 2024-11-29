package hr.fina.student.projekt.enums;

import lombok.Getter;

@Getter
public enum VerificationType {
    ACTIVATE_ACCOUNT("activateAccount"),
    PASSWORD_RESET("passwordReset");

    private final String name;

    VerificationType(String name) {
        this.name = name();
    }

}
