package it.grid.storm.rest.recall.model;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.junit.Test;

public class TaskInsertRequestTest {

  private final String id = "userid";
  private final String vo = "voname";
  private final String stfn = "/path";

  private ObjectMapper mapper = new ObjectMapper();

  private TaskInsertRequest getTestRequest() {
    return TaskInsertRequest.builder().userId(id).voName(vo).pinLifetime(0).retryAttempts(0)
        .stfn(stfn).build();
  }

  @Test
  public void testDeserialization() throws IOException {

    TaskInsertRequest request = getTestRequest();
    String requestAsJson = mapper.writeValueAsString(request);
    TaskInsertRequest out = mapper.readValue(requestAsJson.getBytes(), TaskInsertRequest.class);
    assertThat(out.getUserId(), equalTo(request.getUserId()));
    assertThat(out.getVoName(), equalTo(request.getVoName()));
    assertThat(out.getPinLifetime(), equalTo(request.getPinLifetime()));
    assertThat(out.getRetryAttempts(), equalTo(request.getRetryAttempts()));
    assertThat(out.getStfn(), equalTo(request.getStfn()));
  }

}
