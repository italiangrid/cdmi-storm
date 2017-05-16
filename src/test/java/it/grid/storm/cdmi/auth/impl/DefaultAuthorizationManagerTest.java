package it.grid.storm.cdmi.auth.impl;

import static it.grid.storm.cdmi.auth.impl.AuthUtils.getToken;
import static it.grid.storm.cdmi.auth.impl.AuthUtils.roleAdmin;
import static it.grid.storm.cdmi.auth.impl.AuthUtils.roleUser;

import com.google.common.collect.Lists;

import it.grid.storm.cdmi.auth.AuthorizationException;
import it.grid.storm.cdmi.auth.AuthorizationManager;
import it.grid.storm.cdmi.config.VirtualOrganization;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class DefaultAuthorizationManagerTest {

  private final String voName = "test.vo";
  private final String voPath = "/test.vo";
  private final String voReadScope = "testvo:read";
  private final String voRecallScope = "testvo:recall";
  private final String voGroup = "test.vo-users";

  private final String id = "UserId";

  private VirtualOrganization vo;
  private AuthorizationManager authManager;

  @Before
  public void initOrganizations() throws IOException {

    vo = VirtualOrganization.builder().name(voName).path(voPath).readScope(voReadScope)
        .recallScope(voRecallScope).iamGroup(voGroup).build();
    authManager = new DefaultAuthorizationManager();
  }

  private IamUser getAuthorizedUserWithScopes() {

    String scopes = voReadScope + " " + voRecallScope;
    List<String> groups = Lists.newArrayList();
    List<GrantedAuthority> authorities = Lists.newArrayList(roleUser);

    UsernamePasswordAuthenticationToken token = getToken(id, scopes, groups, voName, authorities);
    return new IamUser(token);
  }

  private IamUser getAuthorizedUserWithGroup() {

    List<String> groups = Lists.newArrayList(voGroup);
    List<GrantedAuthority> authorities = Lists.newArrayList(roleUser);

    UsernamePasswordAuthenticationToken token = getToken(id, "", groups, voName, authorities);
    return new IamUser(token);
  }

  private IamUser getAuthorizedUserWithRoleAdmin() {

    List<String> groups = Lists.newArrayList();
    List<GrantedAuthority> authorities = Lists.newArrayList(roleAdmin);

    UsernamePasswordAuthenticationToken token = getToken(id, "", groups, voName, authorities);
    return new IamUser(token);
  }

  private IamUser getUnAuthorizedUser() {

    List<String> groups = Lists.newArrayList();
    List<GrantedAuthority> authorities = Lists.newArrayList(roleUser);

    UsernamePasswordAuthenticationToken token = getToken(id, "", groups, voName, authorities);
    return new IamUser(token);
  }

  @Test
  public void testReadSuccessWithScopes() throws AuthorizationException, IOException {

    authManager.canRead(getAuthorizedUserWithScopes(), vo);
  }

  @Test
  public void testReadSuccessWithGroup() throws AuthorizationException, IOException {

    authManager.canRead(getAuthorizedUserWithGroup(), vo);
  }

  @Test
  public void testReadSuccessWithRoleAdmin() throws AuthorizationException, IOException {

    authManager.canRead(getAuthorizedUserWithRoleAdmin(), vo);
  }

  @Test(expected = AuthorizationException.class)
  public void testReadNotAuthorized() throws IOException {

    authManager.canRead(getUnAuthorizedUser(), vo);
  }

  @Test
  public void testRecallSuccessWithScopes() throws AuthorizationException, IOException {

    authManager.canRecall(getAuthorizedUserWithScopes(), vo);
  }

  @Test
  public void testRecallSuccessWithGroup() throws AuthorizationException, IOException {

    authManager.canRecall(getAuthorizedUserWithGroup(), vo);
  }

  @Test
  public void testRecallSuccessWithRoleAdmin() throws AuthorizationException, IOException {

    authManager.canRecall(getAuthorizedUserWithRoleAdmin(), vo);
  }

  @Test(expected = AuthorizationException.class)
  public void testRecallNotAuthorized() throws IOException {

    authManager.canRecall(getUnAuthorizedUser(), vo);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReadNullUser() throws AuthorizationException, IOException {

    authManager.canRead(null, vo);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReadNullVo() throws AuthorizationException, IOException {

    authManager.canRead(getAuthorizedUserWithRoleAdmin(), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRecallNullUser() throws AuthorizationException, IOException {

    authManager.canRecall(null, vo);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRecallNullVo() throws AuthorizationException, IOException {

    authManager.canRecall(getAuthorizedUserWithRoleAdmin(), null);
  }
}
