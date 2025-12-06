# Smart Productivity Assistant - Architecture Documentation

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client Layer                            │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │            Web Browser (Frontend)                        │  │
│  │  • index.html (Dashboard UI)                             │  │
│  │  • styles.css (Styling)                                  │  │
│  │  • app.js (JavaScript Logic)                             │  │
│  └──────────────────────────────────────────────────────────┘  │
└───────────────────────────┬─────────────────────────────────────┘
                            │ HTTP/REST
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                    Application Layer (Spring Boot)              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                  Controller Layer                        │  │
│  │  • TaskController                                        │  │
│  │  • NoteController                                        │  │
│  │  • CalendarController                                    │  │
│  └─────────────────────┬────────────────────────────────────┘  │
│                        │                                        │
│  ┌─────────────────────▼────────────────────────────────────┐  │
│  │                  Service Layer                           │  │
│  │  • TaskService (CRUD + Completion)                       │  │
│  │  • NoteService (CRUD + Search)                           │  │
│  │  • CalendarService (Google API Integration)              │  │
│  │  • RecommendationEngine ⭐ (ML Algorithm)                │  │
│  └─────────────────────┬────────────────────────────────────┘  │
│                        │                                        │
│  ┌─────────────────────▼────────────────────────────────────┐  │
│  │                Repository Layer (JPA)                    │  │
│  │  • TaskRepository                                        │  │
│  │  • NoteRepository                                        │  │
│  │  • UserRepository                                        │  │
│  │  • TaskCompletionHistoryRepository                       │  │
│  └─────────────────────┬────────────────────────────────────┘  │
└────────────────────────┼────────────────────────────────────────┘
                         │ JDBC
                         │
┌────────────────────────▼────────────────────────────────────────┐
│                    Data Layer (PostgreSQL)                      │
│  • users                                                        │
│  • tasks                                                        │
│  • notes                                                        │
│  • task_completion_history (ML training data)                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                External Services                                │
│  • Google Calendar API (OAuth 2.0)                              │
└─────────────────────────────────────────────────────────────────┘
```

## Component Architecture

### 1. Frontend Architecture (MVC Pattern)

```
┌─────────────────────────────────────────────────┐
│                   View Layer                    │
│  • index.html                                   │
│    - Dashboard grid layout                      │
│    - Task list components                       │
│    - Note components                            │
│    - Calendar widget                            │
│    - Statistics cards                           │
│    - Modal dialogs                              │
└────────────┬────────────────────────────────────┘
             │
┌────────────▼────────────────────────────────────┐
│              Controller Layer (JS)              │
│  • app.js                                       │
│    - Event handlers                             │
│    - API communication                          │
│    - State management                           │
│    - UI updates                                 │
└────────────┬────────────────────────────────────┘
             │
┌────────────▼────────────────────────────────────┐
│                Model Layer (DTOs)               │
│  • Task objects                                 │
│  • Note objects                                 │
│  • Calendar event objects                       │
│  • Statistics                                   │
└─────────────────────────────────────────────────┘
```

### 2. Backend Architecture (Layered)

```
┌─────────────────────────────────────────────────────────────┐
│                    REST API Layer                           │
│  ┌───────────┐  ┌───────────┐  ┌──────────────┐           │
│  │   Task    │  │   Note    │  │   Calendar   │           │
│  │Controller │  │Controller │  │  Controller  │           │
│  └─────┬─────┘  └─────┬─────┘  └──────┬───────┘           │
│        │              │                │                    │
│        │  @RestController, @RequestMapping                 │
│        │  Input validation, Error handling                 │
└────────┼──────────────┼────────────────┼────────────────────┘
         │              │                │
┌────────▼──────────────▼────────────────▼────────────────────┐
│                   Business Logic Layer                      │
│  ┌────────────┐  ┌────────────┐  ┌──────────────┐          │
│  │    Task    │  │    Note    │  │   Calendar   │          │
│  │  Service   │  │  Service   │  │   Service    │          │
│  └─────┬──────┘  └─────┬──────┘  └──────┬───────┘          │
│        │               │                 │                   │
│        │     ┌─────────▼─────────┐       │                  │
│        └─────►  Recommendation   │───────┘                  │
│              │     Engine ⭐     │                          │
│              └───────────────────┘                           │
│  @Service, @Transactional                                   │
│  Business rules, Validation, ML algorithms                  │
└────────┬──────────────┬────────────────┬─────────────────────┘
         │              │                │
