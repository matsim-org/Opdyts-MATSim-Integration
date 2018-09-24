/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.contrib.opdyts;

import org.matsim.core.config.ReflectiveConfigGroup;

/**
 * 
 * @author Amit, created this on 03.06.17.
 * @author Gunnar, as of Sep 2018.
 */

public class OpdytsConfigGroup extends ReflectiveConfigGroup {

	public static final String GROUP_NAME = "opdyts";

	public OpdytsConfigGroup() {
		super(GROUP_NAME);
	}

	// ==================== UNIVERSAL ====================

	/*
	 * Defines the time discretization.
	 * 
	 * TODO This also is used in the simulation acceleration config group; could
	 * consider pulling this out of the opdyts config group. But then, there may be
	 * cases where one wishes to have different discretizations in different config
	 * groups!?
	 * 
	 * TODO Units!
	 */

	private static final String START_TIME = "startTime";
	private int startTime = 0;

	@StringGetter(START_TIME)
	public int getStartTime() {
		return this.startTime;
	}

	@StringSetter(START_TIME)
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	private static final String BIN_SIZE = "binSize";
	private int binSize = 3600;

	@StringGetter(BIN_SIZE)
	public int getBinSize() {
		return this.binSize;
	}

	@StringSetter(BIN_SIZE)
	public void setBinSize(int binSize) {
		this.binSize = binSize;
	}

	private static final String BIN_COUNT = "binCount";
	private int binCount = 24;

	@StringGetter(BIN_COUNT)
	public int getBinCount() {
		return this.binCount;
	}

	@StringSetter(BIN_COUNT)
	public void setBinCount(int binCount) {
		this.binCount = binCount;
	}

	/*
	 * The maximal memorized number of transitions in a simulation trajectory.
	 */

	private static final String MAX_MEMORY_PER_TRAJECTORY = "maxMemoryPerTrajectory";
	private int maxMemoryPerTrajectory = Integer.MAX_VALUE;

	@StringGetter(MAX_MEMORY_PER_TRAJECTORY)
	public int getMaxMemoryPerTrajectory() {
		return maxMemoryPerTrajectory;
	}

	@StringSetter(MAX_MEMORY_PER_TRAJECTORY)
	public void setMaxMemoryPerTrajectory(int maxMemoryPerTrajectory) {
		this.maxMemoryPerTrajectory = maxMemoryPerTrajectory;
	}

	/*
	 * The maximal total number of memorized simulation trajectory transitions.
	 */

	private static final String MAX_TOTAL_MEMORY = "maxTotalMemory";
	private int maxTotalMemory = Integer.MAX_VALUE;

	@StringGetter(MAX_TOTAL_MEMORY)
	public int getMaxTotalMemory() {
		return maxTotalMemory;
	}

	@StringSetter(MAX_TOTAL_MEMORY)
	public void setMaxTotalMemory(int maxTotalMemory) {
		this.maxTotalMemory = maxTotalMemory;
	}

	/*
	 * Through how many *stages* the optimization is supposed to iterate.
	 * 
	 * TODO Rename into "maxOptimizationStages".
	 * 
	 * TODO Make sure that the effective termination criterion is the stronger one
	 * out of "stage counting" and "transition counting".
	 */

	private static final String MAX_ITERATION = "maxIteration";
	private int maxIteration = 10;

	@StringGetter(MAX_ITERATION)
	public int getMaxIteration() {
		return this.maxIteration;
	}

	@StringSetter(MAX_ITERATION)
	public void setMaxIteration(int maxIteration) {
		this.maxIteration = maxIteration;
	}

	/*
	 * After how many simulation transitions, counted over all stages, the
	 * optimization is supposed to terminate.
	 * 
	 * TODO Rename into "maxSimulationTransitions".
	 * 
	 * TODO Make sure that the effective termination criterion is the stronger one
	 * out of "stage counting" and "transition counting".
	 */

	private static final String MAX_TRANSITION = "maxTransition";
	private int maxTransition = Integer.MAX_VALUE;

	@StringGetter(MAX_TRANSITION)
	public int getMaxTransition() {
		return this.maxTransition;
	}

	@StringSetter(MAX_TRANSITION)
	public void setMaxTransition(int maxTransition) {
		this.maxTransition = maxTransition;
	}

	/*
	 * Defines the warm-up iterations, an idea of Amit.
	 */

	private static final String WARM_UP_ITERATIONS = "warmUpIterations";
	private int warmUpIterations = 1;

	private static final String USE_ALL_WARM_UP_ITERATIONS = "useAllWarmUpIterations";
	private boolean useAllWarmUpIterations = false;

	@StringGetter(WARM_UP_ITERATIONS)
	public int getWarmUpIterations() {
		return warmUpIterations;
	}

