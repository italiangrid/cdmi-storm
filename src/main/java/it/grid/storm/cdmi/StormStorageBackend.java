package it.grid.storm.cdmi;

import static it.grid.storm.cdmi.StormStorageBackend.ContainerClasses.FolderOnDisk;
import static it.grid.storm.cdmi.StormStorageBackend.DataobjectClasses.FileOnDisk;
import static it.grid.storm.cdmi.StormStorageBackend.DataobjectClasses.FileOnDiskAndTape;
import static it.grid.storm.cdmi.StormStorageBackend.DataobjectClasses.FileOnTape;
import static it.grid.storm.cdmi.Utils.buildBackendCapabilities;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FOLDER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.CONTAINER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.DATAOBJECT;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import it.grid.storm.cdmi.config.PluginConfiguration;
import it.grid.storm.cdmi.config.StormCapabilities;
import it.grid.storm.cdmi.config.VoConfiguration;
import it.grid.storm.gateway.SimpleUser;
import it.grid.storm.gateway.StormBackendGateway;
import it.grid.storm.gateway.model.BackendGateway;
import it.grid.storm.rest.metadata.model.StoriMetadata;

import java.util.List;
import java.util.Map;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.CdmiObjectStatus;
import org.indigo.cdmi.spi.StorageBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StormStorageBackend implements StorageBackend {

  private static final Logger log = LoggerFactory.getLogger(StormStorageBackend.class);

  public static final String BASE_CONTAINER = "/cdmi_capabilities/container";
  public static final String BASE_DATAOBJECT = "/cdmi_capabilities/dataobject";

  public static enum ContainerClasses {
    FolderOnDisk
  }

  public static enum DataobjectClasses {
    FileOnDisk, FileOnDiskAndTape, FileOnTape
  }

  private List<BackendCapability> backendCapabilities;
  private BackendGateway backendGateway;
  private Map<String, Object> containerCapabilities;
  private List<VoConfiguration> vos;

  /**
   * Constructor.
   * 
   */
  public StormStorageBackend(PluginConfiguration config, StormCapabilities capabilities) {

    Preconditions.checkNotNull(config, "Invalid null plugin configuration");
    Preconditions.checkNotNull(capabilities, "Invalid null capabilities configuration");
    containerCapabilities = capabilities.getContainerCapabilities();
    vos = config.getVos();
    backendCapabilities = buildBackendCapabilities(capabilities);
    backendGateway = new StormBackendGateway(config.getBackend().getHostname(),
        config.getBackend().getPort(), config.getBackend().getToken());
  }

  /**
   * Set the @BackendGateway to use to contact StoRM Back-end.
   * 
   * @param backendGateway A valid @BackendGateway implementation.
   */
  public void setBackendGateway(BackendGateway backendGateway) {

    Preconditions.checkNotNull(backendGateway, "Invalid null backend gateway");
    this.backendGateway = backendGateway;
  }

  @Override
  public List<BackendCapability> getCapabilities() throws BackEndException {

    return backendCapabilities;
  }

  @Override
  public CdmiObjectStatus getCurrentStatus(String path) throws BackEndException {

    if (isRootPath(path)) {

      log.debug("Root path requested ...");
      return getRootCdmiObjectStatus();
    }

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

  private boolean isRootPath(String path) {

    return path.isEmpty() || path.equals("/");
  }

  private CdmiObjectStatus getRootCdmiObjectStatus() {

    String currentCapabilitiesUri = BASE_CONTAINER + "/" + FolderOnDisk;
    CdmiObjectStatus currentStatus =
        new CdmiObjectStatus(containerCapabilities, currentCapabilitiesUri, null);
    List<String> children = Lists.newArrayList();
    for (VoConfiguration vo : vos) {
      for (String rootStfn : vo.getRoots()) {
        children.add(rootStfn);
      }
    }
    currentStatus.setChildren(children);
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
      return backendCapabilities.stream().filter(
          cap -> cap.getType().equals(CONTAINER) && cap.getName().equals(FolderOnDisk.toString()))
          .findFirst().get();
    }
    if (metadata.getStatus().equals(ONLINE)) {
      if (metadata.getAttributes().getMigrated()) {
        return backendCapabilities.stream().filter(cap -> cap.getType().equals(DATAOBJECT)
            && cap.getName().equals(FileOnDiskAndTape.toString())).findFirst().get();
      }
      return backendCapabilities.stream().filter(
          cap -> cap.getType().equals(DATAOBJECT) && cap.getName().equals(FileOnDisk.toString()))
          .findFirst().get();
    }
    return backendCapabilities.stream()
        .filter(
            cap -> cap.getType().equals(DATAOBJECT) && cap.getName().equals(FileOnTape.toString()))
        .findFirst().get();
  }

  private String getTargetCapabilityUri(BackendCapability cap, StoriMetadata metadata) {

    if (cap.getType().equals(DATAOBJECT)) {
      if (cap.getName().equals(FileOnTape.toString())) {
        String recTasks = metadata.getAttributes().getTsmRecT();
        if (recTasks != null) {
          if (!recTasks.isEmpty()) {
            return BASE_DATAOBJECT + "/" + FileOnDiskAndTape;
          }
        }
      }
    }
    return null;
  }

  private String getCapabilityUri(BackendCapability cap) {

    if (cap.getType().equals(DATAOBJECT)) {
      return BASE_DATAOBJECT + "/" + DataobjectClasses.valueOf(cap.getName());
    }
    return BASE_CONTAINER + "/" + ContainerClasses.valueOf(cap.getName());
  }

}
