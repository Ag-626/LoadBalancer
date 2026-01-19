package org.example.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.config.ConfigLoader;
import org.example.handler.RequestHandler;

public class LoadBalancer {
  private static AtomicInteger index = new AtomicInteger(0);

  private static Server getNextServer(List<Server> servers) {
    return servers.get(index.getAndUpdate(i -> (i + 1) % servers.size()));
  }

  public static void main(String[] args) {
    ConfigLoader config = new ConfigLoader("lb-config.properties");

    int lbPort = config.getLbPort();
    List<Server> backendServers = config.getBackendServers();


    System.out.println("Load Balance listening on port: " + lbPort);
    System.out.println("Backend Servers " + backendServers);

    try (ServerSocket serverSocket = new ServerSocket(lbPort)) {

      while (true) {
        Socket clienSocket = serverSocket.accept();
        Server selectedServer = getNextServer(backendServers);
        System.out.println("Forwarding to backend: " + selectedServer);
        Thread thread = new Thread(new RequestHandler(clienSocket, selectedServer));
        thread.start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
