package com.techprimers.springbatchexample1.config;

import com.techprimers.springbatchexample1.batch.DBWriter;
import com.techprimers.springbatchexample1.batch.ExpProcessor;
import com.techprimers.springbatchexample1.batch.Processor;
import com.techprimers.springbatchexample1.batch.TaskletStep;
import com.techprimers.springbatchexample1.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
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
	
	
    @Bean
    public Job job1() throws Exception {       
        // Job
        return jobBuilderFactory.get("Job1")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }
	
    @Bean
    public Job job2() throws Exception {       
        // Job
        return jobBuilderFactory.get("Job2")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }
    
    @Bean
    public Step step1() throws Exception {
    	return stepBuilderFactory.get("Step 1")
                .<User, User>chunk(100)
                .reader(itemReader(null))
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }
    
    @Bean
    public Step step2() throws Exception {
    	return stepBuilderFactory.get("Step 2")
                .<User, User>chunk(100)
                .reader(h2Reader())
                .processor(expProcessor)
                .writer(csvWriter(null))
                .build();
    }
    
    @Bean
    public Step step3() throws Exception {
    	return stepBuilderFactory.get("Step 3")
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
    
    
    
}
