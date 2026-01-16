package org.example.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.example.core.Server;

public class BackendConnection {

  private static final int BUFFER_SIZE = 8192;
  private final Server server;

  public BackendConnection(Server server) {
    this.server = server;
  }

  public void forward(InputStream clientIn, OutputStream clientOut) throws IOException {

    try (Socket backendSocket = new Socket(server.getHost(), server.getPort())) {

      InputStream backendIn = backendSocket.getInputStream();
      OutputStream backendOut = backendSocket.getOutputStream();

      streamData(clientIn, backendOut);
      backendOut.flush();

      streamData(backendIn, clientOut);
      clientOut.flush();
    }
  }

  private void streamData(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[BUFFER_SIZE];
    int bytesRead;

    while((bytesRead = in.read(buffer)) != -1) {
      out.write(buffer, 0, bytesRead);
    }
  }

}
