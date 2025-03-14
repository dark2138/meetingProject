package org.example.swaggerexam.repository;

import org.example.swaggerexam.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
