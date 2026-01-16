package org.example.backend;

import java.net.ServerSocket;
import java.net.Socket;

public class BackendServer implements Runnable {

  private final int port;

  public BackendServer(int port){
    this.port = port;
  }

  @Override
  public void run() {
    System.out.println("Backend server running on port " + port);

    try (ServerSocket serverSocket = new ServerSocket(port)) {

      while(true){
        Socket loadBalancerSocket = serverSocket.accept();
        Thread thread = new Thread(new LoadBalancerHandler(loadBalancerSocket, port));
        thread.start();
      }
    } catch (Exception e){
      e.printStackTrace();
    }
  }
}
