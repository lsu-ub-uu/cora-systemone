/*
 * Copyright 2015, 2017, 2018 Uppsala University Library
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.gatekeeperclient.authentication.AuthenticatorImp;
import se.uu.ub.cora.metacreator.extended.MetacreatorExtendedFunctionalityProvider;
import se.uu.ub.cora.solr.SolrClientProviderImp;
import se.uu.ub.cora.solrindex.SolrRecordIndexer;
import se.uu.ub.cora.solrsearch.SolrRecordSearch;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.authorization.PermissionRuleCalculator;
import se.uu.ub.cora.spider.record.RecordSearch;
import se.uu.ub.cora.spider.search.RecordIndexer;

public class SystemOneDependencyProviderTest {
	private SystemOneDependencyProvider dependencyProvider;
	private String basePath = "/tmp/recordStorageOnDiskTemp/";
	private Map<String, String> initInfo;

	@BeforeMethod
	public void setUp() throws Exception {
		try {
			makeSureBasePathExistsAndIsEmpty();
			initInfo = new HashMap<>();
			initInfo.put("storageOnDiskClassName", "se.uu.ub.cora.systemone.RecordStorageSpy");
			initInfo.put("gatekeeperURL", "http://localhost:8080/gatekeeper/");
			initInfo.put("storageOnDiskBasePath", basePath);
			initInfo.put("solrURL", "http://localhost:8983/solr/stuff");
			dependencyProvider = new SystemOneDependencyProvider(initInfo);

		} catch (Exception e) {
			// Make the correct tests crash instead of all
		}

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
		assertTrue(dependencyProvider.getAuthenticator() instanceof AuthenticatorImp);
		assertTrue(dependencyProvider
				.getExtendedFunctionalityProvider() instanceof MetacreatorExtendedFunctionalityProvider);
		assertNotNull(dependencyProvider.getDataGroupTermCollector());
		assertTrue(dependencyProvider.getRecordIndexer() instanceof SolrRecordIndexer);
		SolrRecordSearch solrRecordSearch = (SolrRecordSearch) dependencyProvider.getRecordSearch();
		assertTrue(solrRecordSearch.getSearchStorage() instanceof RecordStorageSpy);
	}

	@Test
	public void testMissingStorageClassNameInInitInfo() {
		initInfo.remove("storageOnDiskClassName");

		Exception thrownException = callSystemOneDependencyProviderAndReturnResultingError();

		assertTrue(thrownException instanceof RuntimeException);
		assertEquals(thrownException.getMessage(), "InitInfo must contain storageOnDiskClassName");
	}

	private Exception callSystemOneDependencyProviderAndReturnResultingError() {
		Exception thrownException = null;
		try {
			dependencyProvider = new SystemOneDependencyProvider(initInfo);
		} catch (Exception e) {
			thrownException = e;
		}
		return thrownException;
	}

	@Test
	public void testNonExisitingStorageOnDiskClassNameInInitInfo() {
		initInfo.put("storageOnDiskClassName", "se.uu.ub.cora.systemone.RecordStorageNON");

		Exception thrownException = callSystemOneDependencyProviderAndReturnResultingError();

		assertTrue(thrownException instanceof RuntimeException);
		assertEquals(thrownException.getMessage(), "Error starting SystemOneDependencyProvider: "
				+ "se.uu.ub.cora.systemone.RecordStorageNON");
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ""
			+ "Error starting SystemOneDependencyProvider: "
			+ "Invocation exception from RecordStorageErrorOnStartupSpy")
	public void testHandlingAndGettingCorrectErrorMessageFromErrorsThrowsOnStartup() {
		initInfo.put("storageOnDiskClassName",
				"se.uu.ub.cora.systemone.RecordStorageInvocationErrorOnStartupSpy");
		dependencyProvider = new SystemOneDependencyProvider(initInfo);
	}

	@Test
	public void testCorrectStorageOnDiskClassInitialized() throws Exception {
		assertEquals(dependencyProvider.getRecordStorage().getClass().getName(),
				initInfo.get("storageOnDiskClassName"));
		assertTrue(dependencyProvider.getRecordStorage() instanceof RecordStorageSpy);
	}

	@Test
	public void testCorrectBasePathSentToStorageOnDisk() throws Exception {
		assertEquals(dependencyProvider.getRecordStorage().getClass().getName(),
				initInfo.get("storageOnDiskClassName"));
		assertEquals(((RecordStorageSpy) dependencyProvider.getRecordStorage()).getBasePath(),
				initInfo.get("storageOnDiskBasePath"));
	}

	@Test
	public void testGetPermissionRuleCalculator() {
		PermissionRuleCalculator permissionRuleCalculator = dependencyProvider
				.getPermissionRuleCalculator();
		PermissionRuleCalculator permissionRuleCalculator2 = dependencyProvider
				.getPermissionRuleCalculator();
		assertNotEquals(permissionRuleCalculator, permissionRuleCalculator2);
	}

	@Test
	public void testMissingBasePathInInitInfo() {
		initInfo.remove("storageOnDiskBasePath");

		Exception thrownException = callSystemOneDependencyProviderAndReturnResultingError();

		assertEquals(thrownException.getMessage(), "InitInfo must contain storageOnDiskBasePath");
		assertTrue(thrownException instanceof RuntimeException);
	}

	@Test
	public void testMissingGatekeeperUrlInInitInfo() {
		initInfo.remove("gatekeeperURL");

		Exception thrownException = callSystemOneDependencyProviderAndReturnResultingError();

		assertTrue(thrownException instanceof RuntimeException);
		assertEquals(thrownException.getMessage(), "InitInfo must contain gatekeeperURL");
	}

	@Test
	public void testtestGetAuthenticatorUsesGatekeeperUrl() {
		Authenticator authenticator = dependencyProvider.getAuthenticator();
		assertNotNull(authenticator);
		try {
			Field f2;
			f2 = authenticator.getClass().getDeclaredField("baseUrl");
			f2.setAccessible(true);
			String baseUrl = (String) f2.get(authenticator);

			assertEquals(baseUrl, "http://localhost:8080/gatekeeper/");
		} catch (Exception e) {
			// if exception fail test
			assertTrue(false);
		}
	}

	@Test
	public void testGetRecordSearch() {
		assertNotNull(dependencyProvider.getRecordSearch());
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testMissingSolrUrlInInitInfo() {
		Map<String, String> initInfo = new HashMap<>();
		initInfo.put("storageOnDiskBasePath", basePath);
		initInfo.put("gatekeeperURL", "http://localhost:8080/gatekeeper/");
		dependencyProvider = new SystemOneDependencyProvider(initInfo);
	}

	@Test
	public void testDependencyProviderReturnsOnlyOneInstanceOfRecordndexer() {
		RecordIndexer recordIndexer = dependencyProvider.getRecordIndexer();
		RecordIndexer recordIndexer2 = dependencyProvider.getRecordIndexer();
		assertEquals(recordIndexer, recordIndexer2);
	}

	@Test
	public void testMissingSolrURLInInitInfo() {
		initInfo.remove("solrURL");

		Exception thrownException = callSystemOneDependencyProviderAndReturnResultingError();

		assertEquals(thrownException.getMessage(), "InitInfo must contain solrURL");
		assertTrue(thrownException instanceof RuntimeException);
	}

	@Test
	public void testGetRecordIndexerUsesSolrUrlWhenCreatingSolrClientProvider() {
		RecordIndexer recordIndexer = dependencyProvider.getRecordIndexer();
		assertNotNull(recordIndexer);
		try {
			Field f;
			f = recordIndexer.getClass().getDeclaredField("solrClientProvider");
			f.setAccessible(true);
			SolrClientProviderImp solrClientProviderImp = (SolrClientProviderImp) f
					.get(recordIndexer);

			Field f2;
			f2 = solrClientProviderImp.getClass().getDeclaredField("baseUrl");
			f2.setAccessible(true);
			String baseUrl = (String) f2.get(solrClientProviderImp);

			assertEquals(baseUrl, "http://localhost:8983/solr/stuff");
		} catch (Exception e) {
			// if exception fail test
			assertTrue(false);
		}
	}

	@Test
	public void testGetRecordSearchUsesSolrUrlWhenCreatingSolrClientProvider() {
		RecordSearch recordSearcher = dependencyProvider.getRecordSearch();
		assertNotNull(recordSearcher);
		try {
			Field f;
			f = recordSearcher.getClass().getDeclaredField("solrClientProvider");
			f.setAccessible(true);
			SolrClientProviderImp solrClientProviderImp = (SolrClientProviderImp) f
					.get(recordSearcher);

			Field f2;
			f2 = solrClientProviderImp.getClass().getDeclaredField("baseUrl");
			f2.setAccessible(true);
			String baseUrl = (String) f2.get(solrClientProviderImp);

			assertEquals(baseUrl, "http://localhost:8983/solr/stuff");
		} catch (Exception e) {
			// if exception fail test
			assertTrue(false);
		}
	}

	@Test
	public void testDependencyProviderReturnsDifferentRecordSearch() {
		RecordSearch recordSearch = dependencyProvider.getRecordSearch();
		RecordSearch recordSearch2 = dependencyProvider.getRecordSearch();
		assertNotEquals(recordSearch, recordSearch2);
	}

}