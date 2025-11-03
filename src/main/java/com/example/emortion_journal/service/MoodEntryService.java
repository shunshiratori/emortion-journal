package com.example.emortion_journal.service;

import com.example.emortion_journal.model.MoodEntry;
import com.example.emortion_journal.model.MoodEntryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;


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


    public void deleteEntryOwned(Long entryId, Long userId) {
        var entry = moodEntryRepository.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new RuntimeException("Entry not found"));
        moodEntryRepository.delete(entry);
    }

    @Transactional
    public MoodEntry updateOwnEntry(Long userId, Long id, MoodEntry patch) {
        MoodEntry entry = moodEntryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Entry not found or not owned by user"));

        // moodLevel（null でなければ反映）
        if (patch.getMoodLevel() != null) {
            entry.setMoodLevel(patch.getMoodLevel());
        }

        // memo（null でなければ反映。空文字で上書きしたい場合は空文字を渡す）
        if (patch.getMemo() != null) {
            entry.setMemo(patch.getMemo());
        }

        // entryDate / userId / id は更新しない（無視）
        // @Transactional のダーティチェックで自動フラッシュされるが、明示 save でもOK
        return moodEntryRepository.save(entry);
    }}
