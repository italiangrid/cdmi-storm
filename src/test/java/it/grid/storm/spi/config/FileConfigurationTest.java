package it.grid.storm.spi.config;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileConfigurationTest {

	private static final Logger log = LoggerFactory.getLogger(FileConfigurationTest.class);

	@Test
	public void testLoadFromFile() {

		ClassLoader classLoader = getClass().getClassLoader();

		FileConfiguration conf =
				new FileConfiguration(classLoader.getResource("storm.properties").getFile());
		assertThat(conf.getHostname(), equalTo("test-hostname.local.io"));
		assertThat(conf.getPort(), equalTo(9998));
		assertThat(conf.getToken(), equalTo("testtoken"));

		log.info(conf.toString());
	}

	@Test
	public void testLoadEmptyPropertiesFile() {

		DefaultConfiguration configuration = new DefaultConfiguration();
		ClassLoader classLoader = getClass().getClassLoader();

		FileConfiguration conf =
				new FileConfiguration(classLoader.getResource("empty.properties").getFile());
		assertThat(conf.getHostname(), equalTo(configuration.getHostname()));
		assertThat(conf.getPort(), equalTo(configuration.getPort()));
		assertThat(conf.getToken(), equalTo(configuration.getToken()));
	}

	@Test
	public void testLoadNotFoundPropertiesFile() {

		try {
			new FileConfiguration("/this/is/not/a/path/to/storm.properties");
		} catch (ConfigurationException e) {
			assertThat(e.getCause().getClass(), equalTo(FileNotFoundException.class));
		}
	}

}
