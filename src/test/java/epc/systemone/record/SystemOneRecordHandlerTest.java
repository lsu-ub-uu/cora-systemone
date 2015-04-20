package epc.systemone.record;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.spider.data.SpiderDataRecord;
import epc.spider.record.storage.RecordNotFoundException;
import epc.systemone.SystemBuilderForTest;

public class SystemOneRecordHandlerTest {
	@Test
	public void testCreateRecord() {
		SystemBuilderForTest systemBuilderForTest = new SystemBuilderForTest();
		systemBuilderForTest.createAllDependenciesInSystemHolder();

		SystemOneRecordHandler handler = new SystemOneRecordHandlerImp();
		SpiderDataGroup record = SpiderDataGroup.withDataId("authority");
		SpiderDataRecord recordOut = handler.createRecord("userId", "type", record);
		SpiderDataGroup groupOut = recordOut.getSpiderDataGroup();
		SpiderDataGroup recordInfo = (SpiderDataGroup) groupOut.getChildren().stream()
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

		SpiderDataRecord record = handler.readRecord("userId", "place", "place:0001");

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

		SpiderDataRecord record = handler.readRecord("userId", "place", "place:0001");
		SpiderDataGroup group = record.getSpiderDataGroup();
		group.addChild(SpiderDataAtomic.withDataIdAndValue("atomicId", "atomicValue"));

		SpiderDataRecord recordUpdated = handler.updateRecord("userId", "place", "place:0001",
				group);
		assertNotNull(recordUpdated);

	}
}
