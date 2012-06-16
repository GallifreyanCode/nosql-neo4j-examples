package be.gallifreyan.neo4j.node;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import be.gallifreyan.neo4j.builder.ProjectBuilder;

public class Projects {
	private final GraphDatabaseService db;

	public Projects(GraphDatabaseService db) {
		this.db = db;
	}

	public void insert() {
		Transaction tx = db.beginTx();
		try {
			loadProjects();
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	private void loadProjects() {
		ProjectBuilder.project("A001")
			.usesTechnologies("Java", "OSGi", "Spring", "Oracle DB")
			.hasTeam("emp0", "emp1")
			.build(db);
	}
}
