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
		// Inject Rules
		doInjectRules(RULE_RECEIVEREQUEST, module[0]);
		doInjectRules(RULE_COMPUTEGAP, module[0], new Parameter("y", 2));
		doInjectRules(RULE_CREATEJOININGCOLLABORATION, module[0]);
	}

	private void doInjectFollowerRules() {

	}

	private void doInjectJVRules() {

	}

	private void doInjectRules(String name, Module module, Parameter... paras) {
		rules.put(name, new Rule(name, module, paras));
	}

	public void runRules(String ruleName, boolean saveFile) {
		runRules(ruleName);
		resourceSet.saveEObject(graph.getRoots().get(0), "Result_" + ruleName + ".xmi");
	}

	public void runRules(String ruleName) {
		rules.get(ruleName).executeRule();
	}

}
