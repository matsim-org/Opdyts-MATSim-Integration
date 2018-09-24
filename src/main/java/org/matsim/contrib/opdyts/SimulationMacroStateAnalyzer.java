package org.matsim.contrib.opdyts;

import org.matsim.core.events.handler.EventHandler;

import floetteroed.utilities.math.Vector;

/**
 * An event handler that composes (part of) the macro-state of a simulation.
 * 
 * TODO One could probably condense this further into a single
 * createNewMacroStateAndReset() function.
 * 
 * TODO Provide an explicitly empty default implementation of
 * EventHandler.reset(int)?
 * 
 * @author Gunnar Flötteröd
 *
 */
public interface SimulationMacroStateAnalyzer extends EventHandler {

	/**
	 * Clears the internal state, i.e. all (event) book-keeping from which the
	 * macro-state of interest is composed.
	 * <p>
	 * The reset-functionality required here must *not* be implemented in
	 * EventHandler.reset(int) because that function appears to be called before
	 * BeforeMobsimListener.notifyBeforeMobsim(BeforeMobsimEvent), where the
	 * macro-state is needed.
	 * <p>
	 * The logic is now such that
	 * BeforeMobsimListener.notifyBeforeMobsim(BeforeMobsimEvent) is called within
	 * the framework, extracts all needed information, and then calls
	 * SimulatorMacroStateAnalyzer.resetBeforeMobsim().
	 */
	public void clear();

	/**
	 * Returns a new instance of a fixed-dimension vector-valued macro-state
	 * representation based on the most recently handled event stream.
	 */
	public Vector newStateVectorRepresentation();

}
