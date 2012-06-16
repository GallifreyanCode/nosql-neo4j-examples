package be.gallifreyan.neo4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import be.brail.neo4j.util.DatabaseHelper;
import be.gallifreyan.neo4j.DevelopmentRelationships;

public abstract class AbstractNeo4jITest {
	protected static GraphDatabaseService db;
	protected static DatabaseHelper databaseHelper;
	protected static final String FIRSTNAME = "firstName";
	protected static final String LASTNAME = "lastName";

	@BeforeClass
	public static void setUpClass() {
		db = DatabaseHelper.createDatabase();
		databaseHelper = new DatabaseHelper(db);
	}

	public boolean nodeExistsInDatabase(Node node) {
		return db.getNodeById(node.getId()) != null;
	}

	public void checkRelationDataIntegrity(Node employee) {
		checkDataIntegrityWithPrefix(employee, "");
	}

	public void checkRelationDataIntegrity(Node employee, Node supervisor) {
		checkDataIntegrityWithPrefix(employee, "");
		checkDataIntegrityWithPrefix(supervisor, "supervisor");
	}

	public void checkDefaultRelationDataIntegrity(Node defaultNode,
			Node defaultRelatedNode) {
		checkDataIntegrityWithPrefix(defaultNode, "");
		checkDataIntegrityWithPrefix(defaultRelatedNode, "related");
	}

	private void checkDataIntegrityWithPrefix(Node node, String prefix) {
		final String firstName = "firstNameTest";
		final String lastName = "lastNameTest";

		assertNotNull(node);
		assertEquals(prefix + firstName, node.getProperty(FIRSTNAME));
		assertEquals(prefix + lastName, node.getProperty(LASTNAME));
	}

	protected Node createTestNodes() {
		final String firstName = "firstNameTest";
		final String lastName = "lastNameTest";
		Transaction tx = db.beginTx();
		Node defaultNode = null;
		Node defaultRelatedNode = null;
		Relationship defaultRelationship = null;

		try {
			defaultNode = db.createNode();
			defaultNode.setProperty(FIRSTNAME, firstName);
			defaultNode.setProperty(LASTNAME, lastName);

			defaultRelatedNode = db.createNode();
			defaultRelatedNode.setProperty(FIRSTNAME, "related" + firstName);
			defaultRelatedNode.setProperty(LASTNAME, "related" + lastName);

			defaultRelationship = defaultRelatedNode.createRelationshipTo(
					defaultNode,
					DynamicRelationshipType.withName("DEFAULT_CONNECTION"));

			tx.success();
		} finally {
			tx.finish();
		}

		assertTrue(nodeExistsInDatabase(defaultNode));
		assertTrue(nodeExistsInDatabase(defaultRelatedNode));
		Node storedDefaultNode = db.getNodeById(defaultNode.getId());
		Node storedDefaultRelatedNode = db.getNodeById(defaultRelatedNode
				.getId());
		checkDefaultRelationDataIntegrity(storedDefaultNode,
				storedDefaultRelatedNode);

		Relationship storedDefaultRelationship = db
				.getRelationshipById(defaultRelationship.getId());
		assertNotNull(storedDefaultRelationship);
		assertEquals(defaultRelatedNode,
				storedDefaultRelationship.getStartNode());
		assertEquals(defaultNode, storedDefaultRelationship.getEndNode());
		return defaultRelatedNode;
	}

	protected Node createInaccurateTestNodes() {
		final String firstName = "firstNameTest";
		final String lastName = "lastNameTest";
		Transaction tx = db.beginTx();
		Node defaultNode = null;
		Node defaultRelatedNode = null;
		Relationship defaultRelationship = null;
		Relationship inaccurateRelationship = null;
		
		try {
			defaultNode = db.createNode();
			defaultNode.setProperty(FIRSTNAME, firstName);
			defaultNode.setProperty(LASTNAME, lastName);

			defaultRelatedNode = db.createNode();
			defaultRelatedNode.setProperty(FIRSTNAME, "related" + firstName);
			defaultRelatedNode.setProperty(LASTNAME, "related" + lastName);

			defaultRelationship = defaultRelatedNode.createRelationshipTo(defaultNode,
					DevelopmentRelationships.SUPERVISOR_OF);
			inaccurateRelationship = defaultRelatedNode.createRelationshipTo(defaultNode,
					DevelopmentRelationships.IS_TASKED_WITH);
			tx.success();
		} finally {
			tx.finish();
		}
		
		assertTrue(nodeExistsInDatabase(defaultNode));
		assertTrue(nodeExistsInDatabase(defaultRelatedNode));
		Node storedDefaultNode = db.getNodeById(defaultNode.getId());
		Node storedDefaultRelatedNode = db.getNodeById(defaultRelatedNode
				.getId());
		checkDefaultRelationDataIntegrity(storedDefaultNode,
				storedDefaultRelatedNode);

		Relationship storedDefaultRelationship = db
				.getRelationshipById(defaultRelationship.getId());
		assertNotNull(storedDefaultRelationship);
		assertEquals(defaultRelatedNode,
				storedDefaultRelationship.getStartNode());
		assertEquals(defaultNode, storedDefaultRelationship.getEndNode());
		
		Relationship storedInaccurateRelationship = db
				.getRelationshipById(inaccurateRelationship.getId());
		assertNotNull(storedInaccurateRelationship);
		assertEquals(defaultRelatedNode,
				storedInaccurateRelationship.getStartNode());
		assertEquals(defaultNode, storedInaccurateRelationship.getEndNode());
		return defaultRelatedNode;
	}
	
	protected int destructivelyCountRelationships(
			Iterable<Relationship> relationships) {
		return destructivelyCount(relationships);
	}
	
	@SuppressWarnings("unused")
	private int destructivelyCount(Iterable<?> iterable) {
		int count = 0;
		for (Object o : iterable) {
			count++;
		}
		return count;
	}

	@AfterClass
	public static void tearDownClass() {
		db.shutdown();
	}
}
