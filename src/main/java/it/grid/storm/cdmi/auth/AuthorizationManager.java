package it.grid.storm.cdmi.auth;

import it.grid.storm.cdmi.config.VirtualOrganization;

import java.io.IOException;

public interface AuthorizationManager {

  public void canRead(User user, VirtualOrganization vo) throws AuthorizationException, IOException;

  public void canRecall(User user, VirtualOrganization vo)
      throws AuthorizationException, IOException;

}
