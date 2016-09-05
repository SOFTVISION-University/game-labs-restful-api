package com.practicaSV.gameLabz.services;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionExpireJob implements Job {

    public static final String USER_NAME_KEY = "userName";

    public static final String AUTHENTICATION_SERVICE = "authenticationService";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        String userName = (String) jobExecutionContext.getJobDetail().getJobDataMap().get(USER_NAME_KEY);

        logger.info("The job of " + userName + " has expired!");

        AuthenticationService authenticationService = (AuthenticationService) jobExecutionContext.getJobDetail().getJobDataMap().get(AUTHENTICATION_SERVICE);

        logger.info("Logging out user " + userName);

        authenticationService.doLogout(userName);

        logger.info("User " + userName + " has logged out!");
    }
}
