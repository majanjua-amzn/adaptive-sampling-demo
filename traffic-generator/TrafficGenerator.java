import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TrafficGenerator {

    // Set the base URL of your server (update this if needed)
    private static final String TARGET_URL = "http://localhost:8080/ping_service_a";

    public static void main(String[] args) {
        int requestCount = 0;

        // Infinite loop to keep sending traffic
        while (true) {
            try {
                URL url = new URL(TARGET_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                System.out.println("Request #" + (++requestCount) + " - Response Code: " + responseCode);

                // Optional: Read response (can be commented out if not needed)
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Optional: Print response
                // System.out.println("Response: " + response.toString());

                // Wait between requests (e.g., 1 second)
                Thread.sleep(1000);
            } catch (Exception e) {
                System.err.println("Error during request: " + e.getMessage());
                try {
                    Thread.sleep(2000); // wait before retrying in case of error
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}