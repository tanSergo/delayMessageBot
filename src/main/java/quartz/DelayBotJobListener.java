package quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class DelayBotJobListener implements JobListener {
    @Override
    public String getName() {
        return "DelayBotJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        System.out.println("Job to be executed: " + context.getFireInstanceId() + ", job listener: " + getName());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException e) {
            System.out.println("Job was executed: " + context.getFireInstanceId() + ", job listener: " + getName());
    }
}
