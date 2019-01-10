
public class Simulator {
	private static final String RESOURCEPATH = "henshin";
	private static final String LEADER = "leader.henshin";
	private static final String FOLLOWER = "follower.henshin";
	private static final String JONINGVEHICLE = "joiningvehicle.henshin";
	private static final String DIAGRAM_PATH = "PlatooningSystem.xmi";
	private static final String INITDIAGRAMM = "PlatooningSystem.xmi";
	private static final String PLATOONDIAGRAMM = "PlatoonLeaderDefault.xmi";
	public static int count;

	public static void main(String[] args) throws InterruptedException {
		HenshinPlatoon platoon = new HenshinPlatoon(RESOURCEPATH, DIAGRAM_PATH, LEADER, FOLLOWER, JONINGVEHICLE);
		count = 0;

		// Initiate platoon WARNING: this method will overwrite the original diagram.
//		initPlatoon(platoon,5);

		// Start Simulation
		simu_JoiningManeuver(platoon);
	}

	private static void initPlatoon(HenshinPlatoon platoon, int count) {
		platoon.runRules(HenshinPlatoon.RULE_CREATELEADER);
		for (int i = 0; i < count; i++)
			platoon.runRules(HenshinPlatoon.RULE_ADDFOLLOWER);
		platoon.saveFilebyFileName(DIAGRAM_PATH);
	}

	public static void simu_JoiningManeuver(HenshinPlatoon platoon) {
		System.out.println("\nStart the Simulation of Joining Maneuver.\n");

		// A platoon accepts simultaneously only one JV. The 2. Rule was rejected.
		platoon.runRules(HenshinPlatoon.RULE_RECEIVEREQUEST, true);
		platoon.runRules(HenshinPlatoon.RULE_RECEIVEREQUEST, true);

		// The Follower 2 was selected to form Gap.
		platoon.runRules(HenshinPlatoon.RULE_COMPUTEGAP, true);

		// The Joining-Collaboration was created.
		platoon.runRules(HenshinPlatoon.RULE_CREATEJOININGCOLLABORATION, true);

		// The JV has moved to the Insert Position and waits the command to join.
		platoon.runRules(HenshinPlatoon.RULE_MOVETOINSERTIONPOSITION, true);

		// Leader communicates now with F2.
		// A temporary platoon has be created. F2 has switched his role to a leader.
		platoon.runRules(HenshinPlatoon.RULE_FORMTEMPORALPLATOON, true);

		// All Followers after F2 (the temporary leader) have switched their platoon and
		// see F2
		// as the new leader.
		while (platoon.runRules(HenshinPlatoon.RULE_SWITCHPLATOONANDLEADER))
			;
		platoon.saveFilebyRuleName(HenshinPlatoon.RULE_SWITCHPLATOONANDLEADER);

		// F2 has formed the Gap.
		platoon.runRules(HenshinPlatoon.RULE_FORMGAP, true);

		// Leader communicates now with JV
		// JV has inserted in Gap
		platoon.runRules(HenshinPlatoon.RULE_INSERTINGAP, true);

		// JV has became a new Follower in platoon.
		platoon.runRules(HenshinPlatoon.RULE_BECOMESNEWFOLLOWER, true);

	}
}
