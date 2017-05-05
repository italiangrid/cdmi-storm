package it.grid.storm.cdmi.config;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportsTest {

  private static final Logger log = LoggerFactory.getLogger(ExportsTest.class);

  static final Map<String, ExportIdentifier> export = new HashMap<String, ExportIdentifier>() {
    private static final long serialVersionUID = 1L;
    {
      put("Network/WebHTTP", new ExportIdentifier("http://localhost/cdmi/browse", "oidc"));
    }
  };

  @Test
  public void testLoadFromFile() throws JsonParseException, JsonMappingException, IOException {

    String jsonFile =
        getClass().getClassLoader().getResource("capabilities/exports.json").getFile();

    ObjectMapper mapper = new ObjectMapper();
    Map<String, ExportIdentifier> exports =
        mapper.readValue(new File(jsonFile), new TypeReference<Map<String, ExportIdentifier>>() {});

    log.debug("Exports: {}", exports);

    assertThat(exports.size(), equalTo(1));

    for (String key : exports.keySet()) {
      assertThat(exports.get(key).getIdentifier(), equalTo(export.get(key).getIdentifier()));
      assertThat(exports.get(key).getPermissions(), equalTo(export.get(key).getPermissions()));
    }
  }

}
