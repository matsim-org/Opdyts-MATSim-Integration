package org.matsim.contrib.opdyts;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.opdyts.macrostate.DifferentiatedLinkOccupancyAnalyzer;
import org.matsim.core.config.ConfigUtils;
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

	// -------------------- MEMBERS --------------------

	private final OpdytsConfigGroup opdytsConfig;

	private TimeDiscretization timeDiscretization;

	private ConvergenceCriterion convergenceCriterion;

	private RandomSearch<U> randomSearch;

	private Scenario scenario;

	// -------------------- CONSTRUCTION --------------------

	public MATSimOpdytsRunner(final Scenario scenario) {
		this.opdytsConfig = ConfigUtils.addOrGetModule(scenario.getConfig(), OpdytsConfigGroup.class);
		this.timeDiscretization = newTimeDiscretization();
		this.convergenceCriterion = newFixedIterationNumberConvergenceCriterion();
		this.scenario = scenario;
	}

	// -------------------- INTERNALS --------------------

	private FixedIterationNumberConvergenceCriterion newFixedIterationNumberConvergenceCriterion() {
		return new FixedIterationNumberConvergenceCriterion(this.opdytsConfig.getNumberOfIterationsForConvergence(),
				this.opdytsConfig.getNumberOfIterationsForAveraging());
	}

	private TimeDiscretization newTimeDiscretization() {
		return new TimeDiscretization(this.opdytsConfig.getStartTime_s(), this.opdytsConfig.getBinSize(),
				this.opdytsConfig.getBinCount());
	}

	// -------------------- IMPLEMENTATION --------------------

	// alternatively, one can set all arguments via setters/builders. Amit July'17
	// public void run(final MATSimSimulationWrapper<U> matsim, final
	// DecisionVariableRandomizer<U> randomizer,
	// final U initialDecisionVariable, final ObjectiveFunction objectiveFunction) {
	//
	// final RandomSearch<U> result = new RandomSearch<U>(matsim, randomizer,
	// initialDecisionVariable,
	// convergenceCriterion, this.opdytsConfig.getMaxIteration(),
	// this.opdytsConfig.getMaxTransition(),
	// this.opdytsConfig.getPopulationSize(), objectiveFunction);
	//
	// result.setLogPath(this.opdytsConfig.getOutputDirectory());
	// result.setMaxTotalMemory(this.opdytsConfig.getMaxTotalMemory());
	// result.setMaxMemoryPerTrajectory(this.opdytsConfig.getMaxMemoryPerTrajectory());
	// result.setIncludeCurrentBest(this.opdytsConfig.isIncludeCurrentBest());
	// result.setRandom(MatsimRandom.getRandom());
	// result.setInterpolate(this.opdytsConfig.isInterpolate());
	// result.setWarmupIterations(this.opdytsConfig.getWarmUpIterations());
	// result.setUseAllWarmupIterations(this.opdytsConfig.getUseAllWarmUpIterations());
	// result.setInitialEquilibriumGapWeight(this.opdytsConfig.getEquilibriumGapWeight());
	// result.setInitialUniformityGapWeight(this.opdytsConfig.getUniformityGapWeight());
	//
	// final SelfTuner selfTuner = new SelfTuner(this.opdytsConfig.getInertia());
	// selfTuner.setNoisySystem(this.opdytsConfig.isNoisySystem());
	// selfTuner.setWeightScale(this.opdytsConfig.getSelfTuningWeight());
	// result.setSelfTuner(selfTuner);
	//
	// this.randomSearch = result;
	//
	// result.run();
	// }

	public void run(final MATSimSimulationWrapper<U> matsim, final DecisionVariableRandomizer<U> randomizer,
			final U initialDecisionVariable, final ObjectiveFunction objectiveFunction, final String outputPath) {

		final SelfTuner selfTuner = new SelfTuner(this.opdytsConfig.getInitialEquilibriumGapWeight(),
				this.opdytsConfig.getInitialUniformityGapWeight());
		selfTuner.setInertia(this.opdytsConfig.getInertia());
		selfTuner.setNoisySystem(this.opdytsConfig.isNoisySystem());
		selfTuner.setWeightScale(this.opdytsConfig.getSelfTuningWeightScale());
		
		final RandomSearchBuilder<U> builder = new RandomSearchBuilder<>();
		builder.setConvergenceCriterion(this.convergenceCriterion).setDecisionVariableRandomizer(randomizer)
				.setInitialDecisionVariable(initialDecisionVariable)
				.setMaxOptimizationStages(this.opdytsConfig.getMaxIteration())
				.setMaxSimulationTransitions(this.opdytsConfig.getMaxTransition())
				.setObjectiveFunction(objectiveFunction).setRandom(MatsimRandom.getRandom())
				.setSelfTuner(selfTuner).setSimulator(matsim);
		final RandomSearch<U> result = builder.build();

		// System.out.println("outputPath == " + outputPath);
		// System.out.println("scenario.getConfig().controler().getOutputDirectory() ==
		// " + scenario.getConfig().controler().getOutputDirectory());
		// System.exit(0);

		// result.setLogPath(this.opdytsConfig.getOutputDirectory());
		result.setLogPath(outputPath);

		// result.setLogPath(scenario.getConfig().controler().getOutputDirectory());
		result.setMaxTotalMemory(this.opdytsConfig.getMaxTotalMemory());
		result.setMaxMemoryPerTrajectory(this.opdytsConfig.getMaxMemoryPerTrajectory());
		result.setWarmupIterations(this.opdytsConfig.getWarmUpIterations());
		result.setUseAllWarmupIterations(this.opdytsConfig.getUseAllWarmUpIterations());

		// final RandomSearch<U> result = new RandomSearch<U>(matsim, randomizer,
		// initialDecisionVariable,
		// convergenceCriterion, this.opdytsConfig.getMaxIteration(),
		// this.opdytsConfig.getMaxTransition(),
		// this.opdytsConfig.getPopulationSize(), objectiveFunction);
		//
		// result.setLogPath(this.opdytsConfig.getOutputDirectory());
		// result.setMaxTotalMemory(this.opdytsConfig.getMaxTotalMemory());
		// result.setMaxMemoryPerTrajectory(this.opdytsConfig.getMaxMemoryPerTrajectory());
		// result.setIncludeCurrentBest(this.opdytsConfig.isIncludeCurrentBest());
		// result.setRandom(MatsimRandom.getRandom());
		// result.setInterpolate(this.opdytsConfig.isInterpolate());
		// result.setWarmupIterations(this.opdytsConfig.getWarmUpIterations());
		// result.setUseAllWarmupIterations(this.opdytsConfig.getUseAllWarmUpIterations());
		// result.setInitialEquilibriumGapWeight(this.opdytsConfig.getEquilibriumGapWeight());
		// result.setInitialUniformityGapWeight(this.opdytsConfig.getUniformityGapWeight());

		this.randomSearch = result;

		result.run();
	}

	public RandomSearch<U> getRandomSearch() {
		return this.randomSearch;
	}

	// optional
	public void setTimeDiscretization(TimeDiscretization timeDiscretization) {
		this.timeDiscretization = timeDiscretization;
	}

	public TimeDiscretization getTimeDiscretization() {
		return this.timeDiscretization;
	}

	// optional
	public void setFixedIterationNumberConvergenceCriterion(ConvergenceCriterion convergenceCriterion) {
		this.convergenceCriterion = convergenceCriterion;
	}

	public ConvergenceCriterion getFixedIterationNumberConvergenceCriterion() {
		return this.convergenceCriterion;
	}

	// utils
	public void addPublicTransportOccupancyAnalyzr(final MATSimSimulationWrapper<U> matSimSimulator) {
		throw new UnsupportedOperationException("Need to revisit what sensible pt macro states would be.");
		// if (scenario.getConfig().transit().isUseTransit()) {
		// matSimSimulator.addSimulationStateAnalyzer(new
		// PTOccupancyAnalyzer.Provider(this.timeDiscretization,
		// new HashSet<>(scenario.getTransitSchedule().getFacilities().keySet())));
		// } else {
		// throw new RuntimeException("Switch to use transit is off.");
		// }
	}

	public void addNetworkModeOccupancyAnalyzr(final MATSimSimulationWrapper<U> matSimSimulator) {
		// the name is not necessarily exactly same as network modes in MATSim
		// PlansCalcRouteConfigGroup.
		// Here, this means, which needs to be counted on the links.
		// cant take network modes from PlansCalcRouteConfigGroup because this may have
		// additional modes in there
		// however, this must be same as analyzeModes in TravelTimeCalculatorConfigGroup
		Set<String> networkModes = new HashSet<>(scenario.getConfig().qsim().getMainModes());

		// add for network modes
		if (networkModes.size() > 0.) {
			// TODO NEW 2018-09-24
			matSimSimulator.addSimulationStateAnalyzer(new DifferentiatedLinkOccupancyAnalyzer(this.timeDiscretization,
					networkModes, new LinkedHashSet<>(scenario.getNetwork().getLinks().keySet())));
			// matSimSimulator.addSimulationStateAnalyzer(new
			// DifferentiatedLinkOccupancyAnalyzer.Factory(this.timeDiscretization,
			// networkModes,
			// new LinkedHashSet<>(scenario.getNetwork().getLinks().keySet())));
		}
	}
}
