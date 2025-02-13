package cz.martinkostelecky.logprocessor;

import cz.martinkostelecky.logprocessor.service.EmailService;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Profile("dev")
public class JobStarter {

    private final EmailService emailService;

    public JobStarter(EmailService emailService) {
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void runLogProcessor() throws IOException {

        LogProcessor logProcessor = new LogProcessor(emailService);
        logProcessor.processLogs();
    }
}
