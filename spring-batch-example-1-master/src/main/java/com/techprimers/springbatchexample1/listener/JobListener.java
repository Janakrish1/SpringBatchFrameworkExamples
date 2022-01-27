package com.techprimers.springbatchexample1.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

public class JobListener extends JobExecutionListenerSupport {
	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("\n**********Before " + jobExecution.getJobInstance().getJobName() + " execution.**********\n");
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("\n**********After " + jobExecution.getJobInstance().getJobName() + " execution.**********\n");
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("\n**********" + jobExecution.getStatus() + "**********\n");
		}
		else {
			System.out.println("\n**********Failed!**********\n");
		}
	}
}
