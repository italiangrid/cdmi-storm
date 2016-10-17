package it.grid.storm.cdmi.backend;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.indigo.cdmi.spi.StorageBackend;
import org.indigo.cdmi.spi.StorageBackendFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.cdmi.backend.storm.impl.StormBackendGateway;
import it.grid.storm.cdmi.backend.storm.impl.StormBackendResponseTranslator;


public class StormStorageBackendFactory implements StorageBackendFactory {

  private static final Logger log = LoggerFactory.getLogger(StormStorageBackendFactory.class);

  private final String propertiesFilePath = "storm-cdmi-spi.properties";
  private final String type = "storm";
  private final String description = "StoRM Storage Backend CDMI module";

  private Properties configuration;
  private StormBackendGateway gateway;

  public StormStorageBackendFactory() {

    configuration = loadPropertiesFromFile(propertiesFilePath);

    log.debug("Loaded configuration {}", configuration);

    gateway = new StormBackendGateway(configuration.getProperty("storm.restapi.endpoint.hostname"),
        Integer.valueOf(configuration.getProperty("storm.restapi.endpoint.port")),
        configuration.getProperty("storm.restapi.authentication.user.name"),
        configuration.getProperty("storm.restapi.authentication.user.password"));
  }

  public StorageBackend createStorageBackend(Map<String, String> arg0)
      throws IllegalArgumentException {

    return new StormStorageBackend(gateway, new StormBackendResponseTranslator());
  }

  public String getDescription() {

    return description;
  }

  public String getType() {

    return type;
  }

  private Properties loadPropertiesFromFile(String propertiesFilePath) throws IllegalArgumentException {

    Properties c = new Properties();
    InputStream is = ClassLoader.getSystemResourceAsStream(propertiesFilePath);
    try {
      c.load(is);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Cannot load properties from " + propertiesFilePath, e);
    }
    return c;
  }

}
