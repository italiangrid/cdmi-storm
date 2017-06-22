package it.grid.storm.cdmi.config;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginConfigurationTest {

  private static final Logger log = LoggerFactory.getLogger(PluginConfigurationTest.class);

  private ObjectMapper mapper = new ObjectMapper();

  private PluginConfiguration getPluginConfiguration(ClassLoader classLoader) throws IOException {

    String filePath = classLoader.getResource("storm-properties.json").getFile();
    PluginConfiguration conf = mapper.readValue(new File(filePath), PluginConfiguration.class);
    return conf;
  }

  @Test
  public void testLoadFromFile() throws IOException {

    ClassLoader classLoader = getClass().getClassLoader();

    PluginConfiguration pc = getPluginConfiguration(classLoader);
    assertThat(pc.getBackend().getHostname(), equalTo("test-hostname.local.io"));
    assertThat(pc.getBackend().getPort(), equalTo(9998));
    assertThat(pc.getBackend().getToken(), equalTo("testtoken"));
    assertThat(pc.getVfs().size(), equalTo(2));
    assertThat(pc.getVfs().get(0).getVoName(), equalTo("test.vo"));
    assertThat(pc.getVfs().get(0).getIamGroup(), equalTo("test.vo-users"));
    assertThat(pc.getVfs().get(0).getReadScope(), equalTo("testvo:read"));
    assertThat(pc.getVfs().get(0).getRecallScope(), equalTo("testvo:recall"));
    assertThat(pc.getVfs().get(0).getPath(), equalTo("/test.vo"));
    log.info(pc.toString());
  }

  @Test
  public void testLoadNotFoundPropertiesFile() throws IOException {

    String filePath = "/this/is/not/a/path/to/storm.properties";
    try {
      mapper.readValue(new File(filePath), PluginConfiguration.class);
      Assert.fail();
    } catch (FileNotFoundException e) {
      assertThat(e.getClass(), equalTo(FileNotFoundException.class));
    }
  }

}
