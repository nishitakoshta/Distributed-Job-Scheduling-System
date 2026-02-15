package com.nishita.job_scheduler.repository;

import com.nishita.job_common.entity.ScheduledJob;
import com.nishita.job_common.enums.jobStatus;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long> {

    List<ScheduledJob> findByStatusAndScheduledTimeBefore(
            jobStatus status,
            LocalDateTime time);

    List<ScheduledJob> findByStatusAndUpdatedAtBefore(
            jobStatus status,
            LocalDateTime time);

    @Transactional
    @Modifying
    @Query("""
                UPDATE ScheduledJob j
                SET j.status = :newStatus
                WHERE j.id = :jobId AND j.status = :currentStatus
            """)
    int updateJobStatusIfMatch(
            @Param("jobId") Long jobId,
            @Param("currentStatus") jobStatus currentStatus,
            @Param("newStatus") jobStatus newStatus);

}
