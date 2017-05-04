package it.grid.storm.cdmi.auth.impl;

import com.google.common.collect.Lists;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import it.grid.storm.cdmi.auth.User;

public class IamUser implements User {

  private String sub;
  private List<String> scopes;
  private List<String> groups;
  private String organization;

  public IamUser(UsernamePasswordAuthenticationToken auth) {

    try {

      JSONObject authDetails = new JSONObject(auth.getDetails().toString());

      this.sub = authDetails.getJSONObject("userinfo").getString("sub");
      this.groups = Lists.newArrayList();
      authDetails.getJSONObject("userinfo").getJSONArray("groups").forEach(g -> this.groups.add(g.toString()));
      this.scopes = Lists.newArrayList();
      String scopesStr = authDetails.getJSONObject("tokeninfo").getString("scope");
      if (!scopesStr.isEmpty()) {
        for (String scope : scopesStr.split(" ")) {
          scopes.add(scope);
        }
      }
      this.organization = authDetails.getJSONObject("tokeninfo").getString("organisation_name");

    } catch (JSONException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }

  }

  @Override
  public String getUserId() {

    return sub;
  }

  @Override
  public List<String> getScopes() {

    return scopes;
  }

  @Override
  public boolean hasScope(String scope) {

    return scopes.contains(scope);
  }

  @Override
  public List<String> getGroups() {

    return groups;
  }

  @Override
  public boolean hasGroup(String group) {

    return groups.contains(group);
  }

  @Override
  public String getOrganizationName() {

    return organization;
  }

  @Override
  public String toString() {
    return "IamUser [sub=" + sub + ", scopes=" + scopes + ", groups=" + groups + ", organization="
        + organization + "]";
  }

}
