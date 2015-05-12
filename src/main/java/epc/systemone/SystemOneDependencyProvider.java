package epc.systemone;

import java.util.HashMap;
import java.util.Map;

import epc.beefeater.Authorizator;
import epc.beefeater.AuthorizatorImp;
import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.spider.dependency.SpiderDependencyProvider;
import epc.spider.record.PermissionKeyCalculator;
import epc.spider.record.RecordPermissionKeyCalculator;
import epc.spider.record.storage.RecordIdGenerator;
import epc.spider.record.storage.RecordStorage;
import epc.spider.record.storage.RecordStorageInMemory;
import epc.spider.record.storage.TimeStampIdGenerator;

/**
 * SystemOneDependencyProvider wires up the system for use in "production", as this is in SystemOne
 * production currently means using all in memory storage, so do NOT use this class in production as
 * it is written today. :)
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class SystemOneDependencyProvider implements SpiderDependencyProvider {
	private static final String RECORD_TYPE = "recordType";
	private RecordStorageInMemory recordStorageInMemory;
	private Authorizator authorizator;
	private RecordIdGenerator idGenerator;
	private PermissionKeyCalculator keyCalculator;

	public SystemOneDependencyProvider() {
		Map<String, Map<String, DataGroup>> records = new HashMap<>();
		addRecordTypeRecordType(records);

		recordStorageInMemory = new RecordStorageInMemory(records);
		authorizator = new AuthorizatorImp();
		idGenerator = new TimeStampIdGenerator();
		keyCalculator = new RecordPermissionKeyCalculator();
	}

	@Override
	public Authorizator getAuthorizator() {
		return authorizator;
	}

	@Override
	public RecordStorage getRecordStorage() {
		return recordStorageInMemory;
	}

	@Override
	public RecordIdGenerator getIdGenerator() {
		return idGenerator;
	}

	@Override
	public PermissionKeyCalculator getPermissionKeyCalculator() {
		return keyCalculator;
	}

	private static void addRecordTypeRecordType(Map<String, Map<String, DataGroup>> records) {
		records.put(RECORD_TYPE, new HashMap<String, DataGroup>());
		DataGroup dataGroup = DataGroup.withDataId(RECORD_TYPE);

		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", RECORD_TYPE));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", RECORD_TYPE));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withDataIdAndValue("id", RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("metadataId", RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("presentationViewId",
				"presentation:pgRecordTypeView"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("presentationFormId",
				"presentation:pgRecordTypeForm"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("newMetadataId", "recordTypeNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("newPresentationFormId",
				"presentation:pgRecordTypeFormNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("listPresentationViewId",
				"presentation:pgRecordTypeViewList"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("searchMetadataId",
				"metadata:recordTypeSearch"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("searchPresentationFormId",
				"presentation:pgRecordTypeSearchForm"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("userSuppliedId", "true"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("permissionKey", "RECORDTYPE_RECORDTYPE"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("selfPresentationViewId",
				"presentation:pgrecordTypeRecordType"));
		records.get(RECORD_TYPE).put(RECORD_TYPE, dataGroup);
	}
}
