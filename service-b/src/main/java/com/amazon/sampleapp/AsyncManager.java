package com.amazon.sampleapp;

import io.opentelemetry.api.trace.Span;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncManager {
    private static final Logger logger = LoggerFactory.getLogger(AsyncManager.class);
    private final String rdsConnectionurl = "jdbc:mysql://adaptive-sampling-database.cluster-ro-cb0gswiggqla.us-west-2.rds.amazonaws.com:3306";
    private final String rdsUsername = "admin";
    private final CloseableHttpClient httpClient;

    @Autowired
    public AsyncManager(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    // Async method to return the current password based on the current second
    @Async
    public CompletableFuture<String> connectToDatabase(Boolean success) {
        try {
            Thread.sleep(2000);
            DriverManager.getConnection(
                rdsConnectionurl,
                rdsUsername,
                success ? "password" : "incorrect");
            logger.info("Connection was successful");
        } catch (SQLException e) {
            logger.error("Could not complete SQL request: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return CompletableFuture.completedFuture(getXrayTraceId());
    }

    @Async
    public CompletableFuture<String> callAsyncApi(Boolean success) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (success == null) {
            success = true;
        }
        HttpGet request = new HttpGet("http://localhost:8081/b-async?success=" + success.toString());
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            logger.error("Could not complete HTTP request: {}", e.getMessage());
        }
        return CompletableFuture.completedFuture("placeholder");
    }

    // get x-ray trace id
    private String getXrayTraceId() {
        String traceId = Span.current().getSpanContext().getTraceId();
        String xrayTraceId = "1-" + traceId.substring(0, 8) + "-" + traceId.substring(8);
        return String.format("{\"traceId\": \"%s\"}", xrayTraceId);
    }
}