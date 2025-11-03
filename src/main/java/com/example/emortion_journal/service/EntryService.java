package com.example.emortion_journal.service;

import com.example.emortion_journal.exception.ResourceNotFoundException;
import com.example.emortion_journal.model.MoodEntry;
import com.example.emortion_journal.model.MoodEntryRepository;
import com.example.emortion_journal.model.UserEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class EntryService {

    private final MoodEntryRepository moodEntryRepository;
    private final UserEntryService userEntryService; // findByUsername を想定

    public EntryService(MoodEntryRepository moodEntryRepository,
                        UserEntryService userEntryService) {
        this.moodEntryRepository = moodEntryRepository;
        this.userEntryService = userEntryService;
    }

    private Long currentUserId(String username) {
        UserEntity u = userEntryService.findByUsername(username);
        if (u == null) throw new AccessDeniedException("Authenticated user not found");
        return u.getId();
    }

    @Transactional
    public MoodEntry updateEntry(Long entryId, Map<String, Object> req, String username) {
        Long uid = currentUserId(username);

        // 404 or 他人 → 404 寄せ（403を厳密に出したければ findById→所有者比較に変更）
        MoodEntry entry = moodEntryRepository.findByIdAndUserId(entryId, uid)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found"));

        if (req.containsKey("moodLevel") && req.get("moodLevel") != null) {
            Object v = req.get("moodLevel");
            if (v instanceof Number n) {
                entry.setMoodLevel(n.intValue());
            } else {
                throw new IllegalArgumentException("moodLevel must be a number");
            }
        }
        if (req.containsKey("memo")) {
            entry.setMemo((String) req.get("memo")); // null 可（nullで上書き）
        }

        // JPAの変更検知でUPDATE
        return entry;
    }

    @Transactional
    public void deleteEntry(Long entryId, String username) {
        Long uid = currentUserId(username);
        MoodEntry entry = moodEntryRepository.findByIdAndUserId(entryId, uid)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found"));

        moodEntryRepository.delete(entry);
    }
}
