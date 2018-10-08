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
package org.matsim.contrib.opdyts;

import java.net.URL;

import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.opdyts.buildingblocks.decisionvariables.utils.EveryIterationScoringParameters;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.scoring.functions.ScoringParametersForPerson;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;
import org.matsim.testcases.MatsimTestUtils;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class OpeningTimesInEquilnetIntegrationTest {

	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();

	private static URL EQUIL_DIR = ExamplesUtils.getTestScenarioURL("equil");

	@Test
	public void test() {

		Config config = ConfigUtils.loadConfig(IOUtils.newUrl(EQUIL_DIR, "config.xml"));
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setLastIteration(3);

		//config.addModule(new OpdytsConfigGroup());
		OpdytsConfigGroup opdytsConfig = ConfigUtils.addOrGetModule(config, OpdytsConfigGroup.class);
		opdytsConfig.setBinCount(24);
		opdytsConfig.setBinSize(3600);
		opdytsConfig.setInertia(0.9);
		opdytsConfig.setInitialEquilibriumGapWeight(0.0);
		opdytsConfig.setInitialUniformityGapWeight(0.0);
		opdytsConfig.setMaxIteration(10);
		opdytsConfig.setMaxMemoryPerTrajectory(Integer.MAX_VALUE);
		opdytsConfig.setMaxTotalMemory(Integer.MAX_VALUE);
		opdytsConfig.setMaxTransition(Integer.MAX_VALUE);
		opdytsConfig.setNoisySystem(true);
		opdytsConfig.setNumberOfIterationsForAveraging(3);
		opdytsConfig.setNumberOfIterationsForConvergence(10);
		opdytsConfig.setSelfTuningWeightScale(1.0);
		opdytsConfig.setStartTime(0);
		opdytsConfig.setUseAllWarmUpIterations(true);
		opdytsConfig.setWarmUpIterations(1);
		
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);

		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				bind(ScoringParametersForPerson.class).to(EveryIterationScoringParameters.class);
			}
		});
		

	}

}
