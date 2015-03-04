package epc.systemone;

import static epc.systemone.SystemInitializeStatus.*;
import epc.beefeater.AuthorizationInputBoundary;
import epc.beefeater.Authorizator;
import epc.spider.record.PermissionKeyCalculator;
import epc.spider.record.RecordHandler;
import epc.spider.record.RecordInputBoundary;
import epc.spider.record.RecordPermissionKeyCalculator;
import epc.spider.record.storage.RecordIdGenerator;
import epc.spider.record.storage.RecordInMemoryStorage;
import epc.spider.record.storage.TimeStampIdGenerator;

/**
 * SystemBuilderForProduction wires up the system for use in "production", as
 * this is in TheOne production currently means using all in memory storage, so
 * do NOT use this class in production as it is written today. :)
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

	public static SystemInitializeStatus createAllDependenciesInSystemHolder() {
		if (NOT_STARTED.equals(status)) {
			status = SystemInitializeStatus.STARTING;
			SystemHolder
					.setRecordInputBoundary(defineImplementingRecordInputBoundary());
			status = STARTED_OK;
			return SystemInitializeStatus.STARTED_OK;
		}
		return ALREADY_STARTED;
	}

	private static RecordInputBoundary defineImplementingRecordInputBoundary() {
		RecordInMemoryStorage recordStorage = new RecordInMemoryStorage();
		AuthorizationInputBoundary authorization = new Authorizator();
		RecordIdGenerator idGenerator = new TimeStampIdGenerator();
		PermissionKeyCalculator keyCalculator = new RecordPermissionKeyCalculator();
		return new RecordHandler(authorization, recordStorage, idGenerator,
				keyCalculator);
	}
}
