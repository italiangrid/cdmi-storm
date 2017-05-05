package it.grid.storm.cdmi.auth;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

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
