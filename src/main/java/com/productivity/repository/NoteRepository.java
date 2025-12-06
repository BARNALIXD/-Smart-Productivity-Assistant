package com.productivity.repository;

import com.productivity.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<Note> findByUserIdAndPinnedTrueOrderByUpdatedAtDesc(Long userId);

    @Query("SELECT n FROM Note n WHERE n.user.id = ?1 AND (LOWER(n.title) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', ?2, '%')))")
    List<Note> searchNotes(Long userId, String query);

    List<Note> findByUserIdAndTagsContaining(Long userId, String tag);
}
