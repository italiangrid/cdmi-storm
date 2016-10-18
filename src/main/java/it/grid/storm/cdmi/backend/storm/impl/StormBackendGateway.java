package it.grid.storm.cdmi.backend.storm.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.indigo.cdmi.BackEndException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.grid.storm.cdmi.backend.storm.BackendGateway;

public class StormBackendGateway implements BackendGateway {

  private String hostname;
  private int port;
  
  private String username;
  private String password;
  
  private CloseableHttpClient httpClient;
  private String beCapabilities;

  public StormBackendGateway(String hostname, int port, String username, String password) throws RuntimeException {

    this.hostname = hostname;
    this.port = port;
    this.username = username;
    this.password = password;
    
    this.httpClient = HttpClients.createDefault();
    try {
      loadBackendCapabilitiesFromFile("storm-profiles.json");
    } catch (IOException e) {
      throw new RuntimeException(e.getLocalizedMessage(), e);
    }
  }

  private void loadBackendCapabilitiesFromFile(String profilesFilePath) throws IOException {

    
    InputStream is = getClass().getClassLoader().getResourceAsStream(profilesFilePath);
    if (is == null) {
      throw new RuntimeException("Failed to find required config file on CLASSPATH. "
          + "Could not open " + profilesFilePath);
    }
    
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    StringBuffer stringBuffer = new StringBuffer();
    String inputLine;
    while ((inputLine = reader.readLine()) != null) {
      stringBuffer.append(inputLine);
    }

    beCapabilities = stringBuffer.toString().replaceAll("\t", "").replaceAll(" ", "");
    
    printPrettyJson(beCapabilities);
  }

  private void printPrettyJson(String json) {
    JsonParser jp = new JsonParser();
    JsonElement je = jp.parse(json);
    printPrettyJson(je);
  }

  private void printPrettyJson(JsonElement je) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(je));
  }

  private String getRestApiEndpoint() {

    return String.format("%s:%d", hostname, port);
  }

  private String getMetadataURI(String path) {

    return String.format("http://%s/metadata%s", getRestApiEndpoint(), path);
  }

  @SuppressWarnings("unused")
  private String getRecallURI(String path) {

    return String.format("http://%s/recall?path=%s", getRestApiEndpoint(), path);
  }

  private String getBasicAuthorizationHeader() {

    String str = String.format("%s:%s", username, password);
    return String.format("Basic ", Base64.getEncoder().encodeToString(str.getBytes()));
  }

  @Override
  public String getQosProfiles() {

    return beCapabilities;
  }

  @Override
  public String getFileStatus(String stfn) {

    String url = getMetadataURI(stfn);
    String authorization = getBasicAuthorizationHeader();

    HttpGet httpGet = new HttpGet(url);
    Header authorizationHeader = new BasicHeader("Authorization", authorization);
    httpGet.addHeader(authorizationHeader);

    CloseableHttpResponse response = null;
    BufferedReader buffReader = null;

    JsonObject json = new JsonObject();
    JsonParser jsonParser = new JsonParser();
    
    try {
      response = httpClient.execute(httpGet);

      HttpEntity entity = response.getEntity();
      buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
      StringBuffer stringBuffer = new StringBuffer();
      String inputLine;
      while ((inputLine = buffReader.readLine()) != null) {
        stringBuffer.append(inputLine);
      }
      EntityUtils.consume(entity);

      json = jsonParser.parse(stringBuffer.toString()).getAsJsonObject();

    } catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        response.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (buffReader != null) {
        try {
          buffReader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setFileStatus(String stfn, String status) throws BackEndException {

    throw new BackEndException("Not implemented");

  }

}
