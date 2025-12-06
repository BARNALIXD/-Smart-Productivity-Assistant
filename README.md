# Smart Productivity Assistant

A comprehensive personal productivity hub that combines tasks, notes, schedule management, and AI-powered task recommendations.

## Features

### Core Features
- **Task Management**: Create, update, and track tasks with priorities, due dates, and estimated durations
- **Smart Recommendations**: AI-powered "next best task" suggestions based on:
  - Priority levels (Urgent, High, Medium, Low)
  - Due date urgency
  - Estimated duration (quick wins prioritization)
  - Machine learning from completion patterns
  - Time-of-day productivity patterns
  - Day-of-week productivity patterns

- **Notes System**: Quick note-taking with:
  - Rich text support
  - Tagging system
  - Search functionality
  - Pin important notes

- **Calendar Integration**: Sync with Google Calendar to view today's events and upcoming schedule

- **Productivity Analytics**: Track completion rates, overdue tasks, and productivity metrics

### AI/ML Recommendation Engine

The recommendation engine uses a sophisticated scoring system (0-100 points):

1. **Priority Scoring (0-30 points)**
   - URGENT: 30 points
   - HIGH: 20 points
   - MEDIUM: 10 points
   - LOW: 5 points

2. **Due Date Urgency (0-40 points)**
   - Overdue: 40 points
   - Due within 4 hours: 35 points
   - Due today: 30 points
   - Due tomorrow: 20 points
   - Due this week: 15 points
   - Due later: 10 points

3. **Duration-based Scoring (0-15 points)**
   - Prioritizes quick wins (15-30 min tasks)
   - Builds momentum with shorter tasks

4. **ML Pattern Recognition (0-15 points)**
   - Learns from your completion history
   - Identifies your most productive hours
   - Recognizes your most productive days
   - Matches task complexity to historical success patterns

## Technology Stack

### Backend
- **Spring Boot 3.2.0**: Modern Java framework
- **PostgreSQL**: Robust relational database
- **JPA/Hibernate**: ORM for database operations
- **Google Calendar API**: External calendar integration
- **Apache Commons Math**: ML algorithms

### Frontend
- **HTML5/CSS3**: Modern responsive design
- **Vanilla JavaScript**: No framework dependencies
- **REST API**: Clean API architecture

## Project Structure

```
smart-productivity-assistant/
├── src/
│   ├── main/
│   │   ├── java/com/productivity/
│   │   │   ├── controller/          # REST API endpoints
│   │   │   │   ├── TaskController.java
│   │   │   │   ├── NoteController.java
│   │   │   │   └── CalendarController.java
│   │   │   ├── service/             # Business logic
│   │   │   │   ├── TaskService.java
│   │   │   │   ├── NoteService.java
│   │   │   │   ├── CalendarService.java
│   │   │   │   └── RecommendationEngine.java
│   │   │   ├── repository/          # Data access layer
│   │   │   │   ├── TaskRepository.java
│   │   │   │   ├── NoteRepository.java
│   │   │   │   └── TaskCompletionHistoryRepository.java
│   │   │   ├── entity/              # Database models
│   │   │   │   ├── Task.java
│   │   │   │   ├── Note.java
│   │   │   │   ├── User.java
│   │   │   │   └── TaskCompletionHistory.java
│   │   │   └── dto/                 # Data transfer objects
│   │   │       ├── TaskDTO.java
│   │   │       └── NoteDTO.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/              # Frontend files
│   │           ├── index.html
│   │           ├── styles.css
│   │           └── app.js
│   └── test/                        # Unit tests
└── pom.xml                          # Maven dependencies
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Google Calendar API credentials (optional)

### Database Setup

1. Install PostgreSQL and create a database:
```sql
CREATE DATABASE productivity_db;
```

2. Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/productivity_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Google Calendar Integration (Optional)

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project
3. Enable Google Calendar API
4. Create OAuth 2.0 credentials (Desktop application)
5. Download credentials as `credentials.json`
6. Place `credentials.json` in `src/main/resources/`

### Running the Application

1. Clone the repository:
```bash
cd Smart-Productivity-Assistant
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Access the application:
- Frontend: http://localhost:8080
- API Documentation: http://localhost:8080/api

