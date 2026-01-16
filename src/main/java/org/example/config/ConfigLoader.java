package org.example.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.example.core.Server;

public class ConfigLoader {

  private final Properties properties = new Properties();

  public ConfigLoader(String fileName){
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)){

      if(is == null){
        throw new RuntimeException("Configuration file not found: " + fileName);
      }
      properties.load(is);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load the config file: " + fileName, e);
    }
  }

  public int getLbPort() {
    return Integer.parseInt(properties.getProperty("lb.port"));
  }

  public List<Server> getBackendServers() {
    List<Server> servers = new ArrayList<>();
    String raw = properties.getProperty("backend.servers");

    if(raw == null || raw.isEmpty()) {
      throw new RuntimeException("backend servers not configured");
    }

    for(String entry : raw.split(",")) {
      String[] parts = entry.trim().split(":");
      servers.add(new Server(parts[0], Integer.parseInt((parts[1]))));
    }

    return servers;
  }
}
