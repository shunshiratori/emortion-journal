package com.example.emortion_journal.contoroller;

import com.example.emortion_journal.model.MoodEntry;
import com.example.emortion_journal.service.EntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public class EntryController {
    private final EntryService entryService;

    public EntryController(EntryService entryService) {
        this.entryService = entryService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<MoodEntry> update(@PathVariable Long id,
                                            @RequestBody Map<String, Object> request,
                                            Authentication auth) {
        String username = auth.getName();
        MoodEntry updated = entryService.updateEntry(id, request, username);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();
        entryService.deleteEntry(id, username);
        return ResponseEntity.noContent().build();
    }
}
