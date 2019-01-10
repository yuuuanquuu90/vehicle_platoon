import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.UnitApplicationImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Unit;

public class Rule {
	private String name;
	private UnitApplication rule;

	public Rule(String name, Module module, Parameter... paras) {
		this.name = name;
		rule = new UnitApplicationImpl(HenshinPlatoon.engine);
		rule.setEGraph(HenshinPlatoon.graph);
		Unit unit = module.getUnit(name);
		if (unit == null)
			System.out.println("Rule: " + name + " is not defined.");
		rule.setUnit(unit);
		setParameters(paras);

	}

	private void setParameters(Parameter... paras) {
		for (Parameter p : paras)
			rule.setParameterValue(p.getName(), p.getValue());
	}

	public boolean executeRule() {
		System.out.print(Simulator.count++ + ". Execute Rule: " + this.name);
		if (!rule.execute(null)) {
			System.out.print("   -> Warning: Step " + (int) (Simulator.count - 1) + " " + name + " is not executed.\n");
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