package it.grid.storm.cdmi.capability.impl;

import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FOLDER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.CONTAINER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.DATAOBJECT;

import it.grid.storm.cdmi.capability.CapabilityManager;
import it.grid.storm.rest.metadata.model.StoriMetadata;

import java.util.List;

import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.BackendCapability.CapabilityType;

public class DefaultCapabilityManager implements CapabilityManager<StoriMetadata> {

  private static final String BASE_CONTAINER = "/cdmi_capabilities/container";
  private static final String BASE_DATAOBJECT = "/cdmi_capabilities/dataobject";

  private List<BackendCapability> capabilities;

  public DefaultCapabilityManager(List<BackendCapability> capabilities) {

    this.capabilities = capabilities;
  }

  @Override
  public BackendCapability getBackendCapability(CapabilityType capType, String capName) {

    return capabilities.stream().filter(cap -> cap.getType().equals(capType)
        && cap.getName().toUpperCase().equals(capName.toUpperCase())).findFirst().get();
  }

  @Override
  public BackendCapability getBackendCapability(StoriMetadata metadata) {

    if (metadata.getType().equals(FOLDER)) {
      return getBackendCapability(CONTAINER, "DiskOnly");
    }
    if (metadata.getStatus().equals(ONLINE)) {
      if (metadata.getAttributes().getMigrated()) {
        return getBackendCapability(DATAOBJECT, "DiskAndTape");
      }
      return getBackendCapability(DATAOBJECT, "DiskOnly");
    }
    return getBackendCapability(DATAOBJECT, "TapeOnly");
  }

  @Override
  public String getTargetCapabilityUri(BackendCapability cap, StoriMetadata metadata) {

    if (cap.getType().equals(DATAOBJECT) && cap.getName().equals("TapeOnly")) {
      String recTasks = metadata.getAttributes().getTsmRecT();
      if (recTasks != null && !recTasks.isEmpty()) {
        return getCapabilityUri(DATAOBJECT, "DiskAndTape");
      }
    }
    return null;
  }

  @Override
  public String getCapabilityUri(CapabilityType type, String capName) {

    return buildCapabilityUri(type, capName);
  }

  /**
   * Build capability URI from capability type and name.
   * 
   * @param type The capability type.
   * @param capName The capability name.
   * @return The capability URI.
   */
  public static String buildCapabilityUri(CapabilityType type, String capName) {

    if (type.equals(DATAOBJECT)) {
      return BASE_DATAOBJECT + "/" + capName;
    }
    return BASE_CONTAINER + "/" + capName;
  }

  @Override
  public boolean isAllowedToMove(BackendCapability cap, String targetCapabilityUri) {

    if (cap.getMetadata().containsKey("cdmi_capabilities_allowed")) {
      Object values = cap.getMetadata().get("cdmi_capabilities_allowed");
      if (values instanceof List<?>) {
        List<?> targets = (List<?>) values;
        if (targets.contains(targetCapabilityUri)) {
          return true;
        }
      }
    }
    return false;
  }

}
