package be.gallifreyan.neo4j.itest;

import org.junit.Assert;
import org.junit.Test;

import be.gallifreyan.neo4j.DevelopmentUniverseGenerator;
import be.gallifreyan.neo4j.EmbeddedDevelopmentUniverse;

public class EmbeddedDevelopmentUniverseITest {
	
	@Test
	public void testCreateEmbeddedDatabase() throws Exception {
		EmbeddedDevelopmentUniverse universe = new EmbeddedDevelopmentUniverse(
				new DevelopmentUniverseGenerator());
		Assert.assertNotNull(universe.getDatabase());
		universe.stop();
	}
}
