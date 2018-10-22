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
package org.matsim.contrib.opdyts.buildingblocks.decisionvariables.scalar;

import java.util.Arrays;
import java.util.Collection;

import floetteroed.opdyts.DecisionVariableRandomizer;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class ScalarRandomizer<U extends ScalarDecisionVariable<U>> implements DecisionVariableRandomizer<U> {

	// -------------------- CONSTANTS --------------------

	private final double min;

	private final double max;

	private final double initialDelta;

	private final double searchStageExponent;

	// -------------------- CONSTRUCTION --------------------

	public ScalarRandomizer(final double min, final double max, final double initialDelta,
			final double searchStageExponent) {
		this.min = min;
		this.max = max;
		this.initialDelta = initialDelta;
		this.searchStageExponent = searchStageExponent;
	}

	public ScalarRandomizer() {
		this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0, 0.0);
	}

	// --------------- IMPLEMENTATION OF DecisionVariableRandomizer ---------------

	@Override
	public Collection<U> newRandomVariations(final U decisionVariable, final int searchStage) {
		final U variation1 = decisionVariable.newDeepCopy();
		final U variation2 = decisionVariable.newDeepCopy();
		final double delta = this.initialDelta * Math.pow(searchStage, this.searchStageExponent);
		if ((decisionVariable.getValue() - delta >= this.min) && (decisionVariable.getValue() + delta <= this.max)) {
			variation1.setValue(decisionVariable.getValue() - delta);
			variation2.setValue(decisionVariable.getValue() + delta);
		}
		return Arrays.asList(variation1, variation2);
	}
}
