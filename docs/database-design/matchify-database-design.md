# Matchify Database Design

We will be adding the basic design for our database, tables, columns for the Application.

## Tables for Machify

### User Table

| user_id | first_name | last_name | location_id | email     | password |
|---------|------------|-----------|-------------|-----------|----------|
| 1       | Vaibhav    | Singh     | 1           | vs@dal.ca | ****     |
| 2       | John       | Wick      | 2           | jw@x.a    | *        |

### Location

| location_id | location |
|-------------|----------|
| 1           | Halifax  |
| 2           | Toronto  |

### Interests

| interest_id | category | group |
|-------------|----------|-------|
| 1           | Korean   | Food  |
| 2           | Mexican  | Food  |

### User Interests

| interest_id | user_id |
|-------------|---------|
| 1           | 1       |
| 1           | 2       |

### Event Attendees

| event_id | user_id |
|----------|---------|
| 1        | 1       |
| 1        | 2       |

### Event Interests

| event_id | interest_id |
|----------|-------------|
| 1        | 1           |
| 1        | 2           |

### Event

| event_id | creator_id | name    | description      | additional_instructions | event_date | event_time | event_duration | location_id | lat        | long       |
|----------|------------|---------|------------------|-------------------------|------------|------------|----------------|-------------|------------|------------|
| 1        | 1          | K Bowls | New Korean place | Meet at Room 101        | 20/02/2024 | 8:00 pm    | 1 hour         | 1           | 44.6356° N | 63.5952° W |

### User profile match

| user_id | similarity_user_id | similarity_score |
|---------|--------------------|------------------|
| 1       | 2                  | 70               |
| 2       | 1                  | 50               |