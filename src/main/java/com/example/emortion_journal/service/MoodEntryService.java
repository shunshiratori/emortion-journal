package com.example.emortion_journal.service;

import com.example.emortion_journal.model.MoodEntry;
import com.example.emortion_journal.model.MoodEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoodEntryService {
    private final MoodEntryRepository moodEntryRepository;

    public MoodEntryService(MoodEntryRepository moodEntryRepository) {
        this.moodEntryRepository = moodEntryRepository;
    }

    public MoodEntry saveEntry(MoodEntry entry) {
        return moodEntryRepository.save(entry);
    }

    public List<MoodEntry> getEntriesByUserId(Long userId) {
        return moodEntryRepository.findByUserIdOrderByEntryDateDesc(userId);
    }

    public void deleteEntry(Long id) {
        moodEntryRepository.deleteById(id);
    }
}
