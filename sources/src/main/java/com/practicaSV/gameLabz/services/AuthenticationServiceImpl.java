package com.practicaSV.gameLabz.services;

import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.exceptions.AuthenticationException;
import com.practicaSV.gameLabz.repositories.UserDAO;
import com.practicaSV.gameLabz.utils.SessionWrapper;
import org.apache.commons.lang.RandomStringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Map<String, SessionWrapper> sessions = new HashMap<>();

    @Value("${session.expiry.in.seconds}")
    private Long sessionExpiryInSeconds;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String doLogin(String userName, String pass) {

        String sessionId;
        User user = userDAO.getUserByUserName(userName).orElseThrow(() -> new AuthenticationException(HttpStatus.UNAUTHORIZED, "Username or password is wrong!"));

        if (passwordEncoder.matches(pass, user.getPassword())) {

            sessionId = RandomStringUtils.randomAlphanumeric(16);
            startSessionExpireJob(userName, sessionId);

        } else {
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "Username or password is wrong!");
        }

        return sessionId;
    }

    @Override
    public boolean validateSession(String userName, String sessionId) {

        if (!sessions.containsKey(userName)) {
            logger.error("[Session validation failed! No key was found!]");
            return false;
        }

        if (sessions.get(userName).getSessionId().equals(sessionId)) {
            stopSessionExpireJob(userName);
            startSessionExpireJob(userName, sessionId);

            logger.debug("[Session restarted for user: " + userName + " with session id: " + sessionId + "] ");
            return true;
        }
        return false;
    }

    @Override
    public void doLogout(String userName) {

        logger.debug("[Session ended with session id: " + sessions.get(userName).getSessionId() + " for user: " + userName + "] ");
        stopSessionExpireJob(userName);
        sessions.remove(userName);
    }

    private void startSessionExpireJob(String userName, String sessionId) {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(SessionExpireJob.USER_NAME_KEY, userName);
        jobDataMap.put(SessionExpireJob.AUTHENTICATION_SERVICE, this);

        JobDetail job = JobBuilder.newJob(SessionExpireJob.class).setJobData(jobDataMap).build();

        Trigger trigger = TriggerBuilder.newTrigger().startAt(new Date(System.currentTimeMillis() + sessionExpiryInSeconds * 1000L)).forJob(job).build();

        try {
            scheduler.start();
            scheduler.scheduleJob(job, trigger);

            SessionWrapper sessionWrapper = new SessionWrapper();
            sessionWrapper.setJob(job);
            sessionWrapper.setSessionId(sessionId);

            sessions.put(userName, sessionWrapper);

        } catch (SchedulerException e) {
            logger.error("[Failed to start session expire job for user: " + userName + "]: " + e.getMessage(), e);
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "[Failed to log in!]");
        }

    }

    private void stopSessionExpireJob(String userName) {

        SessionWrapper jobToStop = sessions.get(userName);

        if (jobToStop != null) {
            try {
                scheduler.deleteJob(jobToStop.getJob().getKey());
                logger.debug("[Session stopped successfully for user: " + userName + "]");

            } catch (SchedulerException e) {
                logger.error("[Failed to stop session expire job for user: " + userName + "]: " +  e.getMessage(), e);
            }
        } else {
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "Cannot stop invalid session expire job!");
        }
    }
}
