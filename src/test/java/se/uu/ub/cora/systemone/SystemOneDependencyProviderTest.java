package se.uu.ub.cora.systemone;

import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import se.uu.ub.cora.systemone.SystemOneDependencyProvider;

public class SystemOneDependencyProviderTest {
	@Test
	public void testInit() {
		SystemOneDependencyProvider dependencyProvider = new SystemOneDependencyProvider();
		assertNotNull(dependencyProvider.getAuthorizator());
		assertNotNull(dependencyProvider.getRecordStorage());
		assertNotNull(dependencyProvider.getIdGenerator());
		assertNotNull(dependencyProvider.getPermissionKeyCalculator());
		assertNotNull(dependencyProvider.getDataValidator());
	}
}