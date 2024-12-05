package com.example.rest_service.entities;

public record JobSchedulerInfo (
        String jobName,
        String requestedBy,
        String jobId,
        String status
)
{ }
