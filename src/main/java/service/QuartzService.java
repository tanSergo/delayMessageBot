package service;

import dao.DelayMessageDao;
import org.quartz.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

public class QuartzService {
    DelayMessageDao dao = new DelayMessageDao();

    public Scheduler createAndStartScheduler() throws SchedulerException {
        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
        Scheduler scheduler = schedFact.getScheduler();
        System.out
                .println("Scheduler name is: " + scheduler.getSchedulerName());
        System.out.println("Scheduler instance ID is: "
                + scheduler.getSchedulerInstanceId());
        System.out.println("Scheduler context's value for key QuartzTopic is "
                + scheduler.getContext().getString("QuartzTopic"));
        scheduler.start();
        return scheduler;
    }

    public <T extends Job> void fireJob(String eventId, Scheduler scheduler, Class<T> jobClass)
            throws SchedulerException, InterruptedException {

        // define the job and tie it to our HelloJob class
        JobBuilder jobBuilder = JobBuilder.newJob(jobClass);
        JobDataMap data = new JobDataMap();
        data.put("latch", this);

        JobDetail jobDetail = jobBuilder
                .usingJobData("delayBot",
                        "service.delayMessageService.QuartzSchedulerExample")
                .usingJobData("eventId", eventId)
                .usingJobData(data).build();

        String awakening = dao.getEventAwakeningDate(eventId);
        LocalDateTime ldt = LocalDateTime.parse(awakening, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.n"));
        Date start = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
//        Date start = Date.from(Instant.parse(awakening));

        // Trigger the job to run now, and then every 40 seconds
        Trigger trigger = TriggerBuilder
                .newTrigger()
//                .startNow()
                .startAt(start)
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                )
                .withDescription("DelayMessageTrigger").build();

        if (!dao.saveSchedulerDetails(eventId, scheduler.getSchedulerName(), trigger.getKey().getName(), trigger.getKey().getGroup()))
            System.out.println("Could not save Scheduler Details");
        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public boolean rescheduleEvent(Map<String, String> params) {
        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
        try {
            Map<String, String> scheduleDetails = dao.getScheduleDetails(params.get("messageId"));
            // Берем тот же планировщик
            Scheduler scheduler = schedFact.getScheduler(scheduleDetails.get("scheduler_name"));
            // Берем новую дату
            String awakening = dao.getEventAwakeningDate(params.get("messageId"));
            LocalDateTime ldt = LocalDateTime.parse(awakening, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.n"));
            Date start = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
            // Создаем новый триггер
            Trigger newTrigger = TriggerBuilder
                    .newTrigger()
                    .startAt(start)
                    .withSchedule(
                            SimpleScheduleBuilder.simpleSchedule()
                    )
                    .withDescription("DelayMessageTrigger").build();
            // Перепланируем job-у
            scheduler.rescheduleJob(TriggerKey.triggerKey(scheduleDetails.get("trigger_name"), scheduleDetails.get("trigger_group")), newTrigger);

        } catch (SchedulerException e) {
            System.out.println("Problem with reschedule event! " + e);
            return false;
        }

        return true;
    }
}
