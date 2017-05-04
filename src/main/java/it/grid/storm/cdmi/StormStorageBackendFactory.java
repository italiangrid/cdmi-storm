package it.grid.storm.cdmi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.indigo.cdmi.SubjectBasedStorageBackend;
import org.indigo.cdmi.spi.StorageBackend;
import org.indigo.cdmi.spi.StorageBackendFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.cdmi.config.ExportIdentifier;
import it.grid.storm.cdmi.config.PluginConfiguration;
import it.grid.storm.cdmi.config.StormBackendCapability;
import it.grid.storm.cdmi.config.StormBackendContainerCapability;
import it.grid.storm.cdmi.config.StormBackendDatobjectCapability;

public class StormStorageBackendFactory implements StorageBackendFactory {

  private static final Logger log = LoggerFactory.getLogger(StormStorageBackendFactory.class);

  public static final String type = "storm";
  public static final String description = "StoRM Storage Backend CDMI module";

  public static final String defaultPropertiesFile = "/etc/cdmi-server/plugins/storm-properties.json";
  public static final String defaultCapabilitiesDir = "/etc/cdmi-server/plugins/capabilities";

  private PluginConfiguration config;
  private List<StormBackendCapability> capabilities;
  private Map<String, Object> exports;

  public StormStorageBackendFactory() {
    this(defaultPropertiesFile, defaultCapabilitiesDir);
  }

  public StormStorageBackendFactory(String configFilePath, String capabilitiesDirPath)
      throws IllegalArgumentException {

    Preconditions.checkArgument(configFilePath != null,
        configFilePath + " path is null");
    Preconditions.checkArgument(capabilitiesDirPath != null,
        capabilitiesDirPath + " path is null");

    capabilities = Lists.newArrayList();

    try {

      ObjectMapper mapper = new ObjectMapper();

      config = mapper.readValue(new File(configFilePath), PluginConfiguration.class);
      capabilities.add(mapper.readValue(new File(capabilitiesDirPath + "/container/diskonly.json"), StormBackendContainerCapability.class));
      capabilities.add(mapper.readValue(new File(capabilitiesDirPath + "/dataobject/diskonly.json"), StormBackendDatobjectCapability.class));
      capabilities.add(mapper.readValue(new File(capabilitiesDirPath + "/dataobject/diskandtape.json"), StormBackendDatobjectCapability.class));
      capabilities.add(mapper.readValue(new File(capabilitiesDirPath + "/dataobject/tapeonly.json"), StormBackendDatobjectCapability.class));
      exports = mapper.readValue(new File(capabilitiesDirPath + "/exports.json"), new TypeReference<Map<String, ExportIdentifier>>(){});

    } catch (IOException e) {

      log.error(e.getMessage());
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  @Override
  public StorageBackend createStorageBackend(Map<String, String> args) {

    return new SubjectBasedStorageBackend(new StormStorageBackend(config, capabilities, exports));
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
