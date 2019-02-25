import java.io.File;

//14.01.2019
public class Simulator {
	private static final String RESOURCEPATH = "henshin";
	private static final String LEADER = "leader.henshin";
	private static final String FOLLOWER = "follower.henshin";
	private static final String JONINGVEHICLE = "joiningvehicle.henshin";
	private static final String READRULES = "readrules.henshin";
	private static final String DIAGRAM_PATH = "PlatooningSystem.xmi";
//	private static final String DIAGRAM_PATH = "Result_03_createJoiningCollaboration.xmi";
	public static int count;

	public static void main(String[] args) throws InterruptedException {
		// Check Existence of the Instance Diagram
		if (!new File(RESOURCEPATH + "/" + DIAGRAM_PATH).exists())
			throw new RuntimeException("Instance Digram: " + DIAGRAM_PATH
					+ " can not be found. Please create it before excute the simulator.");

		// Create Henshin Diagram Instance
		HenshinPlatoon platoonLeader = new HenshinPlatoon(RESOURCEPATH, DIAGRAM_PATH, LEADER, FOLLOWER, JONINGVEHICLE,
				READRULES);
//		HenshinPlatoon platoonFollower = new HenshinPlatoon(RESOURCEPATH, DIAGRAM_PATH, LEADER, FOLLOWER, JONINGVEHICLE,
//				READRULES);
//		HenshinPlatoon platoonJV = new HenshinPlatoon(RESOURCEPATH, DIAGRAM_PATH, LEADER, FOLLOWER, JONINGVEHICLE,
//				READRULES);

		// Delete Files
		deleteFiles("henshin");
		
		// Start Simulation
//		simu_JoiningManeuver(platoonLeader);

		// test
//		test(platoonLeader);
	}

	private static void test(HenshinPlatoon platoon) {
		platoon.runRules(HenshinPlatoon.RULE_TEST, true);
	}

	private static void initPlatoon(HenshinPlatoon platoon) {
		System.out.println("\n*****************************************");
		System.out.println("Initialize the platoon.");
		System.out.println("*****************************************\n");
		count = 0;
		platoon.runRules(HenshinPlatoon.RULE_CREATELEADER);
		while (platoon.runRules(HenshinPlatoon.RULE_ADDFOLLOWER))
			;
		platoon.saveFilebyFileName(DIAGRAM_PATH);
	}

	public static void simu_JoiningManeuver(HenshinPlatoon platoonleader) {

		// Initiate platoon WARNING: this method will overwrite the original diagram.
		initPlatoon(platoonleader);

		// Start Simu
		System.out.println("\n*****************************************");
		System.out.println("Start the Simulation of Joining Maneuver.");
		System.out.println("*****************************************\n");
		count = 0;
		// A platoon accepts simultaneously only one JV. The 2. Rule was rejected.
		platoonleader.runRules(HenshinPlatoon.RULE_RECEIVEREQUEST, true);
		platoonleader.runRules(HenshinPlatoon.RULE_RECEIVEREQUEST, true);

		// The Follower 2 was selected to form Gap.
		platoonleader.runRules(HenshinPlatoon.RULE_COMPUTEGAP, true, new Parameter("p", 4));

		// The Joining-Collaboration was created.
		platoonleader.runRules(HenshinPlatoon.RULE_CREATEJOININGCOLLABORATION, true);
		platoonleader.runRules(HenshinPlatoon.RULE_CREATEJOININGCOLLABORATION, true);

		// The JV has moved to the Insert Position and waits the command to join.
		platoonleader.runRules(HenshinPlatoon.RULE_MOVETOINSERTIONPOSITION, true);

		// Leader communicates now with F2.
		// A temporary platoon has be created. F2 has switched his role to a leader.
		platoonleader.runRules(HenshinPlatoon.RULE_FORMTEMPORALPLATOON, true);

		// All Followers after F2 (the temporary leader) have switched their platoon and
		// see F2 as the new leader.
		platoonleader.runRules(HenshinPlatoon.RULE_SWITCHPLATOONFLAGLOOP);

		// F2 has formed the Gap.
		platoonleader.runRules(HenshinPlatoon.RULE_FORMGAP, true, new Parameter("x", 10));

		// Leader communicates now with JV
		// JV has inserted in Gap
		platoonleader.runRules(HenshinPlatoon.RULE_INSERTINGAP, true, new Parameter("lengthOfVehicle", 3));

		// JV has became a new Follower in platoon.
		platoonleader.runRules(HenshinPlatoon.RULE_BECOMESNEWFOLLOWER, true);

		// Leader communicates now with F2
		// F2 has merges its gap.
		platoonleader.runRules(HenshinPlatoon.RULE_MERGEGAP, true);

		// F2 has switched from a temporary leader to a normal follower again.
		platoonleader.runRules(HenshinPlatoon.RULE_MERGEPLATOON, true);

		// The Joining-Collaboration has removed.
		platoonleader.runRules(HenshinPlatoon.RULE_REMOVEJOININGCOLLABORATION, true);

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
