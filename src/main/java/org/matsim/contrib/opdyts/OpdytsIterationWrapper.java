package org.matsim.contrib.opdyts;/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2018 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

/**
 * Just warp around Opdyts transition number to keep track of it at multiple places.
 * It should be same as the iteration in RandomSearch.
 *
 * Created by amit on 02.07.18.
 */

public class OpdytsIterationWrapper {

    private int iteration = 0;

    //
    void nextIteration(){
        this.iteration++;
    }
    public int getIteration() {
        return iteration;
    }
}
