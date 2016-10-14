package it.grid.storm.cdmi.backend.storm.configuration;

public class Configuration {

  private Backend backend;
  private Auth auth;

  public Backend getBackend() {
    return backend;
  }

  public void setBackend(Backend backend) {
    this.backend = backend;
  }

  public Auth getAuth() {
    return auth;
  }

  public void setAuth(Auth auth) {
    this.auth = auth;
  }

  @Override
  public String toString() {
    return "Configuration [backend=" + backend + ", auth=" + auth + "]";
  }
}
