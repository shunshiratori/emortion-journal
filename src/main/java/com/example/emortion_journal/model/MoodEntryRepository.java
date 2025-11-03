package com.example.emortion_journal.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findByUserIdOrderByEntryDateDesc(Long userId);
    Optional<MoodEntry> findByIdAndUserId(Long id, Long userId);
}
