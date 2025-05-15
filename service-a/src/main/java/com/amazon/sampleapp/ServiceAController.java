package com.amazon.sampleapp;

import io.opentelemetry.api.trace.Span;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


@Controller
public class ServiceAController {
  private static final Logger logger = LoggerFactory.getLogger(ServiceAController.class);
  private final CloseableHttpClient httpClient;

  @Autowired
  public ServiceAController(CloseableHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @GetMapping("/")
  @ResponseBody
  public String healthcheck() {
    return "healthcheck";
  }

@GetMapping("/ping_service_a")
@ResponseBody
public ResponseEntity<String> pingServiceB() {
    try {
        HttpGet request = new HttpGet("http://localhost:8081/service_b_async_endpoint");
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            if (statusCode >= 200 && statusCode < 300) {
                return ResponseEntity.ok("Called /ping_service_a and got response: " + result.toString());
            } else {
                return ResponseEntity
                        .status(statusCode)
                        .body("Called /ping_service_b but got error response: " + result.toString());
            }
        }
    } catch (Exception e) {
        return ResponseEntity
                .status(500)
                .body("Exception occurred while calling /ping_service_b: " + e.getMessage());
    }
}

  // get x-ray trace id
  private String getXrayTraceId() {
    String traceId = Span.current().getSpanContext().getTraceId();
    String xrayTraceId = "1-" + traceId.substring(0, 8) + "-" + traceId.substring(8);
    return String.format("{\"traceId\": \"%s\"}", xrayTraceId);
  }
}
