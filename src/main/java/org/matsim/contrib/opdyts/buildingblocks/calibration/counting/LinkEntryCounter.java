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

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class LinkEntryCounter implements LinkEnterEventHandler {

	// -------------------- MEMBERS --------------------

	private final List<Counter> counters = new ArrayList<Counter>(1);

	private Filter<Id<Vehicle>> vehicleFilter = new Filter<Id<Vehicle>>() {
		@Override
		public boolean test(Id<Vehicle> vehicleId) {
			return true;
		}
	};

	private Filter<Id<Link>> linkFilter = null;

	// -------------------- CONSTRUCTION --------------------

	public LinkEntryCounter(final Filter<Id<Link>> linkFilter) {
		this.setLinkFilter(linkFilter);
	}

	public LinkEntryCounter(final Id<Link> linkId) {
		this(new SetBasedFilter<>(linkId));
	}

	// -------------------- CONFIGURATION --------------------

	public void setLinkFilter(final Filter<Id<Link>> linkFilter) {
		if (linkFilter == null) {
			throw new RuntimeException("Link filter must not be null.");
		}
		this.linkFilter = linkFilter;
	}

	public void setVehicleFilter(final Filter<Id<Vehicle>> vehicleFilter) {
		if (vehicleFilter == null) {
			throw new RuntimeException("Vehicle filter must not be null.");
		}
		this.vehicleFilter = vehicleFilter;
	}

	// --------------- IMPLEMENTATION OF LinkEnterEventHandler ---------------

	@Override
	public void reset(final int iteration) {
		for (Counter counter : this.counters) {
			counter.resetData();
		}
	}

	@Override
	public void handleEvent(final LinkEnterEvent event) {
		if (this.linkFilter.test(event.getLinkId()) && this.vehicleFilter.test(event.getVehicleId())) {
			for (Counter trajectoryCounter : this.counters) {
				trajectoryCounter.inc(event.getTime());
			}
		}
	}
}
