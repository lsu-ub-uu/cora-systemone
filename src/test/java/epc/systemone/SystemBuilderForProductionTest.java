package epc.systemone;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.record.SpiderRecordHandlerImp;
import epc.spider.record.SpiderRecordHandler;

public class SystemBuilderForProductionTest {

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<SystemBuilderForProduction> constructor = SystemBuilderForProduction.class
				.getDeclaredConstructor();
		Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}
	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<SystemBuilderForProduction> constructor = SystemBuilderForProduction.class
				.getDeclaredConstructor();
		Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}


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

		SpiderRecordHandler spiderRecordHandler = SystemHolder
				.getSpiderRecordHandler();

		Assert.assertNotNull(spiderRecordHandler,
				"RecordInputBoundry should be instansiated");

		Assert.assertEquals(spiderRecordHandler.getClass(),
				SpiderRecordHandlerImp.class,
				"The returned recordInputBoundary should be the same");
	}
}
