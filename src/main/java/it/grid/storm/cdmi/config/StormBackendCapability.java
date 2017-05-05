package it.grid.storm.cdmi.config;

import java.util.Map;

import org.indigo.cdmi.BackendCapability.CapabilityType;

public class StormBackendCapability {

  private String name;
  private CapabilityType type;
  private Map<String, Object> capabilities;
  private Map<String, Object> metadata;

  /**
   * Constructor.
   * 
   * @param name Capability name.
   * @param type Capability type (container or data-object).
   * @param capabilities Set of enabled capabilities.
   * @param metadata Map of capabilities provided values.
   */
  public StormBackendCapability(String name, CapabilityType type, Map<String, Object> capabilities,
      Map<String, Object> metadata) {

    this.name = name;
    this.type = type;
    this.capabilities = capabilities;
    this.metadata = metadata;
  }

  public Map<String, Object> getCapabilities() {
    return capabilities;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public String getName() {
    return name;
  }

  public CapabilityType getType() {
    return type;
  }

}
