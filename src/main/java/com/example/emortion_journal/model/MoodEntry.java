package com.example.emortion_journal.model;

import jakarta.persistence.*;

import java.time.LocalDate;
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
    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "mood_lavel", nullable = false)
    private Integer moodLevel;

    private String memo;

    // postgreSQLのTEXT[]に対応
    @ElementCollection
    @CollectionTable(name = "mood_entry_tags", joinColumns = @JoinColumn(name="entry_id"))
    @Column(name = "tags")
    private List<String> tags;
}
