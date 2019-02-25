package de.due.paluno.yuan.henshin;

import java.io.File;

public class TestBench {
	// File Path
	private static final String RESOURCEPATH = "henshin";
	private static final String LEADER = "leader.henshin";
	private static final String FOLLOWER = "follower.henshin";
	private static final String JONINGVEHICLE = "joiningvehicle.henshin";
	private static final String READRULES = "readrules.henshin";
	private static final String PLATOONSYSTEM_DIAGRAM_PATH = "PlatooningSystem.xmi";
	private static final String LEADER_DIAGRAM_PATH = "StateOfLeader.xmi";
	private static final String FOLLOWER_DIAGRAM_PATH = "StateOfFollower.xmi";
	private static final String JV_DIAGRAM_PATH = "StateOfJoiningVehicle.xmi";

	// Name of Roles
	public static final String ROLE_LEADER = "platoonLeader";
	public static final String ROLE_FOLLOWER = "platoonFollower";
	public static final String ROLE_JOININGVEHICLE = "joiningVehicle";

	// Rules for Leader
	public static final String RULE_RECEIVEREQUEST = "receiveRequest";
	public static final String RULE_COMPUTEGAP = "computeGap";
	public static final String RULE_CREATEJOININGCOLLABORATION = "createJoiningCollaboration";
	public static final String RULE_ADDFOLLOWER = "addFollower";
	public static final String RULE_CREATELEADER = "createLeader";
	public static final String RULE_REMOVEJOININGCOLLABORATION = "removeJoiningCollaboration";

	// Rules for Follower
	public static final String RULE_FORMTEMPORALPLATOON = "formTemporalPlatoon";
	public static final String RULE_SWITCHPLATOONFLAGLOOP = "switchPlatoonFlagLoop";
	public static final String RULE_FORMGAP = "formGap";
	public static final String RULE_MERGEGAP = "mergeGap";
	public static final String RULE_DISABLELEADERFLAG = "disableLeaderFlag";
	public static final String RULE_SETPLATOONBACK = "setPlatoonback";
	public static final String RULE_MERGEPLATOON = "mergePlatoon";

	// Rules for Joining-Vehicle
	public static final String RULE_MOVETOINSERTIONPOSITION = "moveToInsertionPosition";
	public static final String RULE_INSERTINGAP = "insertInGap";
	public static final String RULE_BECOMESNEWFOLLOWER = "becomesNewFollower";

	// Rules for Read Rules
	public static final String READRULE_FOLLOWERINCOORD = "readRuleFollowerInCoord";
	public static final String READRULE_JOININGVEHICLEINCOORD = "readRuleJoiningVehicleInCoord";

	// Command
	public static final String READRULE = "Read Rule";
	public static final String REFLECTION = "Reflection";
	public static final String MODELTRANSMISSION = "Model Transmission";

	public static void main(String[] args) {
		// Check Existence of the Instance Diagram
		checkExistenceOfInstanceDiagram(LEADER_DIAGRAM_PATH);
		checkExistenceOfInstanceDiagram(FOLLOWER_DIAGRAM_PATH);
		checkExistenceOfInstanceDiagram(JV_DIAGRAM_PATH);

		// Create the related graphs of each roles.
		String[] modules = { LEADER, FOLLOWER, JONINGVEHICLE, READRULES };
		HenshinGraph init = new HenshinGraph("PlatoonSystem", RESOURCEPATH, PLATOONSYSTEM_DIAGRAM_PATH, modules);

		// Init View
		initPlatoon(init);
		
		//HenshinGraph(String name, String resourcePath, String diagramPath, String... modulePath)
		HenshinGraph leader = new HenshinGraph(ROLE_LEADER, RESOURCEPATH, LEADER_DIAGRAM_PATH, modules);
		HenshinGraph follower = new HenshinGraph(ROLE_FOLLOWER, RESOURCEPATH, FOLLOWER_DIAGRAM_PATH, modules);
		HenshinGraph joiningVehicle = new HenshinGraph(ROLE_JOININGVEHICLE, RESOURCEPATH, JV_DIAGRAM_PATH, modules);

		// Delete Files
		deleteFiles(RESOURCEPATH);

		// Start Simple Joining Maneuver
//		simpleSimu_JoiningManeuver(leader);

		// Start Simu_JoiningManeuver
		simu_JoiningManeuver(leader, follower, joiningVehicle);
	}

