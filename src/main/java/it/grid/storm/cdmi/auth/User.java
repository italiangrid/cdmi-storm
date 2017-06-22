package it.grid.storm.cdmi.auth;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface User {

  public String getUserId();

  public List<String> getScopes();

  public boolean hasScope(String scope);

  public List<String> getGroups();

  public boolean hasGroup(String group);

  public String getOrganizationName();

  public List<GrantedAuthority> getAuthorities();

  public boolean hasAuthority(GrantedAuthority authority);

}
