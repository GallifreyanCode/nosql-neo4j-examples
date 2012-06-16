package be.gallifreyan.neo4j.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import be.gallifreyan.neo4j.DevelopmentRelationships;
import be.gallifreyan.neo4j.node.Responsibilities;
import be.gallifreyan.neo4j.util.DatabaseHelper;

public class EmployeeBuilder {
	private final String employeeName;
	private HashSet<Responsibilities> responsibilities;
	private boolean supervisor = false;
	private List<String> supervising;
	private List<String> tasks;
	private List<String> skills;

	public EmployeeBuilder(String employeeName) {
		this.employeeName = employeeName;
	}

	public static EmployeeBuilder employee(String personName) {
		return new EmployeeBuilder(personName);
	}

	public EmployeeBuilder isA(Responsibilities responsibility) {
		if (responsibilities == null) {
			responsibilities = new HashSet<>();
		}
		responsibilities.add(responsibility);
		return this;
	}

	public EmployeeBuilder isSupervisor() {
		supervisor = true;
		return this;
	}

	public EmployeeBuilder isSupervising(String... names) {
		if (this.supervising == null) {
			this.supervising = new ArrayList<>();
		}
		for (String name : names) {
			this.supervising.add(name);
		}
		return this;
	}

	public EmployeeBuilder isTaskedWith(String... tasks) {
		if (this.tasks == null) {
			this.tasks = new ArrayList<>();
		}
		for (String task : tasks) {
			this.tasks.add(task);
		}
		return this;
	}

	public EmployeeBuilder isSkilledIn(String... skills) {
		if (this.skills== null) {
			this.skills = new ArrayList<>();
		}
		for (String skill : skills) {
			this.skills.add(skill);
		}
		return this;
	}

	public void build(GraphDatabaseService db) {
		Node employeeNode = ensureEmployeeIsInDb(employeeName, db);

		// Node theEmployee = db.index().forNodes("employees")
		// .get("employee", "Person").getSingle();

		if (responsibilities != null) {
			for (Responsibilities responsibility : responsibilities) {
				DatabaseHelper
						.ensureRelationshipInDb(employeeNode,
								DevelopmentRelationships.IS_A,
								ResponsibilityBuilder
										.ensureResponsibilitiesInDb(
												responsibility, db));
			}
		}
		if (supervisor) {
			ensureDeveloperRelationshipInDb(employeeNode, db);
		}

		if (skills != null) {
			for (String skill : skills) {
				DatabaseHelper.ensureRelationshipInDb(employeeNode,
						DevelopmentRelationships.HAS_EXPERIENCE_WITH,
						TechnologiesBuilder.ensureTechnologyIsInDb(skill, db));
			}
		}

		if (tasks != null) {
			// TODO: make more like respons
			ensureTaskInDb(employeeNode, tasks, db);
		}
		//
		// if (things != null) {
		// ensureThingsInDb(characterNode, things, db);
		// }
		//
		// Actos: will be like Developers?
		// if (actors != null) {
		// ensureActorsInDb(characterNode, actors, db);
		// }
		//
		// if (wikipediaUri != null) {
		// characterNode.setProperty("wikipedia", wikipediaUri);
		// }
	}

	private void ensureDeveloperRelationshipInDb(Node employeeNode,
			GraphDatabaseService db) {
		Node theEmployee = null;

		if (supervising != null) {
			for (String name : supervising) {
				theEmployee = db.index().forNodes("employees")
						.get("employee", name).getSingle();
				/* Many To One */
				DatabaseHelper.ensureRelationshipInDb(employeeNode,
						DevelopmentRelationships.SUPERVISOR_OF, theEmployee);
				/*
				 * One To Many
				 * DatabaseHelper.ensureRelationshipInDb(theEmployee,
				 * DevelopmentRelationships.IS_SUPERVISED_BY, employeeNode);
				 */
			}
		}
	}

	public static Node ensureEmployeeIsInDb(String name, GraphDatabaseService db) {
		Node theCharacterNode = db.index().forNodes("employees")
				.get("employee", name).getSingle();
		if (theCharacterNode == null) {
			theCharacterNode = db.createNode();
			theCharacterNode.setProperty("employee", name);
			ensureEmployeeIsIndexed(theCharacterNode, db);
		}
		return theCharacterNode;
	}

	private static void ensureEmployeeIsIndexed(Node characterNode,
			GraphDatabaseService database) {
		if (database.index().forNodes("employees")
				.get("employee", characterNode.getProperty("employee"))
				.getSingle() == null) {
			database.index()
					.forNodes("employees")
					.add(characterNode, "employee",
							characterNode.getProperty("employee"));
		}
	}

	public static Node ensureTaskInDb(Node personNode, List<String> tasks,
			GraphDatabaseService database) {
		Node theTaskNode = null;

		for (String task : tasks) {
			// Check if it already exists
			theTaskNode = database.index().forNodes("tasks").get("task", task)
					.getSingle();
			if (theTaskNode == null) {
				theTaskNode = database.createNode();
				theTaskNode.setProperty("task", task);
				ensureTaskIsIndexed(theTaskNode, database);
			}

			/* One 2 Many */
			DatabaseHelper.ensureRelationshipInDb(personNode,
					DevelopmentRelationships.IS_TASKED_WITH, theTaskNode);
		}

		return theTaskNode;
	}

	private static void ensureTaskIsIndexed(Node thePlanetNode,
			GraphDatabaseService database) {
		database.index().forNodes("tasks")
				.add(thePlanetNode, "task", thePlanetNode.getProperty("task"));
	}
}
