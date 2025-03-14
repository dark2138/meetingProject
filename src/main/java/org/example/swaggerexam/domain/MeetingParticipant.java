package org.example.swaggerexam.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_participants")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MeetingParticipant {

    public enum Status {
        ATTENDING, MAYBE, NOT_ATTENDING
    }

    public enum Role {
        OWNER, PARTICIPANT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 참여자 목록
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 미팅 목록
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    //  참석 상태 (attending, maybe, not_attending)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'ATTENDING'") // Hibernate 기본값 설정
    private Status status = Status.ATTENDING; // 자바 측 기본값

    //  역할 ( OWNER,  PARTICIPANT)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 생성 시간
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @Builder
    public MeetingParticipant(User user, Meeting meeting, Status status, Role role) {
        this.user = user;
        this.meeting = meeting;
        this.status = status;
        this.role = role;
    }

}



