package be.gallifreyan.neo4j.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.neo4j.server.NeoServerBootstrapper;
import org.neo4j.server.NeoServerWithEmbeddedWebServer;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.PropertyFileConfigurator;
import org.neo4j.server.configuration.validation.DatabaseLocationMustBeSpecifiedRule;
import org.neo4j.server.configuration.validation.Validator;
import org.neo4j.server.modules.DiscoveryModule;
import org.neo4j.server.modules.ManagementApiModule;
import org.neo4j.server.modules.RESTApiModule;
import org.neo4j.server.modules.ServerModule;
import org.neo4j.server.modules.ThirdPartyJAXRSModule;
import org.neo4j.server.modules.WebAdminModule;
import org.neo4j.server.rest.security.SecurityRule;
import org.neo4j.server.startup.healthcheck.StartupHealthCheck;
import org.neo4j.server.startup.healthcheck.StartupHealthCheckRule;
import org.neo4j.server.web.Jetty6WebServer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServerBuilder {
	private String portNo = "7474";
	private String dbDir = null;
	private String webAdminUri = "/db/manage/";
	private String webAdminDataUri = "/db/data/";
	private StartupHealthCheck startupHealthCheck;
	private final HashMap<String, String> thirdPartyPackages = new HashMap<String, String>();
	private String[] securityRuleClassNames = null;

	private List<Class<? extends ServerModule>> serverModules = null;

	public static ServerBuilder server() {
		return new ServerBuilder();
	}

	@SuppressWarnings("unchecked")
	public NeoServerWithEmbeddedWebServer build() throws IOException {
		if (dbDir == null) {
			this.dbDir = ServerTestUtils.createTempDir().getAbsolutePath();
		}
		File configFile = createPropertiesFiles();

		if (serverModules == null) {
			withSpecificServerModulesOnly(RESTApiModule.class,
					WebAdminModule.class, ManagementApiModule.class,
					ThirdPartyJAXRSModule.class, DiscoveryModule.class);
		}

		if (startupHealthCheck == null) {
			startupHealthCheck = mock(StartupHealthCheck.class);
			when(startupHealthCheck.run()).thenReturn(true);
		}

		return new NeoServerWithEmbeddedWebServer(
				new NeoServerBootstrapper(),
				startupHealthCheck,
				new PropertyFileConfigurator(new Validator(
						new DatabaseLocationMustBeSpecifiedRule()), configFile),
				new Jetty6WebServer(), serverModules);

	}

	public File createPropertiesFiles() throws IOException {
		File temporaryConfigFile = ServerTestUtils.createTempPropertyFile();

		createPropertiesFile(temporaryConfigFile);
		createTuningFile(temporaryConfigFile);

		return temporaryConfigFile;
	}

	private void createPropertiesFile(File temporaryConfigFile) {
		ServerTestUtils.writePropertyToFile(Configurator.DATABASE_LOCATION_PROPERTY_KEY, dbDir,
				temporaryConfigFile);
		if (portNo != null) {
			ServerTestUtils.writePropertyToFile(Configurator.WEBSERVER_PORT_PROPERTY_KEY,
					portNo, temporaryConfigFile);
		}
		ServerTestUtils.writePropertyToFile(Configurator.MANAGEMENT_PATH_PROPERTY_KEY,
				webAdminUri, temporaryConfigFile);
		ServerTestUtils.writePropertyToFile(Configurator.REST_API_PATH_PROPERTY_KEY,
				webAdminDataUri, temporaryConfigFile);

		if (securityRuleClassNames != null && securityRuleClassNames.length > 0) {
			ServerTestUtils.writePropertyToFile(Configurator.SECURITY_RULES_KEY,
					StringUtils.join(securityRuleClassNames, ","),
					temporaryConfigFile);
		}

		if (thirdPartyPackages.keySet().size() > 0) {
			ServerTestUtils.writePropertiesToFile(Configurator.THIRD_PARTY_PACKAGES_KEY,
					thirdPartyPackages, temporaryConfigFile);
		}
	}

	private void createTuningFile(File temporaryConfigFile) throws IOException {

		File databaseTuningPropertyFile = ServerTestUtils.createTempPropertyFile();
		ServerTestUtils.writePropertyToFile("neostore.nodestore.db.mapped_memory", "25M",
				databaseTuningPropertyFile);
		ServerTestUtils.writePropertyToFile("neostore.relationshipstore.db.mapped_memory",
				"50M", databaseTuningPropertyFile);
		ServerTestUtils.writePropertyToFile("neostore.propertystore.db.mapped_memory", "90M",
				databaseTuningPropertyFile);
		ServerTestUtils.writePropertyToFile("neostore.propertystore.db.strings.mapped_memory",
				"130M", databaseTuningPropertyFile);
		ServerTestUtils.writePropertyToFile("neostore.propertystore.db.arrays.mapped_memory",
				"130M", databaseTuningPropertyFile);
		ServerTestUtils.writePropertyToFile(Configurator.DB_TUNING_PROPERTY_FILE_KEY,
				databaseTuningPropertyFile.getAbsolutePath(),
				temporaryConfigFile);

	}

	@SuppressWarnings("unused")
	private File trashFile() throws IOException {
		File f = ServerTestUtils.createTempPropertyFile();

		FileWriter fstream = new FileWriter(f, true);
		BufferedWriter out = new BufferedWriter(fstream);

		for (int i = 0; i < 100; i++) {
			out.write((int) System.currentTimeMillis());
		}

		out.close();
		return f;
	}

	private ServerBuilder() {
	}

	public ServerBuilder onPort(int portNo) {
		this.portNo = String.valueOf(portNo);
		return this;
	}

	public ServerBuilder usingDatabaseDir(String dbDir) {
		this.dbDir = dbDir;
		return this;
	}

	public ServerBuilder withRelativeWebAdminUriPath(String webAdminUri) {
		try {
			URI theUri = new URI(webAdminUri);
			if (theUri.isAbsolute()) {
				this.webAdminUri = theUri.getPath();
			} else {
				this.webAdminUri = theUri.toString();
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public ServerBuilder withRelativeWebDataAdminUriPath(String webAdminDataUri) {
		try {
			URI theUri = new URI(webAdminDataUri);
			if (theUri.isAbsolute()) {
				this.webAdminDataUri = theUri.getPath();
			} else {
				this.webAdminDataUri = theUri.toString();
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public ServerBuilder withoutWebServerPort() {
		portNo = null;
		return this;
	}

	public ServerBuilder withFailingStartupHealthcheck() {
		startupHealthCheck = mock(StartupHealthCheck.class);
		when(startupHealthCheck.run()).thenReturn(false);
		when(startupHealthCheck.failedRule()).thenReturn(
				new StartupHealthCheckRule() {

					public String getFailureMessage() {
						return "mockFailure";
					}

					public boolean execute(Properties properties) {
						return false;
					}
				});
		return this;
	}

	@SuppressWarnings("unchecked")
	public ServerBuilder withSecurityRules(
			Class<? extends SecurityRule>... securityRuleClassNames)
			throws IOException {
		this.securityRuleClassNames = new String[securityRuleClassNames.length];
		for (int i = 0; i < securityRuleClassNames.length; i++) {
			this.securityRuleClassNames[i] = securityRuleClassNames[i]
					.getCanonicalName();
		}
		return this;
	}

	public ServerBuilder withThirdPartyJaxRsPackage(String packageName,
			String mountPoint) {
		thirdPartyPackages.put(packageName, mountPoint);
		return this;
	}

	@SuppressWarnings("unchecked")
	public ServerBuilder withSpecificServerModulesOnly(
			Class<? extends ServerModule>... modules) {
		serverModules = Arrays.asList(modules);
		return this;
	}
}
