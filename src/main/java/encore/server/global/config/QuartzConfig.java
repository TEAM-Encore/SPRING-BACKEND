package encore.server.global.config;

import encore.server.global.util.quartz.SendNotificationJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(SendNotificationJob.class)
                .withIdentity("quartzSendNotificationJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("sendNotificationTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 12 * * ?")) // 매일 12:00 실행
                .build();
    }
}