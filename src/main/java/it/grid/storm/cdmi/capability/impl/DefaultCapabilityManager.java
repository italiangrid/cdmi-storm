package it.grid.storm.cdmi.capability.impl;

import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FOLDER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.CONTAINER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.DATAOBJECT;

import it.grid.storm.cdmi.capability.CapabilityManager;
import it.grid.storm.rest.metadata.model.StoriMetadata;

import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.BackendCapability.CapabilityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
  public Optional<String> getTargetCapabilityUri(BackendCapability cap, StoriMetadata metadata) {

    if (cap.getType().equals(DATAOBJECT) && cap.getName().equals("TapeOnly")) {
      String recTasks = metadata.getAttributes().getTsmRecT();
      if (recTasks != null && !recTasks.isEmpty()) {
        return Optional.of(getCapabilityUri(DATAOBJECT, "DiskAndTape"));
      }
    }
    return Optional.empty();
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

    Map<String, Object> providedMetadata = getMetadataProvided(cap);
    if (providedMetadata.containsKey("cdmi_capabilities_allowed_provided")) {
      Object values = providedMetadata.get("cdmi_capabilities_allowed_provided");
      if (values instanceof List<?>) {
        List<?> targets = (List<?>) values;
        if (targets.contains(targetCapabilityUri)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public Map<String, Object> getMetadataProvided(BackendCapability cap) {

    Map<String, Object> out = new HashMap<String, Object>();
    for (String key: cap.getMetadata().keySet()) {
      out.put(key + "_provided", cap.getMetadata().get(key));
    }
    return out;
  }

}
