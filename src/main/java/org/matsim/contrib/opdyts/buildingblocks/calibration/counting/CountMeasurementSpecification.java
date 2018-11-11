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
import java.util.Collections;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

import floetteroed.utilities.TimeDiscretization;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class CountMeasurementSpecification {

	// -------------------- CONSTANTS --------------------
	
	private final Id<Link> linkId;

	private final TimeDiscretization timeDiscr;
	
	private final Filter<Id<Vehicle>> vehicleFilter;

	private final Filter<Id<Link>> linkFilter;
	
	private final List<Object> featureList;

	// -------------------- CONSTRUCTION --------------------
	
	public CountMeasurementSpecification(final Id<Link> linkId, final TimeDiscretization timeDiscr,
			final Filter<Id<Vehicle>> vehicleFilter) {
		
		this.linkId = linkId;
		this.timeDiscr = timeDiscr;
		this.vehicleFilter = vehicleFilter;
		this.linkFilter = Filter.newSingleObjectFilter(linkId);

		final List<Object> featureList = new ArrayList<>();
		featureList.add(linkId);
		featureList.add(new Integer(timeDiscr.getStartTime_s()));
		featureList.add(new Integer(timeDiscr.getBinSize_s()));
		featureList.add(new Integer(timeDiscr.getBinCnt()));
		featureList.add(vehicleFilter);
		this.featureList = Collections.unmodifiableList(featureList);
	}

	// -------------------- GETTERS --------------------
	
	public Id<Link> getLinkId() {
		return this.linkId;
	}
	
	public Filter<Id<Link>> getLinkFilter() {
		return this.linkFilter;
	}
	
	public TimeDiscretization getTimeDiscretization() {
		return this.timeDiscr;
	}
	
	public Filter<Id<Vehicle>> getVehicleFilter() {
		return this.vehicleFilter;
	}
	
	// -------------------- OVERRIDING OF Object --------------------
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof CountMeasurementSpecification) {
			return this.featureList.equals(((CountMeasurementSpecification) other).featureList);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.featureList.hashCode();
	}

}
