package org.matsim.contrib.opdyts;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.opdyts.macrostate.DifferentiatedLinkOccupancyAnalyzer;
import org.matsim.contrib.opdyts.macrostate.SimulationMacroStateAnalyzer;
import org.matsim.contrib.opdyts.microstate.MATSimStateFactory;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.gbl.MatsimRandom;

import floetteroed.opdyts.DecisionVariable;
import floetteroed.opdyts.DecisionVariableRandomizer;
import floetteroed.opdyts.ObjectiveFunction;
import floetteroed.opdyts.convergencecriteria.ConvergenceCriterion;
import floetteroed.opdyts.convergencecriteria.FixedIterationNumberConvergenceCriterion;
import floetteroed.opdyts.searchalgorithms.RandomSearch;
import floetteroed.opdyts.searchalgorithms.RandomSearchBuilder;
import floetteroed.opdyts.searchalgorithms.SelfTuner;
import floetteroed.utilities.TimeDiscretization;

/**
 * 
 * @author Gunnar Flötteröd
 *
 */
public class MATSimOpdytsRunner<U extends DecisionVariable> {

	// -------------------- CONSTANTS --------------------

	private final String outputDirectory;

	private final OpdytsConfigGroup opdytsConfig;

	private final TimeDiscretization timeDiscretization;

	private final MATSimSimulationWrapper<U> matsimSimulationWrapper;

	// -------------------- MEMBERS --------------------

	private ConvergenceCriterion convergenceCriterion;

	private SelfTuner selfTuner;

	// -------------------- CONSTRUCTION --------------------

	public MATSimOpdytsRunner(final Scenario scenario, final MATSimStateFactory<U> stateFactory) {

		this.outputDirectory = scenario.getConfig().controler().getOutputDirectory();
		this.opdytsConfig = ConfigUtils.addOrGetModule(scenario.getConfig(), OpdytsConfigGroup.class);
		this.timeDiscretization = new TimeDiscretization(this.opdytsConfig.getStartTime_s(),
				this.opdytsConfig.getBinSize_s(), this.opdytsConfig.getBinCount());

		this.matsimSimulationWrapper = new MATSimSimulationWrapper<>(scenario, stateFactory);
		final Set<String> networkModes = new HashSet<>(scenario.getConfig().qsim().getMainModes());
		if (networkModes.size() > 0) {
			this.matsimSimulationWrapper
					.addSimulationStateAnalyzer(new DifferentiatedLinkOccupancyAnalyzer(this.timeDiscretization,
							networkModes, new LinkedHashSet<>(scenario.getNetwork().getLinks().keySet())));
		}

		this.convergenceCriterion = new FixedIterationNumberConvergenceCriterion(
				this.opdytsConfig.getNumberOfIterationsForConvergence(),
				this.opdytsConfig.getNumberOfIterationsForAveraging());

		this.selfTuner = new SelfTuner(this.opdytsConfig.getInitialEquilibriumGapWeight(),
				this.opdytsConfig.getInitialUniformityGapWeight());
		this.selfTuner.setInertia(this.opdytsConfig.getInertia());
		this.selfTuner.setNoisySystem(this.opdytsConfig.isNoisySystem());
		this.selfTuner.setWeightScale(this.opdytsConfig.getSelfTuningWeightScale());

	}

	// --------------- SETTERS TO OVERRIDE OPDYTS DEFAULT CLASSES ---------------

	public void setConvergenceCriterion(final ConvergenceCriterion convergenceCriterion) {
		this.convergenceCriterion = convergenceCriterion;
	}

	public void setSelfTuner(final SelfTuner selfTuner) {
		this.selfTuner = selfTuner;
	}

	// ---------- SETTERS FOR GETTING MODULES INTO THE MATSim Controler ----------

	public void addSimulationStateAnalyzer(final SimulationMacroStateAnalyzer analyzer) {
		this.matsimSimulationWrapper.addSimulationStateAnalyzer(analyzer);
	}

	public final void setReplacingModules(final AbstractModule... replacingModules) {
		this.matsimSimulationWrapper.setReplacingModules(replacingModules);
	}

	public final void addOverridingModule(AbstractModule abstractModule) {
		this.matsimSimulationWrapper.addOverridingModule(abstractModule);
	}

	// -------------------- RUN --------------------

	public void run(final DecisionVariableRandomizer<U> randomizer, final U initialDecisionVariable,
			final ObjectiveFunction objectiveFunction) {

		final RandomSearchBuilder<U> builder = new RandomSearchBuilder<>();
		builder.setConvergenceCriterion(this.convergenceCriterion).setDecisionVariableRandomizer(randomizer)
				.setInitialDecisionVariable(initialDecisionVariable)
				.setMaxOptimizationStages(this.opdytsConfig.getMaxIteration())
				.setMaxSimulationTransitions(this.opdytsConfig.getMaxTransition())
				.setObjectiveFunction(objectiveFunction).setRandom(MatsimRandom.getRandom()).setSelfTuner(selfTuner)
				.setSimulator(this.matsimSimulationWrapper);
		final RandomSearch<U> randomSearch = builder.build();

		randomSearch.setLogPath(this.outputDirectory);
		randomSearch.setMaxTotalMemory(this.opdytsConfig.getMaxTotalMemory());
		randomSearch.setMaxMemoryPerTrajectory(this.opdytsConfig.getMaxMemoryPerTrajectory());
		randomSearch.setWarmupIterations(this.opdytsConfig.getWarmUpIterations());
		randomSearch.setUseAllWarmupIterations(this.opdytsConfig.getUseAllWarmUpIterations());

		randomSearch.run();
	}
}
