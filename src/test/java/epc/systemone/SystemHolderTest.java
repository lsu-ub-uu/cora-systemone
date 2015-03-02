package epc.systemone;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SystemHolderTest {
	@Test
	public void testInit(){
		SystemBuilderForTest systemBuilderForTest = new SystemBuilderForTest();
		systemBuilderForTest.createAllDependenciesInSystemHolder();
		
		Assert.assertNotNull(SystemHolder.getRecordInputBoundary());
		
	}
	
}
