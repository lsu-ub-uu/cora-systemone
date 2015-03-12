package epc.systemone.record;

import epc.spider.data.SpiderDataGroup;

public interface SystemOneRecordHandler {

	SpiderDataGroup createRecord(String userId, String type, SpiderDataGroup record);

	SpiderDataGroup readRecord(String userId, String type, String id);

}
