package com.amazon.sampleapp;

import io.opentelemetry.api.trace.Span;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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

	@GetMapping("/outgoing-http-call")
	@ResponseBody
	public String outgoingHttpCall() {
		logger.info("Service A requested trace");
    HttpGet request = new HttpGet("https://www.amazon.com");
    try (CloseableHttpResponse response = httpClient.execute(request)) {
      int statusCode = response.getStatusLine().getStatusCode();
      logger.info("outgoing-http-call status code: " + statusCode);
    } catch (Exception e) {
      logger.error("Could not complete HTTP request: {}", e.getMessage());
    }
    return getXrayTraceId();
	}

	@GetMapping("/api")
	@ResponseBody
	public ResponseEntity<String> pingServiceB(@RequestParam(name = "success", required = false) Boolean success) {
			if (success == null) {
					success = true;
			}
			try {
					HttpGet request = new HttpGet("http://localhost:8081/b?success=" + success.toString());
					try (CloseableHttpResponse response = httpClient.execute(request)) {
							BufferedReader reader = new BufferedReader(
											new InputStreamReader(response.getEntity().getContent()));
							
							StringBuilder result = new StringBuilder();
							String line;
							while ((line = reader.readLine()) != null) {
									result.append(line);
							}
							return ResponseEntity.ok("Returning OK - /b called with response: " + result.toString());
					}
			} catch (Exception e) {
					return ResponseEntity.ok("Returning OK - /b called with exception: " + e.getMessage());
			}
	}

	@GetMapping("/status/{code}")
	@ResponseBody
	public ResponseEntity<String> status(@PathVariable int code) {
			try {
					HttpGet request = new HttpGet("http://localhost:8081/status/" + code);
					httpClient.execute(request).close();
			} catch (Exception e) {
					// Ignore exception
			}
			logger.info("Service A requested status code {} from Service B", code);
			return ResponseEntity.ok().build();
	}

	@GetMapping("/status/c/{code}")
	@ResponseBody
	public ResponseEntity<String> statusC(@PathVariable int code) {
			try {
					HttpGet request = new HttpGet("http://localhost:8081/status/c/" + code);
					httpClient.execute(request).close();
			} catch (Exception e) {
					// Ignore exception
			}
			logger.info("Service A requested status code {} from Service C through Service B", code);
			return ResponseEntity.ok().build();
	}

  // get x-ray trace id
  private String getXrayTraceId() {
    String traceId = Span.current().getSpanContext().getTraceId();
    String xrayTraceId = "1-" + traceId.substring(0, 8) + "-" + traceId.substring(8);
    return String.format("{\"traceId\": \"%s\"}", xrayTraceId);
  }
}
