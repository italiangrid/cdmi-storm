package it.grid.storm.cdmi.auth;

import it.grid.storm.cdmi.config.VirtualFileSystem;

import java.io.IOException;

public interface AuthorizationManager {

  public void canRead(User user, VirtualFileSystem vo) throws AuthorizationException, IOException;

  public void canRecall(User user, VirtualFileSystem vo)
      throws AuthorizationException, IOException;

}
