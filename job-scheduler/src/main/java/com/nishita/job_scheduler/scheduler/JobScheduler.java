package com.nishita.job_scheduler.scheduler;

import com.nishita.job_common.entity.JobExecutionLog;
import com.nishita.job_common.entity.ScheduledJob;
import com.nishita.job_common.enums.jobStatus;
import com.nishita.job_scheduler.repository.JobExecutionLogRepository;
import com.nishita.job_scheduler.repository.ScheduledJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobScheduler {

    private final ScheduledJobRepository repository;
    private final JobExecutionLogRepository logRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @SuppressWarnings("null")
    @Scheduled(fixedRate = 10000)
    public void pickDueJobs() {

        List<ScheduledJob> jobs = repository.findByStatusAndScheduledTimeBefore(
                jobStatus.PENDING,
                LocalDateTime.now());

        for (ScheduledJob job : jobs) {

            String lockKey = "job-lock:" + job.getId();

            Boolean lockAcquired = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "locked", Duration.ofMinutes(5));

            if (!Boolean.TRUE.equals(lockAcquired)) {
                continue;
            }

            try {

                int updated = repository.updateJobStatusIfMatch(
                        job.getId(),
                        jobStatus.PENDING,
                        jobStatus.RUNNING);

                if (updated == 0) {
                    continue;
                }

                kafkaTemplate.send("job-execution-topic", job.getId().toString());

                job.setStatus(jobStatus.SUCCESS);

                logRepository.save(
                        JobExecutionLog.builder()
                                .jobId(job.getId())
                                .status(jobStatus.SUCCESS)
                                .message("Job executed successfully")
                                .executionTime(LocalDateTime.now())
                                .build());

            } catch (Exception e) {

                log.error("Job failed: {} | Reason: {}",
                        job.getId(),
                        e.getMessage());

                job.setRetryCount(job.getRetryCount() + 1);

                if (job.getRetryCount() >= job.getMaxRetries()) {
                    job.setStatus(jobStatus.FAILED);

                    logRepository.save(
                            JobExecutionLog.builder()
                                    .jobId(job.getId())
                                    .status(jobStatus.FAILED)
                                    .message(e.getMessage())
                                    .executionTime(LocalDateTime.now())
                                    .build());
                } else {
                    job.setStatus(jobStatus.PENDING);
                }

            } finally {
                redisTemplate.delete(lockKey);
                repository.save(job);
            }
        }
    }

    @Scheduled(fixedRate = 30000) // 30 seconds
    public void recoverStuckJobs() {

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(2);

        List<ScheduledJob> stuckJobs = repository.findByStatusAndUpdatedAtBefore(
                jobStatus.RUNNING,
                threshold);

        for (ScheduledJob job : stuckJobs) {

            log.warn("Recovering stuck job {}", job.getId());

            job.setStatus(jobStatus.PENDING);
            repository.save(job);
        }
    }

}
