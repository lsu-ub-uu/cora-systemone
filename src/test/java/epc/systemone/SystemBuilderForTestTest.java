package epc.systemone;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.record.SpiderRecordHandlerImp;
import epc.spider.record.SpiderRecordHandler;

public class SystemBuilderForTestTest {
	@Test
	public void testSystemInit() {
		SystemBuilderForTest systemBuilderForTest = new SystemBuilderForTest();
		systemBuilderForTest.createAllDependenciesInSystemHolder();
		
		SpiderRecordHandler spiderRecordHandler = SystemHolder
				.getSpiderRecordHandler();

		Assert.assertNotNull(spiderRecordHandler,
				"RecordInputBoundry should be instansiated");

		Assert.assertEquals(spiderRecordHandler.getClass(),SpiderRecordHandlerImp.class,
				"The returned recordInputBoundary should be the same");
		
		
	}
}
