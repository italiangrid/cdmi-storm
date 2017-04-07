package it.grid.storm.cdmi.config;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StormCapabilitiesTest {

  private static final Logger log = LoggerFactory.getLogger(StormCapabilitiesTest.class);

  static final Map<String, Boolean> containerCapsMap = new HashMap<String, Boolean>() {
    private static final long serialVersionUID = 1L;
    {
      put("cdmi_export_container_http", true);
      put("cdmi_capabilities_templates", true);
      put("cdmi_capabilities_exact_inherit", true);
      put("cdmi_default_dataobject_capability_class", true);
      put("cdmi_location", true);
      put("cdmi_data_storage_lifetime", true);
      put("cdmi_data_redundancy", true);
      put("cdmi_geographic_placement", true);
      put("cdmi_latency", true);
      put("cdmi_capabilities_allowed", false);
      put("cdmi_capability_lifetime", false);
      put("cdmi_capability_lifetime_action", false);
      put("cdmi_capability_association_time", false);
      put("cdmi_durability", false);
      put("cdmi_recommended_polling_interval", false);
      put("cdmi_throughput", true);
    }
  };

  static final Map<String, Boolean> dataobjectCapsMap = new HashMap<String, Boolean>() {
    private static final long serialVersionUID = 1L;
    {
      put("cdmi_export_container_http", false);
      put("cdmi_capabilities_templates", true);
      put("cdmi_capabilities_exact_inherit", true);
      put("cdmi_default_dataobject_capability_class", false);
      put("cdmi_location", false);
      put("cdmi_data_storage_lifetime", true);
      put("cdmi_data_redundancy", true);
      put("cdmi_geographic_placement", true);
      put("cdmi_latency", true);
      put("cdmi_capabilities_allowed", true);
      put("cdmi_capability_lifetime", false);
      put("cdmi_capability_lifetime_action", false);
      put("cdmi_capability_association_time", true);
      put("cdmi_durability", false);
      put("cdmi_recommended_polling_interval", true);
      put("cdmi_throughput", true);
    }
  };

  @Test
  public void testLoadFromFile() throws JsonParseException, JsonMappingException, IOException {

    String jsonFile = getClass().getClassLoader().getResource("storm-capabilities.json").getFile();

    ObjectMapper mapper = new ObjectMapper();
    StormCapabilities cap =
        mapper.readValue(new File(jsonFile), StormCapabilities.class);

    log.debug("CdmiCapabilitiesConfiguration: {}", cap);

    for (String key : cap.getContainerCapabilities().keySet()) {
      assertThat(cap.getContainerCapabilities().get(key),
          equalTo(containerCapsMap.get(key)));
    }
    for (String key : cap.getDataobjectCapabilities().keySet()) {
      assertThat(cap.getDataobjectCapabilities().get(key),
          equalTo(dataobjectCapsMap.get(key)));
    }
  }

}
