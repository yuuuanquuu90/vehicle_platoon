import java.text.DecimalFormat;

import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.UnitApplicationImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Unit;

public class Rule {
	private String name;
	private UnitApplication rule;

	public Rule(String name, Module module) {
		this.name = name;
		rule = new UnitApplicationImpl(HenshinPlatoon.engine);
		rule.setEGraph(HenshinPlatoon.graph);
		Unit unit = module.getUnit(name);
		if (unit == null)
			System.out.println("Rule: " + name + " is not defined.");
		rule.setUnit(unit);
//		setParameters(paras);

	}

	private void setParameters(Parameter... paras) {
		for (Parameter p : paras)
			rule.setParameterValue(p.getName(), p.getValue());
	}

	public boolean executeRule(Parameter... paras) {
		//Set Parameters
		setParameters(paras);
		//Executes Rule
		DecimalFormat df = new DecimalFormat("00");
		System.out.print(df.format(Simulator.count++) + ". Execute Rule: " + this.name);
		if (!rule.execute(null)) {
			System.out.print(
					"   -> Warning: Step " + df.format(Simulator.count - 1) + " " + name + " is not executed.\n");
			return false;
		} else {
			System.out.println();
			return true;
		}
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