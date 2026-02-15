package com.nishita.job_scheduler.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateJobRequest {
    private String jobName;
    private String payload;
    private LocalDateTime scheduledTime;
    private int maxRetries;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}