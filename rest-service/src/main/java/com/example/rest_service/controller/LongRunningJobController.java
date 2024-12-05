package com.example.rest_service.controller;

import com.example.rest_service.entities.JobSchedulerInfo;
import com.example.rest_service.entities.LongRunningJob;
import com.example.rest_service.services.LongRunningJobService;

import org.jobrunr.jobs.JobId;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class LongRunningJobController {

    @Autowired
    private LongRunningJobService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(LongRunningJobController.class);

    /**
     * Schedules a job and returns metadata like the job name, the person requesting the job,
     * and the internal job ID provided by JobRunr.
     *
     * @param job the Job metadata provided as JSON
     * @return a JSON containing the job name, person requesting the job, and JobRunr ID
     */
    @PostMapping("/job")
    public ResponseEntity<JobSchedulerInfo> submitJob(@RequestBody JobSchedulerInfo job) {

        LOGGER.info("about to process a POST request for the /job endpoint: {}", job.toString());
        JobSchedulerInfo jobInfo;

        if (job.jobName().equals("testjob")) {

            LOGGER.info("scheduling an instance of 'testjob'");

            JobId jobId = BackgroundJob.enqueue(() -> service.doWork(job.requestedBy()));

            jobInfo = new JobSchedulerInfo(
                    job.jobName(),
                    job.requestedBy(),
                    jobId.toString(),
                    "success"
            );
        } else {

            LOGGER.error("no such job - {}", job.jobName());

            jobInfo = new JobSchedulerInfo(
                    job.jobName(),
                    job.requestedBy(),
                    "none",
                    "failure");
        }

        return new ResponseEntity<>(jobInfo, HttpStatus.CREATED);
    }
}
