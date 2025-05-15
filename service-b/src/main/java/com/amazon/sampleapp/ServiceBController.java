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
import io.opentelemetry.api.trace.Span;

import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;


import java.util.concurrent.atomic.AtomicReference;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;




@Controller
public class ServiceBController {
  private static final Logger logger = LoggerFactory.getLogger(ServiceBController.class);

  private final AtomicReference<String> currentPassword = new AtomicReference<>("");

  @Autowired
  private AsyncManager asyncManager;

  @GetMapping("/healthcheck")
  @ResponseBody
  public String healthcheck() {
    return "Remote service healthcheck";
  }

  // Uses the /mysql endpoint to make an SQL call
  @GetMapping("/service_b_async_endpoint")
  @ResponseBody
  public CompletableFuture<String> service_b_async_endpoint() {
    return asyncManager.connectToDatabase();
  }

  // @GetMapping("/cluster_password")
  // @ResponseBody
  // public String clusterPassword() {
  //     return getClusterPassword()
  // }

  // private String getClusterPassword() {
  //   try {
  //       java.net.URL url = new java.net.URL("http://localhost:8081/cluster_password");
  //       java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
  //       conn.setRequestMethod("GET");

  //       try (java.io.BufferedReader in = new java.io.BufferedReader(
  //               new java.io.InputStreamReader(conn.getInputStream()))) {
  //           String inputLine;
  //           StringBuilder content = new StringBuilder();
  //           while ((inputLine = in.readLine()) != null) {
  //               content.append(inputLine);
  //           }
  //           return content.toString();
  //       }
  //   } catch (Exception e) {
  //       logger.error("Failed to fetch cluster password from internal endpoint: {}", e.getMessage());
  //       throw new RuntimeException(e);
  //   }
  // }

  // // Every 10 minutes, hide the password for 10 seconds
  // @Scheduled(fixedRate = 60 * 1000)
  // public void hidePasswordTemporarily() {
  //     currentPassword.set("Fake Password");
  //     logger.info("passport being changed");

  //     new Thread(() -> {
  //         try {
  //             Thread.sleep(10 * 1000); // 10 seconds
  //             currentPassword.set(rdsPassword);
  //             System.out.println("Password restored");
  //         } catch (InterruptedException ignored) {
  //         }
  //     }).start();
  // }

  // get x-ray trace id
  private String getXrayTraceId() {
    String traceId = Span.current().getSpanContext().getTraceId();
    String xrayTraceId = "1-" + traceId.substring(0, 8) + "-" + traceId.substring(8);
    return String.format("{\"traceId\": \"%s\"}", xrayTraceId);
  }
}
