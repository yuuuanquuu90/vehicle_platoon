import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.UnitApplicationImpl;
import org.eclipse.emf.henshin.model.Module;

public class Rule {
	private String name;
	private UnitApplication rule;

	public Rule(String name, Module module, Parameter... paras) {
		this.name = name;
		rule = new UnitApplicationImpl(HenshinPlatoon.engine);
		rule.setEGraph(HenshinPlatoon.graph);
		rule.setUnit(module.getUnit(this.name));
		setParameters(paras);
	}

	private void setParameters(Parameter... paras) {
		for (Parameter p : paras)
			rule.setParameterValue(p.getName(), p.getValue());
	}

	public void executeRule() {
		System.out.println(Simulator.count++ + ". Execute Rule: " + this.name + "\n");
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
