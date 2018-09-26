package org.matsim.contrib.opdyts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.opdyts.macrostate.SimulationMacroStateAnalyzer;
import org.matsim.contrib.opdyts.microstate.MATSimStateFactory;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.TerminationCriterion;

import floetteroed.opdyts.DecisionVariable;
import floetteroed.opdyts.SimulatorState;
import floetteroed.opdyts.searchalgorithms.Simulator;
import floetteroed.opdyts.trajectorysampling.TrajectorySampler;

/**
 * @author michaelzilske created this on 08/10/15.
 * @author Gunnar modified this since 2015.
 */
public class MATSimSimulationWrapper<U extends DecisionVariable> implements Simulator<U> {

	// -------------------- MEMBERS --------------------

	private final MATSimStateFactory<U> stateFactory;

	private final Scenario scenario;

	// A list because the order matters in the state space vector.
	private final List<SimulationMacroStateAnalyzer> simulationStateAnalyzers = new ArrayList<>();

	private AbstractModule[] replacingModules = null;

	private AbstractModule overrides = AbstractModule.emptyModule();

	// -------------------- CONSTRUCTION --------------------

	public MATSimSimulationWrapper(final Scenario scenario, final MATSimStateFactory<U> stateFactory) {
		this.stateFactory = stateFactory;
		this.scenario = scenario;

		// Because the simulation is run multiple times.
		final String outputDirectory = this.scenario.getConfig().controler().getOutputDirectory();
		this.scenario.getConfig().controler().setOutputDirectory(outputDirectory + "_0");

		// Because Opdyts assumes no systematic changes in the simulation dynamics.
		this.scenario.getConfig().strategy().setFractionOfIterationsToDisableInnovation(Double.POSITIVE_INFINITY);
		this.scenario.getConfig().planCalcScore().setFractionOfIterationsToStartScoreMSA(Double.POSITIVE_INFINITY);
	}

	// -------------------- CONFIGURATION --------------------

	public void addSimulationStateAnalyzer(final SimulationMacroStateAnalyzer analyzer) {
		if (this.simulationStateAnalyzers.contains(analyzer)) {
			throw new RuntimeException("Analyzer " + analyzer + " has already been added.");
		}
		this.simulationStateAnalyzers.add(analyzer);
	}

	public final void setReplacingModules(final AbstractModule... replacingModules) {
		this.replacingModules = replacingModules;
	}

	public final void addOverridingModule(AbstractModule abstractModule) {
		this.overrides = AbstractModule.override(Arrays.asList(this.overrides), abstractModule);
	}

	// ---------- TODO FOR COMPATIBLITY WITH AMIT'S CODE. REVISIT. ----------

	private WireOpdytsIntoMATSimControlerListener.BeforeMobsimAnalyzer beforeMobsimAnalyzer = null;

	public void setBeforeMobsimAnalyzer(
			WireOpdytsIntoMATSimControlerListener.BeforeMobsimAnalyzer beforeMobsimAnalyzer) {
		this.beforeMobsimAnalyzer = beforeMobsimAnalyzer;
	}

	private int numberOfCompletedSimulationRuns = 0;

	public int getNumberOfCompletedSimulationRuns() {
		return this.numberOfCompletedSimulationRuns;
	}

	// --------------- IMPLEMENTATION OF Simulator INTERFACE ---------------

	@Override
	public SimulatorState run(final TrajectorySampler<U> trajectorySampler) {

		/*
		 * (1) This function is called in many iterations. Each time, it executes a
		 * complete MATSim run. To avoid that the MATSim output files are overwritten
		 * each time, set iteration-specific output directory names.
		 */

		String outputDirectory = this.scenario.getConfig().controler().getOutputDirectory();
		outputDirectory = outputDirectory.substring(0, outputDirectory.lastIndexOf("_")) + "_"
				+ this.numberOfCompletedSimulationRuns;
		this.scenario.getConfig().controler().setOutputDirectory(outputDirectory);

		/*
		 * (2) Create the MATSimDecisionVariableSetEvaluator that is supposed to
		 * "optimize along" the MATSim run of this iteration.
		 */

		final WireOpdytsIntoMATSimControlerListener<U> matsimDecisionVariableEvaluator = new WireOpdytsIntoMATSimControlerListener<>(
				trajectorySampler, this.stateFactory, this.simulationStateAnalyzers, this.beforeMobsimAnalyzer);

		/*
		 * (3) Create, configure, and run a new MATSim Controler.
		 */

		final Controler controler = new Controler(this.scenario);
		if ((this.replacingModules != null) && (this.replacingModules.length > 0)) {
			controler.setModules(this.replacingModules);
		}
		controler.addOverridingModule(this.overrides);

		controler.addControlerListener(matsimDecisionVariableEvaluator);
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				binder().requestInjection(stateFactory);
			}
		});
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				binder().requestInjection(matsimDecisionVariableEvaluator);
			}
		});
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				binder().requestInjection(trajectorySampler.getObjectiveFunction());
			}
		});
		controler.setTerminationCriterion(new TerminationCriterion() {
			@Override
			public boolean continueIterations(int iteration) {
				return (!matsimDecisionVariableEvaluator.foundSolution());
			}
		});

		controler.run();
		this.numberOfCompletedSimulationRuns++;

		return matsimDecisionVariableEvaluator.getFinalState();
	}

	@Override
	public SimulatorState run(final TrajectorySampler<U> evaluator, final SimulatorState initialState) {
		if (initialState != null) {
			initialState.implementInSimulation();
		}
		return this.run(evaluator);
	}
}
