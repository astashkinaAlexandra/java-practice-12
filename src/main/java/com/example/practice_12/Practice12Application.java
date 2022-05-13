package com.example.practice_12;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class Practice12Application {

    private static String sourceFileName;
    private static String destFileName;

    Logger logger = LoggerFactory.getLogger(Practice12Application.class);

    public static void main(String[] args) {
        sourceFileName = args[0];
        destFileName = args[1];
        SpringApplication.run(Practice12Application.class, args);
    }

    @PostConstruct
    public void readAndHashFile() {
        logger.info("Post construct");
        Path sourceFilePath = new File(sourceFileName).toPath();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileWriter fileWriter = new FileWriter(destFileName, false);

            try {
                md.update(Files.readAllBytes(sourceFilePath));
                fileWriter.write(Converter.fromByteArrayToHex(md.digest()));
                logger.info("Hash has been written to " + destFileName);
            } catch (NoSuchFileException e) {
                fileWriter.write("null");
                logger.info("File " + sourceFilePath + " don't exists");
            } finally {
                fileWriter.flush();
            }

        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    @PreDestroy
    private void deleteInputFile() {
        logger.info("Pre destroy");
        try {
            Path fileToDeletePath = Paths.get(sourceFileName);
            Files.delete(fileToDeletePath);
        } catch (IOException e) {
            logger.info("File " + sourceFileName + " don't exists");
        }
    }
}

class Converter {
    public static String fromByteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}