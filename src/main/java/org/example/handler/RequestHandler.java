package org.example.handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.example.core.Server;
import org.example.net.BackendConnection;

public class RequestHandler implements Runnable {

  private final Socket clientSocket;
  private final Server backendServer;

  public RequestHandler(Socket clientSocket, Server backendServer) {
    this.clientSocket = clientSocket;
    this.backendServer = backendServer;
  }

  @Override
  public void run() {
    try (InputStream clientIn = clientSocket.getInputStream();
        OutputStream clientOut = clientSocket.getOutputStream()) {
      System.out.println("Received request from " + clientSocket.getInetAddress());

      BackendConnection backendConnection = new BackendConnection(backendServer);
      backendConnection.forward(clientIn, clientOut);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        clientSocket.close();
      } catch (Exception ignored) {
      }
    }
  }
}
