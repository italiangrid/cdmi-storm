package it.grid.storm.cdmi.auth.impl;

import com.google.common.base.Preconditions;

import it.grid.storm.cdmi.auth.AuthorizationException;
import it.grid.storm.cdmi.auth.AuthorizationManager;
import it.grid.storm.cdmi.auth.User;
import it.grid.storm.cdmi.config.VirtualOrganization;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class DefaultAuthorizationManager implements AuthorizationManager {

  private static final Logger log = LoggerFactory.getLogger(DefaultAuthorizationManager.class);

  private List<VirtualOrganization> vos;

  public DefaultAuthorizationManager(List<VirtualOrganization> vos) {

    this.vos = vos;
  }

  @Override
  public void canRead(User u, String path) throws AuthorizationException, IOException {

    Preconditions.checkArgument(u != null, "Invalid null User");
    Preconditions.checkArgument(path != null, "Invalid null path");

    if (u.hasAuthority(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
      return;
    }

    VirtualOrganization vo = getVirtualOrganizationFromPath(path);

    if (u.hasScope(vo.getReadScope())) {
      return;
    }
    if (u.hasGroup(vo.getIamGroup())) {
      return;
    }

    throw new AuthorizationException(
        "Missing scope " + vo.getReadScope() + " or group " + vo.getIamGroup());
  }

  @Override
  public void canRecall(User u, String path) throws AuthorizationException, IOException {

    Preconditions.checkArgument(u != null, "Invalid null User");
    Preconditions.checkArgument(path != null, "Invalid null path");

    if (u.hasAuthority(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
      return;
    }

    VirtualOrganization vo = getVirtualOrganizationFromPath(path);

    if (u.hasScope(vo.getRecallScope())) {
      return;
    }
    if (u.hasGroup(vo.getIamGroup())) {
      return;
    }

    throw new AuthorizationException(
        "Missing scope " + vo.getRecallScope() + " or group " + vo.getIamGroup());
  }

  private VirtualOrganization getVirtualOrganizationFromPath(String path)
      throws AuthorizationException, IOException {

    log.debug("Extract virtual organization from path {} ...", path);

    VirtualOrganization vo = PathUtils.getVirtualOrganizationFromPath(vos, path);

    if (vo == null) {
      throw new AuthorizationException("path not supported by the configured vos");
    }
    log.debug("Found Virtual Organization: {}", vo);
    return vo;
  }

}
