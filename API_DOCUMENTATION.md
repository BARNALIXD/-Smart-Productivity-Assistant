# Smart Productivity Assistant - API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
Currently, the API uses a simple user ID parameter. Future versions will include JWT authentication.

---

## Tasks API

### 1. Get All Tasks

**Endpoint:** `GET /tasks`

**Query Parameters:**
- `userId` (required): User ID

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive README and API docs",
    "priority": "HIGH",
    "status": "TODO",
    "dueDate": "2025-12-07T17:00:00",
    "estimatedDuration": 120,
    "recommendationScore": 75.5,
    "createdAt": "2025-12-06T10:00:00",
    "updatedAt": "2025-12-06T10:00:00",
    "isOverdue": false,
    "timeUntilDue": 420
  }
]
```

### 2. Get Recommended Tasks

**Endpoint:** `GET /tasks/recommended`

**Description:** Returns top 5 AI-recommended tasks based on priority, urgency, and ML patterns

**Query Parameters:**
- `userId` (required): User ID

**Response:** `200 OK`
```json
[
  {
    "id": 5,
    "title": "Fix critical bug",
    "priority": "URGENT",
    "status": "TODO",
    "dueDate": "2025-12-06T18:00:00",
    "estimatedDuration": 30,
    "recommendationScore": 95.5,
    "isOverdue": false,
    "timeUntilDue": 120
  }
]
```

### 3. Get Task by ID

**Endpoint:** `GET /tasks/{id}`

**Path Parameters:**
- `id`: Task ID

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive README and API docs",
  "priority": "HIGH",
  "status": "TODO",
  "dueDate": "2025-12-07T17:00:00",
  "estimatedDuration": 120,
  "recommendationScore": 75.5
}
```

**Error Response:** `404 Not Found`

### 4. Get Tasks by Status

**Endpoint:** `GET /tasks/status/{status}`

**Path Parameters:**
- `status`: TODO | IN_PROGRESS | COMPLETED | CANCELLED

**Query Parameters:**
- `userId` (required): User ID

**Response:** `200 OK`

### 5. Create Task

**Endpoint:** `POST /tasks`

**Query Parameters:**
- `userId` (required): User ID

**Request Body:**
```json
{
  "title": "Complete project documentation",
  "description": "Write comprehensive README and API docs",
  "priority": "HIGH",
  "estimatedDuration": 120,
  "dueDate": "2025-12-07T17:00:00"
}
```

**Required Fields:**
- `title`: String

**Optional Fields:**
- `description`: String
- `priority`: LOW | MEDIUM | HIGH | URGENT (default: MEDIUM)
- `estimatedDuration`: Integer (minutes)
- `dueDate`: ISO 8601 DateTime

**Response:** `201 Created`

### 6. Update Task

**Endpoint:** `PUT /tasks/{id}`

**Path Parameters:**
- `id`: Task ID

**Request Body:**
```json
{
  "title": "Updated title",
  "description": "Updated description",
  "priority": "URGENT",
  "status": "IN_PROGRESS",
  "estimatedDuration": 90,
  "dueDate": "2025-12-07T15:00:00"
}
```

**Response:** `200 OK`

### 7. Complete Task

**Endpoint:** `POST /tasks/{id}/complete`

**Path Parameters:**
- `id`: Task ID

**Request Body (Optional):**
```json
{
  "actualDuration": 115
}
```

**Response:** `200 OK`

**Note:** This endpoint saves completion data for ML learning

### 8. Delete Task

**Endpoint:** `DELETE /tasks/{id}`

**Path Parameters:**
- `id`: Task ID

**Response:** `204 No Content`

### 9. Recalculate Recommendation Scores

**Endpoint:** `POST /tasks/recalculate-scores`

**Description:** Manually trigger recalculation of all recommendation scores

**Response:** `200 OK`
```json
{
  "message": "Recommendation scores recalculated"
}
```

---

## Notes API

### 1. Get All Notes

**Endpoint:** `GET /notes`

