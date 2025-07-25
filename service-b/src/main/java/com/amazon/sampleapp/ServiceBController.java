/*
 * Copyright Amazon.com, Inc. or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazon.sampleapp;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ServiceBController {
  private static final Logger logger = LoggerFactory.getLogger(ServiceBController.class);
  private final String rdsConnectionurl = "jdbc:mysql://adaptive-sampling-database.cluster-ro-cb0gswiggqla.us-west-2.rds.amazonaws.com:3306";
  private final String rdsUsername = "admin";
  private final CloseableHttpClient httpClient;

  private static final Tracer tracer = GlobalOpenTelemetry.getTracer("my-async-tracer");

  @Autowired
  public ServiceBController(CloseableHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @GetMapping("/healthcheck")
  @ResponseBody
  public String healthcheck() {
    return "Remote service healthcheck";
  }

  @GetMapping("/b")
  @ResponseBody
  public String b(@RequestParam(name = "success", required = false) Boolean success) {
    if (success == null) {
      success = true;
    }

    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    HttpGet request = new HttpGet("http://localhost:8082/c?success=" + success.toString());
    try (CloseableHttpResponse response = httpClient.execute(request)) {
      int statusCode = response.getStatusLine().getStatusCode();
    } catch (Exception e) {
      logger.error("Could not complete HTTP request: {}", e.getMessage());
    }


    return "Service B called successfully";
  }

  @GetMapping("/b-async")
  @ResponseBody
  public CompletableFuture<String> bAsync(@RequestParam(name = "success", required = false) Boolean success) {
    if (success == null) {
      success = true;
    }
    try {
      DriverManager.getConnection(
        rdsConnectionurl,
        rdsUsername,
        success ? "password" : "incorrect");
      logger.info("Connection was successful");
      return CompletableFuture.completedFuture(getXrayTraceId());
    } catch (SQLException e) {
      // Ignore exception because we meant to pass anyways
      // if (success) {
      //   logger.info("False success - setup is problematic");
      //   return CompletableFuture.completedFuture(getXrayTraceId());
      // }
      logger.error("Could not complete SQL request");
      throw new RuntimeException(e);
    }
  }

  // get x-ray trace id
  private String getXrayTraceId() {
    String traceId = Span.current().getSpanContext().getTraceId();
    String xrayTraceId = "1-" + traceId.substring(0, 8) + "-" + traceId.substring(8);
    return String.format("{\"traceId\": \"%s\"}", xrayTraceId);
  }
}
