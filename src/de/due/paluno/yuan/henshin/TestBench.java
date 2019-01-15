package de.due.paluno.yuan.henshin;

import java.io.File;

public class TestBench {
	// File Path
	private static final String RESOURCEPATH = "henshin";
	private static final String LEADER = "leader.henshin";
	private static final String FOLLOWER = "follower.henshin";
	private static final String JONINGVEHICLE = "joiningvehicle.henshin";
	private static final String READRULES = "readrules.henshin";
	private static final String LEADER_DIAGRAM_PATH = "PlatooningSystem_Leader.xmi";
	private static final String FOLLOWER_DIAGRAM_PATH = "PlatooningSystem_Follower.xmi";
	private static final String JV_DIAGRAM_PATH = "PlatooningSystem_JoiningVehicle.xmi";
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
	public static final String RULE_FOLLOWERVISIBILITY = "followerVisibility";
	public static final String RULE_JOININGVEHICLEINCOORD = "joiningVehicleInCoord";

	public static void main(String[] args) {
		// Check Existence of the Instance Diagram
		checkExistenceOfInstanceDiagram(LEADER_DIAGRAM_PATH);
		checkExistenceOfInstanceDiagram(FOLLOWER_DIAGRAM_PATH);
		checkExistenceOfInstanceDiagram(JV_DIAGRAM_PATH);

		// Create the related graphs of each roles.
		String[] modules = { LEADER, FOLLOWER, JONINGVEHICLE, READRULES };
		HenshinGraph leader = new HenshinGraph("PlatoonLeader", RESOURCEPATH, LEADER_DIAGRAM_PATH, modules);
		HenshinGraph follower = new HenshinGraph("PlatoonFollower", RESOURCEPATH, FOLLOWER_DIAGRAM_PATH, modules);
		HenshinGraph joiningVehicle = new HenshinGraph("JoiningVehicle", RESOURCEPATH, JV_DIAGRAM_PATH, modules);

		// Delete Files
		deleteFiles("henshin");

		// Start Simple Joining Maneuver
//		simpleSimu_JoiningManeuver(leader);

		// Start Simu_JoiningManeuver
		simu_JoiningManeuver(leader, follower, joiningVehicle);
	}

	private static void simu_JoiningManeuver(HenshinGraph leader, HenshinGraph follower, HenshinGraph joiningVehicle) {

		// Init Leader View
		initPlatoon(leader);

		System.out.println("\n*****************************************");
		System.out.println("Start the Simulation of Joining Maneuver.");
		System.out.println("*****************************************\n");

		// A platoon accepts simultaneously only one JV. The 2. Rule was rejected.
		leader.runRule(RULE_RECEIVEREQUEST, true);

		// The Follower 2 was selected to form Gap.
		leader.runRule(RULE_COMPUTEGAP, new Parameter("p", 4));

		// The Joining-Collaboration was created.
		leader.runRule(RULE_CREATEJOININGCOLLABORATION, true);

		// Leader sends its model to joiningVehicle
		String positiveAck = "Result_ContextModel_PositiveAck.xmi";
		leader.saveFilebyFileName(positiveAck);

		// JV receives model form leader
		joiningVehicle.setGraph(positiveAck);
		joiningVehicle.runRule(RULE_JOININGVEHICLEINCOORD);
		joiningVehicle.saveFilebyOverwrite();
	}

	public static void checkExistenceOfInstanceDiagram(String filePath) {
		if (!new File(RESOURCEPATH + "/" + filePath).exists())
			throw new RuntimeException("Instance Digram: " + filePath
					+ " can not be found. Please create it before excute the simulator.");

	}

	private static void simpleSimu_JoiningManeuver(HenshinGraph leader) {

		// Init Platoon
		initPlatoon(leader);

		System.out.println("\n*****************************************");
		System.out.println("Start the Simulation of Joining Maneuver.");
		System.out.println("*****************************************\n");
		// A platoon accepts simultaneously only one JV. The 2. Rule was rejected.
		leader.runRule(RULE_RECEIVEREQUEST, true);
		leader.runRule(RULE_RECEIVEREQUEST, true);

		// The Follower 2 was selected to form Gap.
		leader.runRule(RULE_COMPUTEGAP, true, new Parameter("p", 4));

		// The Joining-Collaboration was created.
		leader.runRule(RULE_CREATEJOININGCOLLABORATION, true);
		leader.runRule(RULE_CREATEJOININGCOLLABORATION, true);

		// The JV has moved to the Insert Position and waits the command to join.
		leader.runRule(RULE_MOVETOINSERTIONPOSITION, true);

		// Leader communicates now with F2.
		// A temporary platoon has be created. F2 has switched his role to a leader.
		leader.runRule(RULE_FORMTEMPORALPLATOON, true);

		// All Followers after F2 (the temporary leader) have switched their platoon and
		// see F2 as the new leader.
		leader.runRule(RULE_SWITCHPLATOONFLAGLOOP);

		// F2 has formed the Gap.
		leader.runRule(RULE_FORMGAP, true, new Parameter("x", 10));

		// Leader communicates now with JV
		// JV has inserted in Gap
		leader.runRule(RULE_INSERTINGAP, true, new Parameter("lengthOfVehicle", 3));

		// JV has became a new Follower in platoon.
		leader.runRule(RULE_BECOMESNEWFOLLOWER, true);

		// Leader communicates now with F2
		// F2 has merges its gap.
		leader.runRule(RULE_MERGEGAP, true);

		// F2 has switched from a temporary leader to a normal follower again.
		leader.runRule(RULE_MERGEPLATOON, true);

		// The Joining-Collaboration has removed.
		leader.runRule(RULE_REMOVEJOININGCOLLABORATION, true);

	}

	private static void initPlatoon(HenshinGraph platoon) {
		System.out.println("\n*****************************************");
		System.out.println("Initialize the platoon.");
		System.out.println("*****************************************\n");
		platoon.runRule(RULE_CREATELEADER);
		while (platoon.runRule(RULE_ADDFOLLOWER))
			;
		platoon.saveFilebyOverwrite();
	}

	private static void deleteFiles(String direction) {
		File dir = new File(direction);
		String[] children = dir.list();
		for (String s : children) {
			if (s.contains("Result_") && s.contains("xmi")) {
				System.out.println("File: " + s + " has be deleted.");
				new File(dir, s).delete();
			}
		}
	}
}
