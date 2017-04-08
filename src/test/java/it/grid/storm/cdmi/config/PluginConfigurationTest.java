package it.grid.storm.cdmi.config;

import static it.grid.storm.cdmi.Utils.loadObjectFromJsonFile;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.cdmi.Utils;

public class PluginConfigurationTest {

  private static final Logger log = LoggerFactory.getLogger(PluginConfigurationTest.class);

  private PluginConfiguration getPluginConfiguration(ClassLoader classLoader) throws IOException {

    String filePath = classLoader.getResource("storm-properties.json").getFile();
    PluginConfiguration conf = Utils.loadObjectFromJsonFile(filePath, PluginConfiguration.class);
    return conf;
  }

  @Test
  public void testLoadFromFile() throws IOException {

    ClassLoader classLoader = getClass().getClassLoader();

    PluginConfiguration pc = getPluginConfiguration(classLoader);
    assertThat(pc.getBackend().getHostname(), equalTo("test-hostname.local.io"));
    assertThat(pc.getBackend().getPort(), equalTo(9998));
    assertThat(pc.getBackend().getToken(), equalTo("testtoken"));
    assertThat(pc.getVos().size(), equalTo(1));
    assertThat(pc.getVos().get(0).getName(), equalTo("test.vo"));
    assertThat(pc.getVos().get(0).getRoots().size(), equalTo(1));
    assertThat(pc.getVos().get(0).getRoots().get(0), equalTo("/test.vo"));
    log.info(pc.toString());
  }

  @Test
  public void testLoadNotFoundPropertiesFile() throws IOException {

    try {
      loadObjectFromJsonFile("/this/is/not/a/path/to/storm.properties", PluginConfiguration.class);
      Assert.fail();
    } catch (FileNotFoundException e) {
      assertThat(e.getClass(), equalTo(FileNotFoundException.class));
    }
  }

}
