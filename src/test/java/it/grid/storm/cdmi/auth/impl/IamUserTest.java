package it.grid.storm.cdmi.auth.impl;

import static it.grid.storm.cdmi.auth.impl.AuthUtils.getToken;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;

import java.util.List;

import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class IamUserTest {

  private String id = "UserId";
  private String scopes = "testvo_read testvo_recall";
  private List<String> groups = Lists.newArrayList("Users", "Developers");
  private String voName = "test.vo";

  @Test
  public void testSuccess() {

    UsernamePasswordAuthenticationToken token = getToken(id, scopes, groups, voName);
    IamUser u = new IamUser(token);
    assertThat(u.getUserId(), equalTo(id));
    assertThat(u.getOrganizationName(), equalTo(voName));
    assertTrue(u.hasScope("testvo_read"));
    assertTrue(u.hasScope("testvo_recall"));
    assertThat(u.getScopes(), containsInAnyOrder("testvo_read", "testvo_recall"));
    assertTrue(u.hasGroup("Users"));
    assertTrue(u.hasGroup("Developers"));
    assertThat(u.getGroups(), containsInAnyOrder("Users", "Developers"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullSubValue() {

    UsernamePasswordAuthenticationToken token = getToken(null, scopes, groups, voName);
    new IamUser(token);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullScopesValue() {

    UsernamePasswordAuthenticationToken token = getToken(id, null, groups, voName);
    new IamUser(token);
  }

  @Test
  public void testEmptyScopesValue() {

    UsernamePasswordAuthenticationToken token = getToken(id, "", groups, voName);
    IamUser u = new IamUser(token);
    assertThat(u.getUserId(), equalTo(id));
    assertThat(u.getOrganizationName(), equalTo(voName));
    assertThat(u.getScopes().size(), equalTo(0));
    assertTrue(u.hasGroup("Users"));
    assertTrue(u.hasGroup("Developers"));
    assertThat(u.getGroups(), containsInAnyOrder("Users", "Developers"));
  }

  @Test
  public void testNullGroupsValue() {

    UsernamePasswordAuthenticationToken token = getToken(id, scopes, null, voName);
    IamUser u = new IamUser(token);
    assertThat(u.getUserId(), equalTo(id));
    assertThat(u.getOrganizationName(), equalTo(voName));
    assertTrue(u.hasScope("testvo_read"));
    assertTrue(u.hasScope("testvo_recall"));
    assertThat(u.getScopes(), containsInAnyOrder("testvo_read", "testvo_recall"));
    assertThat(u.getGroups().size(), equalTo(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullVoNameValue() {

    UsernamePasswordAuthenticationToken token = getToken(id, scopes, groups, null);
    new IamUser(token);
  }
}
