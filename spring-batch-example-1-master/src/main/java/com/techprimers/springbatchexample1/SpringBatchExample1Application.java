package com.techprimers.springbatchexample1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBatchExample1Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchExample1Application.class, args);
	}
}

/*
QUERIES:

select * from USER;

select * from BATCH_JOB_EXECUTION;
select * from BATCH_JOB_EXECUTION_CONTEXT;

select * from BATCH_STEP_EXECUTION;
select * from BATCH_STEP_EXECUTION_CONTEXT;

select * from BATCH_JOB_INSTANCE;
*/