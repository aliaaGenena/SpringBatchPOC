package org.example.springbatchpoc.config;

import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.batch.core.launch.JobOperator;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
public class AutoRecoverAndRetryFailedJobs {

    private final JobExplorer jobExplorer;
    private final JobRepository jobRepository;
    private final JobLauncher jobLauncher;
    private final JobOperator jobOperator;

    public AutoRecoverAndRetryFailedJobs(JobExplorer jobExplorer,
                                         JobRepository jobRepository,
                                         JobRegistry jobRegistry,
                                         JobLauncher jobLauncher,
                                         JobOperator jobOperator) {
        this.jobExplorer = jobExplorer;
        this.jobRepository = jobRepository;
        this.jobLauncher = jobLauncher;
        this.jobOperator = jobOperator;
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

                if (latestExecution.getStatus().isUnsuccessful() || status == BatchStatus.FAILED
                        || (status == BatchStatus.STARTED
                        && latestExecution.getEndTime() == null)){
                       // && latestExecution.getStartTime().isBefore(LocalDateTime.now().minusHours(1)))) {
                    try {
                        System.out.println("Stuck execution found AND Retrying job: " + jobName + " [latestExecutionId=" + latestExecution.getId() + "]");
                       System.out.println("Job appears stuck. Marking as FAILED.");
                        latestExecution.setStatus(BatchStatus.FAILED);
                        latestExecution.setEndTime(LocalDateTime.now());
                        jobRepository.update(latestExecution);
                        jobOperator.restart(latestExecution.getId());

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
