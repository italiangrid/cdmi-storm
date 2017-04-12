package it.grid.storm.cdmi;

import static it.grid.storm.cdmi.StormStorageBackend.CapabilityClasses.DiskAndTape;
import static it.grid.storm.cdmi.StormStorageBackend.CapabilityClasses.DiskOnly;
import static it.grid.storm.cdmi.StormStorageBackend.CapabilityClasses.TapeOnly;
import static it.grid.storm.cdmi.Utils.buildBackendCapabilities;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FOLDER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.CONTAINER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.DATAOBJECT;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.BackendCapability.CapabilityType;
import org.indigo.cdmi.CdmiObjectStatus;
import org.indigo.cdmi.spi.StorageBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.cdmi.config.PluginConfiguration;
import it.grid.storm.cdmi.config.StormCapabilities;
import it.grid.storm.cdmi.config.VoConfiguration;
import it.grid.storm.gateway.SimpleUser;
import it.grid.storm.gateway.StormBackendGateway;
import it.grid.storm.gateway.model.BackendGateway;
import it.grid.storm.gateway.model.BackendGatewayException;
import it.grid.storm.rest.metadata.model.StoriMetadata;


public class StormStorageBackend implements StorageBackend {

  private static final Logger log = LoggerFactory.getLogger(StormStorageBackend.class);

  private static final String BASE_CONTAINER = "/cdmi_capabilities/container";
  private static final String BASE_DATAOBJECT = "/cdmi_capabilities/dataobject";

  public static enum CapabilityClasses {
    DiskOnly, DiskAndTape, TapeOnly
  }

  private List<BackendCapability> backendCapabilities;
  private BackendGateway backendGateway;
  private Map<String, Object> containerCapabilities;
  private List<VoConfiguration> vos;
  private Map<String, Map<String, Object>> exportAttributes;

  /**
   * Constructor.
   * 
   */
  public StormStorageBackend(PluginConfiguration config, StormCapabilities capabilities) {

    Preconditions.checkNotNull(config, "Invalid null plugin configuration");
    Preconditions.checkNotNull(capabilities, "Invalid null capabilities configuration");
    containerCapabilities = capabilities.getContainerCapabilities();
    vos = config.getVos();
    exportAttributes = capabilities.getContainerExports();
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
    log.debug("StoRIMetadata: {}", meta);

    BackendCapability cap = getBackendCapability(meta);
    log.debug("BackendCapability: {}", cap);

    String currentCapabilitiesUri = getCapabilityUri(cap);
    String targetCapabilitiesUri = getTargetCapabilityUri(cap, meta);

    CdmiObjectStatus currentStatus =
        new CdmiObjectStatus(cap.getCapabilities(), currentCapabilitiesUri, targetCapabilitiesUri);

    currentStatus.setChildren(meta.getChildren());
    currentStatus.setExportAttributes(getExportAttributes(path));

    return currentStatus;
  }

  private boolean isRootPath(String path) {

    return path.isEmpty() || path.equals("/");
  }

  private CdmiObjectStatus getRootCdmiObjectStatus() {

    String currentCapabilitiesUri = getCapabilityUri(CONTAINER, DiskOnly);
    CdmiObjectStatus currentStatus =
        new CdmiObjectStatus(containerCapabilities, currentCapabilitiesUri, null);
    List<String> children = Lists.newArrayList();
    for (VoConfiguration vo : vos) {
      for (String rootStfn : vo.getRoots()) {
        children.add(rootStfn);
      }
    }
    currentStatus.setChildren(children);
    currentStatus.setExportAttributes(getExportAttributes("/"));
    return currentStatus;
  }

  private Map<String, Object> getExportAttributes(String path) {

    Map<String, Object> out = new HashMap<String, Object>();

    if (exportAttributes.containsKey("Network/WebDAV")) {
      Map<String, Object> info = new HashMap<String, Object>();
      String identifier =
          exportAttributes.get("Network/WebDAV").get("identifier").toString() + path;
      identifier = identifier.replaceAll("(?<!(http:|https:))[//]+", "/");
      info.put("identifier", identifier);
      info.put("permissions", exportAttributes.get("Network/WebDAV").get("permissions"));
      out.put("Network/WebDAV", info);
    }

    if (exportAttributes.containsKey("Network/SRM")) {
      Map<String, Object> info = new HashMap<String, Object>();
      String identifier = exportAttributes.get("Network/SRM").get("identifier").toString() + path;
      identifier = identifier.replaceAll("(?<!(srm:|httpg:))[//]+", "/");
      info.put("identifier", identifier);
      info.put("permissions", exportAttributes.get("Network/SRM").get("permissions"));
      out.put("Network/SRM", info);
    }

    return out;
  }

