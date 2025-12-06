package com.productivity.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CalendarService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);

    @Value("${google.calendar.credentials.file.path:credentials.json}")
    private String credentialsFilePath;

    @Value("${google.calendar.tokens.directory.path:tokens}")
    private String tokensDirectoryPath;

    @Value("${google.calendar.application.name:Smart Productivity Assistant}")
    private String applicationName;

    /**
     * Creates an authorized Credential object.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = CalendarService.class.getResourceAsStream("/" + credentialsFilePath);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + credentialsFilePath);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDirectoryPath)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Get upcoming calendar events
     */
    public List<CalendarEventDTO> getUpcomingEvents(int maxResults) {
        List<CalendarEventDTO> eventList = new ArrayList<>();

        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(applicationName)
                    .build();

            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = service.events().list("primary")
                    .setMaxResults(maxResults)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            List<Event> items = events.getItems();
            if (items.isEmpty()) {
                log.info("No upcoming events found.");
            } else {
                for (Event event : items) {
                    CalendarEventDTO dto = new CalendarEventDTO();
                    dto.setSummary(event.getSummary());
                    dto.setDescription(event.getDescription());
                    dto.setLocation(event.getLocation());

                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        start = event.getStart().getDate();
                    }
                    dto.setStartTime(convertToLocalDateTime(start));

                    DateTime end = event.getEnd().getDateTime();
                    if (end == null) {
                        end = event.getEnd().getDate();
                    }
                    dto.setEndTime(convertToLocalDateTime(end));

                    eventList.add(dto);
                }
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error fetching calendar events", e);
        }

        return eventList;
    }

    /**
     * Get events for today
     */
    public List<CalendarEventDTO> getTodayEvents() {
        List<CalendarEventDTO> eventList = new ArrayList<>();

        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(applicationName)
                    .build();

            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

            DateTime timeMin = new DateTime(startOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            DateTime timeMax = new DateTime(endOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

            Events events = service.events().list("primary")
                    .setTimeMin(timeMin)
                    .setTimeMax(timeMax)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            List<Event> items = events.getItems();
            for (Event event : items) {
                CalendarEventDTO dto = new CalendarEventDTO();
                dto.setSummary(event.getSummary());
                dto.setDescription(event.getDescription());
                dto.setLocation(event.getLocation());

                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                dto.setStartTime(convertToLocalDateTime(start));

                DateTime end = event.getEnd().getDateTime();
                if (end == null) {
                    end = event.getEnd().getDate();
                }
                dto.setEndTime(convertToLocalDateTime(end));

                eventList.add(dto);
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error fetching today's calendar events", e);
        }

        return eventList;
    }

    private LocalDateTime convertToLocalDateTime(DateTime dateTime) {
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(dateTime.getValue()),
                ZoneId.systemDefault()
        );
    }

    public static class CalendarEventDTO {
        private String summary;
        private String description;
        private String location;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    }
}
