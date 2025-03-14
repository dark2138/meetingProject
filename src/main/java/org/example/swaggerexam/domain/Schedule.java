package org.example.swaggerexam.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "schedules")
@NoArgsConstructor
@AllArgsConstructor
@Getter@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String time;


    @Column(nullable = false)
    private String location;

    private LocalDateTime createdAt;


    // 일정을 만든 유저 (OWNER)
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 설정 (필요할 때만 로드)
    @JoinColumn(name = "owner_id", nullable = false) // 외래 키 매핑
    private User owner;


    @ManyToOne
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @ManyToMany
    @JoinTable(
            name = "user_schedule",
            joinColumns = @JoinColumn(name = "schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users =  new HashSet<>();

    // ScheduleParticipant와의 1:N 관계
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleParticipant> scheduleParticipants = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
