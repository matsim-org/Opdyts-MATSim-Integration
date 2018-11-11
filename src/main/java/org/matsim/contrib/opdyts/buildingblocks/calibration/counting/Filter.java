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

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public interface Filter<T> {

	public static <T> Filter<T> newSingleObjectFilter(final T targetObject) {
		if (targetObject == null) {
			throw new RuntimeException("Target object must not be null.");
		}
		return new Filter<T>() {
			@Override
			public boolean test(T object) {
				return targetObject.equals(object);
			}
		};
	}

	public boolean test(T object);
}
