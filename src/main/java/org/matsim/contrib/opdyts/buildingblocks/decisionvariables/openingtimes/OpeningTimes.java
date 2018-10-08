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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.matsim.core.config.Config;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.utils.misc.Time;

import floetteroed.opdyts.DecisionVariable;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class OpeningTimes implements DecisionVariable {

	// -------------------- INNER CLASS --------------------

	/**
	 * Identifies the opening or closing time for a single activity type.
	 *
	 */
	/* package (for testing) */ static class ElementIdentifier {

		/* package (for testing) */ enum OpenClose {
			OPEN, CLOSE
		};

		private final OpenClose openClose;

		private final String activityType;

		/* package (for testing) */ ElementIdentifier(final String activityType, final OpenClose openClose) {
			this.activityType = activityType;
			this.openClose = openClose;
		}

		@Override
		public int hashCode() {
			return Arrays.asList(this.activityType, this.openClose).hashCode();
		}

		@Override
		public boolean equals(final Object other) {
			if (other instanceof ElementIdentifier) {
				final ElementIdentifier otherIdentifier = (ElementIdentifier) other;
				return (this.openClose.equals(otherIdentifier.openClose)
						&& this.activityType.equals(otherIdentifier.activityType));
			} else {
				return false;
			}
		}
	}

	// -------------------- MEMBERS --------------------

	private final Config config;

	private final Map<ElementIdentifier, Double> element2value;

	// -------------------- CONSTRUCTION --------------------

	public OpeningTimes(final Config config) {
		this.config = config;
		this.element2value = new LinkedHashMap<>();
		for (ActivityParams actParams : config.planCalcScore().getActivityParams()) {
			if (!Time.isUndefinedTime(actParams.getOpeningTime())) {
				this.element2value.put(
						new ElementIdentifier(actParams.getActivityType(), ElementIdentifier.OpenClose.OPEN),
						actParams.getOpeningTime());
			}
			if (!Time.isUndefinedTime(actParams.getClosingTime())) {
				this.element2value.put(
						new ElementIdentifier(actParams.getActivityType(), ElementIdentifier.OpenClose.CLOSE),
						actParams.getClosingTime());
			}
		}
	}

	public OpeningTimes(final OpeningTimes parent) {
		this.config = parent.config;
		this.element2value = new LinkedHashMap<>(parent.element2value);
	}

	// -------------------- GETTERS AND SETTERS --------------------

	public Set<ElementIdentifier> getElementView() {
		return Collections.unmodifiableSet(this.element2value.keySet());
	}

	public Double getElementValue(final ElementIdentifier element) {
		return this.element2value.get(element);
	}

	public void setElementValue(final ElementIdentifier element, final Double value) {
		this.element2value.put(element, value);
	}

	// -------------------- IMPLEMENTATION OF DecisionVariable --------------------

	@Override
	public void implementInSimulation() {
		for (Map.Entry<ElementIdentifier, Double> entry : this.element2value.entrySet()) {
			final ActivityParams actParams = this.config.planCalcScore().getActivityParams(entry.getKey().activityType);
			if (ElementIdentifier.OpenClose.OPEN.equals(entry.getKey().openClose)) {
				actParams.setOpeningTime(entry.getValue());
			} else if (ElementIdentifier.OpenClose.CLOSE.equals(entry.getKey().openClose)) {
				actParams.setClosingTime(entry.getValue());
			} else {
				throw new RuntimeException("Unknown element of enum " + ElementIdentifier.class.getSimpleName() + ": "
						+ entry.getKey().openClose);
			}
		}
	}
}
