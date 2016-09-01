/*
 * Copyright 2015, 2016 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.systemone;

import se.uu.ub.cora.beefeater.Authorizator;
import se.uu.ub.cora.beefeater.AuthorizatorImp;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollectorImp;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.bookkeeper.validator.DataValidatorImp;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.record.PermissionKeyCalculator;
import se.uu.ub.cora.spider.record.storage.RecordIdGenerator;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.record.storage.TimeStampIdGenerator;
import se.uu.ub.cora.storage.RecordStorageOnDisk;
import se.uu.ub.cora.systemone.record.RecordPermissionKeyCalculator;

/**
 * SystemOneDependencyProvider wires up the system for use in "production", as
 * this is in SystemOne production currently means using all in memory storage,
 * so do NOT use this class in production as it is written today. :)
 *
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 * @since 0.1
 */
public class SystemOneDependencyProvider implements SpiderDependencyProvider {

	private RecordStorage recordStorage;
	private MetadataStorage metadataStorage;
	private Authorizator authorizator;
	private RecordIdGenerator idGenerator;
	private PermissionKeyCalculator keyCalculator;

	public SystemOneDependencyProvider() {
		String basePath = "/mnt/data/basicstorage/";
		recordStorage = RecordStorageOnDisk.createRecordStorageOnDiskWithBasePath(basePath);
		metadataStorage = (MetadataStorage) recordStorage;
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
		return recordStorage;
	}

	@Override
	public RecordIdGenerator getIdGenerator() {
		return idGenerator;
	}

	@Override
	public PermissionKeyCalculator getPermissionKeyCalculator() {
		return keyCalculator;
	}

	@Override
	public DataValidator getDataValidator() {
		return new DataValidatorImp(metadataStorage);
	}

	@Override
	public DataRecordLinkCollector getDataRecordLinkCollector() {
		return new DataRecordLinkCollectorImp(metadataStorage);
	}

	@Override
	public ExtendedFunctionalityProvider getExtendedFunctionalityProvider() {
		// TODO Auto-generated method stub
		return null;
	}
}