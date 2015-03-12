package epc.systemone.record;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.systemone.SystemBuilderForTest;

public class SystemOneRecordHandlerTest {
	@Test
	public void testCreateRecord() {
		SystemBuilderForTest systemBuilderForTest = new SystemBuilderForTest();
		systemBuilderForTest.createAllDependenciesInSystemHolder();

		SystemOneRecordHandler input = new SystemOneRecordHandlerImp();
		SpiderDataGroup record = SpiderDataGroup.withDataId("authority");
		SpiderDataGroup recordOut = input.createRecord("userId", "type", record);

		SpiderDataGroup recordInfo = (SpiderDataGroup) recordOut.getChildren().stream()
				.filter(p -> p.getDataId().equals("recordInfo")).findFirst().get();
		SpiderDataAtomic recordId = (SpiderDataAtomic) recordInfo.getChildren().stream()
				.filter(p -> p.getDataId().equals("id")).findFirst().get();

		Assert.assertNotNull(recordId.getValue(), "A new record should have an id");
	}

	@Test
	public void testReadRecord() {
		SystemBuilderForTest systemBuilderForTest = new SystemBuilderForTest();
		systemBuilderForTest.createAllDependenciesInSystemHolder();

		SystemOneRecordHandler input = new SystemOneRecordHandlerImp();

		SpiderDataGroup record = input.readRecord("userId", "place", "place:0001");

		Assert.assertNotNull(record);
	}
}
