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
package org.matsim.contrib.opdyts.deprecated;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.matsim.api.core.v01.population.Population;
import org.matsim.contrib.opdyts.WireOpdytsIntoMATSimControlerListener;
import org.matsim.contrib.opdyts.WireOpdytsIntoMATSimControlerListener.BeforeMobsimAnalyzer;
import org.matsim.contrib.opdyts.macrostate.SimulationMacroStateAnalyzer;
import org.matsim.core.controler.events.BeforeMobsimEvent;
import org.matsim.core.utils.io.IOUtils;

import floetteroed.utilities.math.Vector;

/**
 *
 * @author amit
 * @author Gunnar Flötteröd
 *
 */
public class AmitsWireOpdytsIntoMATSimControlerListener
		implements WireOpdytsIntoMATSimControlerListener.BeforeMobsimAnalyzer {

	private final int fileWritingIteration;

	public AmitsWireOpdytsIntoMATSimControlerListener(final int fileWritingIteration) {
		this.fileWritingIteration = fileWritingIteration;
	}

	public void run(BeforeMobsimEvent event, Population population,
			List<SimulationMacroStateAnalyzer> simulationStateAnalyzers) {
		if (event.getIteration() % this.fileWritingIteration == 0) {
			for (SimulationMacroStateAnalyzer analyzer : simulationStateAnalyzers) {
				String outFile = event.getServices().getControlerIO().getIterationFilename(event.getIteration(),
						"stateVector_" + analyzer.getClass().getSimpleName() + ".txt");
				writeData(analyzer.newStateVectorRepresentation(), outFile);
			}
		}
	}

	void writeData(final Vector vector, final String outFile) {
		List<Double> vectorElements = new ArrayList<>(vector.asList());
		Collections.sort(vectorElements, Collections.reverseOrder());

		try (BufferedWriter writer = IOUtils.getBufferedWriter(outFile)) {
			for (Double d : vectorElements) {
				writer.write(d + "\n");
			}
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException("Data is not written/read. Reason : " + e);
		}
	}

}
