package epc.systemone.record;

import epc.metadataformat.data.DataGroup;
import epc.spider.record.SpiderRecordHandler;
import epc.systemone.SystemHolder;

public class SystemOneRecordHandlerImp implements SystemOneRecordHandler {

	@Override
	public DataGroup createRecord(String userId, String type, DataGroup record) {

		SpiderRecordHandler spiderRecordHandler = SystemHolder.getSpiderRecordHandler();
		DataGroup recordOut = spiderRecordHandler.createAndStoreRecord(userId, type, record);

		return recordOut;

	}

	@Override
	public DataGroup readRecord(String userId, String type, String id) {
		SpiderRecordHandler spiderRecordHandler = SystemHolder.getSpiderRecordHandler();
		DataGroup recordOut = spiderRecordHandler.readRecord(userId, type, id);

		return recordOut;
	}

}
