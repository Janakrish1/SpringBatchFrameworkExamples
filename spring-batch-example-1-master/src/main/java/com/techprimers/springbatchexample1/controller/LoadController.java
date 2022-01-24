package com.techprimers.springbatchexample1.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/jobs")
public class LoadController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Map<String, Job> jobMap = new HashMap<String, Job>();
//    Job job;

    @GetMapping(value = "/start/{jobName}")
    public BatchStatus load(@PathVariable String jobName) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
    	
    	JobParameters jobParameters = new JobParametersBuilder()
							    			.addString("csvFilePath", "src/main/resources/users.csv")
							    			.addString("csvOutput", "src/main/resources/output.csv")
							    			.toJobParameters();
    	
        JobExecution jobExecution = jobLauncher.run(jobMap.get(jobName), jobParameters);
        
        System.out.println("JobExecution: " + jobExecution.getStatus());

        System.out.println("\n**********CSV FILE PATH using jobParameters: " + jobParameters.getString("csvFilePath") + "**********\n");
        System.out.println("Batch is Running...");
        while (jobExecution.isRunning()) {
            System.out.println("...");
        }

        return jobExecution.getStatus();
    }
}
