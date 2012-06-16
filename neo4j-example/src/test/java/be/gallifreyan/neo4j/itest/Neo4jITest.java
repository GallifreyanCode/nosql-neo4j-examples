package be.gallifreyan.neo4j.itest;

import org.junit.Test;
import org.neo4j.graphdb.*;

import be.gallifreyan.neo4j.AbstractNeo4jITest;
import be.gallifreyan.neo4j.DevelopmentRelationships;

import static org.junit.Assert.*;

/**
 * Basics of managing nodes and relationships with the core API.
 */
public class Neo4jITest extends AbstractNeo4jITest {

	@Test
	public void testNodeCreation() {
		Node node = null;
		Transaction tx = db.beginTx();
		try {
			node = db.createNode();
			tx.success();
		} finally {
			tx.finish();
		}
		assertTrue(nodeExistsInDatabase(node));
	}

	@Test
	public void testNodeCreationWithProperties() {
		final String firstName = "firstNameTest";
		final String lastName = "lastNameTest";
		Node node = null;
		Transaction tx = db.beginTx();
		try {
			node = db.createNode();
			node.setProperty(FIRSTNAME, firstName);
			node.setProperty(LASTNAME, lastName);
			tx.success();
		} finally {
			tx.finish();
		}

		assertTrue(nodeExistsInDatabase(node));
		checkRelationDataIntegrity(db.getNodeById(node.getId()));
	}

	@Test
	public void testNodeRelations() {
		final String firstName = "firstNameTest";
		final String lastName = "lastNameTest";
		Node employee = null;
		Node supervisor = null;
		Relationship supervisorRelationship = null;

		Transaction tx = db.beginTx();
		try {
			employee = db.createNode();
			employee.setProperty(FIRSTNAME, firstName);
			employee.setProperty(LASTNAME, lastName);

			supervisor = db.createNode();
			supervisor.setProperty(FIRSTNAME, "supervisor" + firstName);
			supervisor.setProperty(LASTNAME, "supervisor" + lastName);

			supervisorRelationship = supervisor.createRelationshipTo(employee,
					DevelopmentRelationships.SUPERVISOR_OF);
			tx.success();
		} finally {
			tx.finish();
		}

		assertTrue(nodeExistsInDatabase(employee));
		assertTrue(nodeExistsInDatabase(supervisor));
		Node storedEmployee = db.getNodeById(employee.getId());
		Node storedSupervisor = db.getNodeById(supervisor.getId());
		checkRelationDataIntegrity(storedEmployee, storedSupervisor);

		Relationship storedSupervisorRelationship = db
				.getRelationshipById(supervisorRelationship.getId());
		assertNotNull(storedSupervisorRelationship);
		assertEquals(supervisor, storedSupervisorRelationship.getStartNode());
		assertEquals(employee, storedSupervisorRelationship.getEndNode());
	}

	@Test(expected = NotFoundException.class)
	public void testNodeRemoval() {
		Node testNode = createTestNodes();

		Transaction tx = db.beginTx();
		try {
			/* First remove relations */
			Iterable<Relationship> relationships = testNode.getRelationships();
			for (Relationship r : relationships) {
				r.delete();
			}
			testNode.delete();
			tx.success();
		} finally {
			tx.finish();
		}
		testNode.hasProperty("firstName");
	}

	@Test
	public void testRemoveInaccurateRelationship() {
		Node testNode = createInaccurateTestNodes();

		Transaction tx = db.beginTx();
		try {

			Iterable<Relationship> relationships = testNode
					.getRelationships(DevelopmentRelationships.IS_TASKED_WITH,
							Direction.OUTGOING);
			for (Relationship r : relationships) {
				Node n = r.getEndNode();
				/* Remove incorrect relation with the first node */
				if (n.hasProperty(FIRSTNAME)
						&& n.getProperty(FIRSTNAME).equals("firstNameTest")) {
					r.delete();
				}
			}
			tx.success();
		} finally {
			tx.finish();
		}
		assertEquals(1,
				destructivelyCountRelationships(testNode.getRelationships()));
	}
}