┌────────▼──────────────▼────────────────▼─────────────────────┐
│                 Data Access Layer (JPA)                      │
│  ┌────────────┐  ┌────────────┐  ┌──────────────────────┐   │
│  │    Task    │  │    Note    │  │ TaskCompletionHistory│   │
│  │ Repository │  │ Repository │  │    Repository        │   │
│  └─────┬──────┘  └─────┬──────┘  └──────┬───────────────┘   │
│        │               │                 │                    │
│  @Repository, JpaRepository                                  │
│  Custom queries, Derived queries                             │
└────────┼───────────────┼─────────────────┼────────────────────┘
         │               │                 │
┌────────▼───────────────▼─────────────────▼────────────────────┐
│                    Entity Layer                               │
│  ┌────────┐  ┌──────┐  ┌──────┐  ┌──────────────────────┐    │
│  │  Task  │  │ Note │  │ User │  │TaskCompletionHistory │    │
│  └────────┘  └──────┘  └──────┘  └──────────────────────┘    │
│  @Entity, @Table, JPA Annotations                             │
│  Domain models, Relationships                                 │
└───────────────────────────────────────────────────────────────┘
```

## Recommendation Engine Architecture

```
┌─────────────────────────────────────────────────────────────┐
│              RecommendationEngine.java                      │
│                                                             │
│  calculateRecommendationScore(Task, userId)                 │
│           │                                                 │
│           ├─► Priority Scoring (0-30 pts)                   │
│           │    └─ URGENT=30, HIGH=20, MEDIUM=10, LOW=5     │
│           │                                                 │
│           ├─► Due Date Urgency (0-40 pts)                   │
│           │    ├─ Overdue: 40                               │
│           │    ├─ Due in 4h: 35                             │
│           │    ├─ Due today: 30                             │
│           │    ├─ Due tomorrow: 20                          │
│           │    ├─ Due this week: 15                         │
│           │    └─ Due later: 10                             │
│           │                                                 │
│           ├─► Duration Score (0-15 pts)                     │
│           │    ├─ ≤15 min: 15 (quick wins)                 │
│           │    ├─ ≤30 min: 12                               │
│           │    ├─ ≤60 min: 8                                │
│           │    └─ >60 min: 5                                │
│           │                                                 │
│           └─► ML Pattern Score (0-15 pts)                   │
│                │                                            │
│                ├─► Time of Day Score (0-5 pts)              │
│                │    └─ Compare current hour to completion  │
│                │       patterns                             │
│                │                                            │
│                ├─► Day of Week Score (0-5 pts)              │
│                │    └─ Compare current day to completion   │
│                │       patterns                             │
│                │                                            │
│                └─► Priority Success Score (0-5 pts)         │
│                     └─ Match duration to historical avg    │
│                                                             │
│  Total Score = min(100, sum of all factors)                │
└─────────────────────────────────────────────────────────────┘
```

## Data Flow Diagrams

### Task Creation Flow

```
User (UI)
   │
   │ 1. Fill form and submit
   ├──────────────────────────────────────┐
   │                                      │
   ▼                                      ▼
POST /api/tasks?userId=1          TaskController
   │                                      │
   │                                      │ 2. Validate input
   │                                      ▼
   │                               TaskService
   │                                      │
   │                                      │ 3. Create Task entity
   │                                      │ 4. Calculate initial score
   │                                      │    via RecommendationEngine
   │                                      ▼
   │                               TaskRepository
   │                                      │
   │                                      │ 5. Save to database
   │                                      ▼
   │                                  PostgreSQL
   │                                      │
   │ 6. Return TaskDTO                    │
   ◄──────────────────────────────────────┘
   │
   ▼
Display updated task list
```

### Task Completion & ML Training Flow

```
User completes task
   │
   │ POST /api/tasks/{id}/complete
   │ { "actualDuration": 55 }
   ▼
TaskController
   │
   │ 1. Receive completion request
   ▼
TaskService.completeTask()
   │
   ├─► 2. Update task status to COMPLETED
   │   └─► Set completedAt timestamp
   │   └─► Set actualDuration
   │
   └─► 3. Save to history
       │
       ▼
   saveCompletionHistory()
       │
       ├─► Extract: userId, priority, duration
       ├─► Extract: dayOfWeek, hourOfDay
       ├─► Extract: wasOverdue, timeBeforeDue
       │
       ▼
   TaskCompletionHistoryRepository
       │
       │ 4. Save ML training data
       ▼
   PostgreSQL (task_completion_history)
       │
       │ ML data now available for:
       │ • Time-of-day patterns
       │ • Day-of-week patterns
       │ • Duration predictions
       │ • Priority success rates
       └─────────────────────────────────►
                                          │
                                          ▼
                              Future recommendations
                                   use this data!
