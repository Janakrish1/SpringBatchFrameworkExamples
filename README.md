# SpringBatchFrameworkExamples

Spring Batch is an open source Framework which is lightweight and POJO based approach used for batch applications. High volumes of records can be handled by breaking
into small jobs and steps to be executed on it. This can be used to build robust and scalable applications.

# Concepts of Spring Batch
## Job
  - Entity of the entire batch process
  - Container for Step instances
## JobParameters
  - Holds a set of parameters used to start a batch job
  - Pass the required Job Parameters to read it anywhere within Spring Batch application
## JobLauncher
  - Launching a job with a given set of JobParameters
## JobExecution
  - Attempt to run a job 
  - It may end in success or failure
  - Properties like status, startTime, endTime, exitStatus etc.
## JobRepository
  - Provides CRUD operations for JobLauncher, Job and Step
  - Mechanism in Spring Batch that makes all this persistence possible.
## Step
  - Sequential phase of batch job
  - One or more steps
## StepExecution
  - Single attempt to execute a step
  - StepExecution is created each time a step is run
  - Properties like status, startTime, endTime, exitStatus, readCount, writeCount etc.
## ItemReader
  - Retrieving of data for a step
  - Indicated by returning a “null” if items are exhausted
## JdbcCursorItemReader
  - JDBC implementation
  - Requires a SQL to run against the connection obtained from datasource
    - Connection is made through datasource
    - SQL is run against it
    - mapRow method is called for each row
## FlatFileItemReader
  - 2 – dimensional data (tabular)
  - Required dependencies are Resource and LineMapper
  - Properties like lineMapper, setLines, setLinesToSkip, resource etc.
## LineMapper
  - Read one line from file
  - Pass the string to LineTokenizer to retrieve a FieldSet
  - FieldSet tokenized and is returned to FieldSetMapper
  - Then the FieldSet is returned to ItemReader by mapping it with object
## ItemWriter
  - Writing data of a step either one batch or chunk of items at a time
  - For database - JdbcCursorItemWriter
  - For FlatFile - FlatFileItemWriter
## FlatFileItemWriter
  - LineAggregator – aggregate multiple fields into a single string for writing in a file
  - BeanWrapperFieldSetMapper – Convert the domain object to an object array
  - Pass the item to be written to the writer.
  - FieldExtractor – Convert the fields on the item into an array. 
  - Aggregate the resulting array into a line.
## ItemProcessor
  - Business Logic?
  - Given an object transform it and return another
  - We can validate input, transform according to need etc.
  - If the input is invalid it returns null indicating it should not be written out
## JobExecutionListener
  - Provides callbacks before start and after completion of job
  - The annotations for this interface are 
    - BeforeJob
    - AfterJob
## StepExecutionListener
  - Provides callbacks before start and after completion of step
  - The annotations for this interface are 
    - BeforeStep
    - AfterStep
## Retry (RetryOperations interface)
  - To make more robust and less prone to failure – Like While() loop
  - RetryContext – Store the data which may be used to retry later
  - If retry is exhausted this can pass control to RecoveryCallback
    - @Retryable, @EnableRetry
## Repeat (RepeatOperations interface)
  - RepeatStatus.CONTINUABLE or RepeatStatus.FINISHED
## Chunk Oriented Processing
  - Reading data one at a time and creating chunks
## Running a Step
  - Run a step using either flow() or start()
## TaskletStep
  - Tasklet is a simple interface that has one method – execute()
  - Returns RepeatStatus.FINISHED on successful completion else throws exception
## Conditional Flow
  - We can specify the flow when a step fails 
  - JobExecutionDecider is used to assist with decision
  - on() method is used to pass on to next step based on decision
## Parallel Steps
  - Split()
  - FlowSteps
  - taskExecutor() is used to execute the individual flows
  - AsyncTaskExecutor to run steps in parallel since SyncTaskExecutor is by default
