package be.gallifreyan.neo4j.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.AutoIndexer;

import java.util.Set;

public class AutoIndexEmployees extends TypeSafeMatcher<AutoIndexer<Node>> {
	private final Set<String> employeeNames;
	private String failedEmployeeName;

	private AutoIndexEmployees(Set<String> employeeNames) {
		this.employeeNames = employeeNames;
	}

	public void describeTo(Description description) {
		description
				.appendText(String
						.format("The presented arguments did not contain all the supplied employee names. Missing [%s].",
								failedEmployeeName));
	}

	@Override
	public boolean matchesSafely(AutoIndexer<Node> employees) {
		for (String employeeName : employeeNames) {
			if (employees.getAutoIndex().get("employee-name", employeeName)
					.getSingle() == null) {
				failedEmployeeName = employeeName;
				return false;
			}
		}

		return true;
	}

	@Factory
	public static Matcher<AutoIndexer<Node>> containsSpecificEmployees(
			Set<String> employeeNames) {
		return new AutoIndexEmployees(employeeNames);
	}
}
