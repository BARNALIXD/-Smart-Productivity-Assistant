# Quick Start Guide - Smart Productivity Assistant

Get up and running in 5 minutes!

## Option 1: Docker (Recommended)

The easiest way to run the application with all dependencies.

### Prerequisites
- Docker installed
- Docker Compose installed

### Steps

1. **Navigate to project directory:**
```bash
cd Smart-Productivity-Assistant
```

2. **Start all services:**
```bash
docker-compose up -d
```

This will start:
- PostgreSQL database (port 5432)
- Spring Boot application (port 8080)
- PgAdmin (port 5050) - optional database admin tool

3. **Access the application:**
- Frontend: http://localhost:8080
- PgAdmin: http://localhost:5050 (admin@productivity.com / admin)

4. **Stop services:**
```bash
docker-compose down
```

---

## Option 2: Local Development

Run directly on your machine.

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

### Steps

1. **Install and start PostgreSQL:**
```bash
# On macOS
brew install postgresql
brew services start postgresql

# On Ubuntu/Debian
sudo apt-get install postgresql
sudo service postgresql start

# On Windows
# Download and install from https://www.postgresql.org/download/
```

2. **Create database:**
```bash
psql -U postgres
```
```sql
CREATE DATABASE productivity_db;
\q
```

3. **Update database credentials:**

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

4. **Build and run:**
```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

5. **Access the application:**
- Frontend: http://localhost:8080

---

## First Time Setup

### 1. Load Sample Data (Optional)

```bash
# Connect to PostgreSQL
psql -U postgres -d productivity_db -f database/init.sql
```

This will create:
- A demo user
- Sample tasks
- Sample notes
- Historical completion data for ML training

### 2. Create Your First User (API)

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "you@example.com",
    "name": "Your Name",
    "timezone": "America/New_York"
  }'
```

### 3. Create Your First Task (UI)

1. Open http://localhost:8080
2. Click "+ Add Task"
3. Fill in task details
4. Submit

### 4. View Recommendations

The recommendation engine will immediately suggest your best next task based on:
- Priority level
- Due date proximity
- Estimated duration

As you complete more tasks, the ML engine learns your patterns!

---

## Google Calendar Setup (Optional)

To integrate with Google Calendar:

### 1. Get Google Calendar API Credentials

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable "Google Calendar API"
4. Create OAuth 2.0 credentials (Desktop application)
5. Download credentials as JSON

### 2. Add Credentials to Project

1. Rename downloaded file to `credentials.json`
2. Place in `src/main/resources/` directory

### 3. Restart Application

The app will prompt you to authorize on first calendar access.

---

## Testing the API

### Using cURL

**Get recommended tasks:**
```bash
curl "http://localhost:8080/api/tasks/recommended?userId=1"
```

**Create a task:**
```bash
curl -X POST "http://localhost:8080/api/tasks?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Task",
    "priority": "HIGH",
    "estimatedDuration": 30,
    "dueDate": "2025-12-07T15:00:00"
  }'
```

**Complete a task:**
```bash
curl -X POST "http://localhost:8080/api/tasks/1/complete" \
  -H "Content-Type: application/json" \
  -d '{"actualDuration": 25}'
```

### Using Postman

1. Import the API endpoints from `API_DOCUMENTATION.md`
2. Set base URL: `http://localhost:8080/api`
3. Test the endpoints

---

## Understanding the Recommendation Engine

### How It Works

1. **Priority Scoring (30 points max)**
   - URGENT tasks get 30 points
   - HIGH tasks get 20 points
   - MEDIUM tasks get 10 points
   - LOW tasks get 5 points

2. **Urgency Scoring (40 points max)**
   - Overdue: 40 points
   - Due in 4 hours: 35 points
   - Due today: 30 points
   - Due tomorrow: 20 points

3. **Duration Bonus (15 points max)**
   - Quick tasks (15 min): 15 points
   - Short tasks (30 min): 12 points
   - Medium tasks (1 hour): 8 points

4. **ML Patterns (15 points max)**
   - Learns your productive hours
   - Learns your productive days
   - Matches task complexity to your history

### Building Your ML Model

The engine needs at least 5 completed tasks to start learning. To train it:

1. Create several tasks with different priorities
2. Complete them at different times of day
3. Record actual duration when completing
4. After 5+ completions, ML kicks in!

The more tasks you complete, the smarter it gets!

---

## Troubleshooting

### Application won't start

**Check Java version:**
```bash
java -version
# Should be 17 or higher
```

**Check PostgreSQL is running:**
```bash
# macOS/Linux
pg_isready

# Windows
pg_ctl status
```

### Database connection error

**Verify database exists:**
```bash
psql -U postgres -l | grep productivity
```

**Check credentials in application.properties**

### Port 8080 already in use

**Change port in application.properties:**
```properties
server.port=8081
```

### Calendar integration not working

- Verify credentials.json is in src/main/resources/
- Check Google Cloud Console project has Calendar API enabled
- Ensure OAuth consent screen is configured

---

## Next Steps

1. **Explore the UI** - Create tasks, add notes, view recommendations
2. **Complete some tasks** - Train the ML engine
3. **Read API Documentation** - Build integrations
4. **Check the README** - Learn about advanced features

---

## Support

- Issues: Create a GitHub issue
- Documentation: See README.md and API_DOCUMENTATION.md
- Email: support@productivity-assistant.com

---

## Tips for Best Results

1. **Set realistic estimated durations** - Helps with quick win prioritization
2. **Always add due dates** - Improves urgency scoring
3. **Record actual completion time** - Trains the ML model
4. **Complete tasks regularly** - More data = better recommendations
5. **Use priority levels consistently** - Helps the engine learn patterns

Happy productivity! 🚀
