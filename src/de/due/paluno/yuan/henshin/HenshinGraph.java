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
	 * by this method command =Rule
	 * 
	 * @param name  name of rule
	 * @param paras if necessarily for the in or out parameter of rule
	 * @return
	 */
	public boolean runRule(String name, Parameter... paras) {
		String command = "Behavior Rule";
		return runRule(name, command, paras);
	}

	/**
	 * @param name    name of rule
	 * @param command only relevant for output on console
	 * @param paras   if necessarily for the in or out parameter of rule
	 * @return
	 */
	public boolean runRule(String name, String command, Parameter... paras) {
		// If this rule has not been injected.
		if (rules.get(name) == null)
			doInjectRule(name);
		// Else run this rule directly.
		if (rules.get(name).executeRule(paras)) {
			System.out.println(command + "_" + this.name + "_" + printHead(count++) + ": " + name);

			// Save file
			saveFilebyRuleName(name);
			saveFilebyOverwrite();
			return true;
		} else {
			System.out.println(command + "_" + "Rule has not executed: " + name + " WARNING !!!!!");
			return false;
		}

	}

	public void saveFilebyRuleName(String rulename) {
		int c = count - 1;
		// Get Filename e.g. xx.xmi -> xx
		String tmp = filename.substring(7, filename.length() - 4);
		tmp = "Result_" + tmp + "_" + printHead(c) + "_" + rulename + ".xmi";
		resourceSet.saveEObject(graph.getRoots().get(0), tmp);
	}

	public void saveFilebyFileName(String filename) {
		resourceSet.saveEObject(graph.getRoots().get(0), filename);
	}

	public void simu_SendModel(String modelName) {
		saveFilebyFileName(modelName);
		count++;
		System.out.println("Context Model: " + modelName + " has sent.");
	}

	public void simu_GetModel(String modelName) {
		// Overwrite model
		if (this.name.equals(TestBench.ROLE_FOLLOWER) || this.name.equals(TestBench.ROLE_JOININGVEHICLE)) {
			setGraph(modelName);
			this.rules = new HashMap<String, HenshinRule>();
		} else {
			System.out.println("WARNING: Leader can not run this method.");
			return;
		}
		// Check Role and set read rules
		if (this.name.equals(TestBench.ROLE_FOLLOWER)) {
			// Overwrite model
			runRule(TestBench.READRULE_FOLLOWERINCOORD, TestBench.READRULE);
		} else if (this.name.equals(TestBench.ROLE_JOININGVEHICLE)) {
			// Overwrite model
			runRule(TestBench.READRULE_JOININGVEHICLEINCOORD, TestBench.READRULE);
		}
		saveFilebyOverwrite();
	}

	public void saveFilebyOverwrite() {
		resourceSet.saveEObject(graph.getRoots().get(0), filename);
	}

	private void doInjectRule(String name) {
		rules.put(name, new HenshinRule(name, module, engine, graph));
	}

	private String printHead(int count) {
		DecimalFormat df = new DecimalFormat("00");
		return df.format(count);
	}

}
