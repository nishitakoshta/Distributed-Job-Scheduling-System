package com.nishita.job_common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


import com.nishita.job_common.enums.jobStatus;

@Entity
@Table(name = "scheduled_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobName;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    private jobStatus status;

    private LocalDateTime scheduledTime;

    private int retryCount;

    private int maxRetries;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    
}
