package it.grid.storm.cdmi.auth;

import it.grid.storm.cdmi.config.VirtualOrganization;

import java.io.IOException;

public interface AuthorizationManager {

  public void canRead(User u, VirtualOrganization vo) throws AuthorizationException, IOException;

  public void canRecall(User u, VirtualOrganization vo) throws AuthorizationException, IOException;

}
