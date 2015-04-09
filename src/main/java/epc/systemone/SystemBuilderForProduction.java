package epc.systemone;

import static epc.systemone.SystemInitializeStatus.ALREADY_STARTED;
import static epc.systemone.SystemInitializeStatus.NOT_STARTED;
import static epc.systemone.SystemInitializeStatus.STARTED_OK;
import epc.beefeater.Authorizator;
import epc.beefeater.AuthorizatorImp;
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
		RecordStorageInMemory recordMemory = new RecordStorageInMemory();
		Authorizator authorization = new AuthorizatorImp();
		RecordIdGenerator idGenerator = new TimeStampIdGenerator();
		PermissionKeyCalculator keyCalculator = new RecordPermissionKeyCalculator();
		return SpiderRecordHandlerImp
				.usingAuthorizationAndRecordStorageAndIdGeneratorAndKeyCalculator(authorization,
						recordMemory, idGenerator, keyCalculator);
	}
}
