package be.gallifreyan.neo4j;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class EmbeddedDevelopmentUniverse {
	private final GraphDatabaseService db;
	private final Path path;

	public EmbeddedDevelopmentUniverse(DevelopmentUniverseGenerator universe) {
		db = new GraphDatabaseFactory().newEmbeddedDatabase(universe
				.getDatabaseDirectory());
		path = Paths.get(universe.getDatabaseDirectory());
	}

	public Node theEmployee() {
		return db.index().forNodes("employees").get("employee", "emp0")
				.getSingle();
	}

	public void stop() {
		if (db != null) {
			db.shutdown();
		}
	}

	public GraphDatabaseService getDatabase() {
		return db;
	}
	
	public Path getPath() {
		return path;
	}
}