	private static void simu_JoiningManeuver(HenshinGraph leader, HenshinGraph follower, HenshinGraph joiningVehicle) {

		System.out.println("\n*****************************************");
		System.out.println("Start the Simulation of Joining Maneuver.");
		System.out.println("*****************************************\n");

		System.out.println("\n******Communication receives a Request.******");
		// A platoon accepts simultaneously only one JV. The 2. Rule was rejected.
		leader.runRule(RULE_RECEIVEREQUEST);

		// The Follower x was selected to form Gap.
		leader.runRule(RULE_COMPUTEGAP, new Parameter("p", 4));

		// The Joining-Collaboration was created.
		leader.runRule(RULE_CREATEJOININGCOLLABORATION);

		// Leader sends its model to joiningVehicle
		String positiveAck = "ContextModel_PositiveAck.xmi";
		leader.sendSharedPlatoonStructure(positiveAck);

		System.out.println("\n******Communication between Leader and JoiningVehicle******");
		// JV receives model from leader, which was limited by read rules.
		joiningVehicle.getSharedPlatoonStructure(positiveAck);

		// JV has moved to the Insert Position and waits the command to join.
		joiningVehicle.runRuleAnddoRelection(leader,RULE_MOVETOINSERTIONPOSITION);

		// Leader sends its model to follower
		String FormGapCommand = "ContextModel_FormGapCommand.xmi";
		leader.sendSharedPlatoonStructure(FormGapCommand);

		System.out.println("\n******Communication between Leader and Follower******");
		// Follower receives model from leader, which was limited by read rules.
		follower.getSharedPlatoonStructure(FormGapCommand);

		// A temporary platoon has be created. F2 has switched his role to a leader.
		follower.runRuleAnddoRelection(leader,RULE_FORMTEMPORALPLATOON);

		// F2 has formed the Gap.
		follower.runRuleAnddoRelection(leader,RULE_FORMGAP, new Parameter("x", 10));

		// Leader sends its model to joiningVehicle
		String JoinCommand = "ContextModel_JoinCommand.xmi";
		leader.sendSharedPlatoonStructure(JoinCommand);

		System.out.println("\n******Communication between Leader and JoiningVehicle******");
		// JoiningVehicle receives model from leader, which was limited by read rules.
		joiningVehicle.getSharedPlatoonStructure(JoinCommand);

		// Leader communicates now with JV
		System.out.println("\n******Communication between Leader and JoiningVehicle******");
		// JV has inserted in Gap
		joiningVehicle.runRuleAnddoRelection(leader,RULE_INSERTINGAP, new Parameter("lengthOfVehicle", 3));

		// JV has became a new Follower in platoon.
		joiningVehicle.runRuleAnddoRelection(leader, RULE_BECOMESNEWFOLLOWER);

		// Leader sends its model to follower
		String MergePlatoonCommand = "ContextModel_MergePlatoonCommand.xmi";
		leader.sendSharedPlatoonStructure(MergePlatoonCommand);

		System.out.println("\n******Communication between Leader and Follower******");
		// Follower receives model from leader, which was limited by read rules.
		follower.getSharedPlatoonStructure(MergePlatoonCommand);

		// F2 has merges its gap.
		follower.runRuleAnddoRelection(leader,RULE_MERGEGAP);

		// F2 has switched from a temporary leader to a normal follower again.
		follower.runRuleAnddoRelection(leader,RULE_MERGEPLATOON);
		follower.runRule(READRULE_FOLLOWERINCOORD);

		System.out.println("\n******Collaboraton finished******");
		// The Joining-Collaboration has removed.
		leader.runRule(RULE_REMOVEJOININGCOLLABORATION);
	}

	public static void checkExistenceOfInstanceDiagram(String filePath) {
		if (!new File(RESOURCEPATH + "/" + filePath).exists())
			throw new RuntimeException("Instance Digram: " + filePath
					+ " can not be found. Please create it before excute the simulator.");

	}

	private static void simpleSimu_JoiningManeuver(HenshinGraph leader) {

		System.out.println("\n*****************************************");
		System.out.println("Start the Simulation of Joining Maneuver.");
		System.out.println("*****************************************\n");
		// A platoon accepts simultaneously only one JV. The 2. Rule was rejected.
		leader.runRule(RULE_RECEIVEREQUEST);
		leader.runRule(RULE_RECEIVEREQUEST);

		// The Follower 2 was selected to form Gap.
		leader.runRule(RULE_COMPUTEGAP, new Parameter("p", 4));

		// The Joining-Collaboration was created.
		leader.runRule(RULE_CREATEJOININGCOLLABORATION);
		leader.runRule(RULE_CREATEJOININGCOLLABORATION);

		// The JV has moved to the Insert Position and waits the command to join.
		leader.runRule(RULE_MOVETOINSERTIONPOSITION);

		// Leader communicates now with F2.
		// A temporary platoon has be created. F2 has switched his role to a leader.
		leader.runRule(RULE_FORMTEMPORALPLATOON);

		// All Followers after F2 (the temporary leader) have switched their platoon and
		// see F2 as the new leader.
		leader.runRule(RULE_SWITCHPLATOONFLAGLOOP);

		// F2 has formed the Gap.
		leader.runRule(RULE_FORMGAP, new Parameter("x", 10));

		// Leader communicates now with JV
		// JV has inserted in Gap
		leader.runRule(RULE_INSERTINGAP, new Parameter("lengthOfVehicle", 3));

		// JV has became a new Follower in platoon.
		leader.runRule(RULE_BECOMESNEWFOLLOWER);

		// Leader communicates now with F2
		// F2 has merges its gap.
		leader.runRule(RULE_MERGEGAP);

		// F2 has switched from a temporary leader to a normal follower again.
		leader.runRule(RULE_MERGEPLATOON);

		// The Joining-Collaboration has removed.
		leader.runRule(RULE_REMOVEJOININGCOLLABORATION);

	}

	private static void initPlatoon(HenshinGraph platoon) {
		System.out.println("\n*****************************************");
		System.out.println("Initialize the platoon.");
		System.out.println("*****************************************\n");

		// Create a platoon with one leader and 6 followers.
		platoon.runRule(RULE_CREATELEADER);
		while (platoon.runRule(RULE_ADDFOLLOWER))
			;
		platoon.saveFilebyOverwrite();

		// Overwrite the diagram of leader.
		platoon.saveFilebyFileName(LEADER_DIAGRAM_PATH);
//		platoon.saveFilebyFileName(FOLLOWER_DIAGRAM_PATH);
//		platoon.saveFilebyFileName(JV_DIAGRAM_PATH);
	}

	private static void deleteFiles(String direction) {
		File dir = new File(direction);
		String[] children = dir.list();
		for (String s : children) {
			if ((s.contains("Result_") && s.contains("xmi")) || s.contains("Context") && s.contains("xmi")) {
				System.out.println("File: " + s + " has be deleted.");
				new File(dir, s).delete();
			}
		}
	}
}
