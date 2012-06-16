package be.gallifreyan.neo4j;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class ServerDevelopmentUniverse {
	private final NeoServerWithEmbeddedWebServer server;
	private WrappingNeoServerBootstrapper server2;

	public ServerDevelopmentUniverse(NeoServerWithEmbeddedWebServer server)
			throws Exception {
		this.server = server;
		server.start();
	}
	
	public ServerDevelopmentUniverse(WrappingNeoServerBootstrapper server)
			throws Exception {
		this.server = null;
		this.server2 = server;
		server2.start();
	}

	public Map<String, Object> theDoctor() {
		return null;
		//return getJsonFor(getUriFromIndex("characters", "character", "Doctor"));
	}

	public Map<String, Object> getJsonFor(String uri) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource resource = client.resource(uri);
		String response = resource.accept(MediaType.APPLICATION_JSON).get(
				String.class);
		try {
			return JsonHelper.jsonToMap(response);
		} catch (JsonParseException e) {
			throw new RuntimeException(
					"Invalid response when looking up Doctor node");
		}
	}

	public String getUriFromIndex(String indexName, String key, String value) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource resource = client.resource(new FunctionalTestHelper(server)
				.indexNodeUri(indexName, key, value));
		String response = resource.accept(MediaType.APPLICATION_JSON).get(
				String.class);
		try {
			return JsonHelper.jsonToList(response).get(0).get("self")
					.toString();
		} catch (JsonParseException e) {
			throw new RuntimeException("Invalid response when looking up node");
		}
	}

	void stop() {
		server.stop();
	}

	public NeoServerWithEmbeddedWebServer getServer() {
		return server;
	}
}
