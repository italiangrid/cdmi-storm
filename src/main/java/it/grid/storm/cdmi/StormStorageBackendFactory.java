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

  public static final String defaultPropertiesFile = "/etc/cdmi-server/plugins/storm-properties.json";
  public static final String defaultCapabilitiesJsonFile = "/etc/cdmi-server/plugins/storm-capabilities.json";

  private PluginConfiguration config;
  private StormCapabilities capabilities = null;

  public StormStorageBackendFactory() {
    this(defaultPropertiesFile, defaultCapabilitiesJsonFile);
  }

  public StormStorageBackendFactory(String configFilePath, String capabilitiesFilePath)
      throws IllegalArgumentException {

    Preconditions.checkArgument(configFilePath != null,
        defaultPropertiesFile + " path is null");
    Preconditions.checkArgument(capabilitiesFilePath != null,
        defaultCapabilitiesJsonFile + " path is null");

    try {

      config = loadObjectFromJsonFile(configFilePath, PluginConfiguration.class);
      capabilities = loadObjectFromJsonFile(capabilitiesFilePath, StormCapabilities.class);

    } catch (IOException e) {

      log.error(e.getMessage());
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  @Override
  public StorageBackend createStorageBackend(Map<String, String> args) {

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
