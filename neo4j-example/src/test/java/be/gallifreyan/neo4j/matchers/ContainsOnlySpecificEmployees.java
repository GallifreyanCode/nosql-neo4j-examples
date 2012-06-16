package be.gallifreyan.neo4j.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.neo4j.graphdb.Node;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ContainsOnlySpecificEmployees extends
		TypeSafeMatcher<Iterable<Node>> {

	private final Set<String> employees;
	private String failedToFindEmployees;
	private boolean matchedSize;

	public ContainsOnlySpecificEmployees(String... employeeNames) {
		this.employees = new HashSet<String>();
		Collections.addAll(employees, employeeNames);
	}

	public void describeTo(Description description) {
		if (failedToFindEmployees != null) {
			description.appendText(String.format(
					"Failed to find employee [%s] in the given employee names",
					failedToFindEmployees));
		}

		if (!matchedSize) {
			description.appendText(String.format(
					"Mismatched number of employees, expected [%d]",
					employees.size()));
		}
	}

	@Override
	public boolean matchesSafely(Iterable<Node> nodes) {

		for (Node n : nodes) {
			String employeeName = String.valueOf(n.getProperty("employee"));

			if (!employees.contains(employeeName)) {
				failedToFindEmployees = employeeName;
				return false;
			}
			employees.remove(employeeName);
		}

		return matchedSize = employees.size() == 0;
	}

	@Factory
	public static <T> Matcher<Iterable<Node>> containsOnlyEmployees(
			String... employeeNames) {
		return new ContainsOnlySpecificEmployees(employeeNames);
	}
}
