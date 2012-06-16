package be.gallifreyan.neo4j.itest;

import org.junit.Assert;
import org.junit.Test;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import be.gallifreyan.neo4j.AbstractITest;
import be.gallifreyan.neo4j.matchers.ContainsOnlySpecificEmployees;
import be.gallifreyan.neo4j.matchers.ContainsSpecificEmployees;

/**
 * Built-in index framework based on Lucene.
 */
public class IndexingITest extends AbstractITest {

	@Test
	public void testRetrieveEmployeesIndexFromDatabase() {
		Index<Node> employees = null;
		employees = universe.getDatabase().index().forNodes("employees");

		Assert.assertNotNull(employees);
		Assert.assertThat(
				employees,
				ContainsSpecificEmployees.contains("emp0", "emp1", "emp2", "emp3"));
	}

	@Test
	public void testAddingToIndex() {
		GraphDatabaseService db = universe.getDatabase();
		Node newNode = createNewNode(db, "newNode");

		Assert.assertNull(db.index().forNodes("employees")
				.get("employee", "newNode").getSingle());

		Transaction transaction = db.beginTx();
		try {
			db.index()
					.forNodes("employees")
					.add(newNode, "employee",
							newNode.getProperty("employee"));
			transaction.success();
		} finally {
			transaction.finish();
		}

		Assert.assertNotNull(db.index().forNodes("employees")
				.get("employee", "newNode").getSingle());
	}

	@Test
	public void testLuceneQueryOnEmployees()
			throws Exception {
		IndexHits<Node> employees = null;
		
		employees = universe.getDatabase().index().forNodes("employees")
				.query("employee", "e*1");

		Assert.assertThat(
				employees,
				ContainsOnlySpecificEmployees.containsOnlyEmployees("emp1"));
	}

	@Test
	public void testDatabaseAndIndexSyncAfterDeletion()
			throws Exception {
		GraphDatabaseService db = universe.getDatabase();
		Node toBeDeletedNode = retriveToBeDeletedNodeFromIndex(db);

		Transaction tx = db.beginTx();
		try {
			for (Relationship rel : toBeDeletedNode.getRelationships()) {
				rel.delete();
			}
			toBeDeletedNode.delete();
			tx.success();
		} finally {
			tx.finish();
		}
		
		Assert.assertNull(
				"Node has not been deleted from the employees index.",
				retriveToBeDeletedNodeFromIndex(db));

		try {
			db.getNodeById(toBeDeletedNode.getId());
			Assert.fail("Node has not been deleted from the database.");
		} catch (NotFoundException nfe) {}
	}

	private Node retriveToBeDeletedNodeFromIndex(GraphDatabaseService db) {
		return db.index().forNodes("employees")
				.get("employee", "emp4").getSingle();
	}

	private Node createNewNode(GraphDatabaseService db, String name) {
		Node newNode;
		Transaction tx = db.beginTx();
		try {
			newNode = db.createNode();
			newNode.setProperty("employee", name);
			tx.success();
		} finally {
			tx.finish();
		}
		return newNode;
	}
}
