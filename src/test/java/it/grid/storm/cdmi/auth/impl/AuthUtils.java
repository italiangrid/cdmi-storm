package it.grid.storm.cdmi.auth.impl;

import java.util.List;

import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class AuthUtils {

  public static UsernamePasswordAuthenticationToken getToken(String sub, String scopes,
      List<String> groups, String voName) {

    JSONObject userinfo = new JSONObject();
    userinfo.put("sub", sub);
    userinfo.put("groups", groups);

    JSONObject tokeninfo = new JSONObject();
    tokeninfo.put("scope", scopes);
    tokeninfo.put("organisation_name", voName);

    JSONObject details = new JSONObject();
    details.put("userinfo", userinfo);
    details.put("tokeninfo", tokeninfo);

    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(sub, null);
    auth.setDetails(details.toString());
    return auth;
  }
}
