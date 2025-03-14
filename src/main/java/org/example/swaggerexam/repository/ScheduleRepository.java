package org.example.swaggerexam.repository;

import org.example.swaggerexam.domain.Schedule;
import org.example.swaggerexam.domain.ScheduleParticipant;
import org.example.swaggerexam.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT DISTINCT  s FROM Schedule s " +
            "LEFT JOIN FETCH s.scheduleParticipants sp " +
            "LEFT JOIN FETCH sp.user " +
            "LEFT JOIN FETCH s.meeting " +
            "WHERE s.meeting.id = :meetingId")
    List<Schedule> findByMeetingIdWithDetails(@Param("meetingId") Long meetingId);


}
