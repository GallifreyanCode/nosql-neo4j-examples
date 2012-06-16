package be.gallifreyan.neo4j.node;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import be.gallifreyan.neo4j.builder.EmployeeBuilder;

public class Employees {
	private final GraphDatabaseService db;

	public Employees(GraphDatabaseService db) {
		this.db = db;
	}

	public void insert() {
		Transaction tx = db.beginTx();
		try {
			EmployeeBuilder.employee("emp0").isA(Responsibilities.Developer).build(db);
			loadDevelopers();
			loadSupervisors();
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	private void loadDevelopers() {
		EmployeeBuilder.employee("emp1")
			.isA(Responsibilities.Developer)
			.isTaskedWith("Database Maintenance", "Research")
			.isSkilledIn("Java", "EE", "HSQLDB")
			.build(db);
		EmployeeBuilder.employee("emp2")
			.isA(Responsibilities.Developer)
			.isTaskedWith("Enterprise Code")
			.isSkilledIn("Java", "EE", "HSQLDB")
			.build(db);
		EmployeeBuilder.employee("emp3")
			.isA(Responsibilities.Developer)
			.isTaskedWith("Vaadin UI", "Research")
			.build(db);
	}
	
	private void loadSupervisors() {
		EmployeeBuilder.employee("emp4")
			.isA(Responsibilities.Architect)
			.isSupervisor()
			.isSupervising("emp1", "emp2")
			.isTaskedWith("Research", "Enterprise Code", "Business Analysis")
			.build(db);
	}
}
