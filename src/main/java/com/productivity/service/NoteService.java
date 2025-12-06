package com.productivity.service;

import com.productivity.dto.NoteDTO;
import com.productivity.entity.Note;
import com.productivity.entity.User;
import com.productivity.repository.NoteRepository;
import com.productivity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Transactional
    public NoteDTO createNote(Long userId, NoteDTO noteDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Note note = new Note();
        note.setUser(user);
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        note.setTags(noteDTO.getTags());
        note.setPinned(noteDTO.isPinned());

        Note savedNote = noteRepository.save(note);
        return convertToDTO(savedNote);
    }

    @Transactional
    public NoteDTO updateNote(Long noteId, NoteDTO noteDTO) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (noteDTO.getTitle() != null) note.setTitle(noteDTO.getTitle());
        if (noteDTO.getContent() != null) note.setContent(noteDTO.getContent());
        if (noteDTO.getTags() != null) note.setTags(noteDTO.getTags());
        note.setPinned(noteDTO.isPinned());

        Note updatedNote = noteRepository.save(note);
        return convertToDTO(updatedNote);
    }

    public List<NoteDTO> getAllNotes(Long userId) {
        List<Note> notes = noteRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return notes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NoteDTO> getPinnedNotes(Long userId) {
        List<Note> notes = noteRepository.findByUserIdAndPinnedTrueOrderByUpdatedAtDesc(userId);
        return notes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NoteDTO> searchNotes(Long userId, String query) {
        List<Note> notes = noteRepository.searchNotes(userId, query);
        return notes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public NoteDTO getNoteById(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        return convertToDTO(note);
    }

    @Transactional
    public void deleteNote(Long noteId) {
        noteRepository.deleteById(noteId);
    }

    private NoteDTO convertToDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setTags(note.getTags());
        dto.setPinned(note.isPinned());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }
}
