package xyz.bubblefish.gideon.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Request {
    String text = "";
    String endpoint = "";
    String token = "";
    public Request(String endpoint, String token) {
        this.endpoint = endpoint;
        this.token = token;
    }


    public String getText() {
        try {
            URL url = new URL("https://gideonproject.herokuapp.com" + this.endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Token", this.token);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            this.text = content.toString();
            in.close();
            connection.disconnect();
            return this.text;
        } catch (IOException ignored) {}
        return "";
    }
}
