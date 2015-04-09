package epc.systemone.record;

import epc.spider.data.SpiderDataGroup;
import epc.spider.record.SpiderRecordHandler;
import epc.systemone.SystemHolder;

public class SystemOneRecordHandlerImp implements SystemOneRecordHandler {

	@Override
	public SpiderDataGroup createRecord(String userId, String type, SpiderDataGroup record) {

		SpiderRecordHandler spiderRecordHandler = SystemHolder.getSpiderRecordHandler();
		return spiderRecordHandler.createAndStoreRecord(userId, type, record);

	}

	@Override
	public SpiderDataGroup readRecord(String userId, String type, String id) {
		SpiderRecordHandler spiderRecordHandler = SystemHolder.getSpiderRecordHandler();
		return spiderRecordHandler.readRecord(userId, type, id);
	}

	@Override
	public void deleteRecord(String userId, String type, String id) {
		SpiderRecordHandler spiderRecordHandler = SystemHolder.getSpiderRecordHandler();
		spiderRecordHandler.deleteRecord(userId, type, id);
	}

	@Override
	public SpiderDataGroup updateRecord(String userId, String type, String id,
			SpiderDataGroup record) {
		SpiderRecordHandler spiderRecordHandler = SystemHolder.getSpiderRecordHandler();
		return spiderRecordHandler.updateRecord(userId, type, id, record);
	}

}
