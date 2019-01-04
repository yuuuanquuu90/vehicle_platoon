import java.util.ArrayList;

import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.UnitApplicationImpl;
import org.eclipse.emf.henshin.model.Module;

public class Rule {
	private String name;
	private Module module;
	private EGraph graph;
	private Engine engine;

	public Rule(String name, Module module, EGraph graph, Engine engine) {
		this.name = name;
		this.module = module;
		this.graph = graph;
		this.engine = engine;

	}

	void doRule(Parameter... paras) {
		UnitApplication rule = new UnitApplicationImpl(engine);
		rule.setEGraph(graph);
		rule.setUnit(module.getUnit(this.name));
		setParameters(rule, paras);
		throwException(rule);
	}

	private void setParameters(UnitApplication rule, Parameter... paras) {
		if (paras == null)
			return;
		for (Parameter p : paras)
			rule.setParameterValue(p.getName(), p.getValue());

	}

	private void throwException(UnitApplication rule) {
		if (!rule.execute(null))
			throw new RuntimeException("Error: " + name + " is not executed.");

	}
}

class Parameter {
	private String name;
	private int value;

	public Parameter(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

}
