# Smart Productivity Assistant - Project Summary

## Overview

A full-stack personal productivity application built with Spring Boot and PostgreSQL that uses AI/ML to recommend the next best task based on priority, urgency, and historical completion patterns.

## Key Features Implemented

### 1. Task Management System
- **CRUD Operations**: Create, read, update, delete tasks
- **Priority Levels**: LOW, MEDIUM, HIGH, URGENT
- **Status Tracking**: TODO, IN_PROGRESS, COMPLETED, CANCELLED
- **Due Date Management**: Track deadlines and overdue tasks
- **Duration Estimation**: Estimate and track actual completion time

### 2. Intelligent Recommendation Engine

The crown jewel of this application! A sophisticated scoring system that recommends the "next best task":

**Scoring Algorithm (0-100 points):**

1. **Priority-based (0-30 pts)**
   - Urgent: 30 pts
   - High: 20 pts
   - Medium: 10 pts
   - Low: 5 pts

2. **Due Date Urgency (0-40 pts)**
   - Overdue: 40 pts
   - Due in 4 hours: 35 pts
   - Due today: 30 pts
   - Due tomorrow: 20 pts
   - Due this week: 15 pts
   - Due later: 10 pts

3. **Duration Optimization (0-15 pts)**
   - Quick wins (≤15 min): 15 pts
   - Short tasks (≤30 min): 12 pts
   - Medium tasks (≤60 min): 8 pts
   - Long tasks (>60 min): 5 pts

4. **ML Pattern Recognition (0-15 pts)**
   After collecting ≥5 completed tasks:
   - Time-of-day productivity (0-5 pts)
   - Day-of-week productivity (0-5 pts)
   - Priority-duration matching (0-5 pts)

**Auto-refresh:** Scores recalculated every hour via scheduled job

### 3. Machine Learning Component

**Data Collection:**
- Task completion timestamps
- Actual vs estimated duration
- Day of week and hour of day
- Whether task was overdue
- Priority level patterns

**Pattern Recognition:**
- Identifies user's most productive hours
- Recognizes productive days of the week
- Learns typical completion times for each priority level
- Adjusts recommendations based on historical success

**Implementation:**
- Uses Apache Commons Math for statistical calculations
- Stores historical data in `task_completion_history` table
- Requires minimum 5 data points before ML kicks in
- Improves continuously as more tasks are completed

### 4. Notes System
- Rich text note-taking
- Tagging system for organization
- Pin important notes
- Full-text search in title and content
- Recently updated sorting

### 5. Google Calendar Integration
- OAuth 2.0 authentication
- Fetch today's events
- View upcoming calendar events
- Integrates schedule with task management

### 6. Beautiful Frontend Dashboard
- Responsive grid layout
- Real-time task recommendations display
- Calendar events widget
- Quick note access
- Productivity statistics
- Modal forms for task/note creation
- Filter tasks by status
- Color-coded priority indicators

### 7. RESTful API
- Clean REST architecture
- JSON request/response
- Comprehensive error handling
- CORS enabled for frontend access

## Technical Architecture

### Backend Stack
```
Spring Boot 3.2.0
├── Web (REST API)
├── Data JPA (ORM)
├── PostgreSQL Driver
├── Validation
├── Lombok (Boilerplate reduction)
├── Google Calendar API
└── Apache Commons Math (ML)
```

### Database Schema
```
users
├── id (PK)
├── email (unique)
├── name
├── timezone
└── timestamps

tasks
├── id (PK)
├── user_id (FK)
├── title
├── description
├── priority (enum)
├── status (enum)
├── due_date
├── estimated_duration
├── actual_duration
├── completed_at
├── recommendation_score
└── timestamps

notes
├── id (PK)
├── user_id (FK)
├── title
├── content
├── tags (array)
├── pinned
└── timestamps

task_completion_history
├── id (PK)
├── user_id
├── task_id
├── priority
├── durations
├── completed_at
├── day_of_week
├── hour_of_day
└── metrics
```

