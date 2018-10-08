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
package org.matsim.contrib.opdyts.buildingblocks.decisionvariables.openingtimes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.matsim.contrib.opdyts.buildingblocks.decisionvariables.utils.DecisionVariableRandomizationUtils;

import floetteroed.opdyts.DecisionVariableRandomizer;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class OpeningTimesRandomizer implements DecisionVariableRandomizer<OpeningTimes> {

	// -------------------- CONSTANTS --------------------

	private final int numberOfVariedElements;

	private final double initialSearchRange_s;

	private final double searchStageExponent;

	// -------------------- CONSTRUCTION --------------------

	public OpeningTimesRandomizer(final int maxNumberOfVariedElements, final double initialSearchRange_s,
			final double searchStageExponent) {
		this.numberOfVariedElements = maxNumberOfVariedElements;
		this.initialSearchRange_s = initialSearchRange_s;
		this.searchStageExponent = searchStageExponent;
	}

	// --------------- IMPLEMENTATION OF DecisionVariableRandomizer ---------------

	@Override
	public Collection<OpeningTimes> newRandomVariations(final OpeningTimes parent, final int searchStage) {
		final double delta = this.initialSearchRange_s * Math.pow(searchStage, this.searchStageExponent);
		final Set<OpeningTimes.ElementIdentifier> variedElements = DecisionVariableRandomizationUtils
				.drawDistinctElements(parent.getElementView(), this.numberOfVariedElements);
		final List<OpeningTimes> result = new ArrayList<>(this.numberOfVariedElements);
		for (OpeningTimes.ElementIdentifier element : variedElements) {
			final double currentVal = parent.getElementValue(element);
			// negative variation
			final OpeningTimes variation1 = new OpeningTimes(parent);
			variation1.setElementValue(element, new Double(currentVal - delta));
			result.add(variation1);
			// positive variation
			final OpeningTimes variation2 = new OpeningTimes(parent);
			variation2.setElementValue(element, new Double(currentVal + delta));
			result.add(variation2);
		}
		return result;
	}
}
