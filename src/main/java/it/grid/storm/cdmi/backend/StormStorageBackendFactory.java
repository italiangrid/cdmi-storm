package it.grid.storm.cdmi.backend;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.indigo.cdmi.spi.StorageBackend;
import org.indigo.cdmi.spi.StorageBackendFactory;

import it.grid.storm.cdmi.backend.storm.BackendGateway;
import it.grid.storm.cdmi.backend.storm.ResponseConverter;
import it.grid.storm.cdmi.backend.storm.impl.StormBackendGateway;
import it.grid.storm.cdmi.backend.storm.impl.StormBackendResponseConverter;


public class StormStorageBackendFactory implements StorageBackendFactory {

  private final String propertiesFilePath = "storm-cdmi-spi.properties";
  public static final String type = "storm";
  public static final String description = "StoRM Storage Backend CDMI module";

  private Properties configuration = new Properties();
  private BackendGateway gateway;
  private ResponseConverter converter;

  public StormStorageBackendFactory(BackendGateway gateway, ResponseConverter converter) {

    setGateway(gateway);
    setTranslator(converter);
  }

  public StormStorageBackendFactory() {

    loadPropertiesFromFile(propertiesFilePath);
    setGateway(new StormBackendGateway(configuration.getProperty("storm.restapi.endpoint.hostname"),
        Integer.valueOf(configuration.getProperty("storm.restapi.endpoint.port")),
        configuration.getProperty("storm.restapi.authentication.user.name"),
        configuration.getProperty("storm.restapi.authentication.user.password")));
    setTranslator(new StormBackendResponseConverter());
  }

  public StorageBackend createStorageBackend(Map<String, String> arg0)
      throws IllegalArgumentException {

    return new StormStorageBackend(gateway, converter);
  }

  public String getDescription() {

    return description;
  }

  public String getType() {

    return type;
  }

  private void setGateway(BackendGateway gateway) {

    this.gateway = gateway;
  }

  private void setTranslator(ResponseConverter translator) {

    this.converter = translator;
  }

  private void loadPropertiesFromFile(String propertiesFilePath) throws IllegalArgumentException {

    InputStream is = getClass().getClassLoader().getResourceAsStream(propertiesFilePath);
    if (is == null) {
      throw new RuntimeException("Failed to find required config file on CLASSPATH. "
          + "Could not open " + propertiesFilePath);
    }

    try {
      configuration.load(is);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Cannot load properties from " + propertiesFilePath, e);
    }
  }

}
