import java.util.HashMap;

import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class HenshinPlatoon {
	public static final String RULE_RECEIVEREQUEST = "receiveRequest";
	public static final String RULE_COMPUTEGAP = "computeGap";
	public static final String RULE_CREATEJOININGCOLLABORATION = "createJoiningCollaboration";
	public static final String RULE_FORMTEMPORALPLATOON = "formTemporalPlatoon";
	public static final String RULE_SWITCHPLATOONANDLEADER = "switchPlatoonAndLeader";
	public static final String RULE_SWITCHPLATOONANDLEADER2 = "switchPlatoonAndLeader2";
	public static final String RULE_FORMGAP = "formGap";
	public static final String RULE_MERGEGAP = "mergeGap";
	public static final String RULE_CHANGELEADER = "changeLeader";
	public static final String RULE_MOVETOINSERTIONPOSITION = "moveToInsertionPosition";
	public static final String RULE_INSERTINGAP = "insertInGap";
	public static final String RULE_BECOMESNEWFOLLOWER = "becomesNewFollower";
	public static final String RULE_ADDFOLLOWER = "addFollower";
	public static final String RULE_CREATELEADER = "createLeader";
	public static final int MODULE_LEADER = 0;
	public static final int MODULE_FOLLOWER = 1;
	public static final int MODULE_JV = 2;
	private HenshinResourceSet resourceSet;
	public static Module[] module;
	public static EGraph graph;
	public static Engine engine;
	private HashMap<String, Rule> rules;

	public HenshinPlatoon(String resourcePath, String diagramPath, String... modulePath) {
		// Create a resource set with a base directory:
		resourceSet = new HenshinResourceSet(resourcePath);

		// Load the Instance Diagram into an EGraph:
		graph = new EGraphImpl(resourceSet.getResource(diagramPath));

		// Create an engine:
		engine = new EngineImpl();

		// Init Rules Pool
		rules = new HashMap<String, Rule>();

		// Init Henshin Modules
		module = new Module[3];
		module[MODULE_LEADER] = resourceSet.getModule(modulePath[0], false);
		module[MODULE_FOLLOWER] = resourceSet.getModule(modulePath[1], false);
		module[MODULE_JV] = resourceSet.getModule(modulePath[2], false);

		// Inject Leader Rules
		doInjectLeaderRules();

		// Inject Follower Rules
		doInjectFollowerRules();

		// Inject Joining-Vehicle Rules
		doInjectJVRules();
	}

	private void doInjectLeaderRules() {
		doInjectRules(RULE_RECEIVEREQUEST, module[0]);
		doInjectRules(RULE_COMPUTEGAP, module[0], new Parameter("y", 2));
		doInjectRules(RULE_CREATEJOININGCOLLABORATION, module[0]);
		doInjectRules(RULE_ADDFOLLOWER, module[0]);
		doInjectRules(RULE_CREATELEADER, module[0]);
	}

	private void doInjectFollowerRules() {
		doInjectRules(RULE_FORMTEMPORALPLATOON, module[1]);
		doInjectRules(RULE_SWITCHPLATOONANDLEADER, module[1]);
		doInjectRules(RULE_FORMGAP, module[1], new Parameter("x", 10));
		doInjectRules(RULE_MERGEGAP, module[1]);
		doInjectRules(RULE_CHANGELEADER, module[1]);
	}

	private void doInjectJVRules() {
		doInjectRules(RULE_MOVETOINSERTIONPOSITION, module[2]);
		doInjectRules(RULE_INSERTINGAP, module[2],new Parameter("lengthOfVehicle", 3));
		doInjectRules(RULE_BECOMESNEWFOLLOWER, module[2]);
	}

	private void doInjectRules(String name, Module module, Parameter... paras) {
		rules.put(name, new Rule(name, module, paras));
	}

	public boolean runRules(String ruleName, boolean saveFile) {
		boolean out = runRules(ruleName);
		saveFilebyRuleName(ruleName);
		return out;
	}

	public boolean runRules(String ruleName) {
		return rules.get(ruleName).executeRule();
	}

	public void saveFilebyRuleName(String ruleName) {
		int count = Simulator.count - 1;
		resourceSet.saveEObject(graph.getRoots().get(0), "Result_" + count + " " + ruleName + ".xmi");
	}

	public void saveFilebyFileName(String FileName) {
		resourceSet.saveEObject(graph.getRoots().get(0), FileName);
	}

}