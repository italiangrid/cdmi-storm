package it.grid.storm.cdmi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(Include.NON_EMPTY)
public class PluginConfiguration {

  private BackendConfiguration backend;
  private List<VirtualOrganization> vos;

  /**
   * Constructor.
   * 
   * @param backend The @BackendConfiguration info.
   * @param vos Info about the supported @VirtualOrganization.
   */
  @JsonCreator
  public PluginConfiguration(@JsonProperty("backend") BackendConfiguration backend,
      @JsonProperty("vos") List<VirtualOrganization> vos) {

    this.backend = backend;
    this.vos = vos;
  }

  public BackendConfiguration getBackend() {
    return backend;
  }

  public List<VirtualOrganization> getVos() {
    return vos;
  }

  @Override
  public String toString() {
    return "PluginConfiguration [backend=" + backend + ", vos=" + vos + "]";
  }

}
