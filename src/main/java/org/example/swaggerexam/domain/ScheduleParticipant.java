package org.example.swaggerexam.domain;


import jakarta.persistence.*;
import lombok.*;
import org.example.swaggerexam.dto.ScheduleParticipantResponseDto;

@Entity
@Table(name = "schedule_participants")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ScheduleParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    // 참석 상태 (attending, maybe, not_attending)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        ATTENDING, MAYBE, NOT_ATTENDING
    }

    @Builder
    public ScheduleParticipant(User user, Schedule schedule, Status status) {
        this.user = user;
        this.schedule = schedule;
        this.status = status;
    }
}

