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
package org.matsim.contrib.opdyts.buildingblocks.calibration.plotting;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.matsim.core.controler.events.AfterMobsimEvent;
import org.matsim.core.controler.listener.AfterMobsimListener;

import floetteroed.utilities.Time;
import floetteroed.utilities.TimeDiscretization;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class TrajectoryPlotter implements AfterMobsimListener {

	// -------------------- CONSTANTS --------------------

	private final String fileSuffix = ".data";

	private final String filePrefix;

	private final int logInterval;

	// -------------------- MEMBERS --------------------

	private final List<TrajectoryPlotDataSource> dataSources = new ArrayList<>();

	// -------------------- CONSTRUCTION --------------------

	public TrajectoryPlotter(final String filePrefix, final int logInterval) {
		this.filePrefix = filePrefix;
		this.logInterval = logInterval;
	}

	// -------------------- IMPLEMENTATION --------------------

	public void addDataSource(final TrajectoryPlotDataSource dataSource) {
		this.dataSources.add(dataSource);
	}

	// --------------- IMPLEMENTATION OF AfterMobsimListener ---------------

	private void printlnData(final String label, final double[] data, final PrintWriter writer) {
		writer.print(label);
		if (data != null) {
			for (double val : data) {
				writer.print("\t");
				writer.print(val);
			}
		}
		writer.println();
	}

	@Override
	public void notifyAfterMobsim(final AfterMobsimEvent event) {
		if (event.getIteration() % this.logInterval == 0) {
			final Path path = Paths.get(this.filePrefix + "_it" + event.getIteration() + this.fileSuffix);
			try {
				final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path));
				for (TrajectoryPlotDataSource dataSource : this.dataSources) {
					// 1st row: The description
					writer.println(dataSource.getDescription());
					// 2nd row: The time line
					final TimeDiscretization timeDiscr = dataSource.getTimeDiscretization();
					writer.print("time");
					for (int bin = 0; bin < timeDiscr.getBinCnt(); bin++) {
						writer.print("\t[");
						writer.print(Time.strFromSec(timeDiscr.getBinStartTime_s(bin)));
						writer.print(",");
						writer.print(Time.strFromSec(timeDiscr.getBinEndTime_s(bin)));
						writer.print(")");
					}
					writer.println();
					// 3rd row: The simulated data
					this.printlnData("simulated", dataSource.getSimulatedData(), writer);
					// 4th row: The real data
					this.printlnData("real", dataSource.getRealData(), writer);
					// 5th row: empty.
					writer.println();
				}
				writer.flush();
				writer.close();
			} catch (IOException e) {
				throw new RuntimeException(e); // for debugging
				// Logger.getLogger(this.getClass()).error(e);
			}
		}
	}
}
