package it.grid.storm.cdmi;

import static it.grid.storm.cdmi.Utils.loadObjectFromJsonFile;

import com.google.common.base.Preconditions;

import it.grid.storm.cdmi.config.PluginConfiguration;
import it.grid.storm.cdmi.config.StormCapabilities;

import java.io.IOException;
import java.util.Map;

import org.indigo.cdmi.SubjectBasedStorageBackend;
import org.indigo.cdmi.spi.StorageBackend;
import org.indigo.cdmi.spi.StorageBackendFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StormStorageBackendFactory implements StorageBackendFactory {

  private static final Logger log = LoggerFactory.getLogger(StormStorageBackendFactory.class);

  public static final String type = "storm";
  public static final String description = "StoRM Storage Backend CDMI module";

  @Override
  public StorageBackend createStorageBackend(Map<String, String> args)
      throws IllegalArgumentException {

    log.debug("Storm Storage Backend Factory");

    Preconditions.checkArgument(System.getProperties().containsKey("storm.configFile"),
        "system property storm.configFile not found");
    Preconditions.checkArgument(System.getProperties().containsKey("storm.capabilitiesFile"),
        "system property storm.capabilitiesFile not found");

    String configFilePath = System.getProperty("storm.configFile");
    String capabilitiesFilePath = System.getProperty("storm.capabilitiesFile");

    PluginConfiguration config = null;
    StormCapabilities capabilities = null;

    try {

      config = loadObjectFromJsonFile(configFilePath, PluginConfiguration.class);
      capabilities = loadObjectFromJsonFile(capabilitiesFilePath, StormCapabilities.class);

    } catch (IOException e) {

      log.error(e.getMessage());
      throw new IllegalArgumentException(e.getMessage(), e);
    }

    return new SubjectBasedStorageBackend(new StormStorageBackend(config, capabilities));
  }

  @Override
  public String getDescription() {

    return description;
  }

  @Override
  public String getType() {

    return type;
  }
}
