package it.grid.storm.gateway;

import static org.apache.http.HttpVersion.HTTP_1_1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.grid.storm.gateway.StormBackendGateway;
import it.grid.storm.gateway.SimpleUser;
import it.grid.storm.gateway.model.BackendGateway;
import it.grid.storm.gateway.model.BackendGatewayException;
import it.grid.storm.rest.metadata.model.FileAttributes;
import it.grid.storm.rest.metadata.model.StoRIMetadata;

public class StormBackendGatewayTest {

  private final static String FILE_STFN_PATH = "/metadata/test.vo/test.txt";
  private final static String FILE_ABSOLUTE_PATH = "/tmp/test.vo/test.txt";

  private final static String HOSTNAME = "dev.local.io";
  private final static int PORT = 9998;
  private final static String TOKEN = "MY_SECRET_TOKEN";

  private HttpClient getHttpClient() {
    HttpClient client = Mockito.mock(HttpClient.class);
    return client;
  }

  private HttpResponse getSuccessGetMetadataResponse(StoRIMetadata meta)
      throws UnsupportedEncodingException, JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    HttpResponse response = getResponse(200, "Ok");
    response.setEntity(new StringEntity(mapper.writeValueAsString(meta)));
    return response;
  }

  private HttpClient getHttpClientSuccess(StoRIMetadata meta)
      throws ClientProtocolException, IOException {

    HttpClient client = getHttpClient();
    HttpResponse response = getSuccessGetMetadataResponse(meta);
    Mockito.when(client.execute(Mockito.any(HttpGet.class))).thenReturn(response);
    return client;
  }

  private HttpResponse getResponse(int statusCode, String reasonPhrase) {
    return new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, statusCode, reasonPhrase));
  }

  private HttpClient getHttpClientNotFoundResponse() throws ClientProtocolException, IOException {

    HttpClient client = getHttpClient();
    HttpResponse response = getResponse(404, "Not Found");
    Mockito.when(client.execute(Mockito.any(HttpGet.class))).thenReturn(response);
    return client;
  }

  private HttpClient getHttpClientIOException() throws ClientProtocolException, IOException {

    HttpClient client = getHttpClient();
    Mockito.when(client.execute(Mockito.any(HttpGet.class)))
        .thenThrow(new IOException("not found"));
    return client;
  }

  @Test
  public void testSuccessfulGetFileMetadata() throws ClientProtocolException, IOException {

    StoRIMetadata meta = StoRIMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH)
        .attributes(
            FileAttributes.builder().migrated(false).pinned(false).premigrated(false).build())
        .build();
    HttpClient client = getHttpClientSuccess(meta);
    BackendGateway gateway = new StormBackendGateway(client, HOSTNAME, PORT, TOKEN);
    StoRIMetadata metaOut = gateway.getStoRIMetadata(new SimpleUser("cdmi"), FILE_STFN_PATH);
    assertThat(metaOut.getAbsolutePath(), equalTo(meta.getAbsolutePath()));
    assertThat(metaOut.getType(), equalTo(meta.getType()));
  }

  @Test
  public void testSuccessfulGetFileMetadataStfnNoFirstSlash()
      throws ClientProtocolException, IOException {

    StoRIMetadata meta = StoRIMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH)
        .attributes(
            FileAttributes.builder().migrated(false).pinned(false).premigrated(false).build())
        .build();
    HttpClient client = getHttpClientSuccess(meta);
    BackendGateway gateway = new StormBackendGateway(client, HOSTNAME, PORT, TOKEN);
    StoRIMetadata metaOut =
        gateway.getStoRIMetadata(new SimpleUser("cdmi"), FILE_STFN_PATH.substring(1));
    assertThat(metaOut.getAbsolutePath(), equalTo(meta.getAbsolutePath()));
    assertThat(metaOut.getType(), equalTo(meta.getType()));
  }

  @Test(expected = BackendGatewayException.class)
  public void testSuccessfulGetMetadataNotFound() throws ClientProtocolException, IOException {


    HttpClient client = getHttpClientNotFoundResponse();
    BackendGateway gateway = new StormBackendGateway(client, HOSTNAME, PORT, TOKEN);
    gateway.getStoRIMetadata(new SimpleUser("cdmi"), FILE_STFN_PATH);
  }

  @Test(expected = BackendGatewayException.class)
  public void testGetMetadataIOException() throws ClientProtocolException, IOException {

    HttpClient client = getHttpClientIOException();
    BackendGateway gateway = new StormBackendGateway(client, HOSTNAME, PORT, TOKEN);
    gateway.getStoRIMetadata(new SimpleUser("cdmi"), FILE_STFN_PATH);
  }

}
