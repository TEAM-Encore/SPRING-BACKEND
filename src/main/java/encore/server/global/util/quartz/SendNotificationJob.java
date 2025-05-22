package encore.server.global.util.quartz;

import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;

@RequiredArgsConstructor
public class SendNotificationJob implements org.quartz.Job {

    private final JobLauncher jobLauncher;
    private final Job sendNotificationJob;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(sendNotificationJob, jobParameters);
        } catch (Exception e) {
            throw new JobExecutionException("Failed to execute notification job", e);
        }
    }
}