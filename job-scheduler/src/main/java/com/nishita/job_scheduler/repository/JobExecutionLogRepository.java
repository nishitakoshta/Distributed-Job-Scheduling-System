package com.nishita.job_scheduler.repository;

import com.nishita.job_common.entity.JobExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobExecutionLogRepository extends JpaRepository<JobExecutionLog, Long> {
    
}
