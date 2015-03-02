package epc.systemone.record;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.systemone.SystemBuilderForTest;

public class SystemOneRecordInputBoundaryTest {
	@Test
	public void testCreateRecord() {
		SystemBuilderForTest systemBuilderForTest = new SystemBuilderForTest();
		systemBuilderForTest.createAllDependenciesInSystemHolder();

		SystemOneRecordInputBoundary input = new SystemOneRecordHandler();
		DataGroup record = new DataGroup("authority");
		DataGroup recordOut = input.createRecord("userId", "type", record);

		// DataGroup recordOut = input.createAndStoreRecord("userId",
		// "type", record);

		DataGroup recordInfo = (DataGroup) recordOut.getChildren().stream()
				.filter(p -> p.getDataId().equals("recordInfo")).findFirst()
				.get();
		DataAtomic recordId = (DataAtomic) recordInfo.getChildren().stream()
				.filter(p -> p.getDataId().equals("id")).findFirst().get();

		Assert.assertNotNull(recordId.getValue(),
				"A new record should have an id");

		// DataGroup recordRead = recordHandler.readRecord("userId", "type",
		// recordId.getValue());
		// Assert.assertEquals(recordOut, recordRead,
		// "Returned and read record should be the same");
	}
}
