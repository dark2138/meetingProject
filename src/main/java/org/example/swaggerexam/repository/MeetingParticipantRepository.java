package org.example.swaggerexam.repository;

import org.example.swaggerexam.domain.Meeting;
import org.example.swaggerexam.domain.MeetingParticipant;
import org.example.swaggerexam.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {

    Boolean existsByMeetingAndUser(Meeting meeting, User user);

    List<MeetingParticipant> findByMeetingId(Long meetingId);
}