	@StringSetter(WARM_UP_ITERATIONS)
	public void setWarmUpIterations(int warmUpIterations) {
		this.warmUpIterations = warmUpIterations;
	}

	@StringGetter(USE_ALL_WARM_UP_ITERATIONS)
	public boolean getUseAllWarmUpIterations() {
		return useAllWarmUpIterations;
	}

	@StringSetter(USE_ALL_WARM_UP_ITERATIONS)
	public void setUseAllWarmUpIterations(boolean useAllWarmUpIterations) {
		this.useAllWarmUpIterations = useAllWarmUpIterations;
	}

	/*
	 * Indicates that there is stochasticity present in the simulation.
	 */

	private static final String NOISY_SYSTEM = "noisySystem";
	private boolean noisySystem = true;

	@StringGetter(NOISY_SYSTEM)
	public boolean isNoisySystem() {
		return noisySystem;
	}

	@StringSetter(NOISY_SYSTEM)
	public void setNoisySystem(boolean noisySystem) {
		this.noisySystem = noisySystem;
	}

	/*
	 * How many candidate decision variables one wishes to create per optimization
	 * stage.
	 * 
	 * TODO: Rename into something like numberOfCandidateDecisions.
	 */

	private static final String POPULATION_SIZE = "populationSize";
	private int populationSize = 10;

	@StringGetter(POPULATION_SIZE)
	public int getPopulationSize() {
		return this.populationSize;
	}

	@StringSetter(POPULATION_SIZE)
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	/*
	 * Parametrizes the self-tuning logic.
	 * 
	 * Assuming that the current self-tuning will persist as a status quo, this
	 * makes sense as part of the "universal" configuration.
	 * 
	 */

	// TODO Rename into selfTuningInertia

	private static final String INERTIA = "inertia";
	private double inertia = 0.90;

	@StringGetter(INERTIA)
	public double getInertia() {
		return inertia;
	}

	@StringSetter(INERTIA)
	public void setInertia(double inertia) {
		this.inertia = inertia;
	}

	// TODO rename into selfTuningScale

	private static final String SELF_TUNING_WEIGHT = "selfTuningWeight";
	private double selfTuningWeight = 1.0;

	@StringGetter(SELF_TUNING_WEIGHT)
	public double getSelfTuningWeight() {
		return this.selfTuningWeight;
	}

	@StringSetter(SELF_TUNING_WEIGHT)
	public void setSelfTuningWeight(double selfTuningWeight) {
		this.selfTuningWeight = selfTuningWeight;
	}

	// TODO rename into initialEquilbriumGapWeight

	private static final String EQUILIBRIUM_GAP_WEIGHT = "equilibriumGapWeight";
	private double equilibriumGapWeight = 0.;

	@StringGetter(EQUILIBRIUM_GAP_WEIGHT)
	public double getEquilibriumGapWeight() {
		return equilibriumGapWeight;
	}

	@StringSetter(EQUILIBRIUM_GAP_WEIGHT)
	public void setEquilibriumGapWeight(double equilibriumGapWeight) {
		this.equilibriumGapWeight = equilibriumGapWeight;
	}

	// TODO rename into initialUniformityGapWeight

	private static final String UNIFORMITY_GAP_WEIGHT = "uniformityGapWeight";
	private double uniformityGapWeight = 0.;

	@StringGetter(UNIFORMITY_GAP_WEIGHT)
	public double getUniformityGapWeight() {
		return uniformityGapWeight;
	}

	@StringSetter(UNIFORMITY_GAP_WEIGHT)
	public void setUniformityGapWeight(double uniformityGapWeight) {
		this.uniformityGapWeight = uniformityGapWeight;
	}

	/*
	 * Parametrizes the default convergence criterion.
	 * 
	 * TODO This assumes an exogenous, iteration-count based convergence criterion,
	 * which could (and should) be replaced by a more adaptive convergence test.
	 * Meaning that these parameters only apply to one concrete implementation of
	 * ConvergenceCriterion.
	 * 
	 */

	private static final String NUMBER_OF_ITERATION_TO_AVERAGE = "numberOfIterationsForAveraging";
	private int numberOfIterationsForAveraging = 20;

	@StringGetter(NUMBER_OF_ITERATION_TO_AVERAGE)
	public int getNumberOfIterationsForAveraging() {
		return numberOfIterationsForAveraging;
	}

	@StringSetter(NUMBER_OF_ITERATION_TO_AVERAGE)
	public void setNumberOfIterationsForAveraging(int numberOfIterationsForAveraging) {
		this.numberOfIterationsForAveraging = numberOfIterationsForAveraging;
	}

	private static final String NUMBER_OF_ITERATION_FOR_CONVERGENCE = "numberOfIterationsForConvergence";
	private int numberOfIterationsForConvergence = 600;

