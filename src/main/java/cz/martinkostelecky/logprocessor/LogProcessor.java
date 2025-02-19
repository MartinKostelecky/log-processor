package cz.martinkostelecky.logprocessor;

import cz.martinkostelecky.logprocessor.service.EmailService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;

//TODO log to console
@Service
public class LogProcessor {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EmailService emailService;

    // DEV specify paths
    private static final String DEV_READER_PATH = "/home/myprogramminghub/Plocha/app.log";
    private static final String DEV_WRITER_PATH = "/home/myprogramminghub/Plocha/logs.log";

    // PROD specify path
    //private static final String PROD_READER_PATH = "";

    BufferedReader reader;
    BufferedWriter writer;

    String line = "";
    LocalDateTime localDateTimeActual;
    LocalDateTime localDateTimeLast;
    LinkedList<String> lines = new LinkedList<>();
    LocalDateTime localDateTimeNow = LocalDateTime.now();

    public LogProcessor(EmailService emailService) {
        this.emailService = emailService;
    }

    public void processLogs() throws IOException {
        setupReaderAndWriter();

        writeLog("Writing logs started at: " + localDateTimeNow);

        while ((line = reader.readLine()) != null) {

            if (containsRelevantEntries(line) && containsValidDate(line)) {
                processLine(line);
            }
        }

        writeLog("Writing logs done at: " + localDateTimeNow);
        closeReaderAndWriter();
    }

    private boolean containsRelevantEntries(String line) {
        return line.toLowerCase().contains("user") || line.toLowerCase().contains("example") || line.toLowerCase().contains("badge");
    }

    private boolean containsValidDate(String line) {

        try {
            String dateString = line.substring(0, 19);
            LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return true;
        } catch (DateTimeParseException | StringIndexOutOfBoundsException e) {
            // Skip the line if it doesn't contain a valid date
            return false;
        }
    }

    private void processLine(String line) throws IOException {

        localDateTimeActual = LocalDateTime.parse(line.substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (!lines.isEmpty()) {
            localDateTimeLast = LocalDateTime.parse(lines.getLast().substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (localDateTimeActual.equals(localDateTimeLast) || localDateTimeActual.isAfter(localDateTimeLast)) {
                lines.add(line);
                writeLog(line);
            }
        } else {
            lines.add(line);
            writeLog(line);
        }
    }

    private void setupReaderAndWriter() throws IOException {
        // DEV
        reader = new BufferedReader(new FileReader(DEV_READER_PATH));
        writer = new BufferedWriter(new FileWriter(DEV_WRITER_PATH));

        // Uncomment for PROD
        // reader = new BufferedReader(new FileReader(PROD_READER_PATH));
        // writer = null; // No file writer in PROD as we will send an email
    }

    private void writeLog(String content) throws IOException {
        if (writer != null) {
            writer.write(content + "\n");
//        } else {
//            // Send email in PROD environment
//            emailService.sendEmail("recipient@example.com", "Log Update", content);
        }
    }

    private void closeReaderAndWriter() throws IOException {
        reader.close();
        if (writer != null) {
            writer.close();
        }
    }
}

