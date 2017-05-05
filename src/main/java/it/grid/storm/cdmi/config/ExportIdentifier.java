package it.grid.storm.cdmi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class ExportIdentifier {

  private String identifier;
  private String permissions;

  /**
   * Constructor.
   * 
   * @param identifier Endpoint to contact.
   * @param permissions Credentials type to provide.
   */
  @JsonCreator
  public ExportIdentifier(
      @JsonProperty("identifier") String identifier,
      @JsonProperty("permissions") String permissions) {

    this.identifier = identifier;
    this.permissions = permissions;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getPermissions() {
    return permissions;
  }
}
