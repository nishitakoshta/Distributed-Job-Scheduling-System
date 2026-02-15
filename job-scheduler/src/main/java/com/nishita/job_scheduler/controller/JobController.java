package com.nishita.job_scheduler.controller;

import com.nishita.job_scheduler.dto.CreateJobRequest;
import com.nishita.job_scheduler.repository.ScheduledJobRepository;
import com.nishita.job_common.entity.ScheduledJob;
import com.nishita.job_scheduler.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ScheduledJob createJob(@RequestBody CreateJobRequest request) {
        return jobService.createJob(request);
    }
}
