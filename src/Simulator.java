
public class Simulator {
	private static final String RESOURCEPATH = "henshin";
	private static final String LEADER = "leader.henshin";
	private static final String FOLLOWER = "follower.henshin";
	private static final String JONINGVEHICLE = "joiningvehicle.henshin";
	private static final String DIAGRAM_LEADER = "PlatoonLeaderDefault.xmi";

	public static void main(String[] args) {
		HenshinPlatoon platoon = new HenshinPlatoon(RESOURCEPATH, DIAGRAM_LEADER, LEADER, FOLLOWER,
				JONINGVEHICLE);
		Simu_JoiningManeuver(platoon);
	}

	public static void Simu_JoiningManeuver(HenshinPlatoon platoon) {
		platoon.runRules(HenshinPlatoon.RULE_RECEIVEREQUEST);
		platoon.runRules(HenshinPlatoon.RULE_COMPUTEGAP);
		platoon.runRules(HenshinPlatoon.RULE_CREATEJOININGCOLLABORATION, true);

	}
}
