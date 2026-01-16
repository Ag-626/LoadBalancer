package org.example.backend;

public class BackendLauncher {

  public static void main(String[] args) {
    int[] ports = {8080, 8081, 8082};

    for(int port : ports) {
      BackendServer server = new BackendServer(port);
      Thread thread = new Thread(server);
      thread.start();
    }

    System.out.println("Started backend servers on ports 8080, 8081, 8082");
  }

}
