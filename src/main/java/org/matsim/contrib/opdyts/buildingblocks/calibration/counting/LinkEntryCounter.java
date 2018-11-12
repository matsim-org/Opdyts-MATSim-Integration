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

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.Config;
import org.matsim.vehicles.Vehicle;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class LinkEntryCounter implements LinkEnterEventHandler {

	// -------------------- MEMBERS --------------------

	private final Config config;

	private final Counter counter;

	private final Filter<Id<Vehicle>> vehicleFilter;

	private final Filter<Id<Link>> linkFilter;

	// -------------------- CONSTRUCTION --------------------

	public LinkEntryCounter(final Config config, final CountMeasurementSpecification specification) {
		this.config = config;
		this.counter = new Counter(specification.getTimeDiscretization());
		this.vehicleFilter = specification.getVehicleFilter();
		this.linkFilter = specification.getLinkFilter();
	}

	// -------------------- CONTENT ACCESS --------------------

	public int[] getData() {
		return this.counter.getData();
	}

	public double getMATSimsFlowCapFactor() {
		return this.config.qsim().getFlowCapFactor();
	}

	// --------------- IMPLEMENTATION OF LinkEnterEventHandler ---------------

	@Override
	public void reset(final int iteration) {
		this.counter.resetData();
	}

	@Override
	public void handleEvent(final LinkEnterEvent event) {
		if (this.linkFilter.test(event.getLinkId()) && this.vehicleFilter.test(event.getVehicleId())) {
			this.counter.inc(event.getTime());
		}
	}
}
