/*
 * Copyright 2016, 2017, 2018 Uppsala University Library
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
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.storage.UserStorageImp;
import se.uu.ub.cora.userpicker.UserInStorageUserPicker;
import se.uu.ub.cora.userpicker.UserPicker;
import se.uu.ub.cora.userpicker.UserPickerProvider;

public class UserPickerProviderTest {
	private String basePath = "/tmp/recordStorageOnDiskTemp/";
	private Map<String, String> initInfo;
	private UserPickerProvider userPickerProvider;
	private UserInStorageUserPicker userPicker;

	@BeforeMethod
	public void makeSureBasePathExistsAndIsEmpty() throws IOException {
		File dir = new File(basePath);
		dir.mkdir();
		deleteFiles(basePath);
		TestDataAppTokenStorage.createRecordStorageInMemoryWithTestData(basePath);

		initInfo = new HashMap<>();
		initInfo.put("storageOnDiskBasePath", basePath);
		userPickerProvider = new UserPickerProviderImp(initInfo);
		userPicker = (UserInStorageUserPicker) userPickerProvider.getUserPicker();
	}

	private void deleteFiles(String path) throws IOException {
		Stream<Path> list;
		list = Files.list(Paths.get(path));

		list.forEach(p -> deleteFile(p));
		list.close();
	}

	private void deleteFile(Path path) {
		try {
			if (path.toFile().isDirectory()) {
				deleteFiles(path.toString());
			}
			Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterMethod
	public void removeTempFiles() throws IOException {
		if (Files.exists(Paths.get(basePath))) {
			deleteFiles(basePath);
			File dir = new File(basePath);
			dir.delete();
		}
	}

	@Test
	public void testFactor() {
		assertTrue(userPicker instanceof UserInStorageUserPicker);
		UserStorageImp userStorage = (UserStorageImp) userPicker.getUserStorage();
		assertTrue(userStorage instanceof UserStorageImp);
		Map<String, String> initInfoSentToStorage = userStorage.getInitInfo();
		assertEquals(initInfoSentToStorage, initInfo);
	}

	@Test
	public void testSettingGuestUserId() throws Exception {
		assertEquals(userPicker.getCurrentGuestUserId(), "12345");
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testFactorNoBasePath() {
		Map<String, String> initInfo = new HashMap<>();
		UserPickerProvider userPickerFactory = new UserPickerProviderImp(initInfo);
		UserPicker userPicker = userPickerFactory.getUserPicker();
		assertTrue(userPicker instanceof UserInStorageUserPicker);
	}

}
