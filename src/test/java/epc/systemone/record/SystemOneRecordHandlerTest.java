package epc.systemone.record;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.systemone.SystemBuilderForTest;

public class SystemOneRecordHandlerTest {
	@Test
	public void testCreateRecord() {
		SystemBuilderForTest systemBuilderForTest = new SystemBuilderForTest();
		systemBuilderForTest.createAllDependenciesInSystemHolder();

		SystemOneRecordHandler input = new SystemOneRecordHandlerImp();
		DataGroup record = DataGroup.withDataId("authority");
		DataGroup recordOut = input.createRecord("userId", "type", record);


		DataGroup recordInfo = (DataGroup) recordOut.getChildren().stream()
				.filter(p -> p.getDataId().equals("recordInfo")).findFirst()
				.get();
		DataAtomic recordId = (DataAtomic) recordInfo.getChildren().stream()
				.filter(p -> p.getDataId().equals("id")).findFirst().get();

		Assert.assertNotNull(recordId.getValue(),
				"A new record should have an id");
	}
	
	@Test
	public void testReadRecord(){
		SystemBuilderForTest systemBuilderForTest = new SystemBuilderForTest();
		systemBuilderForTest.createAllDependenciesInSystemHolder();
		
		SystemOneRecordHandler input = new SystemOneRecordHandlerImp();
		
		DataGroup record = input.readRecord("userId", "place", "place:0001");
		
		Assert.assertNotNull(record);
	}
}
