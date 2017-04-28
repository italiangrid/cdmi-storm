package it.grid.storm.cdmi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class PluginConfiguration {

  private BackendConfiguration backend;
  private Organization organization;

  /**
   * Constructor.
   * 
   * @param backend The @BackendConfiguration info.
   * @param organization Info about the supported @Organization.
   */
  @JsonCreator
  public PluginConfiguration(@JsonProperty("backend") BackendConfiguration backend,
      @JsonProperty("organization") Organization organization) {

    this.backend = backend;
    this.organization = organization;
  }

  public BackendConfiguration getBackend() {
    return backend;
  }

  public Organization getOrganization() {
    return organization;
  }

  @Override
  public String toString() {
    return "PluginConfiguration [backend=" + backend + ", organization=" + organization + "]";
  }

}