### Frontend Stack
- Vanilla JavaScript (no frameworks)
- CSS3 with gradients and animations
- HTML5 semantic markup
- Fetch API for REST calls

## File Structure

```
smart-productivity-assistant/
├── src/
│   ├── main/
│   │   ├── java/com/productivity/
│   │   │   ├── SmartProductivityAssistantApplication.java (Main)
│   │   │   ├── controller/
│   │   │   │   ├── TaskController.java (Task API)
│   │   │   │   ├── NoteController.java (Note API)
│   │   │   │   └── CalendarController.java (Calendar API)
│   │   │   ├── service/
│   │   │   │   ├── TaskService.java (Task business logic)
│   │   │   │   ├── NoteService.java (Note business logic)
│   │   │   │   ├── CalendarService.java (Google Calendar)
│   │   │   │   └── RecommendationEngine.java (ML engine)
│   │   │   ├── repository/
│   │   │   │   ├── TaskRepository.java
│   │   │   │   ├── NoteRepository.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── TaskCompletionHistoryRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── Task.java
│   │   │   │   ├── Note.java
│   │   │   │   ├── User.java
│   │   │   │   └── TaskCompletionHistory.java
│   │   │   └── dto/
│   │   │       ├── TaskDTO.java
│   │   │       └── NoteDTO.java
│   │   └── resources/
│   │       ├── application.properties (Configuration)
│   │       ├── credentials.json.example (Google Calendar)
│   │       └── static/
│   │           ├── index.html (Dashboard UI)
│   │           ├── styles.css (Styling)
│   │           └── app.js (Frontend logic)
│   └── test/
│       └── java/com/productivity/
│           └── service/
│               └── RecommendationEngineTest.java
├── database/
│   └── init.sql (Sample data & schema)
├── pom.xml (Maven dependencies)
├── Dockerfile (Container image)
├── docker-compose.yml (Multi-container setup)
├── .gitignore
├── README.md (Full documentation)
├── API_DOCUMENTATION.md (API reference)
├── QUICKSTART.md (Getting started guide)
└── PROJECT_SUMMARY.md (This file)
```

## How to Run

### Quick Start (Docker)
```bash
docker-compose up -d
# Access: http://localhost:8080
```

### Local Development
```bash
# Setup PostgreSQL
createdb productivity_db

# Build and run
mvn spring-boot:run

# Access: http://localhost:8080
```

## API Endpoints Summary

### Tasks
- `GET /api/tasks?userId={id}` - All tasks
- `GET /api/tasks/recommended?userId={id}` - AI recommendations ⭐
- `GET /api/tasks/{id}` - Single task
- `POST /api/tasks?userId={id}` - Create task
- `PUT /api/tasks/{id}` - Update task
- `POST /api/tasks/{id}/complete` - Complete task (trains ML) ⭐
- `DELETE /api/tasks/{id}` - Delete task

### Notes
- `GET /api/notes?userId={id}` - All notes
- `GET /api/notes/search?userId={id}&query={q}` - Search
- `POST /api/notes?userId={id}` - Create note
- `PUT /api/notes/{id}` - Update note
- `DELETE /api/notes/{id}` - Delete note

### Calendar
- `GET /api/calendar/today` - Today's events
- `GET /api/calendar/upcoming?maxResults={n}` - Upcoming events

## Configuration

### Database (application.properties)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/productivity_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Recommendation Engine
```properties
recommendation.engine.enabled=true
recommendation.engine.ml.enabled=true
recommendation.engine.min.data.points=5
```

### Google Calendar (Optional)
```properties
google.calendar.credentials.file.path=credentials.json
google.calendar.tokens.directory.path=tokens
```

## Testing

### Unit Tests
```bash
mvn test
```

### Manual API Testing
```bash
# Get recommendations
curl "http://localhost:8080/api/tasks/recommended?userId=1"

# Create task
curl -X POST "http://localhost:8080/api/tasks?userId=1" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","priority":"HIGH"}'
```

## Key Design Decisions

