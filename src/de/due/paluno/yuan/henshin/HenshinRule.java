package de.due.paluno.yuan.henshin;

import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.UnitApplicationImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Unit;


public class HenshinRule {
	private String name;
	private UnitApplication rule;

	public HenshinRule(String name, Module[] module, Engine engine, EGraph graph) {
		this.name = name;
		rule = new UnitApplicationImpl(engine);
		rule.setEGraph(graph);
		Unit unit = null;
		for (Module m : module) {
			unit = m.getUnit(name);
			if (unit != null) {
				rule.setUnit(unit);
				break;
			}
		}
		if (unit == null)
			System.out.println("WARNING Rule: " + name + " is not in henshin_diagram defined.");

	}

	private void setParameters(Parameter... paras) {
		for (Parameter p : paras)
			rule.setParameterValue(p.getName(), p.getValue());
	}

	public boolean executeRule(Parameter... paras) {
		// Set Parameters
		setParameters(paras);

		// Executes Rule
		return rule.execute(null);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UnitApplication getRule() {
		return rule;
	}

	public void setRule(UnitApplication rule) {
		this.rule = rule;
	}

}
