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
import org.matsim.core.controler.Controler;
import org.matsim.core.gbl.MatsimRandom;

import floetteroed.opdyts.DecisionVariable;
import floetteroed.opdyts.DecisionVariableRandomizer;
import floetteroed.opdyts.ObjectiveFunction;
import floetteroed.opdyts.SimulatorState;
import floetteroed.opdyts.convergencecriteria.ConvergenceCriterion;
import floetteroed.opdyts.convergencecriteria.FixedIterationNumberConvergenceCriterion;
import floetteroed.opdyts.searchalgorithms.RandomSearch;
import floetteroed.opdyts.searchalgorithms.RandomSearchBuilder;
import floetteroed.opdyts.searchalgorithms.SelfTuner;
import floetteroed.utilities.TimeDiscretization;

/**
 * The main class for running a MATSim/Opdyts optimization.
 * <p>
 * Implementations of the following interfaces receive injections during every
 * optimization stage (comprising one MATSim run):
 * <ul>
 * <li>StateFactory
 * <li>ObjectiveFunction
 * <li>DecisionVariableRandomizer
 * <li>ConvergenceCriterion
 * </ul>
 * 
 * @author Gunnar Flötteröd
 *
 */
public class MATSimOpdytsRunner<U extends DecisionVariable, X extends SimulatorState> {

	// -------------------- INNER CLASS --------------------

	/*
	 * TODO This is fairly ad hoc, in order to be able to use the pSim as is. Should be revisited.
	 */
	
	public static interface WantsControlerReferenceBeforeInjection {
		
		public void meet(Controler controler);
		
	}
	
	// -------------------- CONSTANTS --------------------

	private final String outputDirectory;

	private final OpdytsConfigGroup opdytsConfig;

	private final TimeDiscretization timeDiscretization;

	private final MATSimSimulationWrapper<U, X> matsimSimulationWrapper;

	// -------------------- MEMBERS --------------------

	private ConvergenceCriterion convergenceCriterion;

	private SelfTuner selfTuner;

	// -------------------- CONSTRUCTION --------------------

	public MATSimOpdytsRunner(final Scenario scenario, final MATSimStateFactory<U, X> stateFactory) {

		this.outputDirectory = scenario.getConfig().controler().getOutputDirectory();
		this.opdytsConfig = ConfigUtils.addOrGetModule(scenario.getConfig(), OpdytsConfigGroup.class);
		this.timeDiscretization = new TimeDiscretization(this.opdytsConfig.getStartTime_s(),
				this.opdytsConfig.getBinSize_s(), this.opdytsConfig.getBinCount());

		this.matsimSimulationWrapper = new MATSimSimulationWrapper<>(scenario, stateFactory,
				opdytsConfig.getEnBlockSimulationIterations());
		final Set<String> networkModes = new HashSet<>(scenario.getConfig().qsim().getMainModes());
		if (networkModes.size() > 0) {
			this.matsimSimulationWrapper
					.addSimulationStateAnalyzer(new DifferentiatedLinkOccupancyAnalyzer(this.timeDiscretization,
							networkModes, new LinkedHashSet<>(scenario.getNetwork().getLinks().keySet())));
		}
		this.matsimSimulationWrapper.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				this.binder().requestInjection(stateFactory);
			}
		});

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

	// ----- CONFIGURATION OF (NOT YET STARTED/CREATED) SIMULATIONS/CONTROLERS -----

	public void addSimulationStateAnalyzer(SimulationMacroStateAnalyzer analyzer) {
		this.matsimSimulationWrapper.addSimulationStateAnalyzer(analyzer);
	}

	public void setReplacingModules(AbstractModule... replacingModules) {
		this.matsimSimulationWrapper.setReplacingModules(replacingModules);
	}

	public void addOverridingModule(AbstractModule abstractModule) {
		this.matsimSimulationWrapper.addOverridingModule(abstractModule);
	}

	public void setFreezeRandomSeed(boolean freezeRandomSeed) {
		this.matsimSimulationWrapper.setFreezeRandomSeed(freezeRandomSeed);
	}

	public void addWantsControlerReferenceBeforeInjection(WantsControlerReferenceBeforeInjection wantsControlerReferenceBeforeInjection) {
		this.matsimSimulationWrapper.addWantsControlerReferenceBeforeInjection(wantsControlerReferenceBeforeInjection);
	}
	
	// -------------------- RUN --------------------

	public void run(final DecisionVariableRandomizer<U> randomizer, final U initialDecisionVariable,
			final ObjectiveFunction<X> objectiveFunction) {

		final RandomSearchBuilder<U, X> builder = new RandomSearchBuilder<>();
		builder.setConvergenceCriterion(this.convergenceCriterion).setDecisionVariableRandomizer(randomizer)
				.setInitialDecisionVariable(initialDecisionVariable)
				.setMaxOptimizationStages(this.opdytsConfig.getMaxIteration())
				.setMaxSimulationTransitions(this.opdytsConfig.getMaxTransition())
				.setObjectiveFunction(objectiveFunction).setRandom(MatsimRandom.getRandom()).setSelfTuner(selfTuner)
				.setSimulator(this.matsimSimulationWrapper);
		final RandomSearch<U, X> randomSearch = builder.build();

		this.matsimSimulationWrapper.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				this.binder().requestInjection(randomizer);
				this.binder().requestInjection(objectiveFunction);
				this.binder().requestInjection(convergenceCriterion);
			}
		});

		randomSearch.setLogPath(this.outputDirectory);
		randomSearch.setMaxTotalMemory(this.opdytsConfig.getMaxTotalMemory());
		randomSearch.setMaxMemoryPerTrajectory(this.opdytsConfig.getMaxMemoryPerTrajectory());
		randomSearch.setWarmupIterations(this.opdytsConfig.getWarmUpIterations());
		randomSearch.setUseAllWarmupIterations(this.opdytsConfig.getUseAllWarmUpIterations());

		randomSearch.run();
	}
}
