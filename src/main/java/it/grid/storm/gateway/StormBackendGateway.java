package it.grid.storm.gateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import it.grid.storm.gateway.model.BackendGateway;
import it.grid.storm.gateway.model.BackendGatewayException;
import it.grid.storm.gateway.model.User;
import it.grid.storm.rest.metadata.model.StoRIMetadata;

public class StormBackendGateway implements BackendGateway {

	private static final Logger log = LoggerFactory.getLogger(StormBackendGateway.class);

	private HttpClient httpclient;
	private String hostname;
	private int port;
	private String token;

	public StormBackendGateway(HttpClient httpclient, String hostname, int port, String token) {

		Preconditions.checkNotNull(httpclient, "Invalid null httpclient");
		Preconditions.checkNotNull(hostname, "Invalid null hostname");
		this.httpclient = httpclient;
		this.hostname = hostname;
		this.port = port;
		this.token = token;
	}

	public StormBackendGateway(String hostname, int port, String token) {

		this(HttpClients.createDefault(), hostname, port, token);
	}

	@Override
	public StoRIMetadata getStoRIMetadata(User user, String path) throws BackendGatewayException {

		log.info("GET {} as {}", path, user.getId());

		String url = buildMetadataURL(path);
		log.debug("Metadata URL: {}", url);

		HttpResponse response = doHttpGet(url);
		log.info(response.getStatusLine().toString());

		if (response.getStatusLine().getStatusCode() == 200) {
			StoRIMetadata storiMetadata;
			try {
				storiMetadata = getEntityContent(response.getEntity());
			} catch (UnsupportedOperationException | IOException e) {
				log.error(e.getMessage());
				throw new BackendGatewayException(e.getMessage(), e);
			}
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
		return String.format(pattern, hostname, port, path);
	}

	private HttpResponse doHttpGet(String url) throws BackendGatewayException {

		HttpGet httpGet = new HttpGet(url);
		Header authorizationHeader = new BasicHeader("Token", token);
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

	private StoRIMetadata getEntityContent(HttpEntity entity) throws UnsupportedOperationException, IOException {

		StringBuffer stringBuffer = new StringBuffer();

		BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));

		String inputLine;
		while ((inputLine = buffReader.readLine()) != null) {
			stringBuffer.append(inputLine);
		}
		EntityUtils.consume(entity);

		buffReader.close();

		log.debug("Response content: {}", stringBuffer.toString());

		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(stringBuffer.toString().getBytes(), StoRIMetadata.class);
	}
}
