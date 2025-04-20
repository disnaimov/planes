package com.example.planes.scheduling;

import com.example.planes.dao.PlaneRepository;
import com.example.planes.entity.Plane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionalCapacityUpdateJob implements Job {

    private final PlaneRepository planeRepository;
    private final Scheduler scheduler;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            log.info("Schedule capacity update started");
            Plane plane = planeRepository.getPlaneById(UUID.fromString("1562e58f-e66e-4330-a8d6-a8b00b47bedd"));
            plane.setCapacity(plane.getCapacity() + 100);
            log.info("Plane received");

            planeRepository.save(plane);
            log.info("Plane saved with ID");

            log.info("Successfully executed TransactionalCapacityUpdateJob.");
        } catch (Exception e) {
            log.error("Error executing TransactionalCapacityUpdateJob: {}", e.getMessage());
            throw new JobExecutionException(e);
        }
    }

    public void schedule() {
        var jobDetail = JobBuilder.newJob(getClass())
                .withIdentity("transactionalCapacityUpdateJob")
                .storeDurably()
                .build();
        log.info("Job detail created");

        var trigger = TriggerBuilder.newTrigger()
                .withIdentity("transactionalCapacityUpdateTrigger")
                .forJob(jobDetail)
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10)
                        .withRepeatCount(5))
                .build();
        log.info("Trigger created");

        try {
            // Проверка, существует ли задача
            if (scheduler.checkExists(jobDetail.getKey())) {
                log.info("Job already exists, skipping scheduling.");
            } else {
                scheduler.scheduleJob(jobDetail, trigger);
                log.info("Scheduled TransactionalCapacityUpdateJob to run every 10 seconds.");
            }
        } catch (SchedulerException e) {
            log.error("Error scheduling TransactionalCapacityUpdateJob: {}", e.getMessage(), e);
        }
    }
}
