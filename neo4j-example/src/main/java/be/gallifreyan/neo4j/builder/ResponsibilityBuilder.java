package be.gallifreyan.neo4j.builder;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import be.gallifreyan.neo4j.DevelopmentRelationships;
import be.gallifreyan.neo4j.node.Responsibilities;
import be.gallifreyan.neo4j.util.DatabaseHelper;

public class ResponsibilityBuilder {
	private final Responsibilities responsibilityName;
	private String[] tasks;

	public static ResponsibilityBuilder responsibilities(Responsibilities responsibilityName) {
		return new ResponsibilityBuilder(responsibilityName);
	}

	private ResponsibilityBuilder(Responsibilities responsibilityName) {
		this.responsibilityName = responsibilityName;
	}

	public void create(GraphDatabaseService db) {
		Node responsibilityNode = ensureResponsibilitiesInDb(
				responsibilityName, db);

		if (tasks != null) {
			for (String task : tasks) {
				Node taskNode = TaskBuilder.ensureTaskInDb(task, db);
				DatabaseHelper.ensureRelationshipInDb(responsibilityNode,
						DevelopmentRelationships.IS_TASKED_WITH, taskNode);
				DatabaseHelper.ensureRelationshipInDb(taskNode,
						DevelopmentRelationships.IS_PERFORMED_BY,
						responsibilityNode);
			}
		}
	}

	public static Node ensureResponsibilitiesInDb(Responsibilities responsibility,
			GraphDatabaseService db) {
		ensureArgumentsAreSane(responsibility, db);

		Node responsibilityNode = db.index().forNodes("responsibilities")
				.get("responsibility", responsibility).getSingle();

		if (responsibilityNode == null) {
			responsibilityNode = db.createNode();
			responsibilityNode.setProperty("responsibility", responsibility.toString());
			db.index().forNodes("responsibilities")
					.add(responsibilityNode, "responsibility", responsibility);
		}
		return responsibilityNode;
	}

	private static void ensureArgumentsAreSane(Responsibilities theResponsibility,
			GraphDatabaseService db) {
		if (theResponsibility == null) {
			throw new RuntimeException(
					"Must provide a value for the species to the responsibility builder");
		}
		if (db == null) {
			throw new RuntimeException(
					"Must provide a value for the universe to the responsibility builder");
		}
	}

}
