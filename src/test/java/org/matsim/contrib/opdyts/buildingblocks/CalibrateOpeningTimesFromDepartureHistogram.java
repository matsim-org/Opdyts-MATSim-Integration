/*
 * Copyright 2018 Gunnar Flötteröd
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * contact: gunnar.flotterod@gmail.com
 *
 */
package org.matsim.contrib.opdyts.buildingblocks;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Rule;
import org.matsim.analysis.LegHistogram;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.opdyts.MATSimOpdytsRunner;
import org.matsim.contrib.opdyts.OpdytsConfigGroup;
import org.matsim.contrib.opdyts.buildingblocks.convergencecriteria.AR1ConvergenceCriterion;
import org.matsim.contrib.opdyts.buildingblocks.decisionvariables.openingtimes.OpeningTimes;
import org.matsim.contrib.opdyts.buildingblocks.decisionvariables.openingtimes.OpeningTimesRandomizer;
import org.matsim.contrib.opdyts.buildingblocks.decisionvariables.utils.EveryIterationScoringParameters;
import org.matsim.contrib.opdyts.buildingblocks.objectivefunctions.WeightedSumObjectiveFunction;
import org.matsim.contrib.opdyts.buildingblocks.objectivefunctions.calibration.LegHistogramObjectiveFunction;
import org.matsim.contrib.opdyts.microstate.MATSimState;
import org.matsim.contrib.opdyts.microstate.MATSimStateFactoryImpl;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.controler.events.AfterMobsimEvent;
import org.matsim.core.controler.listener.AfterMobsimListener;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.scoring.functions.ScoringParametersForPerson;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;
import org.matsim.testcases.MatsimTestUtils;

import com.google.inject.Inject;

import floetteroed.opdyts.DecisionVariableRandomizer;
import floetteroed.opdyts.ObjectiveFunction;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class CalibrateOpeningTimesFromDepartureHistogram {

	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();

	private static URL EQUIL_DIR = ExamplesUtils.getTestScenarioURL("equil");

	static class MyStateFactory extends MATSimStateFactoryImpl<OpeningTimes, MATSimState>
			implements AfterMobsimListener {

		@Inject
		private LegHistogram legHist;

		private LegHistogramObjectiveFunction.StateComponent legHistogramData = null;

		@Override
		public void notifyAfterMobsim(final AfterMobsimEvent event) {
			this.legHistogramData = new LegHistogramObjectiveFunction.StateComponent();
			for (String mode : this.legHist.getLegModes()) {
				this.legHistogramData.mode2departureData.put(mode, this.legHist.getDepartures(mode));
				this.legHistogramData.mode2arrivalData.put(mode, this.legHist.getArrivals(mode));
			}
		}

		@Override
		protected void addComponents(final MATSimState state) {
			state.putComponent(LegHistogramObjectiveFunction.StateComponent.class, this.legHistogramData);
		}
	}

	public void test() {

		Config config = ConfigUtils.loadConfig(IOUtils.newUrl(EQUIL_DIR, "config.xml"));
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setLastIteration(1000);
		config.plans().setInputFile(
				"C:/Users/GunnarF/OneDrive - VTI/My Code/git-2018/Opdyts-MATSim-Integration/output_plans.xml.gz");

		StrategySettings timeChoice = new StrategySettings();
		timeChoice.setStrategyName("TimeAllocationMutator");
		timeChoice.setWeight(0.1);
		config.strategy().addStrategySettings(timeChoice);

		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);
		controler.run();
	}

	// @Test
	public void test2() {

		Config config = ConfigUtils.loadConfig(IOUtils.newUrl(EQUIL_DIR, "config.xml"));
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setLastIteration(3);

		config.plans().setInputFile(
				"C:/Users/GunnarF/OneDrive - VTI/My Code/git-2018/Opdyts-MATSim-Integration/output_plans.xml.gz");

		StrategySettings timeChoice = new StrategySettings();
		timeChoice.setStrategyName("TimeAllocationMutator");
		timeChoice.setWeight(0.1);
		config.strategy().addStrategySettings(timeChoice);

		OpdytsConfigGroup opdytsConfig = ConfigUtils.addOrGetModule(config, OpdytsConfigGroup.class);
		opdytsConfig.setBinCount(24 * 12);
		opdytsConfig.setBinSize(3600 / 12);
		opdytsConfig.setInertia(0.9);
		opdytsConfig.setInitialEquilibriumGapWeight(0.0);
		opdytsConfig.setInitialUniformityGapWeight(0.0);
		opdytsConfig.setMaxIteration(100);
		opdytsConfig.setMaxMemoryPerTrajectory(Integer.MAX_VALUE);
		opdytsConfig.setMaxTotalMemory(Integer.MAX_VALUE);
		opdytsConfig.setMaxTransition(Integer.MAX_VALUE);
		opdytsConfig.setNoisySystem(true);
		opdytsConfig.setNumberOfIterationsForAveraging(50);
		opdytsConfig.setNumberOfIterationsForConvergence(100);
		opdytsConfig.setSelfTuningWeightScale(1.0);
		opdytsConfig.setStartTime(0);
		opdytsConfig.setUseAllWarmUpIterations(true);
		opdytsConfig.setWarmUpIterations(1);

		// OBJECTIVE FUNCTION

		Set<String> modes = new LinkedHashSet<>(Arrays.asList("car"));

		double[] realDepartures = new double[362];
		// Everybody departs at 4 to work. Bin size 300 sec -> bin nr = 4 * 3600 / 300 =
		// 48
		realDepartures[48] = 100;
		// Everybody departs at 5 from work. Bin size 300 sec -> bin nr = 5 * 3600 / 300
		// = 60
		realDepartures[60] = 100;
		ObjectiveFunction<MATSimState> dptObjFct = LegHistogramObjectiveFunction.newDepartures(modes, realDepartures);

		// double[] realArrivals = new double[362];
		// // Everybody arrives one bin later
		// realArrivals[61] = 100;
		// MATSimObjectiveFunction<MATSimState> arrObjFct =
		// LegHistogramObjectiveFunction.newDepartures(modes,
		// realArrivals);

		WeightedSumObjectiveFunction<MATSimState> objFct = new WeightedSumObjectiveFunction<>();
		objFct.add(dptObjFct, 1.0);
		// objFct.add(arrObjFct, 1.0);

		// DECISION VARIABLE RANDOMIZER

		int maxNumberOfVariedElements = 2; // work open and close
		double initialSearchRange_s = 600;
		double searchStageExponent = 0;
		DecisionVariableRandomizer<OpeningTimes> randomizer = new OpeningTimesRandomizer(maxNumberOfVariedElements,
				initialSearchRange_s, searchStageExponent);

		// STATE FACTORY

		MyStateFactory stateFactory = new MyStateFactory();

		// WIRE EVERYTHING TOGETHER

		Scenario scenario = ScenarioUtils.loadScenario(config);
		MATSimOpdytsRunner<OpeningTimes, MATSimState> runner = new MATSimOpdytsRunner<>(scenario, stateFactory);
		runner.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				bind(ScoringParametersForPerson.class).to(EveryIterationScoringParameters.class);
				this.addControlerListenerBinding().toInstance(stateFactory);
			}
		});
		runner.setConvergenceCriterion(new AR1ConvergenceCriterion(2.5));
		runner.run(randomizer, new OpeningTimes(config), objFct);
	}
}
