package com.example.emortion_journal.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mood_entries")
public class MoodEntry {

    // プライマリーキー
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ユーザー
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 記録データ
    @CreationTimestamp // ★このアノテーションを追加
    @Column(name = "entry_date", nullable = false)
    private LocalDateTime entryDate;

    @Column(name = "mood_level", nullable = false)
    private Integer moodLevel;

    private String memo;

    // postgresSQLのTEXT[]に対応
    @ElementCollection
    @CollectionTable(name = "mood_entry_tags", joinColumns = @JoinColumn(name="entry_id"))
    @Column(name = "tags")
    private List<String> tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDateTime entryDate) {
        this.entryDate = entryDate;
    }

    public Integer getMoodLevel() {
        return moodLevel;
    }

    public void setMoodLevel(Integer moodLevel) {
        this.moodLevel = moodLevel;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
