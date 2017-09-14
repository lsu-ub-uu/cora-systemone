/*
 * Copyright 2015, 2016, 2017 Uppsala University Library
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

import java.util.Map;

import se.uu.ub.cora.beefeater.AuthorizatorImp;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollectorImp;
import se.uu.ub.cora.bookkeeper.searchtermcollector.DataGroupTermCollector;
import se.uu.ub.cora.bookkeeper.searchtermcollector.DataGroupTermCollectorImp;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.bookkeeper.validator.DataValidatorImp;
import se.uu.ub.cora.gatekeeperclient.authentication.AuthenticatorImp;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.httphandler.HttpHandlerFactoryImp;
import se.uu.ub.cora.metacreator.extended.MetacreatorExtendedFunctionalityProvider;
import se.uu.ub.cora.searchstorage.SearchStorage;
import se.uu.ub.cora.solr.SolrClientProviderImp;
import se.uu.ub.cora.solrindex.SolrRecordIndexer;
import se.uu.ub.cora.solrsearch.SolrRecordSearch;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.authorization.BasePermissionRuleCalculator;
import se.uu.ub.cora.spider.authorization.PermissionRuleCalculator;
import se.uu.ub.cora.spider.authorization.SpiderAuthorizator;
import se.uu.ub.cora.spider.authorization.SpiderAuthorizatorImp;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.record.RecordSearch;
import se.uu.ub.cora.spider.record.storage.RecordIdGenerator;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.record.storage.TimeStampIdGenerator;
import se.uu.ub.cora.spider.role.RulesProvider;
import se.uu.ub.cora.spider.role.RulesProviderImp;
import se.uu.ub.cora.spider.search.RecordIndexer;
import se.uu.ub.cora.spider.stream.storage.StreamStorage;
import se.uu.ub.cora.storage.RecordStorageOnDisk;
import se.uu.ub.cora.storage.StreamStorageOnDisk;

/**
 * SystemOneDependencyProvider wires up the system for use in "production", as
 * this is in SystemOne production currently means using all in memory storage
 * (stored on disk), so do NOT use this class in production as it is written
 * today. :)
 *
 */
public class SystemOneDependencyProvider extends SpiderDependencyProvider {

	private RecordStorage recordStorage;
	private MetadataStorage metadataStorage;
	private RecordIdGenerator idGenerator;
	private StreamStorage streamStorage;
	private String gatekeeperUrl;
	private String solrUrl;
	private SolrRecordIndexer solrRecordIndexer;
	private SolrRecordSearch solrRecordSearch;

	public SystemOneDependencyProvider(Map<String, String> initInfo) {
		super(initInfo);
		tryToSetGatekeeperUrl();
		String basePath = tryToGetStorageOnDiskBasePath();
		tryToSetSolrUrl();
		recordStorage = RecordStorageOnDisk.createRecordStorageOnDiskWithBasePath(basePath);

		metadataStorage = (MetadataStorage) recordStorage;
		idGenerator = new TimeStampIdGenerator();
		streamStorage = StreamStorageOnDisk.usingBasePath(basePath + "streams/");
		SolrClientProviderImp solrClientProvider = SolrClientProviderImp.usingBaseUrl(solrUrl);
		solrRecordIndexer = SolrRecordIndexer
				.createSolrRecordIndexerUsingSolrClientProvider(solrClientProvider);
		SearchStorage searchStorage = (SearchStorage) recordStorage;
		solrRecordSearch = SolrRecordSearch
				.createSolrRecordSearchUsingSolrClientProviderAndSearchStorage(solrClientProvider,
						searchStorage);
	}

	private void tryToSetGatekeeperUrl() {
		throwErrorIfMissionGatekeeperUrl();
		gatekeeperUrl = initInfo.get("gatekeeperURL");
	}

	private void throwErrorIfMissionGatekeeperUrl() {
		if (!initInfo.containsKey("gatekeeperURL")) {
			throw new RuntimeException("InitInfo must contain gatekeeperURL");
		}
	}

	private String tryToGetStorageOnDiskBasePath() {
		throwErrorIfStorageOnDiskBasePathIsMissing();
		return initInfo.get("storageOnDiskBasePath");
	}

	private void throwErrorIfStorageOnDiskBasePathIsMissing() {
		if (!initInfo.containsKey("storageOnDiskBasePath")) {
			throw new RuntimeException("InitInfo must contain storageOnDiskBasePath");
		}
	}

	private void tryToSetSolrUrl() {
		throwErrorIfMissingSolrUrl();
		solrUrl = initInfo.get("solrURL");
	}

	private void throwErrorIfMissingSolrUrl() {
		if (!initInfo.containsKey("solrURL")) {
			throw new RuntimeException("InitInfo must contain solrURL");
		}
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
		return new MetacreatorExtendedFunctionalityProvider(this);
	}

	@Override
	public Authenticator getAuthenticator() {
		HttpHandlerFactory httpHandlerFactory = new HttpHandlerFactoryImp();
		return AuthenticatorImp.usingBaseUrlAndHttpHandlerFactory(gatekeeperUrl,
				httpHandlerFactory);
	}

	@Override
	public RecordSearch getRecordSearch() {
		return solrRecordSearch;
	}

	@Override
	public DataGroupTermCollector getDataGroupSearchTermCollector() {
		return new DataGroupTermCollectorImp(metadataStorage);
	}

	@Override
	public RecordIndexer getRecordIndexer() {
		return solrRecordIndexer;
	}

}