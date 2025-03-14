package org.example.swaggerexam.repository;

import org.example.swaggerexam.domain.Schedule;
import org.example.swaggerexam.domain.ScheduleParticipant;
import org.example.swaggerexam.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleParticipantRepository extends JpaRepository<ScheduleParticipant, Long> {
    boolean existsByScheduleAndUser(Schedule schedule, User user);
    int countBySchedule(Schedule schedule);

    // 참가자 조회 (With User)
    @Query("SELECT sp FROM ScheduleParticipant sp JOIN FETCH sp.user WHERE sp.schedule = :schedule")
    List<ScheduleParticipant> findByScheduleWithUser(@Param("schedule") Schedule schedule);

    // 사용자별 참가 여부 확인
    Optional<ScheduleParticipant> findByScheduleAndUser(Schedule schedule, User user);

}
