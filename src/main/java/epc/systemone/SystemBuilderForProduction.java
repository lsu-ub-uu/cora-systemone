package epc.systemone;

import static epc.systemone.SystemInitializeStatus.ALREADY_STARTED;
import static epc.systemone.SystemInitializeStatus.NOT_STARTED;
import static epc.systemone.SystemInitializeStatus.STARTED_OK;

import java.util.HashMap;
import java.util.Map;

import epc.beefeater.Authorizator;
import epc.beefeater.AuthorizatorImp;
import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.spider.record.PermissionKeyCalculator;
import epc.spider.record.RecordPermissionKeyCalculator;
import epc.spider.record.SpiderRecordHandler;
import epc.spider.record.SpiderRecordHandlerImp;
import epc.spider.record.storage.RecordIdGenerator;
import epc.spider.record.storage.RecordStorageInMemory;
import epc.spider.record.storage.TimeStampIdGenerator;

/**
 * SystemBuilderForProduction wires up the system for use in "production", as this is in TheOne
 * production currently means using all in memory storage, so do NOT use this class in production as
 * it is written today. :)
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public final class SystemBuilderForProduction {
	private static final String RECORD_TYPE = "recordType";
	private static SystemInitializeStatus status = NOT_STARTED;

	private SystemBuilderForProduction() {
		// not called
		throw new UnsupportedOperationException();
	}

	public static synchronized SystemInitializeStatus createAllDependenciesInSystemHolder() {
		if (NOT_STARTED.equals(status)) {
			status = SystemInitializeStatus.STARTING;
			SystemHolder.setSpiderRecordHandler(defineImplementingSpiderRecordHandler());
			status = STARTED_OK;
			return SystemInitializeStatus.STARTED_OK;
		}
		return ALREADY_STARTED;
	}

	private static SpiderRecordHandler defineImplementingSpiderRecordHandler() {
		Map<String, Map<String, DataGroup>> records = new HashMap<>();
		records.put(RECORD_TYPE, new HashMap<String, DataGroup>());

		addRecordTypeRecordType(records);

		RecordStorageInMemory recordMemory = new RecordStorageInMemory(records);
		Authorizator authorization = new AuthorizatorImp();
		RecordIdGenerator idGenerator = new TimeStampIdGenerator();
		PermissionKeyCalculator keyCalculator = new RecordPermissionKeyCalculator();
		return SpiderRecordHandlerImp
				.usingAuthorizationAndRecordStorageAndIdGeneratorAndKeyCalculator(authorization,
						recordMemory, idGenerator, keyCalculator);
	}

	private static void addRecordTypeRecordType(Map<String, Map<String, DataGroup>> records) {
		String recordType = RECORD_TYPE;
		DataGroup dataGroup = DataGroup.withDataId(recordType);

		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", RECORD_TYPE));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", recordType));
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
		records.get(recordType).put(RECORD_TYPE, dataGroup);
	}
}
