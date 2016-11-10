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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.beefeater.authentication.User;
import se.uu.ub.cora.spider.authentication.UserInfo;
import se.uu.ub.cora.spider.authentication.UserPicker;

public class SystemOneUserPickerTest {
	private UserPicker userPicker;
	private User user;

	@BeforeMethod
	public void setUp() {
		userPicker = new SystemOneUserPicker();

	}

	@Test
	public void testGuest() {
		user = pickUserUsingIdFromLoginAndDomainFromLogin("guest", "system");
		assertUserId("12345");
		assertOnlyOneUserRole("guest");
	}

	private User pickUserUsingIdFromLoginAndDomainFromLogin(String idFromLogin,
			String domainFromLogin) {
		UserInfo userInfo = UserInfo.withLoginIdAndLoginDomain(idFromLogin, domainFromLogin);
		User user = userPicker.pickUser(userInfo);
		return user;
	}

	private void assertUserId(String expectedUserId) {
		assertEquals(user.id, expectedUserId);
	}

	private void assertOnlyOneUserRole(String expectedRole) {
		assertFirstUserRole(expectedRole);
		assertOnlyOneUserRole();
	}

	private void assertFirstUserRole(String expectedFirstRole) {
		String firstRole = user.roles.iterator().next();
		assertEquals(firstRole, expectedFirstRole);
	}

	private void assertOnlyOneUserRole() {
		assertEquals(user.roles.size(), 1);
	}

	@Test
	public void testSystemAdmin() {
		user = pickUserUsingIdFromLoginAndDomainFromLogin("systemAdmin", "system");
		assertUserId("99999");
		assertOnlyOneUserRole("guest");
	}

	@Test
	public void testUser() {
		user = pickUserUsingIdFromLoginAndDomainFromLogin("user", "system");
		assertUserId("10000");
		assertOnlyOneUserRole("user");
	}

	@Test
	public void testFitnesse() {
		user = pickUserUsingIdFromLoginAndDomainFromLogin("fitnesse", "system");
		assertUserId("121212");
		assertOnlyOneUserRole("fitnesse");
	}
}