```

### Recommendation Generation Flow

```
User requests recommendations
   │
   │ GET /api/tasks/recommended?userId=1
   ▼
TaskController
   │
   ▼
TaskService.getRecommendedTasks()
   │
   │ 1. Fetch all active tasks
   ▼
TaskRepository.findActiveTasksByUserOrderByScore()
   │
   │ Query: status NOT IN ('COMPLETED', 'CANCELLED')
   │        ORDER BY recommendation_score DESC
   ▼
List<Task>
   │
   │ 2. Return top 5
   ▼
Convert to DTOs
   │
   │ 3. Return to UI
   ▼
Display recommended tasks with scores
```

### Scheduled Score Recalculation

```
@Scheduled (every hour)
   │
   ▼
TaskService.recalculateRecommendationScores()
   │
   │ 1. Find all active tasks
   ▼
TaskRepository.findAll()
   │
   │ 2. Filter: status != COMPLETED, CANCELLED
   ▼
For each task:
   │
   ├─► RecommendationEngine.calculateScore()
   │       │
   │       ├─► Priority score
   │       ├─► Due date score (time-sensitive!)
   │       ├─► Duration score
   │       └─► ML score (if enough data)
   │           │
   │           ├─► Query completion history
   │           ├─► Analyze patterns
   │           └─► Apply patterns to task
   │
   └─► Update task.recommendationScore
       │
       ▼
   TaskRepository.saveAll()
       │
       ▼
   PostgreSQL (tasks updated)
```

## Database Schema Relationships

```
┌─────────────────┐
│     users       │
│─────────────────│
│ id (PK)         │◄──┐
│ email           │   │
│ name            │   │
│ timezone        │   │
│ created_at      │   │
│ updated_at      │   │
└─────────────────┘   │
                      │ 1:N
                      │
         ┌────────────┴──────────────┐
         │                           │
┌────────▼────────┐         ┌────────▼────────┐
│     tasks       │         │     notes       │
│─────────────────│         │─────────────────│
│ id (PK)         │         │ id (PK)         │
│ user_id (FK)    │         │ user_id (FK)    │
│ title           │         │ title           │
│ description     │         │ content         │
│ priority        │         │ tags[]          │
│ status          │         │ pinned          │
│ due_date        │         │ created_at      │
│ estimated_dur   │         │ updated_at      │
│ actual_dur      │         └─────────────────┘
│ completed_at    │
│ rec_score ⭐    │
│ created_at      │
│ updated_at      │
└────────┬────────┘
         │
         │ Completed tasks create
         │ history entries
         │
┌────────▼─────────────────────┐
│ task_completion_history      │
│──────────────────────────────│
│ id (PK)                      │
│ user_id                      │
│ task_id                      │
│ task_title                   │
│ priority                     │
│ estimated_duration           │
│ actual_duration              │
│ completed_at                 │
│ day_of_week    ◄─┐          │
│ hour_of_day    ◄─┤ ML       │
│ was_overdue    ◄─┤ Features │
│ time_before_due◄─┘          │
│ created_at                   │
└──────────────────────────────┘
         │
         │ Used by
         ▼
  RecommendationEngine
  for ML predictions
```

## Technology Stack Details

### Backend Dependencies

```
Spring Boot 3.2.0
├── spring-boot-starter-web
│   └── REST API, Jackson JSON, Tomcat
├── spring-boot-starter-data-jpa
│   └── Hibernate, JPA, Transaction management
├── spring-boot-starter-validation
│   └── Bean validation, Hibernate validator
├── postgresql
│   └── PostgreSQL JDBC driver
├── lombok
│   └── Reduce boilerplate code
├── google-api-services-calendar
│   └── Google Calendar integration
├── google-api-client
│   └── Google API client libraries
├── google-oauth-client-jetty
│   └── OAuth 2.0 authentication
└── commons-math3
    └── Statistical calculations for ML