### Initial Data Setup

The application will automatically create the database schema on first run. To create a test user:

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "name": "Test User",
    "timezone": "America/New_York"
  }'
```

## API Endpoints

### Tasks

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks?userId={id}` | Get all tasks for user |
| GET | `/api/tasks/recommended?userId={id}` | Get AI-recommended tasks |
| GET | `/api/tasks/{id}` | Get specific task |
| GET | `/api/tasks/status/{status}?userId={id}` | Get tasks by status |
| POST | `/api/tasks?userId={id}` | Create new task |
| PUT | `/api/tasks/{id}` | Update task |
| POST | `/api/tasks/{id}/complete` | Mark task complete |
| DELETE | `/api/tasks/{id}` | Delete task |

### Notes

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/notes?userId={id}` | Get all notes |
| GET | `/api/notes/{id}` | Get specific note |
| GET | `/api/notes/pinned?userId={id}` | Get pinned notes |
| GET | `/api/notes/search?userId={id}&query={q}` | Search notes |
| POST | `/api/notes?userId={id}` | Create note |
| PUT | `/api/notes/{id}` | Update note |
| DELETE | `/api/notes/{id}` | Delete note |

### Calendar

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/calendar/today` | Get today's events |
| GET | `/api/calendar/upcoming?maxResults={n}` | Get upcoming events |

## Usage Examples

### Creating a Task

```javascript
POST /api/tasks?userId=1
Content-Type: application/json

{
  "title": "Complete project documentation",
  "description": "Write comprehensive README and API docs",
  "priority": "HIGH",
  "estimatedDuration": 120,
  "dueDate": "2025-12-07T17:00:00"
}
```

### Getting Recommendations

```javascript
GET /api/tasks/recommended?userId=1

Response:
[
  {
    "id": 5,
    "title": "Fix critical bug",
    "priority": "URGENT",
    "dueDate": "2025-12-06T18:00:00",
    "estimatedDuration": 30,
    "recommendationScore": 95.5,
    "isOverdue": false
  },
  ...
]
```

### Completing a Task

```javascript
POST /api/tasks/5/complete
Content-Type: application/json

{
  "actualDuration": 25
}
```

## How the Recommendation Engine Works

1. **Initial Scoring**: When a task is created, it receives an initial score based on priority and due date

2. **Continuous Learning**: Every time you complete a task, the system records:
   - Time of day
   - Day of week
   - Actual vs estimated duration
   - Priority level
   - Whether it was overdue

3. **Pattern Recognition**: After 5+ completed tasks, the engine identifies:
   - Your most productive hours (when you complete the most tasks)
   - Your most productive days
   - Your typical completion times for different priority levels

4. **Smart Suggestions**: The engine combines all factors to suggest tasks that:
   - Match your current productive time
   - Are urgent but achievable
   - Build momentum with quick wins
   - Align with your historical success patterns

5. **Auto-Refresh**: Scores are recalculated hourly to reflect changing urgency

## Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Recommendation Engine
recommendation.engine.enabled=true
recommendation.engine.ml.enabled=true
recommendation.engine.min.data.points=5

# Database
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server
server.port=8080
```

## Testing

Run unit tests:
```bash
mvn test
```

Run with test coverage:
```bash
mvn clean test jacoco:report
```

## Future Enhancements

- [ ] User authentication and authorization
- [ ] Mobile responsive design improvements
- [ ] Export tasks/notes to various formats
- [ ] Integration with more calendar services (Outlook, Apple Calendar)
- [ ] Task templates and recurring tasks
- [ ] Collaborative features (shared tasks/notes)
- [ ] Advanced ML models (deep learning for better predictions)
- [ ] Pomodoro timer integration
- [ ] Email notifications for due tasks
- [ ] Dark mode theme

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.

## Support

For issues and questions:
- Create an issue in the repository
- Email: support@productivity-assistant.com

## Acknowledgments

- Spring Boot team for the excellent framework
- Google Calendar API for calendar integration
- Apache Commons Math for ML utilities
