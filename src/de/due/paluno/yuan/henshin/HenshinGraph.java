package de.due.paluno.yuan.henshin;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class HenshinGraph {
	// Name of Graph
	private String name;
	private String filename;

	// The relative path of the set of resources.
	private HenshinResourceSet resourceSet;

	// A EGraph refers to a specific Instance Diagram in the form of xmi which basis
	// of the Class Diagram.
	public EGraph graph;

	// A Interface for interpreter engines. EngineImpl() refers to a default Engine
	// Implementation.
	public Engine engine;

	// A module represents a henshin_diagram, which includes a set of Rules.
	public Module module[];

	// A set of rules, which should be injected in the EGraph as noted above.
	private HashMap<String, HenshinRule> rules;

	// Set a count for step
	private int count;

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
	public HenshinGraph(String name, String resourcePath, String diagramPath, String... modulePath) {
		// Set name
		this.name = name;
		filename = diagramPath;

		// Create a resource set with a base directory:
		resourceSet = new HenshinResourceSet(resourcePath);

		// Load the Instance Diagram into an EGraph:
		graph = new EGraphImpl(resourceSet.getResource(diagramPath));

		// Create an engine:
		engine = new EngineImpl();

		// Set Module
		module = new Module[4];
		module[0] = resourceSet.getModule(modulePath[0], false);
		module[1] = resourceSet.getModule(modulePath[1], false);
		module[2] = resourceSet.getModule(modulePath[2], false);
		module[3] = resourceSet.getModule(modulePath[3], false);

		// Init Rules Pool
		rules = new HashMap<String, HenshinRule>();

		// Set count
		count = 0;

	}

	public EGraph getGraph() {
		return graph;
	}

	public void setGraph(String graphPath) {
		graph = new EGraphImpl(resourceSet.getResource(graphPath));
	}

	/**
	 * 
	 * @param name     name of rule
	 * @param saveFile if save the result as new file
	 * @param paras    if necessarily for the in or out parameter of rule
	 * @return if saveFile=true, a new file will be created with name:
	 *         Result_xx_ruleName.xmi
	 */
	public boolean runRule(String name, boolean saveFile, Parameter... paras) {
		boolean out = runRule(name, paras);

		// If saveFile is necessary
		if (saveFile)
			saveFilebyRuleName(name);

		return out;
	}

	public boolean runRule(String name, Parameter... paras) {
		// If this rule has not been injected.
		if (rules.get(name) == null)
			doInjectRule(name);

		// Else run this rule directly.
		count++;
		if (rules.get(name).executeRule(paras)) {
			System.out.println(printHead(count) + "Rule is executed: " + name);
			return true;
		} else {
			System.out.println(printHead(count) + "Rule is not executed: " + name + " WARNING !!!!!");
			return false;
		}
	}

	public void saveFilebyRuleName(String rulename) {
		int c = count - 1;
		resourceSet.saveEObject(graph.getRoots().get(0), "Result_" + printHead(c) + filename + ".xmi");
	}

	public void saveFilebyFileName(String filename) {
		resourceSet.saveEObject(graph.getRoots().get(0), filename);
	}

	public void saveFilebyOverwrite() {
		resourceSet.saveEObject(graph.getRoots().get(0), filename);
	}

	private void doInjectRule(String name) {
		rules.put(name, new HenshinRule(name, module, engine, graph));
	}

	private String printHead(int count) {
		DecimalFormat df = new DecimalFormat("00");
		return this.name + "_" + df.format(count) + "_";
	}

}
