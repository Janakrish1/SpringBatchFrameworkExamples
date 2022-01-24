package com.techprimers.springbatchexample1.listener;


import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class JobListener implements JobExecutionListener{
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("Before " + jobExecution.getJobInstance().getJobName() + " execution.");
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("After " + jobExecution.getJobInstance().getJobName() + " execution.");
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("Success!");
		}
		else {
			System.out.println("Failed!");
		}
	}
}
