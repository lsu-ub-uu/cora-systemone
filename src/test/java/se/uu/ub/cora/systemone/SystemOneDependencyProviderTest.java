/*
 * Copyright 2015 Uppsala University Library
 * Copyright 2017 Olov McKie
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

package se.uu.ub.cora.systemone;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.gatekeeperclient.authentication.AuthenticatorImp;
import se.uu.ub.cora.metacreator.extended.MetacreatorExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.authorization.PermissionRuleCalculator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.testng.Assert.*;

public class SystemOneDependencyProviderTest {
	private SystemOneDependencyProvider dependencyProvider;
	private String basePath = "/tmp/recordStorageOnDiskTemp/";

	@BeforeMethod
	public void setUp() throws IOException {
		makeSureBasePathExistsAndIsEmpty();
		Map<String, String> initInfo = new HashMap<>();
		initInfo.put("gatekeeperURL", "http://localhost:8080/gatekeeper/");
		initInfo.put("storageOnDiskBasePath", basePath);
		dependencyProvider = new SystemOneDependencyProvider(initInfo);

	}

	public void makeSureBasePathExistsAndIsEmpty() throws IOException {
		File dir = new File(basePath);
		dir.mkdir();
		deleteFiles();
	}

	private void deleteFiles() throws IOException {
		Stream<Path> list;
		list = Files.list(Paths.get(basePath));
		list.forEach(p -> deleteFile(p));
		list.close();
	}

	private void deleteFile(Path path) {
		try {
			Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterMethod
	public void removeTempFiles() throws IOException {
		if (Files.exists(Paths.get(basePath))) {
			deleteFiles();
			File dir = new File(basePath);
			dir.delete();
		}
	}

	@Test
	public void testInit() {
		assertNotNull(dependencyProvider.getSpiderAuthorizator());
		assertNotNull(dependencyProvider.getRecordStorage());
		assertNotNull(dependencyProvider.getIdGenerator());
		assertNotNull(dependencyProvider.getPermissionRuleCalculator());
		assertNotNull(dependencyProvider.getDataValidator());
		assertNotNull(dependencyProvider.getDataRecordLinkCollector());
		assertNotNull(dependencyProvider.getStreamStorage());
		assertNotNull(dependencyProvider.getExtendedFunctionalityProvider());
		assertTrue(dependencyProvider
				.getExtendedFunctionalityProvider() instanceof MetacreatorExtendedFunctionalityProvider);
		assertNotNull(dependencyProvider.getAuthenticator());
		assertTrue(dependencyProvider.getAuthenticator() instanceof AuthenticatorImp);
		assertNotNull(dependencyProvider.getDataGroupSearchTermCollector());
	}

	@Test
	public void testGetPermissionRuleCalculator() {
		PermissionRuleCalculator permissionRuleCalculator = dependencyProvider
				.getPermissionRuleCalculator();
		PermissionRuleCalculator permissionRuleCalculator2 = dependencyProvider
				.getPermissionRuleCalculator();
		assertNotEquals(permissionRuleCalculator, permissionRuleCalculator2);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testMissingGatekeeperUrlInInitInfo() {
		Map<String, String> initInfo = new HashMap<>();
		initInfo.put("storageOnDiskBasePath", basePath);
		dependencyProvider = new SystemOneDependencyProvider(initInfo);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testMissingBasePathInInitInfo() {
		Map<String, String> initInfo = new HashMap<>();
		initInfo.put("gatekeeperURL", "http://localhost:8080/gatekeeper/");
		dependencyProvider = new SystemOneDependencyProvider(initInfo);
	}

	@Test
	public void testGetRecordSearch() {
		assertNotNull(dependencyProvider.getRecordSearch());
	}

	@Test
	public void testGetRecordIndexer() {
		assertNotNull(dependencyProvider.getRecordIndexer());
	}

}