package epc.systemone;

import org.testng.annotations.Test;

public class SystemInitalizeStatusTest {
	@Test
	public void test() {
		// This currently only helps to get test coverage of the enum.
		// small hack to get 100% coverage on enum
		SystemInitializeStatus.valueOf(SystemInitializeStatus.ALREADY_STARTED.toString());
	}
}
