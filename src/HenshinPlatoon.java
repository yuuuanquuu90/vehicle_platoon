import java.text.DecimalFormat;
import java.util.HashMap;

import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class HenshinPlatoon {
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

	// Rules for Joning-Vehicle
	public static final String RULE_MOVETOINSERTIONPOSITION = "moveToInsertionPosition";
	public static final String RULE_INSERTINGAP = "insertInGap";
	public static final String RULE_BECOMESNEWFOLLOWER = "becomesNewFollower";

	// Rules for Read Rules
	public static final String RULE_FOLLOWERVISIBILITY = "followerVisibility";
	public static final String RULE_TEST = "joiningVehicleInCoord";
	// A module represents a specific Henshin_Diagram, which includes a set of
	// Rules.
	public static Module[] module;
	public static final int MODULE_LEADER = 0;
	public static final int MODULE_FOLLOWER = 1;
	public static final int MODULE_JV = 2;
	public static final int MODULE_READRULES = 3;

	// The relative path of the set of resources.
	private HenshinResourceSet resourceSet;

	// A EGraph refers to a specific Instance Diagram in the form of xmi which basis
	// of the Class Diagram.
	public static EGraph graph;

	// A Interface for interpreter engines. EngineImpl() refers to a default Engine
	// Implementation.
	public static Engine engine;

	// A set of rules, which should be injected in the EGraph as noted above.
	private HashMap<String, Rule> rules;

	/**
	 * 
	 * @param resourcePath The Path of EGraph. The relative path of the set of
	 *                     resources.
	 * @param diagramPath  The Path of EGraph. A EGraph refers to a specific
	 *                     Instance Diagram in the form of xmi which basis of the
	 *                     Class Diagram.
	 * @param modulePath   The Path of Henshin Diagram. A module represents a
	 *                     specific Henshin_Diagram, which includes a set of Rules.
	 */
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
		module = new Module[4];
		module[MODULE_LEADER] = resourceSet.getModule(modulePath[0], false);
		module[MODULE_FOLLOWER] = resourceSet.getModule(modulePath[1], false);
		module[MODULE_JV] = resourceSet.getModule(modulePath[2], false);
		module[MODULE_READRULES] = resourceSet.getModule(modulePath[3], false);
		// Inject Leader Rules
		doInjectLeaderRules();

		// Inject Follower Rules
		doInjectFollowerRules();

		// Inject Joining-Vehicle Rules
		doInjectJVRules();

		// Inject Read Rules
		doInjectReadRules();
	}

	private void doInjectLeaderRules() {
		doInjectRules(RULE_RECEIVEREQUEST, module[0]);
		doInjectRules(RULE_COMPUTEGAP, module[0]);
		doInjectRules(RULE_CREATEJOININGCOLLABORATION, module[0]);
		doInjectRules(RULE_ADDFOLLOWER, module[0]);
		doInjectRules(RULE_CREATELEADER, module[0]);
		doInjectRules(RULE_REMOVEJOININGCOLLABORATION, module[0]);
	}

	private void doInjectFollowerRules() {
		doInjectRules(RULE_FORMTEMPORALPLATOON, module[1]);
		doInjectRules(RULE_SWITCHPLATOONFLAGLOOP, module[1]);
		doInjectRules(RULE_FORMGAP, module[1]);
		doInjectRules(RULE_MERGEGAP, module[1]);
		doInjectRules(RULE_DISABLELEADERFLAG, module[1]);
		doInjectRules(RULE_MERGEPLATOON, module[1]);
	}

	private void doInjectJVRules() {
		doInjectRules(RULE_MOVETOINSERTIONPOSITION, module[2]);
		doInjectRules(RULE_INSERTINGAP, module[2]);
		doInjectRules(RULE_BECOMESNEWFOLLOWER, module[2]);
	}

	private void doInjectReadRules() {
		doInjectRules(RULE_FOLLOWERVISIBILITY, module[3]);
		doInjectRules(RULE_TEST, module[3]);
	}

	private void doInjectRules(String name, Module module) {
		rules.put(name, new Rule(name, module));
	}

	public boolean runRules(String ruleName, boolean saveFile, Parameter... paras) {
		boolean out = runRules(ruleName,paras);
		saveFilebyRuleName(ruleName);
		return out;
	}

	public boolean runRules(String ruleName, Parameter...paras) {
		return rules.get(ruleName).executeRule(paras);
	}

	public void saveFilebyRuleName(String ruleName) {
		int count = Simulator.count - 1;
		DecimalFormat df = new DecimalFormat("00");
		resourceSet.saveEObject(graph.getRoots().get(0), "Result_" + df.format(count) + "_" + ruleName + ".xmi");
	}

	public void saveFilebyFileName(String FileName) {
		resourceSet.saveEObject(graph.getRoots().get(0), FileName);
	}

}