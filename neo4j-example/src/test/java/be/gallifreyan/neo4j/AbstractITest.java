package be.gallifreyan.neo4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import be.gallifreyan.neo4j.DevelopmentUniverseGenerator;
import be.gallifreyan.neo4j.EmbeddedDevelopmentUniverse;

public abstract class AbstractITest {
	protected static EmbeddedDevelopmentUniverse universe;

	@BeforeClass
	public static void createDatabase() throws Exception {
		universe = new EmbeddedDevelopmentUniverse(
				new DevelopmentUniverseGenerator());
		Assert.assertNotNull(universe.getDatabase());
		/*
		 * TODO: Why does root not work
		 * Assert.assertNotNull(universe.theEmployee());
		 */
		checkIndexNames();
	}

	private static void checkIndexNames() {
		System.out.println("******List of Index Names******");
		boolean responsibilitiesIndexExists = false;
		boolean employeesIndexExists = false;

		for (String s : universe.getDatabase().index().nodeIndexNames()) {
			System.out.println("+ " + s);
			switch (s) {
			case "responsibilities":
				responsibilitiesIndexExists = true;
				break;
			case "employees":
				employeesIndexExists = true;
				break;
			}
		}
		Assert.assertTrue(responsibilitiesIndexExists);
		Assert.assertTrue(employeesIndexExists);
		System.out.println("*******************************");
	}

	@AfterClass
	public static void closeTheDatabase() {
		universe.stop();
		//cleanPath(universe.getPath());
	}

	private static void cleanPath(Path path) {
		try {
			deleteDirectory(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void deleteDirectory(Path path) throws IOException {
		if (path.toFile().isDirectory()) {
			String[] children = path.toFile().list();
			for (String child : children) {
				Path childPath = Paths.get(path.toAbsolutePath().toString(),
						child);
				if (childPath.toFile().isDirectory()) {
					deleteDirectory(childPath);
				} else {
					Files.deleteIfExists(childPath);
				}
			}
		}
		Files.deleteIfExists(path);
	}
}
