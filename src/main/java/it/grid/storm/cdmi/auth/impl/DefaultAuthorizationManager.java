package it.grid.storm.cdmi.auth.impl;

import com.google.common.base.Preconditions;

import it.grid.storm.cdmi.auth.AuthorizationException;
import it.grid.storm.cdmi.auth.AuthorizationManager;
import it.grid.storm.cdmi.auth.User;
import it.grid.storm.cdmi.config.VirtualOrganization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;

public class DefaultAuthorizationManager implements AuthorizationManager {

  private static final Logger log = LoggerFactory.getLogger(DefaultAuthorizationManager.class);

  @Override
  public void canRead(User user, VirtualOrganization vo)
      throws AuthorizationException, IOException {

    Preconditions.checkArgument(user != null, "Invalid null User");
    Preconditions.checkArgument(vo != null, "Invalid null Virtual Organization");

    log.debug("Checking if user {} can read vo {} ...", user.getUserId(), vo.getName());

    if (user.hasAuthority(new SimpleGrantedAuthority("ROLE_ADMIN"))) {

      log.debug("User is admin: ok.");
      return;
    }

    if (user.hasScope(vo.getReadScope())) {

      log.debug("User has read scope.");
      return;
    }

    if (user.hasGroup(vo.getIamGroup())) {

      log.debug("User is member of {}.", vo.getIamGroup());
      return;
    }

    throw new AuthorizationException(
        "Missing scope " + vo.getReadScope() + " or group " + vo.getIamGroup());
  }

  @Override
  public void canRecall(User user, VirtualOrganization vo)
      throws AuthorizationException, IOException {

    Preconditions.checkArgument(user != null, "Invalid null User");
    Preconditions.checkArgument(vo != null, "Invalid null Virtual Organization");

    log.debug("Checking if user {} can read vo {} ...", user.getUserId(), vo.getName());

    if (user.hasAuthority(new SimpleGrantedAuthority("ROLE_ADMIN"))) {

      log.debug("User is admin: ok.");
      return;
    }

    if (user.hasScope(vo.getRecallScope())) {

      log.debug("User has recall scope.");
      return;
    }

    if (user.hasGroup(vo.getIamGroup())) {

      log.debug("User is member of {}.", vo.getIamGroup());
      return;
    }

    throw new AuthorizationException(
        "Missing scope " + vo.getRecallScope() + " or group " + vo.getIamGroup());
  }

}
