/*
 * Copyright 2015, 2016, 2017, 2018 Uppsala University Library
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import se.uu.ub.cora.beefeater.AuthorizatorImp;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollectorImp;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.termcollector.DataGroupTermCollector;
import se.uu.ub.cora.bookkeeper.termcollector.DataGroupTermCollectorImp;
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
	private SolrClientProviderImp solrClientProvider;
	private SearchStorage searchStorage;
	private String basePath;
	private String storageOnDiskClassName;

	public SystemOneDependencyProvider(Map<String, String> initInfo) {
		super(initInfo);
	}

	@Override
	protected void readInitInfo() {
		tryToSetGatekeeperUrl();
		basePath = tryToGetStorageOnDiskBasePath();
		storageOnDiskClassName = tryToGetStorageOnDiskClassName();
		tryToSetSolrUrl();
	}

	private String tryToGetStorageOnDiskClassName() {
		throwErrorIfMissingKeyIsMissingFromInitInfo("storageOnDiskClassName");
		return initInfo.get("storageOnDiskClassName");
	}

	private void throwErrorIfMissingKeyIsMissingFromInitInfo(String key) {
		if (!initInfo.containsKey(key)) {
			throw new RuntimeException("InitInfo must contain " + key);
		}
	}

	@Override
	protected void tryToInitialize() throws NoSuchMethodException, ClassNotFoundException,
			IllegalAccessException, InvocationTargetException {
		recordStorage = tryToCreateRecordStorage(basePath);

		metadataStorage = (MetadataStorage) recordStorage;
		idGenerator = new TimeStampIdGenerator();
		streamStorage = StreamStorageOnDisk.usingBasePath(basePath + "streams/");
		solrClientProvider = SolrClientProviderImp.usingBaseUrl(solrUrl);
		solrRecordIndexer = SolrRecordIndexer
				.createSolrRecordIndexerUsingSolrClientProvider(solrClientProvider);
		searchStorage = (SearchStorage) recordStorage;
	}

	private RecordStorage tryToCreateRecordStorage(String basePath) throws NoSuchMethodException,
			ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		Class<?>[] cArg = new Class[1];
		cArg[0] = String.class;
		Method constructor = Class.forName(storageOnDiskClassName)
				.getMethod("createRecordStorageOnDiskWithBasePath", cArg);
		return (RecordStorage) constructor.invoke(null, basePath);
	}

	private void tryToSetGatekeeperUrl() {
		throwErrorIfMissingKeyIsMissingFromInitInfo("gatekeeperURL");
		gatekeeperUrl = initInfo.get("gatekeeperURL");
	}

	private String tryToGetStorageOnDiskBasePath() {
		throwErrorIfMissingKeyIsMissingFromInitInfo("storageOnDiskBasePath");
		return initInfo.get("storageOnDiskBasePath");
	}

	private void tryToSetSolrUrl() {
		throwErrorIfMissingKeyIsMissingFromInitInfo("solrURL");
		solrUrl = initInfo.get("solrURL");
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
		return SolrRecordSearch.createSolrRecordSearchUsingSolrClientProviderAndSearchStorage(
				solrClientProvider, searchStorage);
	}

	@Override
	public DataGroupTermCollector getDataGroupTermCollector() {
		return new DataGroupTermCollectorImp(metadataStorage);
	}

	@Override
	public RecordIndexer getRecordIndexer() {
		return solrRecordIndexer;
	}

}