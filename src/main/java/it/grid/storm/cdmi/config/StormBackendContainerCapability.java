package it.grid.storm.cdmi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import org.indigo.cdmi.BackendCapability.CapabilityType;

@JsonInclude(Include.NON_NULL)
public class StormBackendContainerCapability extends StormBackendCapability {

  /**
   * Constructor.
   * 
   * @param capabilities List of enabled container capabilities.
   * @param metadata List of provided capabilities static values.
   */
  @JsonCreator
  public StormBackendContainerCapability(
      @JsonProperty("name") String name,
      @JsonProperty("capabilities") Map<String, Object> capabilities,
      @JsonProperty("metadata") Map<String, Object> metadata) {

    super(name, CapabilityType.CONTAINER, capabilities, metadata);
  }
}
