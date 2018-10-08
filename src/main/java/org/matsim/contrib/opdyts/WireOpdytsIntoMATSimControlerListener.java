package org.matsim.contrib.opdyts;

import java.util.LinkedList;
import java.util.List;

import org.matsim.api.core.v01.population.Population;
import org.matsim.contrib.opdyts.macrostate.SimulationMacroStateAnalyzer;
import org.matsim.contrib.opdyts.microstate.MATSimStateFactory;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.controler.events.BeforeMobsimEvent;
import org.matsim.core.controler.events.ShutdownEvent;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.BeforeMobsimListener;
import org.matsim.core.controler.listener.ShutdownListener;
import org.matsim.core.controler.listener.StartupListener;

import com.google.inject.Inject;

import floetteroed.opdyts.DecisionVariable;
import floetteroed.opdyts.SimulatorState;
import floetteroed.opdyts.trajectorysampling.TrajectorySampler;
import floetteroed.utilities.math.Vector;

/**
 * 
 * @author Gunnar Flötteröd
 * 
 */
class WireOpdytsIntoMATSimControlerListener<U extends DecisionVariable, X extends SimulatorState>
		implements StartupListener, BeforeMobsimListener, ShutdownListener {

	// -------------------- CONSTANTS --------------------

	private final boolean averageMemory = false;

	private final int memory = 1;

	// -------------------- MEMBERS --------------------

	private final TrajectorySampler<U, X> trajectorySampler;

	private final MATSimStateFactory<U, X> stateFactory;

	// A list because the order matters in the state space vector.
	private final List<SimulationMacroStateAnalyzer> simulationStateAnalyzers;

	@Inject
	private EventsManager eventsManager;

	@Inject
	private Population population;

	private LinkedList<Vector> stateList = null;

	private X finalState = null;

	private boolean justStarted = true;

	// -------------------- CONSTRUCTION --------------------

	WireOpdytsIntoMATSimControlerListener(final TrajectorySampler<U, X> trajectorySampler,
			final MATSimStateFactory<U, X> stateFactory, final List<SimulationMacroStateAnalyzer> simulationStateAnalyzers) {
		this.trajectorySampler = trajectorySampler;
		this.stateFactory = stateFactory;
		this.simulationStateAnalyzers = simulationStateAnalyzers;
	}

	// -------------------- INTERNALS --------------------

	private X newState() {
		final Vector newSummaryStateVector;
		if (this.averageMemory) {
			// average state vectors
			newSummaryStateVector = this.stateList.getFirst().copy();
			for (int i = 1; i < this.memory; i++) {
				newSummaryStateVector.add(this.stateList.get(i));
			}
			newSummaryStateVector.mult(1.0 / this.memory);
		} else {
			// concatenate state vectors
			newSummaryStateVector = Vector.concat(this.stateList);
		}
		return this.stateFactory.newState(this.population, newSummaryStateVector,
				this.trajectorySampler.getCurrentDecisionVariable());
	}

	// -------------------- RESULT ACCESS --------------------

	boolean foundSolution() {
		return this.trajectorySampler.foundSolution();
	}

	X getFinalState() {
		return finalState;
	}

	// --------------- CONTROLLER LISTENER IMPLEMENTATIONS ---------------

	@Override
	public void notifyStartup(final StartupEvent event) {

		this.stateList = new LinkedList<Vector>();

		if (this.simulationStateAnalyzers.isEmpty()) {
			throw new RuntimeException("No simulation state analyzers have been added.");
		}
		for (SimulationMacroStateAnalyzer analyzer : this.simulationStateAnalyzers) {
			analyzer.clear();
			this.eventsManager.addHandler(analyzer);
		}

		this.justStarted = true;
	}

	@Override
	public void notifyBeforeMobsim(final BeforeMobsimEvent event) {

		/*
		 * (1) The mobsim must have been run at least once to allow for the extraction
		 * of a vector-valued system state. The "just started" MATSim iteration is hence
		 * run through without Opdyts in the loop.
		 */
		if (this.justStarted) {

			this.justStarted = false;

		} else {

			/*
			 * (2) Extract the instantaneous state vector.
			 */
			Vector newInstantaneousStateVector = null;
			for (SimulationMacroStateAnalyzer analyzer : this.simulationStateAnalyzers) {
				if (newInstantaneousStateVector == null) {
					newInstantaneousStateVector = analyzer.newStateVectorRepresentation();
				} else {
					newInstantaneousStateVector = Vector.concat(newInstantaneousStateVector,
							analyzer.newStateVectorRepresentation());
				}
			}

			/*
			 * (3) Add instantaneous state vector to the list of past state vectors and
			 * ensure that the size of this list is equal to what the memory parameter
			 * prescribes.
			 */
			this.stateList.addFirst(newInstantaneousStateVector);
			while (this.stateList.size() < this.memory) {
				this.stateList.addFirst(newInstantaneousStateVector);
			}
			while (this.stateList.size() > this.memory) {
				this.stateList.removeLast();
			}

			/*
			 * (4) Inform the TrajectorySampler that one iteration has been completed and
			 * provide the resulting state.
			 */
			this.trajectorySampler.afterIteration(this.newState());
		}

		/*
		 * (5) Prepare the simulation state analyzers for a new mobsim run.
		 */
		for (SimulationMacroStateAnalyzer analyzer : this.simulationStateAnalyzers) {
			analyzer.clear();
		}

	}

	/*
	 * TODO Given that an iteration is assumed to end before the "mobsim execution"
	 * step, the final state is only approximately correctly computed because it
	 * leaves out the last iteration's "replanning" step.
	 * 
	 */
	@Override
	public void notifyShutdown(final ShutdownEvent event) {
		this.finalState = this.newState();
	}
}
