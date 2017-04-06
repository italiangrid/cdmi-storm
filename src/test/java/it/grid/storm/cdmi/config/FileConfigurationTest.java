package it.grid.storm.cdmi.config;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.cdmi.config.ConfigurationException;
import it.grid.storm.cdmi.config.DefaultPluginConfiguration;
import it.grid.storm.cdmi.config.FilePluginConfiguration;

public class FileConfigurationTest {

	private static final Logger log = LoggerFactory.getLogger(FileConfigurationTest.class);

	@Test
	public void testLoadFromFile() {

		ClassLoader classLoader = getClass().getClassLoader();

		FilePluginConfiguration conf =
				new FilePluginConfiguration(classLoader.getResource("storm.properties").getFile());
		assertThat(conf.getHostname(), equalTo("test-hostname.local.io"));
		assertThat(conf.getPort(), equalTo(9998));
		assertThat(conf.getToken(), equalTo("testtoken"));

		log.info(conf.toString());
	}

	@Test
	public void testLoadEmptyPropertiesFile() {

		DefaultPluginConfiguration configuration = new DefaultPluginConfiguration();
		ClassLoader classLoader = getClass().getClassLoader();

		FilePluginConfiguration conf =
				new FilePluginConfiguration(classLoader.getResource("empty.properties").getFile());
		assertThat(conf.getHostname(), equalTo(configuration.getHostname()));
		assertThat(conf.getPort(), equalTo(configuration.getPort()));
		assertThat(conf.getToken(), equalTo(configuration.getToken()));
	}

	@Test
	public void testLoadNotFoundPropertiesFile() {

		try {
			new FilePluginConfiguration("/this/is/not/a/path/to/storm.properties");
		} catch (ConfigurationException e) {
			assertThat(e.getCause().getClass(), equalTo(FileNotFoundException.class));
		}
	}

}
