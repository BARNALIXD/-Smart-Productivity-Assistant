package com.productivity.controller;

import com.productivity.dto.NoteDTO;
import com.productivity.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteDTO> createNote(
            @RequestParam Long userId,
            @RequestBody NoteDTO noteDTO) {
        NoteDTO created = noteService.createNote(userId, noteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<NoteDTO>> getAllNotes(@RequestParam Long userId) {
        List<NoteDTO> notes = noteService.getAllNotes(userId);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNoteById(@PathVariable Long id) {
        NoteDTO note = noteService.getNoteById(id);
        return ResponseEntity.ok(note);
    }

    @GetMapping("/pinned")
    public ResponseEntity<List<NoteDTO>> getPinnedNotes(@RequestParam Long userId) {
        List<NoteDTO> notes = noteService.getPinnedNotes(userId);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/search")
    public ResponseEntity<List<NoteDTO>> searchNotes(
            @RequestParam Long userId,
            @RequestParam String query) {
        List<NoteDTO> notes = noteService.searchNotes(userId, query);
        return ResponseEntity.ok(notes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(
            @PathVariable Long id,
            @RequestBody NoteDTO noteDTO) {
        NoteDTO updated = noteService.updateNote(id, noteDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}
