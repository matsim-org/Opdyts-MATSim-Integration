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
package org.matsim.contrib.opdyts.example.modechoice;

import org.matsim.core.config.ReflectiveConfigGroup;

/**
 * Putting experimental parameters here that do not appear sufficiently generic
 * to be stored in OpdytsConfigGroup.
 * 
 * @author Amit, created this on 03.06.17.
 * @author Gunnar, as of Sep 2018.
 * 
 */
class OpdytsExperimentalConfigGroup extends ReflectiveConfigGroup {

	static final String GROUP_NAME = "opdytsExperimental";

	OpdytsExperimentalConfigGroup() {
		super(GROUP_NAME);
	}

	// =============== NUMBER OF CANDIDATE DECISION VARIABLES ===============

	private int populationSize = 10; // TODO magic number

	@StringGetter("numberOfCandidateDecisionVariables")
	int getPopulationSize() {
		return this.populationSize;
	}

	@StringSetter("numberOfCandidateDecisionVariables")
	void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	// =============== RANDOM SEED FOR DECISION VARIABLE GENERATION ===============

	private int randomSeedToRandomizeDecisionVariable = 4711; // TODO magic number

	@StringGetter("randomSeedToRandomizeDecisionVariable")
	int getRandomSeedToRandomizeDecisionVariable() {
		return this.randomSeedToRandomizeDecisionVariable;
	}

	@StringSetter("randomSeedToRandomizeDecisionVariable")
	void setRandomSeedToRandomizeDecisionVariable(int randomSeedToRandomizeDecisionVariable) {
		this.randomSeedToRandomizeDecisionVariable = randomSeedToRandomizeDecisionVariable;
	}

	// =============== REAL-VALUED DECISION VARIABLE SCALING ===============

	private double decisionVariableStepSize = 0.1; // TODO magic number

	@StringGetter("decisionVariableStepSize")
	double getDecisionVariableStepSize() {
		return decisionVariableStepSize;
	}

	@StringSetter("decisionVariableStepSize")
	void setDecisionVariableStepSize(double decisionVariableStepSize) {
		this.decisionVariableStepSize = decisionVariableStepSize;
	}
}
