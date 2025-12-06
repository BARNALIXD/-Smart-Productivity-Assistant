# Smart Productivity Assistant

A comprehensive personal productivity hub that combines tasks, notes, schedule management, and AI-powered task recommendations.
<img width="1919" height="892" alt="image" src="https://github.com/user-attachments/assets/e2be7de2-b433-4628-ac15-7438cc950361" />


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

