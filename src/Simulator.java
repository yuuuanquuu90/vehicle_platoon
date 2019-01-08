

public class Simulator {
	private static final String RESOURCEPATH = "henshin";
	private static final String LEADER = "leader.henshin";
	private static final String FOLLOWER = "follower.henshin";
	private static final String JONINGVEHICLE = "joiningvehicle.henshin";
	private static final String DIAGRAM_LEADER = "PlatoonLeaderDefault.xmi";
	public static int count;

	public static void main(String[] args) {
		HenshinPlatoon platoon = new HenshinPlatoon(RESOURCEPATH, DIAGRAM_LEADER, LEADER, FOLLOWER, JONINGVEHICLE);
		Simu_JoiningManeuver(platoon);
	}

	public static void Simu_JoiningManeuver(HenshinPlatoon platoon) {
		count = 0;
		platoon.runRules(HenshinPlatoon.RULE_RECEIVEREQUEST);
		platoon.runRules(HenshinPlatoon.RULE_COMPUTEGAP);
		platoon.runRules(HenshinPlatoon.RULE_CREATEJOININGCOLLABORATION);
		platoon.runRules(HenshinPlatoon.RULE_FORMTEMPORALPLATOON,true);
		platoon.runRules(HenshinPlatoon.RULE_SWITCHPLATOONANDLEADER1,true);
		platoon.runRules(HenshinPlatoon.RULE_SWITCHPLATOONANDLEADER2, true);
		

	}
}
