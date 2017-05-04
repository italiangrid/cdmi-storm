package it.grid.storm.cdmi.auth.impl;

import static it.grid.storm.cdmi.auth.impl.AuthUtils.getToken;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import it.grid.storm.cdmi.auth.User;
import it.grid.storm.cdmi.auth.UserProvider;
import it.grid.storm.cdmi.auth.UserProviderException;

public class SecurityContextUserProviderTest {

  private String id = "UserId";
  private String scopes = "testvo_read testvo_recall";
  private List<String> groups = Lists.newArrayList("Users", "Developers");
  private String voName = "test.vo";

  private UserProvider userProvider = new SecurityContextUserProvider();

  @Test
  public void testSuccess() {

    UsernamePasswordAuthenticationToken token = getToken(id, scopes, groups, voName);
    SecurityContextHolder.getContext().setAuthentication(token);

    User u = userProvider.getUser();
    assertThat(u.getUserId(), equalTo(id));
    assertThat(u.getOrganizationName(), equalTo(voName));
    assertTrue(u.hasScope("testvo_read"));
    assertTrue(u.hasScope("testvo_recall"));
    assertThat(u.getScopes(), containsInAnyOrder("testvo_read", "testvo_recall"));
    assertTrue(u.hasGroup("Users"));
    assertTrue(u.hasGroup("Developers"));
    assertThat(u.getGroups(), containsInAnyOrder("Users", "Developers"));
    System.out.println(u);
  }

  @Test
  public void testNullAuthentication() {

    try {
      userProvider.getUser();
    } catch (UserProviderException e) {
      assertThat(e.getMessage(), equalTo("Null Authentication found!"));
    }
  }

  @Test
  public void testUnexpectedAuthentication() {

    AnonymousAuthenticationToken token = Mockito.mock(AnonymousAuthenticationToken.class);
    SecurityContextHolder.getContext().setAuthentication(token);

    try {
      userProvider.getUser();
    } catch (UserProviderException e) {
      assertThat(e.getMessage(), equalTo("Unexpected Authentication found!"));
    }
  }

}
