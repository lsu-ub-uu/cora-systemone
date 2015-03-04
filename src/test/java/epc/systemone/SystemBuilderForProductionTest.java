package epc.systemone;

import static org.testng.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.record.RecordHandler;
import epc.spider.record.RecordInputBoundary;

public class SystemBuilderForProductionTest {
	@Test
	public void testSystemInit() {
		SystemInitializeStatus startResult = SystemBuilderForProduction
				.createAllDependenciesInSystemHolder();

		assertEquals(startResult, SystemInitializeStatus.STARTED_OK,
				"start should return STARTED OK");

		SystemInitializeStatus startResult2 = SystemBuilderForProduction
				.createAllDependenciesInSystemHolder();
		assertEquals(startResult2, SystemInitializeStatus.ALREADY_STARTED,
				"starting a second time should return ALREADY STARTED");

		RecordInputBoundary recordInputBoundary = SystemHolder
				.getRecordInputBoundary();

		Assert.assertNotNull(recordInputBoundary,
				"RecordInputBoundry should be instansiated");

		Assert.assertEquals(recordInputBoundary.getClass(),
				RecordHandler.class,
				"The returned recordInputBoundary should be the same");
	}
}
