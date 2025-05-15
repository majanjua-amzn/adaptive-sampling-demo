package com.amazon.sampleapp;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;

import io.opentelemetry.api.trace.Span;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import java.sql.SQLException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;



@Service
public class AsyncManager {
    private static final Logger logger = LoggerFactory.getLogger(AsyncManager.class);
    private final String rdsConnectionurl = "jdbc:mysql://adaptive-sampling-demo-database-2.cluster-ci3qowicxt1h.us-east-1.rds.amazonaws.com:3306";
    private final String rdsUsername = "admin";

    private static final String rdsPassword = "password";
    // Async method to return the current password based on the current second
    @Async
    public CompletableFuture<String> connectToDatabase() {
        String retrievedRdsPassword = getPassword();
        try {
            Connection connection = DriverManager.getConnection(
                rdsConnectionurl,
                rdsUsername,
                retrievedRdsPassword);
            logger.info("Connection was successful");
        } catch (SQLException e) {
            logger.error("Could not complete SQL request: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(getXrayTraceId());
    }

    public String getPassword() {
        int currentSecond = java.time.LocalTime.now().getSecond();
        String password = (currentSecond <= 10) ? "Fake Password" : rdsPassword;
        logger.info("Current second: {}. Password being used: {}", currentSecond, password);

        return password;
    }

      // get x-ray trace id
    private String getXrayTraceId() {
        String traceId = Span.current().getSpanContext().getTraceId();
        String xrayTraceId = "1-" + traceId.substring(0, 8) + "-" + traceId.substring(8);
        return String.format("{\"traceId\": \"%s\"}", xrayTraceId);
    }
}