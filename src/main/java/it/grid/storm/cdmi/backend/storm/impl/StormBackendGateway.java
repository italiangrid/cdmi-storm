package it.grid.storm.cdmi.backend.storm.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.grid.storm.cdmi.backend.storm.BackendGateway;
import it.grid.storm.cdmi.backend.storm.configuration.Backend;
import it.grid.storm.cdmi.backend.storm.configuration.User;

public class StormBackendGateway implements BackendGateway {

  private static final Logger log = LoggerFactory.getLogger(StormBackendGateway.class);

  private Backend backend;
  private User user;
  private CloseableHttpClient httpClient;
  private String beCapabilities;

  public StormBackendGateway(Backend backend, User user) throws RuntimeException {

    this.backend = backend;
    this.user = user;
    this.httpClient = HttpClients.createDefault();
    try {
      loadBackendCapabilitiesFromFile("/profiles.json");
    } catch (IOException e) {
      throw new RuntimeException(e.getLocalizedMessage(), e);
    }
  }

  private void loadBackendCapabilitiesFromFile(String filePath) throws IOException {

    beCapabilities = FileUtils.readFileToString(new File(filePath));
  }

  private String getRestApiEndpoint() {

    return String.format("%s:%d", backend.getHostname(), backend.getPort());
  }

  private String getMetadataURI(String path) {

    return String.format("http://%s/metadata%s", getRestApiEndpoint(), path);
  }

  private String getRecallURI(String path) {

    return String.format("http://%s/recall?path=%s", getRestApiEndpoint(), path);
  }

  private String getBasicAuthorizationHeader() {

    String str = String.format("%s:%s", user.getName(), user.getPassword());
    return String.format("Basic ", Base64.getEncoder().encodeToString(str.getBytes()));
  }

  @Override
  public String getQosProfiles() {

    return beCapabilities;
  }

  @Override
  public String getFileStatus(String stfn) {

    String url = getMetadataURI(stfn);
    log.info("GET {}", url);
    String authorization = getBasicAuthorizationHeader();
    log.info("Authorization {}", authorization);

    HttpGet httpGet = new HttpGet(url);
    Header authorizationHeader = new BasicHeader("Authorization", authorization);
    httpGet.addHeader(authorizationHeader);

    CloseableHttpResponse response = null;
    BufferedReader buffReader = null;

    JsonObject json = new JsonObject();
    JsonParser jsonParser = new JsonParser();
    
    try {
      response = httpClient.execute(httpGet);

      log.info(response.getStatusLine().toString());
      HttpEntity entity = response.getEntity();
      buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
      StringBuffer stringBuffer = new StringBuffer();
      String inputLine;
      while ((inputLine = buffReader.readLine()) != null) {
        stringBuffer.append(inputLine);
      }
      EntityUtils.consume(entity);

      json = jsonParser.parse(stringBuffer.toString()).getAsJsonObject();
      log.info(json.toString());

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
