package com.example.emortion_journal.contoroller;

import com.example.emortion_journal.model.MoodEntry;
import com.example.emortion_journal.service.MoodEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class MoodEntryContoroller {
    private final MoodEntryService moodEntryService;

    public MoodEntryContoroller(MoodEntryService moodEntryService) {
        this.moodEntryService = moodEntryService;
    }

    @PostMapping
    public ResponseEntity<MoodEntry> createEntry(@RequestBody MoodEntry entry) {
        MoodEntry saveEntries = moodEntryService.saveEntry(entry);
        return ResponseEntity.ok(saveEntries);
    }

    @GetMapping
    public ResponseEntity<List<MoodEntry>> getEntries(@RequestParam Long userId) {
        List<MoodEntry> entries = moodEntryService.getEntriesByUserId(userId);
        return ResponseEntity.ok(entries);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        moodEntryService.deleteEntry(id);
        return ResponseEntity.noContent().build();
    }
}
