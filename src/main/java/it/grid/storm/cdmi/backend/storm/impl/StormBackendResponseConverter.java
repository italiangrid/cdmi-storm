package it.grid.storm.cdmi.backend.storm.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.BackendCapability.CapabilityType;
import org.indigo.cdmi.CdmiObjectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import it.grid.storm.cdmi.backend.storm.ResponseConverter;

public class StormBackendResponseConverter implements ResponseConverter {

  private static final Logger log = LoggerFactory.getLogger(StormBackendResponseConverter.class);

  private static final JsonParser jsonParser = new JsonParser();

  @Override
  public List<BackendCapability> getBackendCapabilities(String json) throws BackEndException {

    List<BackendCapability> capabilities = new ArrayList<BackendCapability>();

    try {

      JsonArray qosProfiles = jsonParser.parse(json).getAsJsonArray();

      for (JsonElement profile : qosProfiles) {
        capabilities.add(convertFromJSONCapability(profile.getAsJsonObject()));
      }

    } catch (Throwable ex) {

      log.error(ex.getLocalizedMessage(), ex);
      throw new BackEndException(ex.getLocalizedMessage(), ex);
    }
    
    return capabilities;
  }

  private void validateCapability(JsonObject cap) throws IllegalArgumentException {

    Preconditions.checkArgument(!cap.get("type").isJsonNull(), "Null type");
    Preconditions.checkArgument(!cap.get("name").isJsonNull(), "Null name");
    Preconditions.checkArgument(!cap.get("metadata").isJsonNull(), "Null metadata");
    Preconditions.checkArgument(!cap.get("capabilities").isJsonNull(), "Null capabilities");
  }

  private BackendCapability convertFromJSONCapability(JsonObject capability)
      throws IllegalArgumentException {

    validateCapability(capability);

    CapabilityType capabilityType =
        CapabilityType.valueOf(capability.get("type").getAsString().toUpperCase());
    String capabilityName = capability.get("name").getAsString();
    BackendCapability beCapability = new BackendCapability(capabilityName, capabilityType);

    Gson gson = new Gson();
    Type stringObjectMap = new TypeToken<Map<String, Object>>() {}.getType();

    Map<String, Object> metadata = gson.fromJson(capability.get("metadata"), stringObjectMap);
    beCapability.setMetadata(metadata);

    Map<String, Object> capabilities =
        gson.fromJson(capability.get("capabilities"), stringObjectMap);
    beCapability.setCapabilities(capabilities);

    return beCapability;
  }

  @Override
  public CdmiObjectStatus getStatus(String status) throws BackEndException {
    // TODO Auto-generated method stub
    return null;
  }

}
