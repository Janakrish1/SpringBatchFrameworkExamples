package com.techprimers.springbatchexample1.batch;

import java.io.File;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskletStep implements Tasklet {
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		try {
			File file = new File("src/main/resources/users.csv");
//			if(file.delete()) {
			if(file.getName().equals("JANA")) {
				System.out.println("\n*********File-" + file.getName() + " is deleted Successfully using Tasklet step!*********\n");
			}
			else {
				System.out.println("Deletion operation is Failed!");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
