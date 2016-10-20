/*
 * Copyright 2016 Uppsala University Library
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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.beefeater.authentication.User;
import se.uu.ub.cora.spider.authentication.UserInfo;
import se.uu.ub.cora.spider.authentication.UserPicker;

public class SystemOneUserPickerTest {
	@Test
	public void testGuest() {
		UserPicker userPicker = new SystemOneUserPicker();
		UserInfo userInfo = UserInfo.withLoginIdAndLoginDomain("guest", "system");
		User user = userPicker.pickUser(userInfo);
		assertEquals(user.id, "12345");
	}

	@Test
	public void testSystemAdmin() {
		UserPicker userPicker = new SystemOneUserPicker();
		UserInfo userInfo = UserInfo.withLoginIdAndLoginDomain("systemAdmin", "system");
		User user = userPicker.pickUser(userInfo);
		assertEquals(user.id, "99999");
	}

	@Test
	public void testUser() {
		UserPicker userPicker = new SystemOneUserPicker();
		UserInfo userInfo = UserInfo.withLoginIdAndLoginDomain("user", "system");
		User user = userPicker.pickUser(userInfo);
		assertEquals(user.id, "10000");
	}
}
