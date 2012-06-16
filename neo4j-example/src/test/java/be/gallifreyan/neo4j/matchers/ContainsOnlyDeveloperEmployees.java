package be.gallifreyan.neo4j.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

import be.gallifreyan.neo4j.DevelopmentRelationships;

import java.util.Set;

public class ContainsOnlyDeveloperEmployees extends TypeSafeMatcher<Set<Node>> {

	private Node failedNode;

	public void describeTo(Description description) {
		description
				.appendText(String
						.format("Node [%d] does not have an IS_A relationship to the developer responsibility node.",
								failedNode.getId()));
	}

	@Override
	public boolean matchesSafely(Set<Node> nodes) {
		for (Node n : nodes) {
			if (!(n.hasRelationship(DevelopmentRelationships.IS_A,
					Direction.OUTGOING) && n
					.getSingleRelationship(DevelopmentRelationships.IS_A,
							Direction.OUTGOING).getEndNode()
					.getProperty("responsibility").equals("Developer"))) {
				failedNode = n;
				return false;
			}
		}
		return true;
	}

	@Factory
	public static ContainsOnlyDeveloperEmployees containsOnlyHumanCompanions() {
		return new ContainsOnlyDeveloperEmployees();
	}
}
