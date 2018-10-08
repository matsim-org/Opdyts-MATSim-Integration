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

import org.junit.Assert;
import org.junit.Test;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.utils.misc.Time;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
public class OpeningTimesTest {

	@Test
	public void testIdentifier() {
		OpeningTimes.ElementIdentifier ident1 = new OpeningTimes.ElementIdentifier("w",
				OpeningTimes.ElementIdentifier.OpenClose.OPEN);
		OpeningTimes.ElementIdentifier ident2 = new OpeningTimes.ElementIdentifier("w",
				OpeningTimes.ElementIdentifier.OpenClose.OPEN);
		Assert.assertTrue(ident1.equals(ident2));
		Assert.assertEquals(ident1.hashCode(), ident2.hashCode());
		OpeningTimes.ElementIdentifier ident3 = new OpeningTimes.ElementIdentifier("h",
				OpeningTimes.ElementIdentifier.OpenClose.OPEN);
		OpeningTimes.ElementIdentifier ident4 = new OpeningTimes.ElementIdentifier("w",
				OpeningTimes.ElementIdentifier.OpenClose.CLOSE);
		Assert.assertFalse(ident1.equals(ident3));
		Assert.assertFalse(ident1.equals(ident4));
		Assert.assertFalse(ident3.equals(ident4));
	}

	static Config newInitializedTestConfig() {
		Config config = ConfigUtils.createConfig();
		{
			ActivityParams actParams = new ActivityParams("h");
			actParams.setOpeningTime(Time.getUndefinedTime());
			actParams.setClosingTime(Time.getUndefinedTime());
			config.planCalcScore().addActivityParams(actParams);
		}
		{
			ActivityParams actParams = new ActivityParams("w");
			actParams.setOpeningTime(6 * 3600);
			actParams.setClosingTime(18 * 3600);
			config.planCalcScore().addActivityParams(actParams);
		}
		{
			ActivityParams actParams = new ActivityParams("o1");
			actParams.setOpeningTime(Time.getUndefinedTime());
			actParams.setClosingTime(20 * 3600);
			config.planCalcScore().addActivityParams(actParams);
		}
		{
			ActivityParams actParams = new ActivityParams("o2");
			actParams.setOpeningTime(8 * 3600);
			actParams.setClosingTime(Time.getUndefinedTime());
			config.planCalcScore().addActivityParams(actParams);
		}
		return config;
	}

	@Test
	public void testIfRightDataIsStored() {
		Config config = newInitializedTestConfig();
		OpeningTimes openingTimes = new OpeningTimes(config);
		Assert.assertEquals(4, openingTimes.getElementView().size());

		Assert.assertNotNull(openingTimes.getElementValue(
				new OpeningTimes.ElementIdentifier("w", OpeningTimes.ElementIdentifier.OpenClose.OPEN)));
		Assert.assertNotNull(openingTimes.getElementValue(
				new OpeningTimes.ElementIdentifier("w", OpeningTimes.ElementIdentifier.OpenClose.CLOSE)));
		Assert.assertNotNull(openingTimes.getElementValue(
				new OpeningTimes.ElementIdentifier("o1", OpeningTimes.ElementIdentifier.OpenClose.CLOSE)));
		Assert.assertNotNull(openingTimes.getElementValue(
				new OpeningTimes.ElementIdentifier("o2", OpeningTimes.ElementIdentifier.OpenClose.OPEN)));

		Assert.assertNull(openingTimes.getElementValue(
				new OpeningTimes.ElementIdentifier("h", OpeningTimes.ElementIdentifier.OpenClose.OPEN)));
		Assert.assertNull(openingTimes.getElementValue(
				new OpeningTimes.ElementIdentifier("h", OpeningTimes.ElementIdentifier.OpenClose.CLOSE)));
		Assert.assertNull(openingTimes.getElementValue(
				new OpeningTimes.ElementIdentifier("o1", OpeningTimes.ElementIdentifier.OpenClose.OPEN)));
		Assert.assertNull(openingTimes.getElementValue(
				new OpeningTimes.ElementIdentifier("o2", OpeningTimes.ElementIdentifier.OpenClose.CLOSE)));
	}

	@Test
	public void testIfModificationsWork() {
		Config config = newInitializedTestConfig();
		OpeningTimes openingTimes = new OpeningTimes(config);

		openingTimes.setElementValue(
				new OpeningTimes.ElementIdentifier("w", OpeningTimes.ElementIdentifier.OpenClose.OPEN), 1000.0);
		openingTimes.setElementValue(
				new OpeningTimes.ElementIdentifier("w", OpeningTimes.ElementIdentifier.OpenClose.CLOSE), 2000.0);
		
		Assert.assertEquals(4, openingTimes.getElementView().size());
		Assert.assertEquals(openingTimes.getElementValue(
				new OpeningTimes.ElementIdentifier("w", OpeningTimes.ElementIdentifier.OpenClose.OPEN)), 1000.0, 1e-8);
		Assert.assertEquals(openingTimes.getElementValue(
				new OpeningTimes.ElementIdentifier("w", OpeningTimes.ElementIdentifier.OpenClose.CLOSE)), 2000.0, 1e-8);

		openingTimes.implementInSimulation();
		Assert.assertEquals(config.planCalcScore().getActivityParams("w").getOpeningTime(), 1000.0, 1e-8);
		Assert.assertEquals(config.planCalcScore().getActivityParams("w").getClosingTime(), 2000.0, 1e-8);
		Assert.assertEquals(config.planCalcScore().getActivityParams("o1").getClosingTime(), 20 * 3600, 1e-8);
		Assert.assertEquals(config.planCalcScore().getActivityParams("o2").getOpeningTime(), 8 * 3600, 1e-8);
	}
}
