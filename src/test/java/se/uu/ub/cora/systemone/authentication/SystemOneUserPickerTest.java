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
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.spider.authentication.UserInfo;
import se.uu.ub.cora.spider.authentication.UserPicker;
import se.uu.ub.cora.spider.record.storage.RecordStorage;

public class SystemOneUserPickerTest {
	private static final String SYSTEM = "system";
	private static final String FITNESSE_USER_ID = "121212";
	private static final String GUEST_ID = "12345";
	private UserPicker userPicker;
	private User user;

	private RecordStorage recordStorage;

	@BeforeMethod
	public void setUp() {
		recordStorage = TestDataRecordInMemoryStorage.createRecordStorageInMemoryWithUserTestData();
		userPicker = SystemOneUserPicker.usingRecordStorage(recordStorage);

	}

	@Test
	public void testGuest() {
		user = pickUserUsingIdFromLoginAndDomainFromLogin(GUEST_ID, SYSTEM);
		assertUserId(GUEST_ID);
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
	public void testUnknownUserIsGuest() {
		user = pickUserUsingIdFromLoginAndDomainFromLogin("unknownUser", SYSTEM);
		assertUserId(GUEST_ID);
		assertOnlyOneUserRole("guest");
	}

	@Test
	public void testUserWithTwoRoles() {
		user = pickUserUsingIdFromLoginAndDomainFromLogin(FITNESSE_USER_ID, SYSTEM);
		assertUserId(FITNESSE_USER_ID);
		assertNumberOfRoles(2);
		assertUserRoles("fitnesse", "metadataAdmin");
	}

	private void assertNumberOfRoles(int numberOfRoles) {
		assertEquals(user.roles.size(), numberOfRoles);
	}

	private void assertUserRoles(String... userRoles) {
		int i = 0;
		for (String userRole : user.roles) {
			assertEquals(userRole, userRoles[i]);
			i++;
		}
	}

	@Test
	public void testInactiveUserReturnsGuest() {
		user = pickUserUsingIdFromLoginAndDomainFromLogin("666666", SYSTEM);
		assertUserId(GUEST_ID);
	}

	@Test
	public void testGuestInactive() {
		updateUserGuestInStorageToBeInactive();
		user = pickUserUsingIdFromLoginAndDomainFromLogin(GUEST_ID, SYSTEM);
		assertNumberOfRoles(0);
	}

	private void updateUserGuestInStorageToBeInactive() {
		DataGroup guestGroup = recordStorage.read("systemOneUser", GUEST_ID);
		guestGroup.removeFirstChildWithNameInData("activeStatus");
		guestGroup.addChild(DataAtomic.withNameInDataAndValue("activeStatus", "inactive"));
		recordStorage.update("systemOneUser", GUEST_ID, guestGroup,
				DataGroup.withNameInData("collectedLinksList"), "cora");
	}

}
