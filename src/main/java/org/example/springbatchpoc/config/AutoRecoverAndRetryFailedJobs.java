package org.example.springbatchpoc.config;

import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
public class AutoRecoverAndRetryFailedJobs {

    private final JobExplorer jobExplorer;
    private final JobRegistry jobRegistry;
    private final JobLauncher jobLauncher;

    public AutoRecoverAndRetryFailedJobs(JobExplorer jobExplorer,
                                         JobRepository jobRepository,
                                         JobRegistry jobRegistry,
                                         JobLauncher jobLauncher) {
        this.jobExplorer = jobExplorer;
        this.jobRegistry = jobRegistry;
        this.jobLauncher = jobLauncher;
    }


    @Scheduled(fixedRate = 60 * 60 * 1000) // every 1 hour
    public void launchJob(){
        for (String jobName : jobExplorer.getJobNames()) {
            List<JobInstance> instances = jobExplorer.findJobInstancesByJobName(jobName, 0, 100);

            for (JobInstance instance : instances) {
                List<JobExecution> executions = jobExplorer.getJobExecutions(instance);
                if (executions.isEmpty()) continue;


                // ✅ Get the most recent execution only
                JobExecution latestExecution = executions.stream()
                        .max(Comparator.comparing(JobExecution::getCreateTime))
                        .orElse(null);

                if (latestExecution == null) continue;



                BatchStatus status = latestExecution.getStatus();


                // ✅ Skip COMPLETED jobs
                if (status == BatchStatus.COMPLETED) {
                    System.out.println("Skipping already COMPLETED job: " + jobName);
                    break; // ✅ Skip the rest of this instance's executions
                }

                if (status == BatchStatus.FAILED
                        || (status == BatchStatus.STARTED
                        && latestExecution.getEndTime() == null)){
                       // && latestExecution.getStartTime().isBefore(LocalDateTime.now().minusHours(1)))) {
                    try {
                        System.out.println("Stuck execution found AND Retrying job: " + jobName + " [latestExecutionId=" + latestExecution.getId() + "]");

                        Job job = jobRegistry.getJob(jobName);
                        JobParameters jobParameters = latestExecution.getJobParameters();

                        jobLauncher.run(job, jobParameters);

                    } catch (JobExecutionAlreadyRunningException e) {
                        System.err.println("Job already running: JobExecutionAlreadyRunningException : " + jobName);
                    } catch (JobInstanceAlreadyCompleteException e) {
                        System.err.println("Job already completed : JobInstanceAlreadyCompleteException : " + jobName);
                    } catch (JobRestartException e) {
                        System.err.println("Failed to restart job '" + jobName + "': " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("Unexpected error for job '" + jobName + "': " + e.getMessage());
                    }
                    break; // ✅ Don't retry older executions — exit the loop after one retry
                }
            }
        }

    }}