### 1. Scoring Algorithm Design
- **Multi-factor approach**: Combines objective factors (due date) with subjective (priority)
- **Bounded scores**: All scores capped at 100 for consistency
- **ML enhancement**: Baseline scoring works without ML, enhanced with user data

### 2. ML Implementation
- **Minimum data requirement**: Prevents poor recommendations from insufficient data
- **Pattern recognition**: Focuses on time-based patterns (hour, day)
- **Historical learning**: Uses actual completion data, not predictions

### 3. Database Design
- **Normalized schema**: Separate tables for concerns
- **History table**: Dedicated table for ML training data
- **Indexed queries**: Performance optimization on common queries

### 4. API Design
- **RESTful conventions**: Standard HTTP methods
- **Query parameters**: User context via userId
- **DTO pattern**: Separation of persistence and API models

### 5. Frontend Architecture
- **No framework**: Vanilla JS for simplicity
- **Modular functions**: Separated concerns (tasks, notes, calendar)
- **Real-time updates**: Fetch-based updates after mutations

## Performance Optimizations

1. **Database Indexes**
   - User-status composite index on tasks
   - User-score index for recommendations
   - Due date index for urgency queries

2. **Scheduled Jobs**
   - Hourly score recalculation (not per request)
   - Batch processing of score updates

3. **Query Optimization**
   - Custom JPQL queries for complex operations
   - Lazy loading for relationships
   - DTO projections to limit data transfer

4. **Caching Opportunities** (Future)
   - Cache recommendation scores between recalculations
   - Cache user productivity patterns
   - Cache calendar events

## Security Considerations

**Current:**
- Basic user ID parameter (development only)
- CORS enabled for all origins

**Production Recommendations:**
- Implement JWT authentication
- Add user session management
- Restrict CORS to specific origins
- Add rate limiting
- Validate all inputs
- Encrypt sensitive data
- Secure Google Calendar tokens

## Future Enhancements

1. **Authentication & Authorization**
   - JWT tokens
   - User registration/login
   - Role-based access control

2. **Advanced ML**
   - Task complexity prediction
   - Deadline prediction based on history
   - Collaborative filtering for team recommendations

3. **Integrations**
   - Slack notifications
   - Email reminders
   - GitHub issue sync
   - Jira integration

4. **Features**
   - Recurring tasks
   - Task templates
   - Team collaboration
   - Mobile app
   - Pomodoro timer
   - Gamification/achievements

5. **Analytics**
   - Productivity dashboards
   - Trend analysis
   - Weekly/monthly reports
   - Burndown charts

## Learning Value

This project demonstrates:

✅ **Backend Development**
- Spring Boot architecture
- JPA/Hibernate ORM
- RESTful API design
- Service layer pattern
- Repository pattern

✅ **Database Design**
- Schema normalization
- Relationship mapping
- Index optimization
- Query optimization

✅ **Machine Learning**
- Pattern recognition
- Statistical analysis
- Historical learning
- Recommendation systems

✅ **Frontend Development**
- Vanilla JavaScript
- REST API consumption
- Responsive design
- State management

✅ **DevOps**
- Docker containerization
- Docker Compose orchestration
- Database migrations
- Environment configuration

✅ **Software Engineering**
- Clean code principles
- SOLID principles
- Testing strategies
- Documentation

## Success Metrics

The application is successful if:

1. ✅ Tasks can be created, updated, and completed
2. ✅ Recommendations are generated based on multiple factors
3. ✅ ML improves recommendations after 5+ completions
4. ✅ Notes can be created and searched
5. ✅ Calendar integration works (with credentials)
6. ✅ UI is intuitive and responsive
7. ✅ API is well-documented and tested

## Credits

- **Spring Boot**: Framework
- **PostgreSQL**: Database
- **Google Calendar API**: Calendar integration
- **Apache Commons Math**: Statistical calculations
- **Lombok**: Boilerplate reduction

---

## Get Started

1. Read [QUICKSTART.md](QUICKSTART.md) for setup
2. Read [README.md](README.md) for full documentation
3. Read [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for API reference
4. Run `docker-compose up -d` to start
5. Visit http://localhost:8080

Happy coding! 🚀
