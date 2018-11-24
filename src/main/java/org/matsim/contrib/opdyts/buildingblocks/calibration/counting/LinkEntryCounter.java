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

import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.core.controler.events.AfterMobsimEvent;
import org.matsim.core.controler.listener.AfterMobsimListener;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class LinkEntryCounter implements LinkEnterEventHandler, AfterMobsimListener {

	// -------------------- MEMBERS --------------------

	private final Counter counter;

	private final CountMeasurementSpecification specification;

	private Integer lastCompletedIteration = null;

	private int[] countsOfLastCompletedIteration = null;

	// -------------------- CONSTRUCTION --------------------

	public LinkEntryCounter(final CountMeasurementSpecification specification) {
		this.counter = new Counter(specification.getTimeDiscretization());
		this.specification = specification;
	}

	// --------------- IMPLEMENTATION OF LinkEnterEventHandler ---------------

	@Override
	public void reset(final int iteration) {
		this.counter.resetData();
	}

	@Override
	public void handleEvent(final LinkEnterEvent event) {
		if (this.specification.getLinks().contains(event.getLinkId())
				&& this.specification.getVehicleFilter().test(event.getVehicleId())) {
			this.counter.inc(event.getTime());
		}
	}

	// --------------- IMPLEMENTATION OF AfterMobsimHandler ---------------

	@Override
	public void notifyAfterMobsim(final AfterMobsimEvent event) {
		this.lastCompletedIteration = event.getIteration();
		this.countsOfLastCompletedIteration = new int[this.counter.getData().length];
		System.arraycopy(this.counter.getData(), 0, this.countsOfLastCompletedIteration, 0,
				this.counter.getData().length);
	}

	// -------------------- CONTENT ACCESS --------------------

	public CountMeasurementSpecification getSpecification() {
		return this.specification;
	}

	public Integer getLastCompletedIteration() {
		return this.lastCompletedIteration;
	}

	public int[] getDataOfLastCompletedIteration() {
		return this.countsOfLastCompletedIteration;
	}
}