```

### Deployment Architecture (Docker)

```
┌─────────────────────────────────────────────────┐
│            Docker Compose Environment            │
│                                                  │
│  ┌────────────────────────────────────────────┐ │
│  │  productivity-app (Spring Boot)            │ │
│  │  • Port: 8080                              │ │
│  │  • Depends on: postgres                    │ │
│  │  • Health check: /actuator/health          │ │
│  └───────────────┬────────────────────────────┘ │
│                  │                               │
│                  │ JDBC                          │
│                  │                               │
│  ┌───────────────▼────────────────────────────┐ │
│  │  productivity-db (PostgreSQL 15)          │ │
│  │  • Port: 5432                              │ │
│  │  • Volume: postgres_data                   │ │
│  │  • Init script: database/init.sql          │ │
│  └───────────────┬────────────────────────────┘ │
│                  │                               │
│                  │ SQL                           │
│                  │                               │
│  ┌───────────────▼────────────────────────────┐ │
│  │  productivity-pgadmin (Optional)          │ │
│  │  • Port: 5050                              │ │
│  │  • Web-based DB management                 │ │
│  └────────────────────────────────────────────┘ │
│                                                  │
└─────────────────────────────────────────────────┘
```

## Security Architecture (Future)

```
┌─────────────────────────────────────────────────┐
│                  Client Layer                   │
│  • Store JWT token in localStorage              │
│  • Include token in Authorization header        │
└────────────────────┬────────────────────────────┘
                     │
                     │ HTTPS (TLS 1.3)
                     │
┌────────────────────▼────────────────────────────┐
│            Security Filter Chain                │
│  ┌───────────────────────────────────────────┐  │
│  │  1. CORS Filter                           │  │
│  │     └─ Allow specific origins             │  │
│  ├───────────────────────────────────────────┤  │
│  │  2. JWT Authentication Filter             │  │
│  │     ├─ Validate token                     │  │
│  │     ├─ Extract user claims                │  │
│  │     └─ Set SecurityContext                │  │
│  ├───────────────────────────────────────────┤  │
│  │  3. Authorization Filter                  │  │
│  │     ├─ Check user roles                   │  │
│  │     └─ Verify resource ownership          │  │
│  ├───────────────────────────────────────────┤  │
│  │  4. Rate Limiting Filter                  │  │
│  │     └─ 100 req/min per user               │  │
│  └───────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
              Controller Layer
```

## Performance Considerations

### Database Optimization

```
Indexes Created:
├── idx_tasks_user_status
│   └── Speeds up: findByUserIdAndStatus()
├── idx_tasks_user_score
│   └── Speeds up: findActiveTasksByUserOrderByScore()
├── idx_tasks_due_date
│   └── Speeds up: overdue task queries
├── idx_notes_user
│   └── Speeds up: findByUserId()
├── idx_history_user
│   └── Speeds up: ML pattern queries
└── idx_history_completed_at
    └── Speeds up: time-based analytics
```

### Caching Strategy (Future)

```
┌─────────────────────────────────────────────────┐
│              Application Layer                  │
│                                                 │
│  ┌───────────────────────────────────────────┐ │
│  │         Redis Cache Layer                 │ │
│  │  • Recommendation scores (1 hour TTL)     │ │
│  │  • User productivity patterns (24h TTL)   │ │
│  │  • Calendar events (15 min TTL)           │ │
│  └─────────────┬─────────────────────────────┘ │
│                │                                │
│           Cache miss                            │
│                │                                │
│  ┌─────────────▼─────────────────────────────┐ │
│  │         Service Layer                     │ │
│  │  • Query database                         │ │
│  │  • Calculate if needed                    │ │
│  │  • Update cache                           │ │
│  └───────────────────────────────────────────┘ │
└─────────────────────────────────────────────────┘
```

## Scalability Architecture (Future)

```
┌──────────────────────────────────────────────────┐
│              Load Balancer (Nginx)               │
│                   Port: 443                      │
└────────┬──────────────┬──────────────┬───────────┘
         │              │              │
    ┌────▼────┐    ┌────▼────┐    ┌────▼────┐
    │  App    │    │  App    │    │  App    │
    │Instance1│    │Instance2│    │Instance3│
    └────┬────┘    └────┬────┘    └────┬────┘
         │              │              │
         └──────────────┴──────────────┘
                        │
                   ┌────▼────┐
                   │PostgreSQL│
                   │ Primary │
                   └────┬────┘
                        │
              ┌─────────┴─────────┐
         ┌────▼────┐         ┌────▼────┐
         │PostgreSQL│         │PostgreSQL│
         │ Replica1│         │ Replica2│
         └─────────┘         └─────────┘
```

---

This architecture is designed for:
- **Scalability**: Stateless design allows horizontal scaling
- **Maintainability**: Clear separation of concerns
- **Testability**: Each layer can be tested independently
- **Extensibility**: Easy to add new features
- **Performance**: Optimized queries and caching strategy
