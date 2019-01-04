import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.impl.UnitApplicationImpl;
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

		// Load the example model into an EGraph:
		EGraph graph = new EGraphImpl(resourceSet.getResource(DIAGRAMM_LEADER));
		// Create an engine and a rule application:
		Engine engine = new EngineImpl();
		UnitApplication createAccountApp = new UnitApplicationImpl(engine);
		createAccountApp.setEGraph(graph);
		createAccountApp.setUnit(module.getUnit("receiveRequest"));
		if (!createAccountApp.execute(null)) {
			throw new RuntimeException("Error receiveRequest");
		}
		// Saving the result:
		if (saveResult) {
//			System.out.println(graph.getRoots().get(0));
			resourceSet.saveEObject(graph.getRoots().get(0), "Result.xmi");
		}

	}

	public static void main(String[] args) {
		run(PATH, true);
	}

	private static String getResultFileName(String s) {
		StringBuilder sb = new StringBuilder(s);
		sb.insert(s.length() - 4, "_Result");
		return sb.toString();
	}
}
