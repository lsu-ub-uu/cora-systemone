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

import java.util.List;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.userpicker.User;
import se.uu.ub.cora.userpicker.UserInfo;
import se.uu.ub.cora.userpicker.UserPicker;
import se.uu.ub.cora.spider.record.storage.RecordNotFoundException;
import se.uu.ub.cora.spider.record.storage.RecordStorage;

public final class SystemOneUserPicker implements UserPicker {

	private static final String USER_RECORDTYPE = "systemOneUser";
	private static final String GUEST_ID = "12345";
	private RecordStorage recordStorage;
	private DataGroup dataGroupUser;
	private User user;

	public static SystemOneUserPicker usingRecordStorage(RecordStorage recordStorage) {
		return new SystemOneUserPicker(recordStorage);
	}

	private SystemOneUserPicker(RecordStorage recordStorage) {
		this.recordStorage = recordStorage;
	}

	@Override
	public User pickUser(UserInfo userInfo) {
		ensureActiveUserOrGuest(userInfo);
		createNewUserWithUserId();
		possiblyAddUserRoles();
		return user;
	}

	private void ensureActiveUserOrGuest(UserInfo userInfo) {
		try {
			tryToReadActiveUserOrGuest(userInfo);
		} catch (RecordNotFoundException e) {
			readGuestUserFromStorage();
		}
	}

	private void readGuestUserFromStorage() {
		dataGroupUser = recordStorage.read(USER_RECORDTYPE, GUEST_ID);
	}

	private void tryToReadActiveUserOrGuest(UserInfo userInfo) {
		tryToReadLoggedInUser(userInfo);
		if (!userIsActive()) {
			readGuestUserFromStorage();
		}
	}

	private void tryToReadLoggedInUser(UserInfo userInfo) {
		dataGroupUser = recordStorage.read(USER_RECORDTYPE, userInfo.idInUserStorage);
	}

	private void createNewUserWithUserId() {
		String id = extractUserIdFromUserFromStorage();
		user = new User(id);
	}

	private String extractUserIdFromUserFromStorage() {
		DataGroup recordInfo = dataGroupUser.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private void possiblyAddUserRoles() {
		if (userIsActive()) {
			addUserRoleIdsToUserRoles();
		}
	}

	private boolean userIsActive() {
		String activeStatus = dataGroupUser.getFirstAtomicValueWithNameInData("activeStatus");
		return "active".equals(activeStatus);
	}

	private void addUserRoleIdsToUserRoles() {
		List<DataGroup> allGroupsWithNameInData = dataGroupUser
				.getAllGroupsWithNameInData("userRole");
		for (DataGroup extractedRole : allGroupsWithNameInData) {
			addUserRoleIdToUserRoles(extractedRole);
		}
	}

	private void addUserRoleIdToUserRoles(DataGroup extractedRole) {
		DataGroup extractedRoleLink = extractedRole.getFirstGroupWithNameInData("userRole");
		String roleId = extractedRoleLink.getFirstAtomicValueWithNameInData("linkedRecordId");
		user.roles.add(roleId);
	}
}
