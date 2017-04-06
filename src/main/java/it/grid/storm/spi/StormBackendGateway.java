package it.grid.storm.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.grid.storm.spi.config.Configuration;
import it.grid.storm.spi.rest.metadata.model.StoRIMetadata;

public class StormBackendGateway implements BackendGateway {

	private static final Logger log = LoggerFactory.getLogger(StormBackendGateway.class);

	private HttpClient httpclient;
	private Configuration configuration;

	public StormBackendGateway(HttpClient httpclient, Configuration configuration) {
		this.httpclient = httpclient;
		this.configuration = configuration;
	}

	@Override
	public StoRIMetadata getStoRIMetadata(User user, String path) throws BackendGatewayException {

		log.info("GET {} as {}", path, user.getId());

		String url = buildMetadataURL(path);
		log.debug("Metadata URL: {}", url);

		HttpResponse response = doHttpGet(url);
		log.info(response.getStatusLine().toString());

		if (response.getStatusLine().getStatusCode() == 200) {
			StoRIMetadata storiMetadata = getEntityContent(response.getEntity());
			log.debug("Response entity: {}", storiMetadata);
			return storiMetadata;
		}
		throw new BackendGatewayException(response.getStatusLine().toString());
	}

	@Override
	public void addRecallTask(User user, String filepath) {
		// TODO Auto-generated method stub

	}

	private String buildMetadataURL(String path) {

		String pattern = "http://%s:%d/metadata/%s";
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		log.debug("build metadata URL for path {}", path);
		return String.format(pattern, configuration.getHostname(), configuration.getPort(), path);
	}

	private HttpResponse doHttpGet(String url) throws BackendGatewayException {

		HttpGet httpGet = new HttpGet(url);
		Header authorizationHeader = new BasicHeader("Token", configuration.getToken());
		httpGet.addHeader(authorizationHeader);

		HttpResponse response = null;

		try {
			response = httpclient.execute(httpGet);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new BackendGatewayException(e.getMessage(), e);
		}

		return response;
	}

	private StoRIMetadata getEntityContent(HttpEntity entity) throws BackendGatewayException {

		BufferedReader buffReader = null;
		StringBuffer stringBuffer = new StringBuffer();

		try {
			buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));

			String inputLine;
			while ((inputLine = buffReader.readLine()) != null) {
				stringBuffer.append(inputLine);
			}
			EntityUtils.consume(entity);

		} catch (UnsupportedOperationException | IOException e) {

			e.printStackTrace();
			throw new BackendGatewayException(e.getMessage(), e);

		} finally {
			if (buffReader != null) {
				try {
					buffReader.close();
				} catch (IOException ex) {
					log.warn(ex.getMessage());
				}
			}
		}

		log.debug("Response content: {}", stringBuffer.toString());

		ObjectMapper mapper = new ObjectMapper();
		try {

			return mapper.readValue(stringBuffer.toString().getBytes(), StoRIMetadata.class);

		} catch (IOException e) {

			e.printStackTrace();
			throw new BackendGatewayException(e.getMessage(), e);
		}
	}
}
