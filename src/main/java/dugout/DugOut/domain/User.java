package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {
    
    @Id
    @Column(name = "user_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userIdx;
    
    @Column(nullable = false, length = 20)
    private String name;
    
    @Column(nullable = false, length = 50)
    private String nickname;
    
    @Column(name = "cheering_team_id", nullable = false)
    private Integer cheeringTeamId;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    private LocalDate birth;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('F','M')")
    private Gender gender;
    
    @Column(length = 100, unique = true)
    private String email;
    
    @Column(name = "phone_number", length = 15, unique = true)
    private String phoneNumber;
    
    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private Integer status;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum Gender {
        M, F
    }
} 