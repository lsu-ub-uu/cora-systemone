package epc.systemone;

import epc.spider.record.SpiderRecordHandler;

public final class SystemHolder {

	private SystemHolder() {
		// not called
		throw new UnsupportedOperationException();
	}

	private static SpiderRecordHandler spiderRecordHandler;

	public static SpiderRecordHandler getSpiderRecordHandler() {
		return spiderRecordHandler;

	}

	public static void setSpiderRecordHandler(SpiderRecordHandler spiderRecordHandlerIn) {
		spiderRecordHandler = spiderRecordHandlerIn;
	}

}
