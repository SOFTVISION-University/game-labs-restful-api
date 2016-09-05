package com.practicaSV.gameLabz.utils.spring;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public Scheduler getScheduler() throws SchedulerException {
        return new StdSchedulerFactory().getScheduler();
    }

}
