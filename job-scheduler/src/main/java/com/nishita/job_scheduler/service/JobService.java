package com.nishita.job_scheduler.service;

import com.nishita.job_scheduler.dto.CreateJobRequest;
import com.nishita.job_common.entity.ScheduledJob;
import com.nishita.job_common.enums.jobStatus;
import com.nishita.job_scheduler.repository.ScheduledJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JobService {

    private final ScheduledJobRepository repository;

    @SuppressWarnings("null")
    public ScheduledJob createJob(CreateJobRequest request) {

        ScheduledJob job = ScheduledJob.builder()
                .jobName(request.getJobName())
                .payload(request.getPayload())
                .status(jobStatus.PENDING)
                .scheduledTime(request.getScheduledTime())
                .retryCount(0)
                .maxRetries(request.getMaxRetries())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return repository.save(job);
    }
}
