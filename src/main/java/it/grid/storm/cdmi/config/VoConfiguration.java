package it.grid.storm.cdmi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(Include.NON_EMPTY)
public class VoConfiguration {

  private String name;
  private List<String> roots;

  /**
   * Constructor.
   * 
   * @param name Virtual File-system name.
   * @param roots The list of access points.
   */
  @JsonCreator
  public VoConfiguration(@JsonProperty("name") String name,
      @JsonProperty("roots") List<String> roots) {

    this.name = name;
    this.roots = roots;
  }

  public String getName() {
    return name;
  }

  public List<String> getRoots() {
    return roots;
  }

  @Override
  public String toString() {
    return "VOConfiguration [name=" + name + ", roots=" + roots + "]";
  }
}
