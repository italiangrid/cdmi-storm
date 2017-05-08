package it.grid.storm.gateway.impl;

import static org.apache.http.HttpVersion.HTTP_1_1;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.grid.storm.cdmi.auth.User;
import it.grid.storm.gateway.BackendGateway;
import it.grid.storm.gateway.BackendGatewayException;
import it.grid.storm.gateway.impl.StormBackendGateway;
import it.grid.storm.rest.metadata.model.FileAttributes;
import it.grid.storm.rest.metadata.model.StoriMetadata;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;
import org.mockito.Mockito;

public class StormBackendGatewayTest {

  private static final String FILE_STFN_PATH = "/metadata/test.vo/test.txt";
  private static final String FILE_ABSOLUTE_PATH = "/tmp/test.vo/test.txt";

  private static final String HOSTNAME = "dev.local.io";
  private static final int PORT = 9998;
  private static final String TOKEN = "MY_SECRET_TOKEN";

  private static final User USER = getUser("cdmi");

  private HttpClient getHttpClient() {
    HttpClient client = Mockito.mock(HttpClient.class);
    return client;
  }

  private static User getUser(String id) {
    User user = Mockito.mock(User.class);
    Mockito.when(user.getUserId()).thenReturn(id);
    return user;
  }

  private HttpResponse getSuccessGetMetadataResponse(StoriMetadata meta)
      throws UnsupportedEncodingException, JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    HttpResponse response = getResponse(200, "Ok");
    response.setEntity(new StringEntity(mapper.writeValueAsString(meta)));
    return response;
  }

  private HttpClient getHttpClientMetadataSuccess(StoriMetadata meta)
      throws ClientProtocolException, IOException {

    HttpClient client = getHttpClient();
    HttpResponse response = getSuccessGetMetadataResponse(meta);
    Mockito.when(client.execute(Mockito.any(HttpGet.class))).thenReturn(response);
    return client;
  }

  private HttpResponse getResponse(int statusCode, String reasonPhrase) {
    return new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, statusCode, reasonPhrase));
  }

  private HttpClient getHttpClientMetadataNotFoundResponse()
      throws ClientProtocolException, IOException {

    HttpClient client = getHttpClient();
    HttpResponse response = getResponse(404, "Not Found");
    Mockito.when(client.execute(Mockito.any(HttpGet.class))).thenReturn(response);
    return client;
  }

  private HttpClient getHttpClientMetadataIoException()
      throws ClientProtocolException, IOException {

    HttpClient client = getHttpClient();
    Mockito.when(client.execute(Mockito.any(HttpGet.class)))
        .thenThrow(new IOException("not found"));
    return client;
  }

  private HttpClient getHttpClientRecallSuccess() throws ClientProtocolException, IOException {

    HttpClient client = getHttpClient();
    HttpResponse response = getResponse(201, "Created");
    Mockito.when(client.execute(Mockito.any(HttpPost.class))).thenReturn(response);
    return client;
  }

  private HttpClient getHttpClientRecallFailWith(int statusCode)
      throws ClientProtocolException, IOException {

    HttpClient client = getHttpClient();
    HttpResponse response = getResponse(statusCode, "Internal server error");
    Mockito.when(client.execute(Mockito.any(HttpPost.class))).thenReturn(response);
    return client;
  }

  @Test
  public void testSuccessfulGetFileMetadata() throws ClientProtocolException, IOException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH)
        .attributes(
            FileAttributes.builder().migrated(false).pinned(false).premigrated(false).build())
        .build();
    HttpClient client = getHttpClientMetadataSuccess(meta);
    BackendGateway gateway = new StormBackendGateway(client, HOSTNAME, PORT, TOKEN);
    StoriMetadata metaOut = gateway.getStoriMetadata(USER, FILE_STFN_PATH);
    assertThat(metaOut.getAbsolutePath(), equalTo(meta.getAbsolutePath()));
    assertThat(metaOut.getType(), equalTo(meta.getType()));
  }

  @Test
  public void testSuccessfulGetFileMetadataStfnNoFirstSlash()
      throws ClientProtocolException, IOException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH)
        .attributes(
            FileAttributes.builder().migrated(false).pinned(false).premigrated(false).build())
        .build();
    HttpClient client = getHttpClientMetadataSuccess(meta);
    BackendGateway gateway = new StormBackendGateway(client, HOSTNAME, PORT, TOKEN);
    StoriMetadata metaOut = gateway.getStoriMetadata(USER, FILE_STFN_PATH.substring(1));
    assertThat(metaOut.getAbsolutePath(), equalTo(meta.getAbsolutePath()));
    assertThat(metaOut.getType(), equalTo(meta.getType()));
  }

  @Test(expected = BackendGatewayException.class)
  public void testSuccessfulGetMetadataNotFound() throws ClientProtocolException, IOException {


    HttpClient client = getHttpClientMetadataNotFoundResponse();
    BackendGateway gateway = new StormBackendGateway(client, HOSTNAME, PORT, TOKEN);
    gateway.getStoriMetadata(USER, FILE_STFN_PATH);
  }

  @Test(expected = BackendGatewayException.class)
  public void testGetMetadataIoException() throws ClientProtocolException, IOException {

    HttpClient client = getHttpClientMetadataIoException();
    BackendGateway gateway = new StormBackendGateway(client, HOSTNAME, PORT, TOKEN);
    gateway.getStoriMetadata(USER, FILE_STFN_PATH);
  }

  @Test
  public void testSuccessfulPostRecallTask() throws ClientProtocolException, IOException {

    HttpClient client = getHttpClientRecallSuccess();
    BackendGateway gateway = new StormBackendGateway(client, HOSTNAME, PORT, TOKEN);
    gateway.addRecallTask(USER, FILE_STFN_PATH);
  }

  @Test
  public void testErrorOnPostRecallTask() throws ClientProtocolException, IOException {

    HttpClient client = getHttpClientRecallFailWith(500);
    BackendGateway gateway = new StormBackendGateway(client, HOSTNAME, PORT, TOKEN);
    try {
      gateway.addRecallTask(USER, FILE_STFN_PATH);
    } catch (BackendGatewayException bge) {
      assertThat(bge.getMessage(), containsString("500"));
    }
  }

}
