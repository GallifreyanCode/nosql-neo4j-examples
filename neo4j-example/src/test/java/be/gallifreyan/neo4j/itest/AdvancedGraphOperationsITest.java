package be.gallifreyan.neo4j.itest;

import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;

import be.gallifreyan.neo4j.AbstractITest;
import be.gallifreyan.neo4j.DevelopmentRelationships;
import be.gallifreyan.neo4j.matchers.ContainsOnlyDeveloperEmployees;
import be.gallifreyan.neo4j.matchers.ContainsOnlyProjects;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Advanced graph operations.
 */
public class AdvancedGraphOperationsITest extends AbstractITest {

	@Test
	public void findDeveloperRelationFromEmployee() {
		Node developer = universe.getDatabase().index().forNodes("responsibilities")
				.get("responsibility", "Developer").getSingle();
		Assert.assertNotNull(developer);

		Assert.assertTrue(universe.theEmployee().hasRelationship());
		Assert.assertTrue(universe.theEmployee().hasRelationship(DevelopmentRelationships.IS_A));
		Assert.assertTrue(universe.theEmployee().hasRelationship(Direction.OUTGOING));
		
		Relationship relationship = universe.theEmployee()
				.getSingleRelationship(DevelopmentRelationships.IS_A, Direction.OUTGOING);
		Assert.assertNotNull(relationship);
		
		Node startNode = relationship.getStartNode();
		Node endNode = relationship.getEndNode();
		Assert.assertNotNull(endNode);
		Assert.assertTrue(endNode.equals(developer));
		Assert.assertTrue(endNode.hasProperty("responsibility"));
		Assert.assertEquals("Developer", endNode.getProperty("responsibility"));
		Assert.assertNotNull(startNode);
		Assert.assertFalse(startNode.equals(developer));
		Assert.assertTrue(startNode.hasProperty("employee"));
		Assert.assertEquals("emp0", startNode.getProperty("employee"));
	}
	
	@Test
	public void findAllDeveloperEmployees() {
		HashSet<Node> developerEmployees = new HashSet<Node>();
		
		Node developer = universe.getDatabase().index().forNodes("responsibilities")
				.get("responsibility", "Developer").getSingle();
		Assert.assertNotNull(developer);

		Assert.assertTrue(developer.hasRelationship());
		Assert.assertTrue(developer.hasRelationship(DevelopmentRelationships.IS_A));
		Assert.assertTrue(developer.hasRelationship(Direction.INCOMING));

		Iterable<Relationship> relationships = developer.getRelationships(Direction.INCOMING,
				DevelopmentRelationships.IS_A);
		Assert.assertNotNull(relationships);
		
		for (Relationship relationship : relationships) {
            Node employeeNode = relationship.getStartNode();
            if (employeeNode.hasRelationship(Direction.OUTGOING, DevelopmentRelationships.IS_A)) {
                Relationship singleRelationship = employeeNode.getSingleRelationship(DevelopmentRelationships.IS_A, Direction.OUTGOING);
                Assert.assertNotNull(singleRelationship);
                
                Node startNode = singleRelationship.getStartNode();
                Node endNode = singleRelationship.getEndNode();
                
                Assert.assertNotNull(endNode);
                Assert.assertTrue(endNode.equals(developer));
        		Assert.assertTrue(endNode.hasProperty("responsibility"));
        		Assert.assertEquals("Developer", endNode.getProperty("responsibility"));
        		Assert.assertNotNull(startNode);
        		Assert.assertFalse(startNode.equals(developer));
        		Assert.assertTrue(startNode.hasProperty("employee"));
        		Assert.assertTrue(startNode.getProperty("employee").toString().startsWith("emp"));
                
                if (endNode.equals(developer)) {
                	developerEmployees.add(employeeNode);
                }
            }
        }
		
        assertEquals(4, developerEmployees.size());
        assertThat(developerEmployees, ContainsOnlyDeveloperEmployees.containsOnlyHumanCompanions());
	}
	
	@Test
	public void testFindJavaProjectsWithEmp1() {
		Index<Node> employeesIndex = universe.getDatabase().index().forNodes("employees");
		Assert.assertNotNull(employeesIndex);
		Index<Node> technologiesIndex = universe.getDatabase().index().forNodes("technologies");
		Assert.assertNotNull(technologiesIndex);
		
		HashSet<Node> javaProjectsWithEmp1 = new HashSet<>();
		
		Node emp1 = employeesIndex.get("employee", "emp1").getSingle();
		Assert.assertNotNull(emp1);
		Node java = technologiesIndex.get("technology", "Java").getSingle();
		Assert.assertNotNull(java);
		
		for(Relationship relationship1 : emp1.getRelationships(
				DevelopmentRelationships.CONTRIBUTES_TO, Direction.OUTGOING)) {
			Node project = relationship1.getEndNode();
			Assert.assertNotNull(project);
			Assert.assertTrue(project.hasProperty("project"));
			Assert.assertEquals("A001", project.getProperty("project"));
			
			for(Relationship relationship2 : project.getRelationships(
					DevelopmentRelationships.COMPOSED_OF, Direction.OUTGOING)) {
				Assert.assertTrue(relationship2.getEndNode().hasProperty("technology"));
				Assert.assertNotNull(relationship2.getEndNode().getProperty("technology"));
				if(relationship2.getEndNode().equals(java)) {
					javaProjectsWithEmp1.add(project);
				}
			}
		}
		Assert.assertEquals(1, javaProjectsWithEmp1.size());
		assertThat(
				javaProjectsWithEmp1,
				ContainsOnlyProjects.containsOnlyProjects("A001"));
	}
}
