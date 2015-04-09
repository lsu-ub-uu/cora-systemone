package epc.systemone.record;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.spider.record.storage.RecordNotFoundException;
import epc.systemone.SystemBuilderForTest;

public class SystemOneRecordHandlerTest {
	@Test
	public void testCreateRecord() {
		SystemBuilderForTest systemBuilderForTest = new SystemBuilderForTest();
		systemBuilderForTest.createAllDependenciesInSystemHolder();

		SystemOneRecordHandler handler = new SystemOneRecordHandlerImp();
		SpiderDataGroup record = SpiderDataGroup.withDataId("authority");
		SpiderDataGroup recordOut = handler.createRecord("userId", "type", record);

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

		SystemOneRecordHandler handler = new SystemOneRecordHandlerImp();

		SpiderDataGroup record = handler.readRecord("userId", "place", "place:0001");

		Assert.assertNotNull(record);
	}

	@Test
	public void testDeleteRecord() {
		SystemBuilderForTest systemBuilderForTest = new SystemBuilderForTest();
		systemBuilderForTest.createAllDependenciesInSystemHolder();

		SystemOneRecordHandler handler = new SystemOneRecordHandlerImp();

		handler.deleteRecord("userId", "place", "place:0001");
		boolean recordFound = true;
		try {
			handler.readRecord("userId", "place", "place:0001");
		} catch (RecordNotFoundException e) {
			recordFound = false;
		}
		assertFalse(recordFound);
	}

	@Test
	public void testUpdateRecord() {
		SystemBuilderForTest systemBuilderForTest = new SystemBuilderForTest();
		systemBuilderForTest.createAllDependenciesInSystemHolder();
		SystemOneRecordHandler handler = new SystemOneRecordHandlerImp();

		SpiderDataGroup record = handler.readRecord("userId", "place", "place:0001");

		record.addChild(SpiderDataAtomic.withDataIdAndValue("atomicId", "atomicValue"));

		SpiderDataGroup recordUpdated = handler.updateRecord("userId", "place", "place:0001",
				record);
		assertNotNull(recordUpdated);

	}
}
