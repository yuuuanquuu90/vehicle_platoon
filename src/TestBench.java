import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class TestBench {
	public static final String PATH = "henshin";
	public static final String HENSHIN_LEADER = "leader.henshin";
	public static final String DIAGRAMM_LEADER = "PlatoonLeaderDefault.xmi";

	public static void run(String path, boolean saveResult) {
		// Create a resource set with a base directory:
		HenshinResourceSet resourceSet = new HenshinResourceSet(path);

		// Load the module:
		Module module = resourceSet.getModule(HENSHIN_LEADER, false);

		// Load the example model into an EGraph (distance diagram in the form of xmi
		// format):
		EGraph graph = new EGraphImpl(resourceSet.getResource(DIAGRAMM_LEADER));

		// Create an engine:
		Engine engine = new EngineImpl();

		// Execute the rules
		executeRules(module, graph, engine);

		// Saving the result:
		if (saveResult)
			resourceSet.saveEObject(graph.getRoots().get(0), "Result.xmi");

	}

	private static void executeRules(Module module, EGraph graph, Engine engine) {
// Receive Request
		Rule receiveRequest = new Rule("receiveRequest", module, graph, engine);
		receiveRequest.doRule();
// Compute Gap
		Rule computeGap = new Rule("computeGap", module, graph, engine);
		computeGap.doRule(new Parameter("y", 2));

	}

	public static void main(String[] args) {
		run(PATH, true);
	}

}
