package epc.systemone.record;

import epc.metadataformat.data.DataGroup;

public interface SystemOneRecordHandler {

	DataGroup createRecord(String userId, String type, DataGroup record);

	DataGroup readRecord(String userId, String type, String id);

}
