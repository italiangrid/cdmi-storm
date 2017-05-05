package it.grid.storm.cdmi.auth.impl;

import java.util.List;

import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AuthUtils {

  public static final SimpleGrantedAuthority roleUser = new SimpleGrantedAuthority("ROLE_USER");
  public static final SimpleGrantedAuthority roleAdmin = new SimpleGrantedAuthority("ROLE_ADMIN");

  public static UsernamePasswordAuthenticationToken getToken(String sub, String scopes,
      List<String> groups, String voName, List<GrantedAuthority> authorities) {

    JSONObject userinfo = new JSONObject();
    userinfo.put("sub", sub);
    userinfo.put("groups", groups);

    JSONObject tokeninfo = new JSONObject();
    tokeninfo.put("scope", scopes);
    tokeninfo.put("organisation_name", voName);

    JSONObject details = new JSONObject();
    details.put("userinfo", userinfo);
    details.put("tokeninfo", tokeninfo);

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(sub, null, authorities);
    auth.setDetails(details.toString());
    return auth;
  }
}
