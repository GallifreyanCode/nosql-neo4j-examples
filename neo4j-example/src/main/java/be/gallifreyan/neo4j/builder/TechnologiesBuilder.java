package be.gallifreyan.neo4j.builder;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import be.gallifreyan.neo4j.DevelopmentRelationships;
import be.gallifreyan.neo4j.util.DatabaseHelper;

public class TechnologiesBuilder {
	private static final String NODE_INDEX = "technologies";
	private static final String NODE_PROPERTY = "technology";
	private final String technologyName;
	private String techcategory;

	public TechnologiesBuilder(String technologyName) {
		this.technologyName = technologyName;
	}

	public static TechnologiesBuilder technology(String technologyName) {
		return new TechnologiesBuilder(technologyName);
	}

	public TechnologiesBuilder isLanguage() {
		techcategory = "language";
		return this;
	}

	public TechnologiesBuilder isFramework() {
		techcategory = "framework";
		return this;
	}

	public TechnologiesBuilder isDatabase() {
		techcategory = "database";
		return this;
	}

	public void build(GraphDatabaseService db) {
		Node theTechnology = db.index().forNodes(NODE_INDEX)
				.get(NODE_PROPERTY, technologyName).getSingle();
		if (theTechnology == null) {
			theTechnology = ensureTechnologyIsInDb(technologyName, db);

			if (techcategory != null) {
				ensureCategoryRelationshipInDb(theTechnology, db);
			}
//			if (framework) {
//				ensureFrameworkRelationshipInDb(theTechnology, db);
//			}
//			if (database) {
//				ensureDatabaseRelationshipInDb(theTechnology, db);
//			}
		}
	}

	public static Node ensureTechnologyIsInDb(String name, GraphDatabaseService db) {
		Node theTechnologyNode = db.index().forNodes(NODE_INDEX)
				.get(NODE_PROPERTY, name).getSingle();
		if (theTechnologyNode == null) {
			theTechnologyNode = db.createNode();
			theTechnologyNode.setProperty(NODE_PROPERTY, name);
			ensureTechnologyIsIndexed(theTechnologyNode, db);
		}
		return theTechnologyNode;
	}
	
	private static void ensureTechnologyIsIndexed(Node technologyNode,
			GraphDatabaseService database) {
		if (database.index().forNodes(NODE_INDEX)
				.get(NODE_PROPERTY, technologyNode.getProperty(NODE_PROPERTY))
				.getSingle() == null) {
			database.index()
					.forNodes(NODE_INDEX)
					.add(technologyNode, NODE_PROPERTY,
							technologyNode.getProperty(NODE_PROPERTY));
		}
	}
	
	private void ensureCategoryRelationshipInDb(Node technologyNode,
			GraphDatabaseService database) {
		Node theLanguageNode = database.index().forNodes("techcategories").get("techcategory", techcategory)
				.getSingle();
		if (theLanguageNode  == null) {
			theLanguageNode = database.createNode();
			theLanguageNode.setProperty("techcategory",  techcategory);
			ensureCategoryIsIndexed(theLanguageNode , database);
		}

		/* One 2 Many */
		DatabaseHelper.ensureRelationshipInDb(technologyNode,
				DevelopmentRelationships.IS_TECH, theLanguageNode );
	}
	
	private static void ensureCategoryIsIndexed(Node thePlanetNode,
			GraphDatabaseService database) {
		database.index().forNodes("techcategories")
				.add(thePlanetNode, "techcategory", thePlanetNode.getProperty("techcategory"));
	}
}