**Query Parameters:**
- `userId` (required): User ID

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Meeting Notes",
    "content": "Discussed Q1 goals and deliverables",
    "tags": ["meeting", "planning"],
    "pinned": true,
    "createdAt": "2025-12-06T09:00:00",
    "updatedAt": "2025-12-06T09:00:00"
  }
]
```

### 2. Get Note by ID

**Endpoint:** `GET /notes/{id}`

**Path Parameters:**
- `id`: Note ID

**Response:** `200 OK`

### 3. Get Pinned Notes

**Endpoint:** `GET /notes/pinned`

**Query Parameters:**
- `userId` (required): User ID

**Response:** `200 OK`

### 4. Search Notes

**Endpoint:** `GET /notes/search`

**Query Parameters:**
- `userId` (required): User ID
- `query` (required): Search term

**Description:** Searches in both title and content

**Response:** `200 OK`

### 5. Create Note

**Endpoint:** `POST /notes`

**Query Parameters:**
- `userId` (required): User ID

**Request Body:**
```json
{
  "title": "Meeting Notes",
  "content": "Discussed Q1 goals and deliverables",
  "tags": ["meeting", "planning"],
  "pinned": false
}
```

**Required Fields:**
- `title`: String

**Optional Fields:**
- `content`: String
- `tags`: Array of Strings
- `pinned`: Boolean (default: false)

**Response:** `201 Created`

### 6. Update Note

**Endpoint:** `PUT /notes/{id}`

**Path Parameters:**
- `id`: Note ID

**Request Body:**
```json
{
  "title": "Updated title",
  "content": "Updated content",
  "tags": ["updated", "tags"],
  "pinned": true
}
```

**Response:** `200 OK`

### 7. Delete Note

**Endpoint:** `DELETE /notes/{id}`

**Path Parameters:**
- `id`: Note ID

**Response:** `204 No Content`

---

## Calendar API

### 1. Get Today's Events

**Endpoint:** `GET /calendar/today`

**Description:** Retrieves all calendar events for the current day from Google Calendar

**Response:** `200 OK`
```json
[
  {
    "summary": "Team Standup",
    "description": "Daily sync with the team",
    "location": "Conference Room A",
    "startTime": "2025-12-06T10:00:00",
    "endTime": "2025-12-06T10:30:00"
  }
]
```

**Note:** Requires Google Calendar API credentials

### 2. Get Upcoming Events

**Endpoint:** `GET /calendar/upcoming`

**Query Parameters:**
- `maxResults` (optional): Maximum number of events (default: 10)

**Response:** `200 OK`

---

## Data Models

### Task Status
- `TODO`: Not started
- `IN_PROGRESS`: Currently working on
- `COMPLETED`: Finished
- `CANCELLED`: No longer relevant

### Task Priority
- `LOW`: Can be done later
- `MEDIUM`: Normal priority
- `HIGH`: Important
- `URGENT`: Critical, needs immediate attention

### Recommendation Score
- Range: 0-100
- Based on:
  - Priority (0-30 points)
  - Due date urgency (0-40 points)
  - Estimated duration (0-15 points)
  - ML patterns (0-15 points)

---

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2025-12-06T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid input",
  "path": "/api/tasks"
}
```

### 404 Not Found
```json
{
  "timestamp": "2025-12-06T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found",
  "path": "/api/tasks/999"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2025-12-06T10:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/tasks"
}
```

---

## Testing Examples

### Using cURL

**Create a task:**
```bash
curl -X POST "http://localhost:8080/api/tasks?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Task",
    "priority": "HIGH",
    "estimatedDuration": 60,
    "dueDate": "2025-12-07T15:00:00"
  }'
```

**Get recommendations:**
```bash
curl "http://localhost:8080/api/tasks/recommended?userId=1"
```

**Complete a task:**
```bash
curl -X POST "http://localhost:8080/api/tasks/5/complete" \
  -H "Content-Type: application/json" \
  -d '{"actualDuration": 55}'
```

**Search notes:**
```bash
curl "http://localhost:8080/api/notes/search?userId=1&query=meeting"
```

### Using JavaScript (Fetch API)

```javascript
// Create task
const createTask = async () => {
  const response = await fetch('http://localhost:8080/api/tasks?userId=1', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      title: 'New Task',
      priority: 'HIGH',
      estimatedDuration: 60,
      dueDate: '2025-12-07T15:00:00'
    })
  });
  return await response.json();
};

// Get recommended tasks
const getRecommendations = async () => {
  const response = await fetch('http://localhost:8080/api/tasks/recommended?userId=1');
  return await response.json();
};

// Complete task
const completeTask = async (taskId, duration) => {
  const response = await fetch(`http://localhost:8080/api/tasks/${taskId}/complete`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ actualDuration: duration })
  });
  return await response.json();
};
```

---

## Rate Limiting

Currently, there is no rate limiting. Future versions will implement:
- 100 requests per minute per user
- 1000 requests per hour per user

---

## Versioning

Current version: v1.0.0

API versioning will be introduced in future releases.
