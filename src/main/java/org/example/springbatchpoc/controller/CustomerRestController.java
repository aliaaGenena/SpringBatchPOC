package org.example.springbatchpoc.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class CustomerRestController {

	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private Job job;
	
	@GetMapping("/import")
	public void loadDataToDB() throws Exception{

		JobParameters jobParameters = new JobParametersBuilder()
				.addString("jobName", "customerJob")
				.toJobParameters();

		jobLauncher.run(job, jobParameters);
	}
	
	
}


