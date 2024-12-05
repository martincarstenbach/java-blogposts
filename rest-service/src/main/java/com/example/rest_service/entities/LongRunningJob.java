package com.example.rest_service.entities;

import java.time.Instant;

/**
 * Class to represent martin.job_log_table
 */
public class LongRunningJob {

    private long id;
    private String jobName;
    private String requestedBy;
    private Instant startTime;
    private Instant finishTime;

    public void setId(long id) {
        this.id = id;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void setFinishTime(Instant finishTime) {
        this.finishTime = finishTime;
    }

    public long getId() {
        return id;
    }

    public String getJobName() {
        return jobName;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }
}
