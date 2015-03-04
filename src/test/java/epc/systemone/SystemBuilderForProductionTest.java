package epc.systemone;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.record.RecordHandler;
import epc.spider.record.RecordInputBoundary;

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

		RecordInputBoundary recordInputBoundary = SystemHolder
				.getRecordInputBoundary();

		Assert.assertNotNull(recordInputBoundary,
				"RecordInputBoundry should be instansiated");

		Assert.assertEquals(recordInputBoundary.getClass(),
				RecordHandler.class,
				"The returned recordInputBoundary should be the same");
	}
}
