package it.grid.storm.cdmi.backend;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.indigo.cdmi.spi.StorageBackend;
import org.indigo.cdmi.spi.StorageBackendFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import it.grid.storm.cdmi.backend.storm.configuration.Configuration;
import it.grid.storm.cdmi.backend.storm.impl.StormBackendGateway;
import it.grid.storm.cdmi.backend.storm.impl.StormBackendResponseTranslator;


public class StormStorageBackendFactory implements StorageBackendFactory {

  private static final Logger log = LoggerFactory.getLogger(StormStorageBackendFactory.class);

  private final String configFilePath = "/application.yml";
  private final String type = "storm";
  private final String description = "StoRM Storage Backend CDMI module";

  private Configuration config;

  public StormStorageBackendFactory() {

    try {

      config = loadConfigurationFromFile(configFilePath);
      log.debug("Loaded configuration {}", config.toString());

    } catch (IOException e) {
      throw new IllegalArgumentException("Cannot load configuration from " + configFilePath, e);
    }
  }

  public StorageBackend createStorageBackend(Map<String, String> arg0)
      throws IllegalArgumentException {

    return new StormStorageBackend(
        new StormBackendGateway(config.getBackend(), config.getAuth().getUser()),
        new StormBackendResponseTranslator());
  }

  public String getDescription() {

    return description;
  }

  public String getType() {

    return type;
  }

  private Configuration loadConfigurationFromFile(String filePath) throws IOException {

    InputStream in = Files.newInputStream(Paths.get(filePath));
    return new Yaml().loadAs(in, Configuration.class);
  }


}
