package org.example.swaggerexam.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String refreshToken;

    // 사용자가 주최한 미팅 목록 (OneToMany 관계)
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Meeting> ownerMeetings = new HashSet<>();

    // 사용자가 주최한 일정 목록 (OneToMany 관계)
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Schedule> ownerSchedule = new HashSet<>();

    // 사용자가 참가한 미팅 목록 (ManyToMany 관계)
    @ManyToMany(mappedBy = "users")
    private Set<Meeting> meetings = new HashSet<>();

    // 사용자가 참가한 스케줄 목록 (ManyToMany 관계)
    @ManyToMany(mappedBy = "users")
    private Set<Schedule> schedules = new HashSet<>();

    // 사용자가 참가한 미팅 참가자 목록 (OneToMany 관계)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MeetingParticipant> meetingParticipants = new HashSet<>();

    // 사용자가 참가한 스케줄 참가자 목록 (OneToMany 관계)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ScheduleParticipant> scheduleParticipants = new HashSet<>();
}
