package be.gallifreyan.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;

import be.gallifreyan.neo4j.node.Employees;
import be.gallifreyan.neo4j.node.Projects;
import be.gallifreyan.neo4j.node.Tasks;
import be.gallifreyan.neo4j.node.Technologies;
import be.gallifreyan.neo4j.util.DatabaseHelper;

public class DevelopmentUniverseGenerator {
	private final String dbDir = DatabaseHelper.createTempDatabaseDir()
			.getAbsolutePath();

	public DevelopmentUniverseGenerator() throws InterruptedException {
		GraphDatabaseService db = DatabaseHelper.createDatabase(dbDir);
		/* Add technologies first for link to categories 
		 * TODO: to fix issue, if a node is already found, update the relations 
		 * More or less add data first before data which is linked */
		addTechnologies(db);
		//addResponsibilities(db);
		addTasks(db);
		addEmployees(db);
		addProjects(db);
		db.shutdown();
	}

//	private void addActors(GraphDatabaseService db) {
//		Actors actors = new Actors(db);
//		actors.insert();
//	}

//	private void addEpisodes(GraphDatabaseService db) {
//		Episodes episodes = new Episodes(db);
//		episodes.insert();
//	}

	private void addEmployees(GraphDatabaseService db) {
		Employees employees = new Employees(db);
		employees.insert();
	}

//	private void addResponsibilities(GraphDatabaseService db) {
//		Responsibilities species = new Responsibilities(db);
//		species.insert();
//	}

	//TODO: develop these with checks if they already exist or not
	private void addTasks(GraphDatabaseService db) {
		Tasks planets = new Tasks(db);
		planets.insert();
	}
	
	private void addTechnologies(GraphDatabaseService db) {
		Technologies technologies = new Technologies(db);
		technologies.insert();
	}
	
	private void addProjects(GraphDatabaseService db) {
		Projects projects = new Projects(db);
		projects.insert();
	}

//	private void addDalekProps(GraphDatabaseService db) {
//		DalekProps dalekProps = new DalekProps(db);
//		dalekProps.insert();
//	}

	public final String getDatabaseDirectory() {
		return dbDir;
	}
}
