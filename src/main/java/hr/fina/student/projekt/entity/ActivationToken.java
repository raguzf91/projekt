package hr.fina.student.projekt.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivationToken {
    private String key;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private User user;
}
