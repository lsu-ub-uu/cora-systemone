package epc.systemone;

import epc.spider.record.RecordInputBoundary;

public class SystemHolder {

	private static RecordInputBoundary recordInputBoundary;

	public static RecordInputBoundary getRecordInputBoundary() {
		return recordInputBoundary;

	}

	public static void setRecordInputBoundary(
			RecordInputBoundary recordInputBoundaryIn) {
		recordInputBoundary = recordInputBoundaryIn;
	}

}
