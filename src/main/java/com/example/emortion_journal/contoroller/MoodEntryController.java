package com.example.emortion_journal.contoroller;

import com.example.emortion_journal.model.MoodEntry;
import com.example.emortion_journal.service.MoodEntryService;
import com.example.emortion_journal.service.UserEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class MoodEntryController {
    private final MoodEntryService moodEntryService;
    private final UserEntryService userEntryService;

    public MoodEntryController(MoodEntryService moodEntryService, UserEntryService userEntryService) {
        this.moodEntryService = moodEntryService;
        this.userEntryService = userEntryService;
    }

    /** 作成時はボディの userId/id を無視し、認証ユーザーの userId をセット */
    @PostMapping
    public ResponseEntity<MoodEntry> createEntry(@RequestBody MoodEntry entry, Authentication auth) {
        String username = (String) auth.getPrincipal();
        Long userId = userEntryService.findByUsername(username).getId();

        entry.setId(null);              // クライアントがidを送ってきても無視
        entry.setUserId(userId);        // 所有者を強制セット

        MoodEntry saved = moodEntryService.saveEntry(entry);
        return ResponseEntity
                .created(URI.create("/api/entries/" + saved.getId()))
                .body(saved);           // 201 Created
    }

    /** ログインユーザーのエントリ一覧（降順） */
    @GetMapping
    public List<MoodEntry> getEntries(Authentication auth) {
        String username = (String) auth.getPrincipal();
        Long userId = userEntryService.findByUsername(username).getId();
        return moodEntryService.getEntriesByUserId(userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MoodEntry> updateEntry(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody MoodEntry patch
    ) {
        String username = (String) auth.getPrincipal();
        Long userId = userEntryService.findByUsername(username).getId();

        // 所有者チェック inside serviceでもOK
        MoodEntry updated = moodEntryService.updateOwnEntry(userId, id, patch);
        return ResponseEntity.ok(updated);
    }


    /** 自分のエントリだけ削除可能（所有者チェック） */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id, Authentication auth) {
        String username = (String) auth.getPrincipal();
        Long userId = userEntryService.findByUsername(username).getId();

        // 所有者チェック込みで削除（見つからなければ 404）
        moodEntryService.deleteEntryOwned(id, userId);
        return ResponseEntity.noContent().build(); // 204
    }
}
