package be.gallifreyan.neo4j.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.neo4j.graphdb.Node;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ContainsOnlyProjects extends TypeSafeMatcher<Iterable<Node>> {
	private final Set<String> projects;
	private Node failedNode;

	public ContainsOnlyProjects(String... specificProjects) {
		this.projects = new HashSet<String>();
		Collections.addAll(projects, specificProjects);
	}

	public void describeTo(Description description) {
		description.appendText(String.format(
				"Node [%d] does not contain all of the specified projects",
				failedNode.getId()));
	}

	@Override
	public boolean matchesSafely(Iterable<Node> candidateNodes) {
		for (Node n : candidateNodes) {
			String property = String.valueOf(n.getProperty("project"));

			if (!projects.contains(property)) {
				failedNode = n;
				return false;
			}
			projects.remove(property);
		}

		return projects.size() == 0;
	}

	@Factory
	public static Matcher<Iterable<Node>> containsOnlyProjects(String... titles) {
		return new ContainsOnlyProjects(titles);
	}
}
