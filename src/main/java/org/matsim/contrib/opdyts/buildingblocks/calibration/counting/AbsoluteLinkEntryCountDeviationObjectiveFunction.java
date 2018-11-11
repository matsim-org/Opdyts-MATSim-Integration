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
package org.matsim.contrib.opdyts.buildingblocks.calibration.counting;

import org.matsim.contrib.opdyts.microstate.MATSimState;

import floetteroed.opdyts.ObjectiveFunction;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class AbsoluteLinkEntryCountDeviationObjectiveFunction implements ObjectiveFunction<MATSimState> {

	private final double[] realData;

	private final LinkEntryCounter simulationCounter;

	public AbsoluteLinkEntryCountDeviationObjectiveFunction(final double[] realData,
			final LinkEntryCounter simulationCounter) {
		this.realData = realData;
		this.simulationCounter = simulationCounter;
	}

	@Override
	public double value(final MATSimState state) {
		final int[] simData = this.simulationCounter.getData();
		final double simulationFlowUpscale = 1.0 / this.simulationCounter.getSimulatedFlowFactor();
		double result = 0;
		for (int i = 0; i < realData.length; i++) {
			result += Math.abs(this.realData[i] - simulationFlowUpscale * simData[i]);
		}		
		return result;
	}
}
