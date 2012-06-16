package be.gallifreyan.neo4j.itest;

import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import be.gallifreyan.neo4j.AbstractITest;
import be.gallifreyan.neo4j.matchers.AutoIndexEmployees;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertThat;

/**
 * Auto-indexing which handles lifecycle of nodes and relationships in the
 * indexes automatically.
 */
public class AutoIndexITest extends AbstractITest {

	@Test
	public void testAutoIndexCreationOfEmployees() {
		Set<String> employeeNames = getAllEmployeeNames();
		AutoIndexer<Node> employeeAutoIndex = null;

		employeeAutoIndex = universe.getDatabase().index()
				.getNodeAutoIndexer();
		employeeAutoIndex.startAutoIndexingProperty("employee-name");
		employeeAutoIndex.setEnabled(true);

		Transaction tx = universe.getDatabase().beginTx();

		try {
			for (String employeeName : employeeNames) {
				Node n = universe.getDatabase().createNode();
				n.setProperty("employee-name", employeeName);
			}
			tx.success();
		} finally {
			tx.finish();
		}

		assertThat(employeeAutoIndex,
				AutoIndexEmployees.containsSpecificEmployees(employeeNames));
	}

	private Set<String> getAllEmployeeNames() {
		Index<Node> employees = universe.getDatabase().index()
				.forNodes("employees");
		IndexHits<Node> results = employees.query("employee", "*");

		HashSet<String> employeeNames = new HashSet<String>();

		for (Node character : results) {
			employeeNames.add((String) character.getProperty("employee"));
		}
		return employeeNames;
	}
}
