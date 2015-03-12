package epc.systemone.record;

import epc.spider.data.SpiderDataGroup;
import epc.spider.record.SpiderRecordHandler;
import epc.systemone.SystemHolder;

public class SystemOneRecordHandlerImp implements SystemOneRecordHandler {

	@Override
	public SpiderDataGroup createRecord(String userId, String type, SpiderDataGroup record) {

		SpiderRecordHandler spiderRecordHandler = SystemHolder.getSpiderRecordHandler();
		SpiderDataGroup recordOut = spiderRecordHandler.createAndStoreRecord(userId, type, record);

		return recordOut;

	}

	@Override
	public SpiderDataGroup readRecord(String userId, String type, String id) {
		SpiderRecordHandler spiderRecordHandler = SystemHolder.getSpiderRecordHandler();
		SpiderDataGroup recordOut = spiderRecordHandler.readRecord(userId, type, id);

		return recordOut;
	}

}
