package org.example.springbatchpoc.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CustomerJobScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

  //  @Scheduled(fixedRate = 60 * 60 * 1000) // every 1 hour
    public void launchJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("jobName", "customerJob")
                .toJobParameters();

        JobExecution execution = jobLauncher.run(job, params);
        System.out.println("Batch job executed at " + LocalDateTime.now() + " :: Status - " + execution.getStatus());
    }
}