/*
 * Copyright 2016, 2017 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.systemone.authentication;

import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import se.uu.ub.cora.userpicker.UserPicker;
import se.uu.ub.cora.userpicker.UserPickerProvider;

public class UserPickerProviderTest {
	@Test
	public void testFactor() {
		Map<String, String> initInfo = new HashMap<>();
		initInfo.put("storageOnDiskBasePath", "/mnt/data/basicstorage");
		UserPickerProvider userPickerProvider = new UserPickerProviderImp(initInfo);
		UserPicker userPicker = userPickerProvider.getUserPicker();
		assertTrue(userPicker instanceof SystemOneUserPicker);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testFactorNoBasePath() {
		Map<String, String> initInfo = new HashMap<>();
		UserPickerProvider userPickerFactory = new UserPickerProviderImp(initInfo);
		UserPicker userPicker = userPickerFactory.getUserPicker();
		assertTrue(userPicker instanceof SystemOneUserPicker);
	}

}
