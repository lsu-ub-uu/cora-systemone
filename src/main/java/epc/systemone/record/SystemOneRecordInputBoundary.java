package epc.systemone.record;

import epc.metadataformat.data.DataGroup;

public interface SystemOneRecordInputBoundary {

	DataGroup createRecord(String userId, String type, DataGroup record);

}
