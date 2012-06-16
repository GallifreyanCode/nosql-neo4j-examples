package be.gallifreyan.neo4j.builder;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class TaskBuilder {
	private final String taskName;

	public static TaskBuilder planet(String taskName) {
		return new TaskBuilder(taskName);
	}

	private TaskBuilder(String taskName) {
		this.taskName = taskName;
	}

	public void fact(GraphDatabaseService db) {
		ensureTaskInDb(taskName, db);
	}

	public static Node ensureTaskInDb(String task, GraphDatabaseService db) {
		Node taskNode = db.index().forNodes("tasks").get("task", task)
				.getSingle();

		if (taskNode == null) {
			taskNode = db.createNode();
			taskNode.setProperty("task", task);
			db.index().forNodes("tasks").add(taskNode, "task", task);
		}

		return taskNode;
	}
}
