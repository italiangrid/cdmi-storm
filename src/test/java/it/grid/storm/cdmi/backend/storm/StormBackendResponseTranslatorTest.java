package it.grid.storm.cdmi.backend.storm;

import static org.junit.Assert.fail;

import java.util.List;

import org.apache.http.util.Asserts;
import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.BackendCapability.CapabilityType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.grid.storm.cdmi.backend.storm.impl.StormBackendResponseTranslator;

public class StormBackendResponseTranslatorTest {

  private static final Logger log = LoggerFactory.getLogger(StormBackendResponseTranslatorTest.class);

  private JsonObject getFolderCapability() {

    JsonObject jsonCap = new JsonObject();
    jsonCap.addProperty("name", "Folder");
    jsonCap.addProperty("type", "container");
    JsonObject jsonMetadata = new JsonObject();
    jsonMetadata.addProperty("cdmi_latency", 0);
    jsonMetadata.addProperty("cdmi_data_redundancy", 1);
    jsonMetadata.addProperty("cdmi_geographic_placement", "[IT]");
    jsonCap.add("metadata", jsonMetadata);
    JsonObject jsonCapabilities = new JsonObject();
    jsonCapabilities.addProperty("cdmi_capabilities_templates", true);
    jsonCapabilities.addProperty("cdmi_capabilities_exact_inherit", true);
    jsonCapabilities.addProperty("cdmi_data_redundancy", true);
    jsonCapabilities.addProperty("cdmi_geographic_placement", true);
    jsonCapabilities.addProperty("cdmi_latency", true);
    jsonCap.add("capabilities", jsonCapabilities);
    
    return jsonCap;
  }

  private JsonObject getFileOnDiskCapability() {

    JsonObject jsonCap = new JsonObject();
    jsonCap.addProperty("name", "FileOnDisk");
    jsonCap.addProperty("type", "dataobject");
    JsonObject jsonMetadata = new JsonObject();
    jsonMetadata.addProperty("cdmi_latency", 0);
    jsonMetadata.addProperty("cdmi_data_redundancy", 1);
    jsonMetadata.addProperty("cdmi_geographic_placement", "[IT]");
    jsonCap.add("metadata", jsonMetadata);
    JsonObject jsonCapabilities = new JsonObject();
    jsonCapabilities.addProperty("cdmi_capabilities_templates", true);
    jsonCapabilities.addProperty("cdmi_capabilities_exact_inherit", true);
    jsonCapabilities.addProperty("cdmi_data_redundancy", true);
    jsonCapabilities.addProperty("cdmi_geographic_placement", true);
    jsonCapabilities.addProperty("cdmi_latency", true);
    jsonCap.add("capabilities", jsonCapabilities);
    
    return jsonCap;
  }

  private JsonArray getTestCapabilities() {
    
    JsonArray toTest = new JsonArray();
    toTest.add(getFolderCapability());
    toTest.add(getFileOnDiskCapability());
    return toTest;
  }

  private void isCapabilityAsExpected(JsonObject test, BackendCapability cap) {

    Asserts.check(cap.getName().equals(test.get("name").getAsString()), "Returned different capability name!");
    Asserts.check(cap.getType().equals(CapabilityType.valueOf(test.get("type").getAsString().toUpperCase())), "Returned different capability type");

    Asserts.check(cap.getMetadata().size() == test.get("metadata").getAsJsonObject().entrySet().size(), "Returned different metadata sizes");
    Asserts.check(cap.getCapabilities().size() == test.get("capabilities").getAsJsonObject().entrySet().size(), "Returned different capabilities sizes");

    cap.getMetadata().forEach((key,value) -> {
      JsonElement jsonMeta = test.get("metadata").getAsJsonObject().get(key);
      Asserts.check(jsonMeta != null, key + " metadata not found");
      if (value instanceof String) {
        log.debug("{} is a string", value);
        Asserts.check(jsonMeta.getAsString().equals((String) value), key + " metadata value is not equal");
      } else if (value instanceof Integer) {
        log.debug("{} is an integer", value);
        Asserts.check(jsonMeta.getAsInt() == ((Integer) value).intValue(), key + " metadata value is not equal");
      } else if (value instanceof Double) {
        log.debug("{} is a double", value);
        Asserts.check(jsonMeta.getAsDouble() == ((Double) value).doubleValue(), key + " metadata value is not equal");
      } else if (value instanceof Boolean) {
        log.debug("{} is a boolean", value);
        Asserts.check(jsonMeta.getAsBoolean() == ((Boolean) value).booleanValue(), key + " metadata value is not equal");
      } else {
        log.warn("{} not checked", value);
      }
    });

    cap.getCapabilities().forEach((key,value) -> {
      JsonElement jsonCap = test.get("capabilities").getAsJsonObject().get(key);
      Asserts.check(jsonCap != null, key + " capability not found");
      Asserts.check(((boolean) value) == true, "wrong capability value");
    });
  }
  
  private void assertCapabilitiesAreEquals(JsonArray toTest, List<BackendCapability> caps) {
    
    Asserts.check(caps.size() == toTest.size(), "Wrong size of Backend Capability list!");
    for (int i=0; i<toTest.size(); i++) {
      isCapabilityAsExpected(toTest.get(i).getAsJsonObject(), caps.get(i));
    }
  }

  @Test
  public void testGetBackendCapabilities() {

    JsonArray toTest = getTestCapabilities();
    StormBackendResponseTranslator translator = new StormBackendResponseTranslator();

    log.info("Backend Capability as JSON: {}", toTest.toString());
    
    List<BackendCapability> caps = null;
    try {
      caps = translator.getBackendCapabilities(toTest.toString());
    } catch (BackEndException e) {
      log.error("Unexpected {}", e);
      fail();
    }

    log.info("Backend Capability returned: {}", caps);

    assertCapabilitiesAreEquals(toTest, caps);
  }

}
