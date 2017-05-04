package it.grid.storm.cdmi.config;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.indigo.cdmi.BackendCapability.CapabilityType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StormBackendContainerCapabilityTest {

  private static final Logger log = LoggerFactory.getLogger(StormBackendContainerCapabilityTest.class);

  static final String name = "DiskOnly";
  static final CapabilityType type = CapabilityType.CONTAINER;

  static final Map<String, Boolean> capabilities = new HashMap<String, Boolean>() {
    private static final long serialVersionUID = 1L;
    {
      put("cdmi_export_container_http", true);
      put("cdmi_capabilities_templates", true);
      put("cdmi_capabilities_exact_inherit", true);
      put("cdmi_default_dataobject_capability_class", true);
      put("cdmi_location", true);
      put("cdmi_data_redundancy", true);
      put("cdmi_geographic_placement", true);
      put("cdmi_latency", true);
      put("cdmi_throughput", true);
    }
  };

  static final Map<String, Object> metadata = new HashMap<String, Object>() {
    private static final long serialVersionUID = 1L;
    {
      put("cdmi_data_redundancy_provided", "1");
      put("cdmi_geographic_placement_provided", Lists.newArrayList("IT"));
      put("cdmi_latency_provided", "0");
      put("cdmi_throughput_provided", "4194304");
      put("cdmi_location", Lists.newArrayList("/"));
      put("cdmi_default_dataobject_capability_class", "DiskOnly");
    }
  };

  @Test
  public void testLoadFromFile() throws JsonParseException, JsonMappingException, IOException {

    String jsonFile = getClass().getClassLoader().getResource("capabilities/container/diskonly.json").getFile();

    ObjectMapper mapper = new ObjectMapper();
    StormBackendContainerCapability cap =
        mapper.readValue(new File(jsonFile), StormBackendContainerCapability.class);

    log.debug("StormBackendContainerCapability: {}", cap);

    assertThat(cap.getName(), equalTo(name));
    assertThat(cap.getType(), equalTo(CapabilityType.CONTAINER));
    for (String key : cap.getCapabilities().keySet()) {
      assertThat(cap.getCapabilities().get(key), equalTo(capabilities.get(key)));
    }
    for (String key : cap.getMetadata().keySet()) {
      assertThat(cap.getMetadata().get(key), equalTo(metadata.get(key)));
    }
  }

}
