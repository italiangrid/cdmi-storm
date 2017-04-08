package it.grid.storm.cdmi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class BackendConfiguration {

  private String hostname;
  private Integer port;
  private String token;

  @JsonCreator
  public BackendConfiguration(@JsonProperty("hostname") String hostname,
      @JsonProperty("port") Integer port, @JsonProperty("token") String token) {

    this.hostname = hostname;
    this.port = port;
    this.token = token;
  }

  public String getHostname() {
    return hostname;
  }

  public Integer getPort() {
    return port;
  }

  public String getToken() {
    return token;
  }

  @Override
  public String toString() {
    return "BackendConfiguration [hostname=" + hostname + ", port=" + port + ", token=" + token
        + "]";
  }

}
