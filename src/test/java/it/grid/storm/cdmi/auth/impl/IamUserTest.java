package it.grid.storm.cdmi.auth.impl;

import static it.grid.storm.cdmi.auth.impl.AuthUtils.getToken;
import static it.grid.storm.cdmi.auth.impl.AuthUtils.roleAdmin;
import static it.grid.storm.cdmi.auth.impl.AuthUtils.roleUser;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;

import java.util.List;

import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class IamUserTest {

  private String id = "UserId";
  private String scopes = "testvo_read testvo_recall";
  private List<String> groups = Lists.newArrayList("Users", "Developers");
  private String voName = "test.vo";
  private List<GrantedAuthority> authorities = Lists.newArrayList(roleUser, roleAdmin);

  @Test
  public void testSuccess() {

    UsernamePasswordAuthenticationToken token = getToken(id, scopes, groups, voName, authorities);
    IamUser u = new IamUser(token);
    assertThat(u.getUserId(), equalTo(id));
    assertThat(u.getOrganizationName(), equalTo(voName));
    assertTrue(u.hasScope("testvo_read"));
    assertTrue(u.hasScope("testvo_recall"));
    assertThat(u.getScopes(), containsInAnyOrder("testvo_read", "testvo_recall"));
    assertTrue(u.hasGroup("Users"));
    assertTrue(u.hasGroup("Developers"));
    assertThat(u.getGroups(), containsInAnyOrder("Users", "Developers"));
    assertThat(roleUser, isIn(u.getAuthorities()));
    assertThat(roleAdmin, isIn(u.getAuthorities()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullSubValue() {

    UsernamePasswordAuthenticationToken token = getToken(null, scopes, groups, voName, authorities);
    new IamUser(token);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullScopesValue() {

    UsernamePasswordAuthenticationToken token = getToken(id, null, groups, voName, authorities);
    new IamUser(token);
  }

  @Test
  public void testEmptyScopesValue() {

    UsernamePasswordAuthenticationToken token = getToken(id, "", groups, voName, authorities);
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

    UsernamePasswordAuthenticationToken token = getToken(id, scopes, null, voName, authorities);
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

    UsernamePasswordAuthenticationToken token = getToken(id, scopes, groups, null, authorities);
    new IamUser(token);
  }

  @Test
  public void testNullAuthorities() {

    UsernamePasswordAuthenticationToken token = getToken(id, scopes, groups, voName, null);
    IamUser u = new IamUser(token);
    assertThat(u.getUserId(), equalTo(id));
    assertThat(u.getOrganizationName(), equalTo(voName));
    assertTrue(u.hasScope("testvo_read"));
    assertTrue(u.hasScope("testvo_recall"));
    assertThat(u.getScopes(), containsInAnyOrder("testvo_read", "testvo_recall"));
    assertTrue(u.hasGroup("Users"));
    assertTrue(u.hasGroup("Developers"));
    assertThat(u.getGroups(), containsInAnyOrder("Users", "Developers"));
    assertThat(u.getAuthorities().size(), equalTo(0));
  }
}
