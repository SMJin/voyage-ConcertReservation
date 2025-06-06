package kr.hhplus.be.server.point.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String type; // "CHARGE" or "USE"
    private Long amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public PointHistory(User user, String type, Long amount) {
        this.user = user;
        this.type = type;
        this.amount = amount;
    }
}

