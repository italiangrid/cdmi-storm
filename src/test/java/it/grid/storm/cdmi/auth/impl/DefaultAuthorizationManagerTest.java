package it.grid.storm.cdmi.auth.impl;

import static it.grid.storm.cdmi.auth.impl.AuthUtils.getToken;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import it.grid.storm.cdmi.auth.AuthorizationException;
import it.grid.storm.cdmi.auth.AuthorizationManager;
import it.grid.storm.cdmi.config.PluginConfiguration;
import it.grid.storm.cdmi.config.VirtualOrganization;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class DefaultAuthorizationManagerTest {

  private List<VirtualOrganization> vos;

  private List<VirtualOrganization> getVirtualOrganizations() throws IOException {

    ObjectMapper mapper = new ObjectMapper();
    String filePath = getClass().getClassLoader().getResource("storm-properties.json").getFile();
    PluginConfiguration conf = mapper.readValue(new File(filePath), PluginConfiguration.class);
    return conf.getVos();
  }

  @Before
  public void initOrganizations() throws IOException {

    vos = getVirtualOrganizations();
  }

  private IamUser getAuthorizedUserWithScopes() {

    String id = "UserId";
    String scopes = "testvo:read testvo:recall";
    List<String> groups = Lists.newArrayList();
    String voName = "test.vo";

    UsernamePasswordAuthenticationToken token = getToken(id, scopes, groups, voName);
    return new IamUser(token);
  }

  private IamUser getAuthorizedUserWithGroup() {

    String id = "UserId";
    String scopes = "";
    List<String> groups = Lists.newArrayList("test.vo-users");
    String voName = "test.vo";

    UsernamePasswordAuthenticationToken token = getToken(id, scopes, groups, voName);
    return new IamUser(token);
  }

  private IamUser getUnAuthorizedUser() {

    String id = "UserId";
    String scopes = "";
    List<String> groups = Lists.newArrayList();
    String voName = "test.vo";

    UsernamePasswordAuthenticationToken token = getToken(id, scopes, groups, voName);
    return new IamUser(token);
  }

  @Test
  public void testReadSuccessWithScopes() throws AuthorizationException, IOException {

    AuthorizationManager authManager = new DefaultAuthorizationManager(vos);
    authManager.canRead(getAuthorizedUserWithScopes(), "/test.vo");
  }

  @Test
  public void testReadSuccessWithGroup() throws AuthorizationException, IOException {

    AuthorizationManager authManager = new DefaultAuthorizationManager(vos);
    authManager.canRead(getAuthorizedUserWithGroup(), "/test.vo");
  }

  @Test(expected = AuthorizationException.class)
  public void testReadNotAuthorized() throws IOException {

    AuthorizationManager authManager = new DefaultAuthorizationManager(vos);
    authManager.canRead(getUnAuthorizedUser(), "/test.vo");
  }

  @Test
  public void testRecallSuccessWithScopes() throws AuthorizationException, IOException {

    AuthorizationManager authManager = new DefaultAuthorizationManager(vos);
    authManager.canRecall(getAuthorizedUserWithScopes(), "/test.vo");
  }

  @Test
  public void testRecallSuccessWithGroup() throws AuthorizationException, IOException {

    AuthorizationManager authManager = new DefaultAuthorizationManager(vos);
    authManager.canRecall(getAuthorizedUserWithGroup(), "/test.vo");
  }

  @Test(expected = AuthorizationException.class)
  public void testRecallNotAuthorized() throws IOException {

    AuthorizationManager authManager = new DefaultAuthorizationManager(vos);
    authManager.canRecall(getUnAuthorizedUser(), "/test.vo");
  }

  @Test(expected = AuthorizationException.class)
  public void testRecallPathNotSupported() throws IOException {

    AuthorizationManager authManager = new DefaultAuthorizationManager(vos);
    authManager.canRecall(getUnAuthorizedUser(), "/notvalidpath");
  }

  @Test(expected = AuthorizationException.class)
  public void testRecallPathNotValid() throws IOException {

    AuthorizationManager authManager = new DefaultAuthorizationManager(vos);
    authManager.canRecall(getUnAuthorizedUser(), "/#@#");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRecallNullPath() throws AuthorizationException, IOException {

    AuthorizationManager authManager = new DefaultAuthorizationManager(vos);
    authManager.canRecall(getAuthorizedUserWithScopes(), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRecallNullUser() throws AuthorizationException, IOException {

    AuthorizationManager authManager = new DefaultAuthorizationManager(vos);
    authManager.canRecall(null, "/path");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReadNullPath() throws AuthorizationException, IOException {

    AuthorizationManager authManager = new DefaultAuthorizationManager(vos);
    authManager.canRead(getAuthorizedUserWithScopes(), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReadNullUser() throws AuthorizationException, IOException {

    AuthorizationManager authManager = new DefaultAuthorizationManager(vos);
    authManager.canRead(null, "/path");
  }
}
