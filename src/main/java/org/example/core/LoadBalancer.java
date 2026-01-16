package org.example.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import org.example.config.ConfigLoader;
import org.example.handler.RequestHandler;

public class LoadBalancer {
  public static void main(String[] args) {
    ConfigLoader config = new ConfigLoader("lb-config.properties");

    int lbPort = config.getLbPort();
    List<Server> backendServers = config.getBackendServers();

    Server backend = backendServers.get(0);

    System.out.println("Load Balance listening on port: " + lbPort);
    System.out.println("Forwarding to backend: " + backend);

    try (ServerSocket serverSocket = new ServerSocket(lbPort)) {

      while (true) {
        Socket clienSocket = serverSocket.accept();
        Thread thread = new Thread(new RequestHandler(clienSocket, backend));
        thread.start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
