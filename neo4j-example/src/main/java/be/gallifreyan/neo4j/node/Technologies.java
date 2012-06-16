package be.gallifreyan.neo4j.node;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import be.gallifreyan.neo4j.builder.TechnologiesBuilder;

public class Technologies {
	private final GraphDatabaseService db;

	public Technologies(GraphDatabaseService db) {
		this.db = db;
	}

	public void insert() {
		Transaction tx = db.beginTx();
		try {
			loadLanguages();
			loadFrameworks();
			loadDatabases();
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	private void loadLanguages() {
		TechnologiesBuilder.technology("Java").isLanguage().build(db);
		TechnologiesBuilder.technology("Scala").isLanguage().build(db);
		TechnologiesBuilder.technology("Groovy").isLanguage().build(db);
		TechnologiesBuilder.technology("Ruby").isLanguage().build(db);
		TechnologiesBuilder.technology("Python").isLanguage().build(db);
		TechnologiesBuilder.technology("JavaScript").isLanguage().build(db);
	}
	
	private void loadFrameworks() {
		TechnologiesBuilder.technology("Spring").isFramework().build(db);
		TechnologiesBuilder.technology("EE").isFramework().build(db);
	}
	
	private void loadDatabases() {
		TechnologiesBuilder.technology("Oracle DB").isDatabase().build(db);
		TechnologiesBuilder.technology("HSQLDB").isDatabase().build(db);
		TechnologiesBuilder.technology("H2").isDatabase().build(db);
	}
}
