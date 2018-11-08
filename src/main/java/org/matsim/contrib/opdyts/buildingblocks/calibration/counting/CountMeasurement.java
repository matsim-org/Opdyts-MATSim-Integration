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

import org.matsim.contrib.opdyts.buildingblocks.calibration.DiscretizationChanger;
import org.matsim.contrib.opdyts.buildingblocks.calibration.TrajectoryDataUtils;

import floetteroed.utilities.TimeDiscretization;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class CountMeasurement {

	// -------------------- CONSTANTS --------------------

	private final double[] measCounts;

	private final TimeDiscretization measTimeDiscr;

	// -------------------- MEMBERS --------------------

	private double[] simCounts = null;

	// -------------------- CONSTRUCTION --------------------

	public CountMeasurement(final double[] measCounts, final TimeDiscretization measTimeDiscr) {
		TrajectoryDataUtils.assertCompatibility(measCounts, measTimeDiscr);
		this.measCounts = measCounts;
		this.measTimeDiscr = measTimeDiscr;
	}

	// -------------------- SETTERS --------------------

	public void setSimCounts(final double[] simCounts, final TimeDiscretization simTimeDiscr) {
		TrajectoryDataUtils.assertCompatibility(simCounts, simTimeDiscr);
		final DiscretizationChanger simToMeasDiscrChanger = new DiscretizationChanger(simTimeDiscr, simCounts,
				DiscretizationChanger.DataType.TOTALS);
		simToMeasDiscrChanger.run(this.measTimeDiscr);
		this.simCounts = simToMeasDiscrChanger.getToTotalsCopy();
		TrajectoryDataUtils.assertCompatibility(this.simCounts, this.measTimeDiscr); // this should be guaranteed
	}

	// -------------------- IMPLEMENTATION --------------------

	public double[] getResiduals() {
		final double[] result = new double[this.measCounts.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = this.simCounts[i] - this.measCounts[i];
		}
		return result;
	}

	public double getSumOfAbsoluteErrors() {
		double result = 0;
		for (double residual : this.getResiduals()) {
			result += Math.abs(residual);
		}
		return result;
	}
}
