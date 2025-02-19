package cz.martinkostelecky.logprocessor;

import cz.martinkostelecky.logprocessor.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class JobStarterDev implements CommandLineRunner {

    private final EmailService emailService;

    @Override
    public void run(String... args) throws Exception {
        runLogProcessor();
    }

    public void runLogProcessor() throws IOException {

        LogProcessor logProcessor = new LogProcessor(emailService);
        logProcessor.processLogs();
    }
}
