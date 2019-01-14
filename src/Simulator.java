import java.io.File;

//14.01.2019
public class Simulator {
	private static final String RESOURCEPATH = "henshin";
	private static final String LEADER = "leader.henshin";
	private static final String FOLLOWER = "follower.henshin";
	private static final String JONINGVEHICLE = "joiningvehicle.henshin";
	private static final String DIAGRAM_PATH = "PlatooningSystem.xmi";
	public static int count;

	public static void main(String[] args) throws InterruptedException {
		// Check Existence of the Instance Diagram
		if (!new File(RESOURCEPATH + "/" + DIAGRAM_PATH).exists())
			throw new RuntimeException("Instance Digram: " + DIAGRAM_PATH
					+ " can not be found. Please create it before excute the simulator.");

		// Create Henshin Diagram Instance
		HenshinPlatoon platoon = new HenshinPlatoon(RESOURCEPATH, DIAGRAM_PATH, LEADER, FOLLOWER, JONINGVEHICLE);

		// Initiate platoon WARNING: this method will overwrite the original diagram.
		initPlatoon(platoon);

		// Start Simulation
		simu_JoiningManeuver(platoon);
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

	public static void simu_JoiningManeuver(HenshinPlatoon platoon) {
		System.out.println("\n*****************************************");
		System.out.println("Start the Simulation of Joining Maneuver.");
		System.out.println("*****************************************\n");
		count = 0;
		// A platoon accepts simultaneously only one JV. The 2. Rule was rejected.
		platoon.runRules(HenshinPlatoon.RULE_RECEIVEREQUEST, true);
		platoon.runRules(HenshinPlatoon.RULE_RECEIVEREQUEST, true);

		// The Follower 2 was selected to form Gap.
		platoon.runRules(HenshinPlatoon.RULE_COMPUTEGAP, true);

		// The Joining-Collaboration was created.
		platoon.runRules(HenshinPlatoon.RULE_CREATEJOININGCOLLABORATION, true);
		platoon.runRules(HenshinPlatoon.RULE_CREATEJOININGCOLLABORATION, true);

		// The JV has moved to the Insert Position and waits the command to join.
		platoon.runRules(HenshinPlatoon.RULE_MOVETOINSERTIONPOSITION, true);

		// Leader communicates now with F2.
		// A temporary platoon has be created. F2 has switched his role to a leader.
		platoon.runRules(HenshinPlatoon.RULE_FORMTEMPORALPLATOON, true);

		// All Followers after F2 (the temporary leader) have switched their platoon and
		// see F2 as the new leader.
		platoon.runRules(HenshinPlatoon.RULE_SWITCHPLATOONFLAGLOOP);

		// F2 has formed the Gap.
		platoon.runRules(HenshinPlatoon.RULE_FORMGAP, true);

		// Leader communicates now with JV
		// JV has inserted in Gap
		platoon.runRules(HenshinPlatoon.RULE_INSERTINGAP, true);

		// JV has became a new Follower in platoon.
		platoon.runRules(HenshinPlatoon.RULE_BECOMESNEWFOLLOWER, true);

		// Leader communicates now with F2
		// F2 has merges its gap.
		platoon.runRules(HenshinPlatoon.RULE_MERGEGAP, true);

		// F2 has switched from a temporary leader to a normal follower again.
		platoon.runRules(HenshinPlatoon.RULE_MERGEPLATOON, true);

		// The Joining-Collaboration has removed.
		platoon.runRules(HenshinPlatoon.RULE_REMOVEJOININGCOLLABORATION, true);

	}
}
