package be.gallifreyan.neo4j;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import be.gallifreyan.neo4j.itest.*;

@RunWith(Suite.class)
@SuiteClasses({ EmbeddedDevelopmentUniverseITest.class,
				Neo4jITest.class,
				IndexingITest.class,
				AutoIndexITest.class,
				AdvancedGraphOperationsITest.class
				})
public class AllISuite {

}
