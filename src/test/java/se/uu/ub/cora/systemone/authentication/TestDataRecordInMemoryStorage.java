/*
 * Copyright 2015 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.storage.RecordStorageInMemory;

public class TestDataRecordInMemoryStorage {

	private static final String CORA = "cora";
	private static final String RECORD_TYPE = "recordType";

	public static RecordStorageInMemory createRecordStorageInMemoryWithUserTestData() {
		RecordStorageInMemory recordsInMemory = new RecordStorageInMemory();
		addRecordTypeUser(recordsInMemory);
		addUserGuest(recordsInMemory);
		addUserFitnesse(recordsInMemory);
		addInactivatedUser(recordsInMemory);
		return recordsInMemory;
	}

	private static void addRecordTypeUser(RecordStorageInMemory recordsInMemory) {
		DataGroup dataGroup = DataCreator.createRecordTypeWithIdAndUserSuppliedIdAndAbstract("user",
				"true", "false");
		// createRecordTypeInStorage(recordsInMemory, dataGroup, "user");
		createRecordTypeInStorage(recordsInMemory, dataGroup, "systemOneUser");
	}

	private static void createRecordTypeInStorage(RecordStorageInMemory recordsInMemory,
			DataGroup dataGroup, String recordId) {
		recordsInMemory.create(RECORD_TYPE, recordId, dataGroup,
				DataGroup.withNameInData("collectedLinksList"), CORA);
	}

	private static void addUserGuest(RecordStorageInMemory recordsInMemory) {
		String recordId = "12345";
		DataGroup dataGroup = createUserWithRecordIdAndRoleNames(recordId, "guest");
		createUserInStorage(recordsInMemory, dataGroup, recordId);
	}

	private static DataGroup createUserWithRecordIdAndRoleNames(String userRecordId,
			String... roleNames) {
		DataGroup recordInfo = DataCreator.createRecordInfoWithRecordTypeAndRecordId("user",
				userRecordId);
		DataGroup userDataGroup = DataGroup.withNameInData("user");
		userDataGroup.addChild(recordInfo);

		// DataGroup userRole = DataGroup.withNameInData("userRole");
		// dataGroup.addChild(userRole);

		addRoleNamesAsRoles(userDataGroup, roleNames);
		userDataGroup.addChild(DataAtomic.withNameInDataAndValue("activeStatus", "active"));
		return userDataGroup;
	}

	private static void addRoleNamesAsRoles(DataGroup user, String... roleNames) {
		for (String roleName : roleNames) {
			DataGroup userRole = DataGroup.withNameInData("userRole");
			user.addChild(userRole);
			userRole.addChild(createPermissionRoleWithPermissionRoleId(roleName));
		}
	}

	private static DataGroup createPermissionRoleWithPermissionRoleId(String roleName) {
		DataGroup userRoleLink = DataGroup.withNameInData("userRole");
		userRoleLink
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "permissionRole"));
		userRoleLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", roleName));
		return userRoleLink;
	}

	private static void createUserInStorage(RecordStorageInMemory recordsInMemory,
			DataGroup dataGroup, String userRecordId) {
		// recordsInMemory.create("user", userRecordId, dataGroup,
		// DataGroup.withNameInData("collectedLinksList"), CORA);
		recordsInMemory.create("systemOneUser", userRecordId, dataGroup,
				DataGroup.withNameInData("collectedLinksList"), CORA);
	}

	private static void addUserFitnesse(RecordStorageInMemory recordsInMemory) {
		String recordId = "121212";
		DataGroup dataGroup = createUserWithRecordIdAndRoleNames(recordId, "fitnesse",
				"metadataAdmin");
		createUserInStorage(recordsInMemory, dataGroup, recordId);
	}

	private static void addInactivatedUser(RecordStorageInMemory recordsInMemory) {
		String recordId = "666666";
		DataGroup dataGroup = createUserWithRecordIdAndRoleNames(recordId, "fitnesse");
		dataGroup.removeFirstChildWithNameInData("activeStatus");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("activeStatus", "inactive"));
		createUserInStorage(recordsInMemory, dataGroup, recordId);

	}
}
