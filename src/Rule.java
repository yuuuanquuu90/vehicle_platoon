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
			throwException(new NullPointerException("Rule: " + name + " is not defined.\n"));
		rule.setUnit(unit);
		setParameters(paras);

	}

	private void setParameters(Parameter... paras) {
		for (Parameter p : paras)
			rule.setParameterValue(p.getName(), p.getValue());
	}

	public void executeRule() {
		System.out.println(Simulator.count++ + ". Execute Rule: " + this.name + "\n");
		if (!rule.execute(null))
			throwException(new RuntimeException("Error: " + name + " is not executed.\n"));
	}

	private void throwException(Exception exc) {
		try {
			throw exc;
		} catch (Exception e) {
			e.printStackTrace();
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
