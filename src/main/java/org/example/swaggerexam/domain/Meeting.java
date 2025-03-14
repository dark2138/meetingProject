package org.example.swaggerexam.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "meetings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Integer maxParticipants;
    private LocalDateTime createdAt;


    // 미팅을 만든 유저 (OWNER)
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 설정 (필요할 때만 로드)
    @JoinColumn(name = "owner_id", nullable = false) // 외래 키 매핑
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "user_meeting",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();


    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Schedule> schedules = new HashSet<>();

    // 미팅 참가자 목록
    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MeetingParticipant> meetingParticipants = new HashSet<>();



    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
