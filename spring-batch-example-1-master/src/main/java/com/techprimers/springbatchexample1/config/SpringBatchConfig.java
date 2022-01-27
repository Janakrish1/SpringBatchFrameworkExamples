package com.techprimers.springbatchexample1.config;

import com.techprimers.springbatchexample1.batch.DBWriter;
import com.techprimers.springbatchexample1.batch.ExpProcessor;
import com.techprimers.springbatchexample1.batch.Processor;
import com.techprimers.springbatchexample1.batch.TaskletStep;
import com.techprimers.springbatchexample1.deciders.MyJobExecutionDecider;
import com.techprimers.springbatchexample1.listener.JobListener;
import com.techprimers.springbatchexample1.model.User;
import com.techprimers.springbatchexample1.service.MyRetryService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private DBWriter itemWriter;
	@Autowired
	private Processor itemProcessor;
	@Autowired
	private ExpProcessor expProcessor;
	@Autowired
	private TaskletStep taskletStep;
	@Autowired
	private DataSource dataSource;
	@Autowired
	private MyJobExecutionDecider decider;
	@Autowired
	private MyRetryService myRetryService;

	private static Random random = new Random();

	public boolean isTestPassed() {
		return random.nextInt() % 2 == 0;
	}
	
	
    @Bean
    public Job job1() throws Exception {       
        // Job 1
        return jobBuilderFactory.get("Job1")
        		.listener(new JobListener())
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }
	
    @Bean
    public Job job2() throws Exception {       
        // Job 2
        return jobBuilderFactory.get("Job2")
        		.listener(new JobListener())
				.start(step4()).next(decider)
				.from(decider).on("STEP2").to(step5())
				.from(decider).on("STEP3").to(step6())
				.end().build();
    }
    
    @Bean
    public Job job3() throws Exception {       
        // Job 3
        return jobBuilderFactory.get("Job3")
        		.listener(new JobListener())
        		.incrementer(new RunIdIncrementer())
                .start(splitFlow())
                .build()		// builds FlowJobBuilder instance
                .build();		// builds Job instance
    }
    
    @Bean
	public Job job4() throws Exception {
		return jobBuilderFactory.get("Job4")
				.start(step9())
				.build();
	}
    
    /*JOB 1 --> Multiple Steps with tasklet*/
    
    @Bean
    public Step step1() throws Exception {
    	return stepBuilderFactory.get("Step1")
                .<User, User>chunk(100)
                .reader(itemReader(null))
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }
    
    @Bean
    public Step step2() throws Exception {
    	return stepBuilderFactory.get("Step2")
                .<User, User>chunk(100)
                .reader(h2Reader())
                .processor(expProcessor)
                .writer(csvWriter(null))
                .build();
    }
    
    @Bean
    public Step step3() throws Exception {
    	return stepBuilderFactory.get("Step3")
    			.tasklet(taskletStep)
    			.build();
    }

    @Bean
    @StepScope
    @Value("#{jobParameters['csvFilePath']}")
    public FlatFileItemReader<User> itemReader(String filePath) {

        FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(filePath));
        flatFileItemReader.setName("CSV-Reader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;
    }
    
    @Bean
    @StepScope
    public JdbcCursorItemReader<User> h2Reader() {
    	JdbcCursorItemReader<User> reader = new JdbcCursorItemReader<User>();
    	reader.setDataSource(dataSource);
    	reader.setSql("Select id, dept, exp, name, salary from user");
    	reader.setRowMapper(new RowMapper<User>() {
    		
    		@Override
    		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
    			User s = new User();
    			s.setId(rs.getInt("id"));
    			s.setDept(rs.getString("dept"));
    			s.setExp(rs.getInt("exp"));
    			s.setName(rs.getString("name"));
    			s.setSalary(rs.getInt("salary"));
    			return s;
    		}
    	});
    	
    	return reader;
    }
    
    @Bean
    @StepScope
    @Value("#{jobParameters['csvOutput']}")
    public FlatFileItemWriter<User> csvWriter(String filepath) {
    	FlatFileItemWriter<User> writer = new FlatFileItemWriter<User>();
    	writer.setResource(new FileSystemResource(filepath));
    	DelimitedLineAggregator<User> aggregator = new DelimitedLineAggregator<>();
    	BeanWrapperFieldExtractor<User> fieldExtractor = new BeanWrapperFieldExtractor<>();
    	fieldExtractor.setNames(new String[] {"id", "dept", "exp", "name", "salary", "profileName"});
    	aggregator.setFieldExtractor(fieldExtractor);
    	writer.setLineAggregator(aggregator);
    	return writer;
    }

    @Bean
    @StepScope
    public LineMapper<User> lineMapper() {

        DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "name", "dept", "salary", "exp");

        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }
    
    /*JOB 2 --> Multiple Steps with deciders*/
    

	@Bean
	public Step step4() throws Exception {
		return this.stepBuilderFactory.get("MyStep1")
				.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {

					if (isTestPassed()) {
						chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
								.put("status", "passed");
						System.out.println("\n**********Number is Even**********\n");
					} else {
						chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
								.put("status", "failed");
						System.out.println("\n**********Number is Odd**********\n");
					}

					return RepeatStatus.FINISHED;

				}).build();
	}

	@Bean
	public Step step5() throws Exception {
		return this.stepBuilderFactory.get("MyStep2")
				.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {

					System.out.println("\n**********Executing step 2**********\n");
					return RepeatStatus.FINISHED;

				}).build();
	}

	@Bean
	public Step step6() throws Exception {
		return this.stepBuilderFactory.get("MyStep3")
				.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
					System.out.println("\n**********Executing step 3**********\n");
					return RepeatStatus.FINISHED;

				}).build();
	}

	
	
	@Bean
	public JobListener listener() {
		return new JobListener();
	}
	
	
	/*JOB 3 --> Parallel Steps*/
	
	@Bean
	public Flow splitFlow()  throws Exception {
	    return new FlowBuilder<SimpleFlow>("splitFlow")
	        .split(taskExecutor())
	        .add(flow1(), flow2())
	        .build();
	}
	
	@Bean
	public Flow flow1() throws Exception {
	    return new FlowBuilder<SimpleFlow>("flow1")
	        .start(step7())
	        .build();
	}
	
	@Bean
	public Flow flow2() throws Exception {
	    return new FlowBuilder<SimpleFlow>("flow2")
	        .start(step8())
	        .build();
	}
	
	@Bean
	public Step step7() throws Exception {
		return this.stepBuilderFactory.get("ParallelStep1")
				.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {

					System.out.println("\n**********Parallel Step 1**********\n");
					return RepeatStatus.FINISHED;

				}).build();
	}

	@Bean
	public Step step8() throws Exception {
		return this.stepBuilderFactory.get("ParallelStep2")
				.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
					System.out.println("\n**********Parallel Step 2**********\n");
					return RepeatStatus.FINISHED;

				}).build();
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
	    return new SimpleAsyncTaskExecutor("spring_batch");
	}
	
	
	/*JOB 4 --> Steps with Retry Service*/
	
	@Bean
	protected Tasklet tasklet() {

		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution,
					ChunkContext context) {
			    myRetryService.process();
				return RepeatStatus.FINISHED;
			}
		};

	}

	@Bean
	protected Step step9() throws Exception {
		return this.stepBuilderFactory.get("step9").tasklet(tasklet()).build();
	}
}