  @Override
  public void updateCdmiObject(String path, String targetCapabilitiesUri) throws BackEndException {

    log.info("Updating {} to QoS {} ... ", path, targetCapabilitiesUri);

    StoriMetadata meta = backendGateway.getStoriMetadata(new SimpleUser("cdmi"), path);
    log.debug("StoRIMetadata: {}", meta);

    BackendCapability cap = getBackendCapability(meta);
    log.debug("BackendCapability: {}", cap);

    if (cap.getType().equals(CONTAINER)) {

      log.debug("{} is a container and cannot change QoS");
      throw new BackEndException("Containers QoS cannot change");
    }

    String currentTargetCapabilitiesUri = getTargetCapabilityUri(cap, meta);

    if (currentTargetCapabilitiesUri != null) {

      if (currentTargetCapabilitiesUri.equals(targetCapabilitiesUri)) {

        log.info("{} is already in transition to {}", path, targetCapabilitiesUri);
        return;
      }
      throw new BackEndException("Already in transition to another capability");
    }

    if (!cap.getMetadata().containsKey("cdmi_capabilities_allowed")) {
      throw new BackEndException("No transitions allowed from current capability");
    }

    boolean isAllowed = false;
    List<String> caps = getCapabilitiesAllowed(cap);
    for (String target : caps) {
      if (target.equals(targetCapabilitiesUri)) {
        isAllowed = true;
        break;
      }
    }

    if (!isAllowed) {
      throw new BackEndException(
          "Transition to " + targetCapabilitiesUri + " not allowed from current QoS!");
    }

    try {
      backendGateway.addRecallTask(new SimpleUser("cdmi"), path);
    } catch (BackendGatewayException e) {
      throw new BackEndException(e.getMessage(), e);
    }

    log.info("Transition to {} started successful for {}", targetCapabilitiesUri, path);
  }

  private BackendCapability getBackendCapability(StoriMetadata metadata) {

    if (metadata.getType().equals(FOLDER)) {
      return backendCapabilities.stream()
          .filter(
              cap -> cap.getType().equals(CONTAINER) && cap.getName().equals(DiskOnly.toString()))
          .findFirst().get();
    }
    if (metadata.getStatus().equals(ONLINE)) {
      if (metadata.getAttributes().getMigrated()) {
        return backendCapabilities.stream().filter(
            cap -> cap.getType().equals(DATAOBJECT) && cap.getName().equals(DiskAndTape.toString()))
            .findFirst().get();
      }
      return backendCapabilities.stream()
          .filter(
              cap -> cap.getType().equals(DATAOBJECT) && cap.getName().equals(DiskOnly.toString()))
          .findFirst().get();
    }
    return backendCapabilities.stream()
        .filter(
            cap -> cap.getType().equals(DATAOBJECT) && cap.getName().equals(TapeOnly.toString()))
        .findFirst().get();
  }

  public static String getTargetCapabilityUri(BackendCapability cap, StoriMetadata metadata) {

    if (cap.getType().equals(DATAOBJECT)) {
      if (cap.getName().equals(TapeOnly.toString())) {
        String recTasks = metadata.getAttributes().getTsmRecT();
        if (recTasks != null) {
          if (!recTasks.isEmpty()) {
            return getCapabilityUri(DATAOBJECT, DiskAndTape);
          }
        }
      }
    }
    return null;
  }

  public static String getCapabilityUri(BackendCapability cap) {

    return getCapabilityUri(cap.getType(), CapabilityClasses.valueOf(cap.getName()));
  }

  public static String getCapabilityUri(CapabilityType type, CapabilityClasses name) {

    if (type.equals(DATAOBJECT)) {
      return BASE_DATAOBJECT + "/" + name.toString();
    }
    return BASE_CONTAINER + "/" + name.toString();
  }

  @SuppressWarnings("unchecked")
  public static List<String> getCapabilitiesAllowed(BackendCapability cap) throws BackEndException {

    List<String> caps = null;
    try {
      caps = (List<String>) cap.getMetadata().get("cdmi_capabilities_allowed");
    } catch (ClassCastException e) {
      throw new BackEndException("Unable to read cdmi_capabilities_allowed: not a List");
    }
    return caps;
  }

}
