package be.gallifreyan.neo4j.builder;

import java.util.HashSet;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import be.gallifreyan.neo4j.DevelopmentRelationships;
import be.gallifreyan.neo4j.util.DatabaseHelper;

public class ProjectBuilder {
	private static final String NODE_INDEX = "projects";
	private static final String NODE_PROPERTY = "project";
	private final String projectName;
	private HashSet<String> technologies;
	private HashSet<String> team;
	
	public ProjectBuilder(String projectName) {
		this.projectName = projectName;
	}

	public static ProjectBuilder project(String projectName) {
		return new ProjectBuilder(projectName);
	}
	
	public ProjectBuilder usesTechnologies(String... technologies) {
		if (this.technologies == null) {
			this.technologies = new HashSet<>();
		}
		for (String technology : technologies) {
			this.technologies.add(technology);
		}
		return this;
	}
	
	public ProjectBuilder hasTeam(String... team) {
		if (this.team == null) {
			this.team = new HashSet<>();
		}
		for (String member : team) {
			this.team.add(member);
		}
		return this;
	}
	
	public void build(GraphDatabaseService db) {
		Node projectNode = ensureProjectIsInDb(projectName, db);

		// Node theEmployee = db.index().forNodes("employees")
		// .get("employee", "Person").getSingle();

		if (technologies != null) {
			for (String technology : technologies) {
				DatabaseHelper
						.ensureRelationshipInDb(projectNode,
								DevelopmentRelationships.COMPOSED_OF,
								TechnologiesBuilder
										.ensureTechnologyIsInDb(
												technology, db));
			}
		}
		
		if (team != null) {
			for (String member : team) {
				Node memberNode = EmployeeBuilder.ensureEmployeeIsInDb(member, db);
				DatabaseHelper.ensureRelationshipInDb(memberNode,
						DevelopmentRelationships.CONTRIBUTES_TO, projectNode);
			}
		}
	}

	public Node ensureProjectIsInDb(String name,
			GraphDatabaseService db) {
		Node theProjectNode = db.index().forNodes(NODE_INDEX)
				.get(NODE_PROPERTY, name).getSingle();
		if (theProjectNode == null) {
			theProjectNode = db.createNode();
			theProjectNode.setProperty(NODE_PROPERTY, name);
			ensureProjectIsIndexed(theProjectNode, db);
		}
		return theProjectNode;
	}
	
	private static void ensureProjectIsIndexed(Node characterNode,
			GraphDatabaseService database) {
		if (database.index().forNodes(NODE_INDEX)
				.get(NODE_PROPERTY, characterNode.getProperty(NODE_PROPERTY))
				.getSingle() == null) {
			database.index()
					.forNodes(NODE_INDEX)
					.add(characterNode, NODE_PROPERTY,
							characterNode.getProperty(NODE_PROPERTY));
		}
	}
}
