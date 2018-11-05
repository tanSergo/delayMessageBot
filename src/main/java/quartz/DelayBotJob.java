package quartz;

import bot.DelayMessageBot;
import dao.DelayMessageDao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.*;
import service.DelayMessageService;

import java.util.Map;

public class DelayBotJob implements Job {
    private DelayMessageDao dao = new DelayMessageDao();
    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Delay message awakening Job start: " + jobContext.getFireTime());
//        System.out.println("jobContext.getJobDetail().getJobDataMap(): " + jobContext.getJobDetail().getJobDataMap().getString("eventId"));
//        System.out.println("jobContext.getTrigger().getDescription(): "  + jobContext.getTrigger().getDescription());
        SendMessage delayMessage = new SendMessage();
        Map<String, String> eventParams = dao.getEvent(jobContext.getJobDetail().getJobDataMap().getString("eventId"));
        delayMessage.setChatId(eventParams.get("receiver_id"));
        delayMessage.setText(eventParams.get("message"));
        DelayMessageBot.getBot().sendMessage(delayMessage);

        System.out.println("Job's thread name is: " + Thread.currentThread().getName());
        System.out.println("Job end");
        System.out.println("--------------------------------------------------------------------");
    }
}
