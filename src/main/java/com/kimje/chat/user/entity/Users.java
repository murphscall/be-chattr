package com.kimje.chat.user.entity;

import com.kimje.chat.user.enums.UserRole;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;

@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    @Column(nullable = false , length = 100 , unique = true)
    private String email;
    @Column(nullable = false , length = 255)
    private String password;
    @Column(nullable = false , length = 50)
    private String name;
    @Column(nullable = false , length = 50)
    private String phone;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserLogin> userLogins;
    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now(); // 수동으로 현재 시간 할당
        }
    }
}
