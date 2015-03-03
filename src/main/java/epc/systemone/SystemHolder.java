package epc.systemone;

import epc.spider.record.RecordInputBoundary;

public final class SystemHolder {
	
	private SystemHolder(){
		//not called
		throw new UnsupportedOperationException();
	}
	
	private static RecordInputBoundary recordInputBoundary;

	public static RecordInputBoundary getRecordInputBoundary() {
		return recordInputBoundary;

	}

	public static void setRecordInputBoundary(
			RecordInputBoundary recordInputBoundaryIn) {
		recordInputBoundary = recordInputBoundaryIn;
	}

}
