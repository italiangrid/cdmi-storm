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

  /**
   * Constructor.
   * 
   * @param hostname The StoRM Back-end host-name.
   * @param port The port where REST service is listening.
   * @param token The token needed to authenticate.
   */
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