	@StringGetter(NUMBER_OF_ITERATION_FOR_CONVERGENCE)
	public int getNumberOfIterationsForConvergence() {
		return numberOfIterationsForConvergence;
	}

	@StringSetter(NUMBER_OF_ITERATION_FOR_CONVERGENCE)
	public void setNumberOfIterationsForConvergence(int numberOfIterationsForConvergence) {
		this.numberOfIterationsForConvergence = numberOfIterationsForConvergence;
	}

	// ==================== EXPERIMENTAL ====================

	/*
	 * Sets a random seed for the random decision variable generation that is
	 * somehow different from the MATSim-specific random seed.
	 * 
	 * TODO This is a parametrization of the DecisionVariableRandomizer, which is
	 * problem-specific, and not of MATSim/Optyts in general.
	 * 
	 */

	private static final String RANDOM_SEED_TO_RANDOMIZE_DECISION_VARIABLE = "randomSeedToRandomizeDecisionVariable";
	private int randomSeedToRandomizeDecisionVariable = 4711;

	@StringGetter(RANDOM_SEED_TO_RANDOMIZE_DECISION_VARIABLE)
	public int getRandomSeedToRandomizeDecisionVariable() {
		return this.randomSeedToRandomizeDecisionVariable;
	}

	@StringSetter(RANDOM_SEED_TO_RANDOMIZE_DECISION_VARIABLE)
	public void setRandomSeedToRandomizeDecisionVariable(int randomSeedToRandomizeDecisionVariable) {
		this.randomSeedToRandomizeDecisionVariable = randomSeedToRandomizeDecisionVariable;
	}

	/*
	 * A step size scaling for random decision variables.
	 * 
	 * TODO This is a parametrization of the DecisionVariableRandomizer, which is
	 * problem-specific, and not of MATSim/Optyts in general.
	 */

	private static final String DECISION_VARIABLE_STEP_SIZE = "decisionVariableStepSize";
	private double decisionVariableStepSize = 0.1;

	@StringGetter(DECISION_VARIABLE_STEP_SIZE)
	public double getDecisionVariableStepSize() {
		return decisionVariableStepSize;
	}

	@StringSetter(DECISION_VARIABLE_STEP_SIZE)
	public void setDecisionVariableStepSize(double decisionVariableStepSize) {
		this.decisionVariableStepSize = decisionVariableStepSize;
	}

	// ==================== DEPRECATED ====================

	/*
	 * "interpolate" is a misleading term for "use opdyts". Should always be true.
	 */

	private static final String IS_INTERPOLATE = "interpolate";
	private boolean interpolate = true;

	@StringGetter(IS_INTERPOLATE)
	public boolean isInterpolate() {
		return interpolate;
	}

	@StringSetter(IS_INTERPOLATE)
	public void setInterpolate(boolean interpolate) {
		this.interpolate = interpolate;
	}

	/*
	 * Use the standard MATSim output path.
	 */

	private static final String OUTPUT_DIRECTORY = "outputDirectory";
	private String outputDirectory = "./output/";

	@StringGetter(OUTPUT_DIRECTORY)
	public String getOutputDirectory() {
		return this.outputDirectory;
	}

	@StringSetter(OUTPUT_DIRECTORY)
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	/*
	 * Preferably couple this to some MATSim standard parameter instead.
	 * 
	 * Also, this seems to be used only for a single analysis class, perhaps it is
	 * possible to decouple this a bit more from the innermost opdyts functionality?
	 */

	private static final String FILE_WRITING_INTERVAL = "fileWritingInterval";
	private int fileWritingInterval = 10;

	@StringGetter(FILE_WRITING_INTERVAL)
	public int getFileWritingInterval() {
		return fileWritingInterval;
	}

	@StringSetter(FILE_WRITING_INTERVAL)
	public void setFileWritingInterval(int fileWritingInterval) {
		this.fileWritingInterval = fileWritingInterval;
	}

	/*
	 * If the best decision variable found in the previous stage is to be included
	 * again as a candidate decision variable in the upcoming stage.
	 * 
	 * It appears difficult to find a good reason for doing this; given that the
	 * candidate decision variables are created by symmetric +/- variations of the
	 * currently best decision variable and that the entire approach is based on
	 * linear interpolations, evaluating an "inner point" does not make much sense.
	 * 
	 */

	private static final String INCLUDE_CURRENT_BEST = "includeCurrentBest";
	private boolean includeCurrentBest = false;

	@StringGetter(INCLUDE_CURRENT_BEST)
	public boolean isIncludeCurrentBest() {
		return includeCurrentBest;
	}

	@StringSetter(INCLUDE_CURRENT_BEST)
	public void setIncludeCurrentBest(boolean includeCurrentBest) {
		this.includeCurrentBest = includeCurrentBest;
	}

}