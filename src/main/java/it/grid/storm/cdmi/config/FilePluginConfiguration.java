package it.grid.storm.cdmi.config;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilePluginConfiguration implements PluginConfiguration {

  private static final Logger log = LoggerFactory.getLogger(FilePluginConfiguration.class);

  private String hostname;
  private int port;
  private String token;

  public FilePluginConfiguration(String absoluteFilePath) throws ConfigurationException {

    log.info("Crating FileConfiguration for {}", absoluteFilePath);
    PluginConfiguration def = new DefaultPluginConfiguration();
    Properties prop = loadPropertiesFromFile(absoluteFilePath);
    hostname = prop.getProperty("storm.hostname", def.getHostname());
    if (prop.containsKey("storm.port")) {
      port = Integer.valueOf(prop.getProperty("storm.port"));
    } else {
      port = def.getPort();
    }
    token = prop.getProperty("storm.token", def.getToken());
    if (token.equals(def.getToken())) {
      log.warn("PLEASE define your own token instead of using the default one");
    }
  }

  private Properties loadPropertiesFromFile(String absoluteFilePath) throws ConfigurationException {

    Properties prop = new Properties();
    log.info("Loading properties from file {}", absoluteFilePath);
    try {
      prop.load(new FileInputStream(absoluteFilePath));
    } catch (Throwable e) {
      throw new ConfigurationException(e.getMessage(), e);
    }
    return prop;
  }

  @Override
  public String getHostname() {
    return hostname;
  }

  @Override
  public Integer getPort() {
    return port;
  }

  @Override
  public String getToken() {
    return token;
  }

  @Override
  public String toString() {
    return "FileConfiguration [hostname=" + hostname + ", port=" + port + ", token=" + token + "]";
  }

}
