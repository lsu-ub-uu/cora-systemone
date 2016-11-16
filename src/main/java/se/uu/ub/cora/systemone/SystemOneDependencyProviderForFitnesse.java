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

import se.uu.ub.cora.beefeater.AuthorizatorImp;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollectorImp;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.bookkeeper.validator.DataValidatorImp;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.authentication.AuthenticatorImp;
import se.uu.ub.cora.spider.authentication.UserPicker;
import se.uu.ub.cora.spider.authorization.BasePermissionRuleCalculator;
import se.uu.ub.cora.spider.authorization.PermissionRuleCalculator;
import se.uu.ub.cora.spider.authorization.SpiderAuthorizator;
import se.uu.ub.cora.spider.authorization.SpiderAuthorizatorImp;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extended.BaseExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.record.storage.RecordIdGenerator;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.record.storage.TimeStampIdGenerator;
import se.uu.ub.cora.spider.role.RulesProvider;
import se.uu.ub.cora.spider.role.RulesProviderImp;
import se.uu.ub.cora.spider.stream.storage.StreamStorage;
import se.uu.ub.cora.storage.RecordStorageInMemoryReadFromDisk;
import se.uu.ub.cora.storage.StreamStorageOnDisk;
import se.uu.ub.cora.systemone.authentication.SystemOneUserPicker;

public class SystemOneDependencyProviderForFitnesse implements SpiderDependencyProvider {
	private RecordStorage recordStorage;
	private MetadataStorage metadataStorage;
	private RecordIdGenerator idGenerator;
	private StreamStorage streamStorage;

	public SystemOneDependencyProviderForFitnesse() {
		String basePath = "/mnt/data/basicstorage-fitnesse/";
		recordStorage = RecordStorageInMemoryReadFromDisk
				.createRecordStorageOnDiskWithBasePath(basePath);
		metadataStorage = (MetadataStorage) recordStorage;
		idGenerator = new TimeStampIdGenerator();
		streamStorage = StreamStorageOnDisk.usingBasePath(basePath + "streams/");
	}

	@Override
	public SpiderAuthorizator getSpiderAuthorizator() {
		RulesProvider rulesProvider = new RulesProviderImp(recordStorage);
		return SpiderAuthorizatorImp.usingSpiderDependencyProviderAndAuthorizatorAndRulesProvider(
				this, new AuthorizatorImp(), rulesProvider);
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
	public PermissionRuleCalculator getPermissionRuleCalculator() {
		return new BasePermissionRuleCalculator();
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
	public StreamStorage getStreamStorage() {
		return streamStorage;
	}

	@Override
	public ExtendedFunctionalityProvider getExtendedFunctionalityProvider() {
		return new BaseExtendedFunctionalityProvider(this);
	}

	@Override
	public Authenticator getAuthenticator() {
		UserPicker userPicker = SystemOneUserPicker.usingRecordStorage(recordStorage);
		return new AuthenticatorImp(userPicker);
	}
}
