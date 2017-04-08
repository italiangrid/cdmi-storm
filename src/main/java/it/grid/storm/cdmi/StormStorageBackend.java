package it.grid.storm.cdmi;

import static it.grid.storm.cdmi.Utils.buildBackendCapabilities;
import static it.grid.storm.cdmi.Utils.loadObjectFromJsonFile;

import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FOLDER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.CONTAINER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.DATAOBJECT;

import com.google.common.base.Preconditions;

import it.grid.storm.cdmi.config.PluginConfiguration;
import it.grid.storm.cdmi.config.StormCapabilities;
import it.grid.storm.gateway.SimpleUser;
import it.grid.storm.gateway.StormBackendGateway;
import it.grid.storm.gateway.model.BackendGateway;
import it.grid.storm.rest.metadata.model.StoriMetadata;

import java.io.IOException;

import java.util.List;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.CdmiObjectStatus;
import org.indigo.cdmi.spi.StorageBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StormStorageBackend implements StorageBackend {

  private static final Logger log = LoggerFactory.getLogger(StormStorageBackend.class);

  private List<BackendCapability> backendCapabilities;
  private BackendGateway backendGateway;

  /**
   * Constructor.
   * 
   */
  public StormStorageBackend() {

    log.debug("Storm Storage Backend");

    Preconditions.checkArgument(System.getProperties().containsKey("storm.configFile"),
        "system property storm.configFile not found");
    Preconditions.checkArgument(System.getProperties().containsKey("storm.capabilitiesFile"),
        "system property storm.capabilitiesFile not found");

    String configFilePath = System.getProperty("storm.configFile");
    String capabilitiesFilePath = System.getProperty("storm.capabilitiesFile");

    PluginConfiguration config = null;
    try {
      config = loadObjectFromJsonFile(configFilePath, PluginConfiguration.class);
      StormCapabilities cap = loadObjectFromJsonFile(capabilitiesFilePath, StormCapabilities.class);
      backendCapabilities = buildBackendCapabilities(cap);
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new IllegalArgumentException(e.getMessage(), e);
    }

    backendGateway = new StormBackendGateway(config.getBackend().getHostname(),
        config.getBackend().getPort(), config.getBackend().getToken());
  }

  public StormStorageBackend(List<BackendCapability> backendCapabilities,
      BackendGateway backendGateway) {

    init(backendCapabilities, backendGateway);
  }

  private void init(List<BackendCapability> backendCapabilities, BackendGateway backendGateway) {

    Preconditions.checkNotNull(backendCapabilities, "Invalid null list of BackendCapability");
    Preconditions.checkNotNull(backendGateway, "Invalid null BackendGateway");
    this.backendCapabilities = backendCapabilities;
    this.backendGateway = backendGateway;
  }

  @Override
  public List<BackendCapability> getCapabilities() throws BackEndException {

    return backendCapabilities;
  }

  @Override
  public CdmiObjectStatus getCurrentStatus(String path) throws BackEndException {

    StoriMetadata meta = backendGateway.getStoriMetadata(new SimpleUser("cdmi"), path);
    log.info("StoRIMetadata: {}", meta);

    BackendCapability cap = getBackendCapability(meta);
    log.debug("BackendCapability: {}", cap);

    String currentCapabilitiesUri = getCapabilityUri(cap);
    String targetCapabilitiesUri = getTargetCapabilityUri(cap, meta);

    CdmiObjectStatus currentStatus =
        new CdmiObjectStatus(cap.getCapabilities(), currentCapabilitiesUri, targetCapabilitiesUri);

    currentStatus.setChildren(meta.getChildren());

    return currentStatus;
  }

  @Override
  public void updateCdmiObject(String path, String targetCapabilitiesUri) throws BackEndException {

    CdmiObjectStatus objectStatus = getCurrentStatus(path);

    if (objectStatus.getTargetCapabilitiesUri() != null) {
      log.debug("object {} already in transition to {}", path,
          objectStatus.getTargetCapabilitiesUri());
      return;
    }
    log.debug("current object status {}", objectStatus.toString());

    throw new BackEndException("Not implemented");
  }

  private BackendCapability getBackendCapability(StoriMetadata metadata) {

    if (metadata.getType().equals(FOLDER)) {
      return backendCapabilities.stream()
          .filter(cap -> cap.getType().equals(CONTAINER) && cap.getName().equals("Disk"))
          .findFirst().get();
    }
    if (metadata.getStatus().equals(ONLINE)) {
      if (metadata.getAttributes().getMigrated()) {
        return backendCapabilities.stream()
            .filter(cap -> cap.getType().equals(DATAOBJECT) && cap.getName().equals("DiskAndTape"))
            .findFirst().get();
      }
      return backendCapabilities.stream()
          .filter(cap -> cap.getType().equals(DATAOBJECT) && cap.getName().equals("Disk"))
          .findFirst().get();
    }
    return backendCapabilities.stream()
        .filter(cap -> cap.getType().equals(DATAOBJECT) && cap.getName().equals("Tape")).findFirst()
        .get();
  }

  private String getTargetCapabilityUri(BackendCapability cap, StoriMetadata metadata) {

    if (cap.getType().equals(CONTAINER)) {
      return null;
    }
    if (!cap.getName().equals("Tape")) {
      return null;
    }
    String recTasks = metadata.getAttributes().getTsmRecT();
    if (recTasks != null && !recTasks.isEmpty()) {
      return "/cdmi_capabilities/dataobject/DiskAndTape";
    }
    return null;
  }

  private String getCapabilityUri(BackendCapability cap) {

    return "/cdmi_capabilities/" + cap.getType().name().toLowerCase() + "/" + cap.getName();
  }
}
