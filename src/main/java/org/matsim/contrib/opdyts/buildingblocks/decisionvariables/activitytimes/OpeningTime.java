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
package org.matsim.contrib.opdyts.buildingblocks.decisionvariables.activitytimes;

import org.matsim.core.config.Config;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class OpeningTime extends ActivityTime<OpeningTime> {

	// -------------------- CONSTRUCTION --------------------

	public OpeningTime(final Config config, final String activityType, final double value_s) {
		super(config, activityType, value_s);
	}

	// --------------- IMPLEMENTATION OF ActivityTime ---------------

	@Override
	public void implementInSimulation() {
		this.getActivityParams().setOpeningTime(this.getValue());
	}

	@Override
	public OpeningTime newDeepCopy() {
		return new OpeningTime(this.getConfig(), this.getActivityType(), this.getValue());
	}
}
