package be.gallifreyan.neo4j;

import org.neo4j.graphdb.RelationshipType;
/**
 * Person IS_A developer
 * person APPEARS_IN Project AXXX
 * Techs USER_IN Project AXXX
 * MEMBER_OF Competence Center
 * supervisor is SUPERVISOR OF person
 * person MEMBER_OF team1
 * techs COMPOSED_OF there things
 * person EXPERIENCED_IN tech
 * version PROMOTED_TO verion+
 * Dev IS_TASKED_WITH something
 * something IS_PERFORMED_BY dev
 * @author EXF133
 *
 */
public enum DevelopmentRelationships implements RelationshipType {
	IS_A,
	APPEARS_IN,
	USED_IN,
	MEMBER_OF,
	SUPERVISOR_OF,
	IS_SUPERVISED_BY,
	COMPOSED_OF,
	EXPERIENCED_IN,
	PROMOTED_TO,
	IS_TASKED_WITH,
	IS_PERFORMED_BY,
	IS_TECH,
	CONTRIBUTES_TO,
	HAS_EXPERIENCE_WITH
}
