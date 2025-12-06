package com.productivity.controller;

import com.productivity.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/upcoming")
    public ResponseEntity<List<CalendarService.CalendarEventDTO>> getUpcomingEvents(
            @RequestParam(defaultValue = "10") int maxResults) {
        List<CalendarService.CalendarEventDTO> events = calendarService.getUpcomingEvents(maxResults);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/today")
    public ResponseEntity<List<CalendarService.CalendarEventDTO>> getTodayEvents() {
        List<CalendarService.CalendarEventDTO> events = calendarService.getTodayEvents();
        return ResponseEntity.ok(events);
    }
}
