package com.practicaSV.gameLabz.utils;

import org.quartz.JobDetail;

public class SessionWrapper {

    private JobDetail job;

    private String sessionId;

    public JobDetail getJob() {
        return job;
    }

    public void setJob(JobDetail job) {
        this.job = job;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
