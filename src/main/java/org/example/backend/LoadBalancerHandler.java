package org.example.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.example.core.LoadBalancer;

public class LoadBalancerHandler implements Runnable {

  private final Socket loadBalancerSocket;
  private final int port;

  public LoadBalancerHandler(Socket loadBalancerSocket, int port) {
    this.loadBalancerSocket = loadBalancerSocket;
    this.port = port;
  }

  @Override
  public void run() {
    try (
        InputStream in = loadBalancerSocket.getInputStream();
        OutputStream out = loadBalancerSocket.getOutputStream();
        ){
      streamData(in);
      String msg = "Hello, this is backend server listening on port " + port;

      byte[] body = msg.getBytes(StandardCharsets.UTF_8);

      String headers =
          "HTTP/1.1 200 OK\r\n" +
              "Content-Type: text/plain\r\n" +
              "Content-Length: " + body.length + "\r\n" +
              "\r\n";

      out.write(headers.getBytes(StandardCharsets.UTF_8));
      out.write(body);
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        loadBalancerSocket.close();
      } catch (Exception ignored) {}
    }
  }

  private void streamData(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader((in)));
    String line;
    while(!(line = reader.readLine()).isEmpty()) {
      System.out.println("Request: " + line);
    }

  }


}
