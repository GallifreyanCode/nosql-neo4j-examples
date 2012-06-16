package be.gallifreyan.neo4j.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

public class ContainsSpecificEmployees extends TypeSafeMatcher<Index<Node>> {

	private final String[] employeeNames;

	private ContainsSpecificEmployees(String[] employeeNames) {
		this.employeeNames = employeeNames;
	}

	@Override
	public void describeTo(Description description) {
		description
				.appendText("Checks whether each index in the presented arguments contains the supplied employee names.");
	}

	@Override
	public boolean matchesSafely(Index<Node> employees) {
		for (String name : employeeNames) {
			if (employees.get("employee", name).getSingle() == null) {
				System.out.println(name);
				return false;
			}
		}

		return true;
	}

	@Factory
	public static <T> Matcher<Index<Node>> contains(String... employeeNames) {
		return new ContainsSpecificEmployees(employeeNames);
	}
}
