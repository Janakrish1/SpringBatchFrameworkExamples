# Spring Boot with Spring Batch Example 1
## Load CSV to DB
- `http://localhost:8081/jobs/start/{jobName}` - Trigger point for Spring Batch
- `http://localhost:8081/h2-console` - H2 Console for querying the in-memory tables.
- `{jobName}` - Dynamic name for jobs. For example --> job1, job2, job3 and job4

## H2 Config
- `testdb` - Database.
- `sa` - User
- `password` - Password.
