package com.nishita.job_worker.consumer;


import com.nishita.job_common.entity.ScheduledJob;
import com.nishita.job_common.entity.JobExecutionLog;
import com.nishita.job_common.enums.jobStatus;
import com.nishita.job_worker.repository.ScheduledJobRepository;
import com.nishita.job_worker.repository.JobExecutionLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobExecutionConsumer {

    private final ScheduledJobRepository repository;
    private final JobExecutionLogRepository logRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "job-execution-topic")
    public void handleJob(String jobId) {

        Long id = Long.valueOf(jobId);

        ScheduledJob job = repository.findById(id)
                .orElseThrow();

        try {

            log.info("Worker executing job {}", id);
            if (job.getStatus() != jobStatus.RUNNING) {
                return;
            }

            executeJob(job);

            job.setStatus(jobStatus.SUCCESS);

            logRepository.save(
                    JobExecutionLog.builder()
                            .jobId(job.getId())
                            .status(jobStatus.SUCCESS)
                            .message("Job executed successfully")
                            .executionTime(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {

            log.error("Worker failed job {}", id);

            job.setRetryCount(job.getRetryCount() + 1);

            if (job.getRetryCount() < job.getMaxRetries()) {

                job.setStatus(jobStatus.PENDING);

                kafkaTemplate.send("job-execution-topic",
                        job.getId().toString());

            } else {

                job.setStatus(jobStatus.FAILED);

                kafkaTemplate.send("job-dead-letter-topic",
                        job.getId().toString());
            }


            logRepository.save(
                    JobExecutionLog.builder()
                            .jobId(job.getId())
                            .status(jobStatus.FAILED)
                            .message(e.getMessage())
                            .executionTime(LocalDateTime.now())
                            .build()
            );
        }

        repository.save(job);
    }

    private void executeJob(ScheduledJob job) {

        if (Math.random() < 0.5) {
            throw new RuntimeException("Random failure in worker");
        }
    }
    @KafkaListener(topics = "job-dead-letter-topic")
    public void handleDeadLetter(String jobId) {

        log.error("Job moved to Dead Letter Topic: {}", jobId);
    }

}

