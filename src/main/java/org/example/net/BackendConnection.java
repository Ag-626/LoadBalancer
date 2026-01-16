package org.example.net;

import org.example.core.Server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class BackendConnection {

  private final Server server;

  public BackendConnection(Server server) {
    this.server = server;
  }

  public void forward(InputStream clientIn, OutputStream clientOut) throws IOException {

    try (Socket backendSocket = new Socket(server.getHost(), server.getPort())) {

      backendSocket.setSoTimeout(5000); // prevent infinite hanging

      InputStream backendIn = backendSocket.getInputStream();
      OutputStream backendOut = backendSocket.getOutputStream();

      // 1️⃣ Read request headers from client
      String headers = readHeaders(clientIn);

      // 2️⃣ Write headers to backend
      backendOut.write(headers.getBytes(StandardCharsets.UTF_8));
      backendOut.flush();

      // 3️⃣ If request has body, forward exactly Content-Length bytes
      int contentLength = getContentLength(headers);
      if (contentLength > 0) {
        byte[] body = clientIn.readNBytes(contentLength);
        backendOut.write(body);
        backendOut.flush();
      }

      // 4️⃣ Now forward backend response back to client
      relay(backendIn, clientOut);
    }
  }

  /** Read HTTP headers until a blank line */
  private String readHeaders(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    StringBuilder sb = new StringBuilder();

    String line;
    while ((line = reader.readLine()) != null) {
      sb.append(line).append("\r\n");
      if (line.isEmpty()) break;  // headers finished
    }

    return sb.toString();
  }

  /** Extract Content-Length header */
  private int getContentLength(String headers) {
    for (String line : headers.split("\r\n")) {
      if (line.toLowerCase().startsWith("content-length:")) {
        return Integer.parseInt(line.split(":")[1].trim());
      }
    }
    return 0;
  }

  /** Relay backend response to client */
  private void relay(InputStream backendIn, OutputStream clientOut) throws IOException {
    byte[] buffer = new byte[8192];
    int bytesRead;

    while ((bytesRead = backendIn.read(buffer)) != -1) {
      clientOut.write(buffer, 0, bytesRead);
      clientOut.flush();
    }
  }
}