package cz.martinkostelecky.logprocessor;

import cz.martinkostelecky.logprocessor.service.EmailService;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class LogProcessor {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm:ss");

    private final EmailService emailService;

    // DEV specify paths
    private static final String DEV_READER_PATH = "";
    private static final String DEV_WRITER_PATH = "";

    // PROD specify path
    //private static final String PROD_READER_PATH = "";

    BufferedReader reader;
    BufferedWriter writer;

    String line = "";
    LocalDateTime localDateTimeActual;
    LocalDateTime localDateTimeLast;
    LinkedList<String> lines;
    LocalDateTime localDateTimeNow = LocalDateTime.now();

    public LogProcessor(EmailService emailService) {
        this.emailService = emailService;
        this.lines = new LinkedList<>();
    }

    public void processLogs() throws IOException {
        setupReaderAndWriter();

        if ((line = reader.readLine()) == null) {
            while ((line = reader.readLine()) != null) {
                writeLog("Writing logs started at: " + localDateTimeNow);
                lines.add(line);
                writeLog(line);
                writeLog("Writing logs done at: " + localDateTimeNow);
            }
        } else {
            while ((line = reader.readLine()) != null) {

                localDateTimeActual = LocalDateTime.parse(lines.getLast().substring(0, 18), formatter);
                localDateTimeLast = LocalDateTime.parse(line.substring(0, 18), formatter);

                writeLog("Writing logs started at: " + localDateTimeNow);

                if (line.toLowerCase().contains("user") || line.toLowerCase().contains("example") || line.toLowerCase().contains("badge")) {
                    if (localDateTimeActual.isAfter(localDateTimeLast)) {
                        lines.add(line);
                        writeLog(line);
                    }
                }
            }

            writeLog("Writing logs done at: " + localDateTimeNow);
            closeReaderAndWriter();
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

