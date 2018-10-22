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
package org.matsim.contrib.opdyts.buildingblocks.decisionvariables.composites;

import java.util.List;

import floetteroed.opdyts.DecisionVariable;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class CompositeDecisionVariable implements DecisionVariable {

	// -------------------- MEMBERS --------------------

	private final List<SelfRandomizingDecisionVariable<?>> decisionVariables;

	// --------------- CONSTRUCTION OF IDENTIFIABLE INSTANCE ---------------

	public CompositeDecisionVariable(final List<SelfRandomizingDecisionVariable<?>> decisionVariables) {
		this.decisionVariables = decisionVariables;
	}

	// --------------- IMPLEMENTATION OF DecisionVariable ---------------

	public List<SelfRandomizingDecisionVariable<?>> getDecisionVariables() {
		return this.decisionVariables;
	}

	// --------------- IMPLEMENTATION OF DecisionVariable ---------------

	@Override
	public void implementInSimulation() {
		for (DecisionVariable decisionVariable : this.decisionVariables) {
			decisionVariable.implementInSimulation();
		}
	}

	// -------------------- OVERRIDING OF Object --------------------

	@Override
	public String toString() {
		final StringBuffer result = new StringBuffer();
		result.append("[");
		for (SelfRandomizingDecisionVariable<?> decisionVariable : this.decisionVariables) {
			result.append(",");
			result.append(decisionVariable.toString());
		}
		result.append("]");
		return result.toString();
	}
}
