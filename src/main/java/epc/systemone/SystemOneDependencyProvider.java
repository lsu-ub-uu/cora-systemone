package epc.systemone;

import java.util.HashMap;
import java.util.Map;

import epc.beefeater.Authorizator;
import epc.beefeater.AuthorizatorImp;
import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.storage.MetadataStorage;
import epc.metadataformat.validator.DataValidator;
import epc.metadataformat.validator.DataValidatorImp;
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
	private static final String A_Z = "(^[A-Z\\_]{2,50}$)";
	private static final String TRUE_OR_FALSE = "^true$|^false$";
	private static final String SELF_PRESENTATION_VIEW_ID = "selfPresentationViewId";
	private static final String PERMISSION_KEY = "permissionKey";
	private static final String USER_SUPPLIED_ID = "userSuppliedId";
	private static final String SEARCH_PRESENTATION_FORM_ID = "searchPresentationFormId";
	private static final String SEARCH_METADATA_ID = "searchMetadataId";
	private static final String LIST_PRESENTATION_VIEW_ID = "listPresentationViewId";
	private static final String NEW_PRESENTATION_FORM_ID = "newPresentationFormId";
	private static final String PRESENTATION_FORM_ID = "presentationFormId";
	private static final String PRESENTATION_VIEW_ID = "presentationViewId";
	private static final String METADATA_ID = "metadataId";
	private static final String REPEAT_MAX = "repeatMax";
	private static final String REPEAT_MIN = "repeatMin";
	private static final String DEF_TEXT_ID = "defTextId";
	private static final String TEXT_ID = "textId";
	private static final String RECORD_TYPE_NEW = "recordTypeNew";
	private static final String METADATA_NEW = "metadataNew";
	private static final String ATTRIBUTE_REFERENCES = "attributeReferences";
	private static final String NEW_METADATA_ID = "newMetadataId";
	private static final String CHILD_REFERENCE = "childReference";
	private static final String GROUP = "group";
	private static final String RECORD_TYPE_TYPE_GROUP = "recordTypeTypeGroup";
	private static final String RECORD_TYPE_TYPE_COLLECTION = "recordTypeTypeCollection";
	private static final String RECORD_TYPE_TYPE_COLLECTION_VAR = "recordTypeTypeCollectionVar";
	private static final String ID = "id";
	private static final String DATA_ID = "dataId";
	private static final String CHILD_REFERENCES = "childReferences";
	private static final String RECORD_INFO_NEW = "recordInfoNew";
	private static final String METADATA = "metadata";
	private static final String UPDATED_BY = "updatedBy";
	private static final String CREATED_BY = "createdBy";
	private static final String USER_ID = "userId";
	private static final String RECORD_INFO = "recordInfo";
	private static final String RECORD_TYPE = "recordType";
	private RecordStorage recordStorage;
	private MetadataStorage metadataStorage;
	private Authorizator authorizator;
	private RecordIdGenerator idGenerator;
	private PermissionKeyCalculator keyCalculator;

	public SystemOneDependencyProvider() {
		Map<String, Map<String, DataGroup>> records = new HashMap<>();
		addRecordTypeRecordType(records);

		recordStorage = new RecordStorageInMemory(records);
		metadataStorage = (MetadataStorage) recordStorage;
		authorizator = new AuthorizatorImp();
		idGenerator = new TimeStampIdGenerator();
		keyCalculator = new RecordPermissionKeyCalculator();

		addRecordTypeRecordType();
		addMetadataTextVariableWithId(ID);
		addMetadataTextVariableWithId(CREATED_BY);
		addMetadataTextVariableWithId(METADATA_ID);
		addMetadataTextVariableWithId(PRESENTATION_VIEW_ID);
		addMetadataTextVariableWithId(PRESENTATION_FORM_ID);
		addMetadataTextVariableWithId(NEW_METADATA_ID);
		addMetadataTextVariableWithId(NEW_PRESENTATION_FORM_ID);
		addMetadataTextVariableWithId(LIST_PRESENTATION_VIEW_ID);
		addMetadataTextVariableWithId(LIST_PRESENTATION_VIEW_ID);
		addMetadataTextVariableWithId(SEARCH_METADATA_ID);
		addMetadataTextVariableWithId(SEARCH_PRESENTATION_FORM_ID);
		addMetadataTextVariableWithIdAndRegEx(USER_SUPPLIED_ID, TRUE_OR_FALSE);
		addMetadataTextVariableWithIdAndRegEx(PERMISSION_KEY, A_Z);
		addMetadataTextVariableWithId(SELF_PRESENTATION_VIEW_ID);
		addMetadataRecordInfoNew();
		addMetadataRecordInfo();
		addMetadataRecordTypeNew();
		addMetadataRecordType();
		addRecordTypeMetadata();

		addMetadataTextVariableWithId(DATA_ID);
		addMetadataTextVariableWithId(TEXT_ID);
		addMetadataTextVariableWithId(DEF_TEXT_ID);
		addMetadataTextVariableWithId("ref");
		addMetadataAttributeReferences();

		addMetadataTextVariableWithIdAndRegEx(REPEAT_MIN, "(^[0-9\\_]{1,3}$)");
		addMetadataTextVariableWithIdAndRegEx("repeatMinKey", A_Z);
		addMetadataTextVariableWithIdAndRegEx(REPEAT_MAX, "(^[0-9|X\\_]{1,3}$)");
		addMetadataTextVariableWithIdAndRegEx("secret", TRUE_OR_FALSE);
		addMetadataTextVariableWithIdAndRegEx("secretKey", A_Z);
		addMetadataTextVariableWithIdAndRegEx("readOnly", TRUE_OR_FALSE);
		addMetadataTextVariableWithIdAndRegEx("readOnlyKey", A_Z);
		addMetadataChildReference();
		addMetadataChildReferences();
		addMetadataCollectionVariableMetadataType();
		addMetadataTextVariableWithId("type");
		addMetadataMetadataNew();
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

	private static void addRecordTypeRecordType(Map<String, Map<String, DataGroup>> records) {
		records.put(RECORD_TYPE, new HashMap<String, DataGroup>());
		DataGroup dataGroup = DataGroup.withDataId(RECORD_TYPE);

		DataGroup recordInfo = DataGroup.withDataId(RECORD_INFO);
		recordInfo.addChild(DataAtomic.withDataIdAndValue(ID, RECORD_TYPE));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", RECORD_TYPE));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withDataIdAndValue(ID, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(METADATA_ID, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(PRESENTATION_VIEW_ID,
				"presentation:pgRecordTypeView"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(PRESENTATION_FORM_ID,
				"presentation:pgRecordTypeForm"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(NEW_METADATA_ID, RECORD_TYPE_NEW));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(NEW_PRESENTATION_FORM_ID,
				"presentation:pgRecordTypeFormNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(LIST_PRESENTATION_VIEW_ID,
				"presentation:pgRecordTypeViewList"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(SEARCH_METADATA_ID,
				"metadata:recordTypeSearch"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(SEARCH_PRESENTATION_FORM_ID,
				"presentation:pgRecordTypeSearchForm"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(USER_SUPPLIED_ID, "true"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(PERMISSION_KEY, "RECORDTYPE_RECORDTYPE"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(SELF_PRESENTATION_VIEW_ID,
				"presentation:pgrecordTypeRecordType"));
		records.get(RECORD_TYPE).put(RECORD_TYPE, dataGroup);
	}

	private void addRecordTypeRecordType() {
		DataGroup dataGroup = DataGroup.withDataId(RECORD_TYPE);
		DataGroup recordInfo = DataGroup.withDataId(RECORD_INFO);
		dataGroup.addChild(recordInfo);
		recordInfo.addChild(DataAtomic.withDataIdAndValue(ID, RECORD_TYPE));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", RECORD_TYPE));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(METADATA_ID, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(PRESENTATION_VIEW_ID, "pgRecordTypeView"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(PRESENTATION_FORM_ID, "pgRecordTypeForm"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(NEW_METADATA_ID, RECORD_TYPE_NEW));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(NEW_PRESENTATION_FORM_ID,
				"pgRecordTypeFormNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(LIST_PRESENTATION_VIEW_ID,
				"pgRecordTypeList"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(SEARCH_METADATA_ID, "recordTypeSearch"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(SEARCH_PRESENTATION_FORM_ID,
				"pgRecordTypeSearchForm"));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(USER_SUPPLIED_ID, "true"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(PERMISSION_KEY, "RECORDTYPE_RECORDTYPE"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(SELF_PRESENTATION_VIEW_ID,
				"pgRecordTypeSelf"));
		recordStorage.create(RECORD_TYPE, RECORD_TYPE, dataGroup);
	}

	private void addMetadataTextVariableWithId(String id) {
		addMetadataTextVariableWithIdAndRegEx(id, "(^[0-9A-Za-z]{2,50}$)");
	}

	private void addMetadataTextVariableWithIdAndRegEx(String id, String regEx) {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", "textVariable");

		DataGroup recordInfo = DataGroup.withDataId(RECORD_INFO);
		dataGroup.addChild(recordInfo);
		recordInfo.addChild(DataAtomic.withDataIdAndValue(ID, id));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", METADATA));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, id));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, id + "Text"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, id + "DeffText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("regEx", regEx));
		recordStorage.create(METADATA, id, dataGroup);
	}

	private void addMetadataCollectionVariableMetadataType() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", "collectionVariable");

		DataGroup recordInfo = DataGroup.withDataId(RECORD_INFO);
		recordInfo.addChild(DataAtomic.withDataIdAndValue(ID, RECORD_TYPE_TYPE_COLLECTION_VAR));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", METADATA));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, "type"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID,
				"recordTypeTypeCollectionVarTextId"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID,
				"recordTypeTypeCollectionVarDeffTextId"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("refCollectionId",
				RECORD_TYPE_TYPE_COLLECTION));

		recordStorage.create(METADATA, RECORD_TYPE_TYPE_COLLECTION_VAR, dataGroup);

		// collection
		DataGroup dataGroup2 = DataGroup.withDataId(METADATA);
		dataGroup2.addAttributeByIdWithValue("type", "itemCollection");

		DataGroup recordInfo2 = DataGroup.withDataId(RECORD_INFO);
		recordInfo2.addChild(DataAtomic.withDataIdAndValue(ID, RECORD_TYPE_TYPE_COLLECTION));
		recordInfo2.addChild(DataAtomic.withDataIdAndValue("type", METADATA));
		recordInfo2.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo2.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));
		dataGroup2.addChild(recordInfo2);

		dataGroup2.addChild(DataAtomic.withDataIdAndValue(DATA_ID, RECORD_TYPE_TYPE_COLLECTION));
		dataGroup2.addChild(DataAtomic
				.withDataIdAndValue(TEXT_ID, "recordTypeTypeCollectionTextId"));
		dataGroup2.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID,
				"recordTypeTypeCollectionDeffTextId"));

		DataGroup collectionItemReferences = DataGroup.withDataId("collectionItemReferences");
		collectionItemReferences.addChild(DataAtomic.withDataIdAndValue("ref",
				RECORD_TYPE_TYPE_GROUP));
		dataGroup2.addChild(collectionItemReferences);

		recordStorage.create(METADATA, RECORD_TYPE_TYPE_COLLECTION, dataGroup2);

		// item1
		DataGroup dataGroup3 = DataGroup.withDataId(METADATA);
		dataGroup3.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup recordInfo3 = DataGroup.withDataId(RECORD_INFO);
		recordInfo3.addChild(DataAtomic.withDataIdAndValue(ID, RECORD_TYPE_TYPE_GROUP));
		recordInfo3.addChild(DataAtomic.withDataIdAndValue("type", METADATA));
		recordInfo3.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo3.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));
		dataGroup3.addChild(recordInfo3);

		dataGroup3.addChild(DataAtomic.withDataIdAndValue(DATA_ID, GROUP));
		dataGroup3.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "recordTypeTypeGroupTextId"));
		dataGroup3.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID,
				"recordTypeTypeGroupDeffTextId"));
		recordStorage.create(METADATA, RECORD_TYPE_TYPE_GROUP, dataGroup3);

		// item2
		DataGroup dataGroup4 = DataGroup.withDataId(METADATA);
		dataGroup4.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup recordInfo4 = DataGroup.withDataId(RECORD_INFO);
		recordInfo4.addChild(DataAtomic.withDataIdAndValue(ID, RECORD_TYPE_TYPE_GROUP));
		recordInfo4.addChild(DataAtomic.withDataIdAndValue("type", METADATA));
		recordInfo4.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo4.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));
		dataGroup4.addChild(recordInfo4);

		dataGroup4.addChild(DataAtomic.withDataIdAndValue(DATA_ID, "textVariable"));
		dataGroup4.addChild(DataAtomic.withDataIdAndValue(TEXT_ID,
				"recordTypeTypeTextVariableTextId"));
		dataGroup4.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID,
				"recordTypeTypeTextVariableDeffTextId"));
		recordStorage.create(METADATA, "recordTypeTypeTextVariable", dataGroup4);

	}

	private void addMetadataRecordInfoNew() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		DataGroup recordInfo = DataGroup.withDataId(RECORD_INFO);
		dataGroup.addChild(recordInfo);
		recordInfo.addChild(DataAtomic.withDataIdAndValue(ID, RECORD_INFO_NEW));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", METADATA));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, RECORD_INFO));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "recordInfoText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "recordInfoDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		DataGroup childReference = DataGroup.withDataId(CHILD_REFERENCE);
		childReference.addChild(DataAtomic.withDataIdAndValue("ref", ID));
		childReference.addChild(DataAtomic.withDataIdAndValue(REPEAT_MIN, "1"));
		childReference.addChild(DataAtomic.withDataIdAndValue(REPEAT_MAX, "1"));
		childReferences.addChild(childReference);

		recordStorage.create(METADATA, RECORD_INFO_NEW, dataGroup);
	}

	private void addMetadataRecordInfo() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		DataGroup recordInfo = DataGroup.withDataId(RECORD_INFO);
		dataGroup.addChild(recordInfo);
		recordInfo.addChild(DataAtomic.withDataIdAndValue(ID, RECORD_INFO));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", METADATA));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, RECORD_INFO));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "recordInfoText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "recordInfoDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithId(childReferences, ID);
		addChildReferenceWithId(childReferences, "type");
		addChildReferenceWithId(childReferences, CREATED_BY);

		recordStorage.create(METADATA, RECORD_INFO, dataGroup);
	}

	private void addMetadataRecordTypeNew() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		DataGroup recordInfo = DataGroup.withDataId(RECORD_INFO);
		dataGroup.addChild(recordInfo);
		recordInfo.addChild(DataAtomic.withDataIdAndValue(ID, RECORD_TYPE_NEW));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", METADATA));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "recordTypeText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "recordTypeDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithId(childReferences, METADATA_ID);
		addChildReferenceWithId(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithId(childReferences, PRESENTATION_VIEW_ID);
		addChildReferenceWithId(childReferences, PRESENTATION_FORM_ID);
		addChildReferenceWithId(childReferences, NEW_METADATA_ID);
		addChildReferenceWithId(childReferences, NEW_PRESENTATION_FORM_ID);
		addChildReferenceWithId(childReferences, LIST_PRESENTATION_VIEW_ID);
		addChildReferenceWithId(childReferences, SEARCH_METADATA_ID);
		addChildReferenceWithId(childReferences, SEARCH_PRESENTATION_FORM_ID);
		addChildReferenceWithId(childReferences, USER_SUPPLIED_ID);
		addChildReferenceWithId(childReferences, PERMISSION_KEY);
		addChildReferenceWithId(childReferences, SELF_PRESENTATION_VIEW_ID);

		recordStorage.create(METADATA, RECORD_TYPE_NEW, dataGroup);
	}

	private void addMetadataRecordType() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		DataGroup recordInfo = DataGroup.withDataId(RECORD_INFO);
		dataGroup.addChild(recordInfo);
		recordInfo.addChild(DataAtomic.withDataIdAndValue(ID, RECORD_TYPE));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", METADATA));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "recordTypeText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "recordTypeDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithId(childReferences, METADATA_ID);
		addChildReferenceWithId(childReferences, RECORD_INFO);
		addChildReferenceWithId(childReferences, PRESENTATION_VIEW_ID);
		addChildReferenceWithId(childReferences, PRESENTATION_FORM_ID);
		addChildReferenceWithId(childReferences, NEW_METADATA_ID);
		addChildReferenceWithId(childReferences, NEW_PRESENTATION_FORM_ID);
		addChildReferenceWithId(childReferences, LIST_PRESENTATION_VIEW_ID);
		addChildReferenceWithId(childReferences, SEARCH_METADATA_ID);
		addChildReferenceWithId(childReferences, SEARCH_PRESENTATION_FORM_ID);
		addChildReferenceWithId(childReferences, USER_SUPPLIED_ID);
		addChildReferenceWithId(childReferences, PERMISSION_KEY);
		addChildReferenceWithId(childReferences, SELF_PRESENTATION_VIEW_ID);

		recordStorage.create(METADATA, RECORD_TYPE, dataGroup);
	}

	private void addChildReferenceWithId(DataGroup childReferences, String id) {
		DataGroup childReference = DataGroup.withDataId(CHILD_REFERENCE);
		childReference.addChild(DataAtomic.withDataIdAndValue("ref", id));
		childReference.addChild(DataAtomic.withDataIdAndValue(REPEAT_MIN, "1"));
		childReference.addChild(DataAtomic.withDataIdAndValue(REPEAT_MAX, "1"));
		childReferences.addChild(childReference);
	}

	private void addRecordTypeMetadata() {
		DataGroup dataGroup = DataGroup.withDataId(RECORD_TYPE);
		DataGroup recordInfo = DataGroup.withDataId(RECORD_INFO);
		dataGroup.addChild(recordInfo);
		recordInfo.addChild(DataAtomic.withDataIdAndValue(ID, METADATA));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", RECORD_TYPE));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(METADATA_ID, METADATA));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(PRESENTATION_VIEW_ID, "pgMetadataView"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(PRESENTATION_FORM_ID, "pgMetadataForm"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(NEW_METADATA_ID, METADATA_NEW));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(NEW_PRESENTATION_FORM_ID,
				"pgMetadataFormNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(LIST_PRESENTATION_VIEW_ID,
				"pgMetadataList"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(SEARCH_METADATA_ID, "metadataSearch"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(SEARCH_PRESENTATION_FORM_ID,
				"pgMetadataeSearchForm"));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(USER_SUPPLIED_ID, "true"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(PERMISSION_KEY, "RECORDTYPE_METADATA"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(SELF_PRESENTATION_VIEW_ID,
				"pgMetadataSelf"));
		recordStorage.create(RECORD_TYPE, METADATA, dataGroup);
	}

	private void addMetadataAttributeReferences() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		DataGroup recordInfo = DataGroup.withDataId(RECORD_INFO);
		dataGroup.addChild(recordInfo);
		recordInfo.addChild(DataAtomic.withDataIdAndValue(ID, ATTRIBUTE_REFERENCES));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", METADATA));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, ATTRIBUTE_REFERENCES));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "attributeReferencesText"));
		dataGroup.addChild(DataAtomic
				.withDataIdAndValue(DEF_TEXT_ID, "attributeReferencesDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		DataGroup childReference = DataGroup.withDataId(CHILD_REFERENCE);
		childReference.addChild(DataAtomic.withDataIdAndValue("ref", "ref"));
		childReference.addChild(DataAtomic.withDataIdAndValue(REPEAT_MIN, "1"));
		childReference.addChild(DataAtomic.withDataIdAndValue(REPEAT_MAX, "X"));
		childReferences.addChild(childReference);

		recordStorage.create(METADATA, ATTRIBUTE_REFERENCES, dataGroup);
	}

	private void addMetadataChildReferences() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		DataGroup recordInfo = DataGroup.withDataId(RECORD_INFO);
		dataGroup.addChild(recordInfo);
		recordInfo.addChild(DataAtomic.withDataIdAndValue(ID, CHILD_REFERENCES));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", METADATA));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, CHILD_REFERENCES));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "childReferencesText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "childReferencesDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		DataGroup childReference = DataGroup.withDataId(CHILD_REFERENCE);
		childReference.addChild(DataAtomic.withDataIdAndValue("ref", CHILD_REFERENCE));
		childReference.addChild(DataAtomic.withDataIdAndValue(REPEAT_MIN, "1"));
		childReference.addChild(DataAtomic.withDataIdAndValue(REPEAT_MAX, "X"));
		childReferences.addChild(childReference);

		recordStorage.create(METADATA, CHILD_REFERENCES, dataGroup);
	}

	private void addMetadataChildReference() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		DataGroup recordInfo = DataGroup.withDataId(RECORD_INFO);
		dataGroup.addChild(recordInfo);
		recordInfo.addChild(DataAtomic.withDataIdAndValue(ID, CHILD_REFERENCE));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", METADATA));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, CHILD_REFERENCE));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "childReferenceText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "childReferenceDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithId(childReferences, "ref");
		addChildReferenceWithId(childReferences, REPEAT_MIN);
		DataGroup childReference = DataGroup.withDataId(CHILD_REFERENCE);
		childReference.addChild(DataAtomic.withDataIdAndValue("ref", "repeatMinKey"));
		childReference.addChild(DataAtomic.withDataIdAndValue(REPEAT_MIN, "0"));
		childReference.addChild(DataAtomic.withDataIdAndValue(REPEAT_MAX, "1"));
		childReferences.addChild(childReference);
		addChildReferenceWithId(childReferences, REPEAT_MAX);
		DataGroup childReference1 = DataGroup.withDataId(CHILD_REFERENCE);
		childReference1.addChild(DataAtomic.withDataIdAndValue("ref", "secret"));
		childReference1.addChild(DataAtomic.withDataIdAndValue(REPEAT_MIN, "0"));
		childReference1.addChild(DataAtomic.withDataIdAndValue(REPEAT_MAX, "1"));
		childReferences.addChild(childReference1);
		DataGroup childReference2 = DataGroup.withDataId(CHILD_REFERENCE);
		childReference2.addChild(DataAtomic.withDataIdAndValue("ref", "secretKey"));
		childReference2.addChild(DataAtomic.withDataIdAndValue(REPEAT_MIN, "0"));
		childReference2.addChild(DataAtomic.withDataIdAndValue(REPEAT_MAX, "1"));
		childReferences.addChild(childReference2);
		DataGroup childReference3 = DataGroup.withDataId(CHILD_REFERENCE);
		childReference3.addChild(DataAtomic.withDataIdAndValue("ref", "readOnly"));
		childReference3.addChild(DataAtomic.withDataIdAndValue(REPEAT_MIN, "0"));
		childReference3.addChild(DataAtomic.withDataIdAndValue(REPEAT_MAX, "1"));
		childReferences.addChild(childReference3);
		DataGroup childReference4 = DataGroup.withDataId(CHILD_REFERENCE);
		childReference4.addChild(DataAtomic.withDataIdAndValue("ref", "readOnlyKey"));
		childReference4.addChild(DataAtomic.withDataIdAndValue(REPEAT_MIN, "0"));
		childReference4.addChild(DataAtomic.withDataIdAndValue(REPEAT_MAX, "1"));
		childReferences.addChild(childReference4);

		recordStorage.create(METADATA, CHILD_REFERENCE, dataGroup);
	}

	private void addMetadataMetadataNew() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		DataGroup recordInfo = DataGroup.withDataId(RECORD_INFO);
		dataGroup.addChild(recordInfo);
		recordInfo.addChild(DataAtomic.withDataIdAndValue(ID, METADATA_NEW));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", METADATA));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(CREATED_BY, USER_ID));
		recordInfo.addChild(DataAtomic.withDataIdAndValue(UPDATED_BY, USER_ID));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, METADATA));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "metadataText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "metadataDeffText"));

		DataGroup attributeReferences = DataGroup.withDataId(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(DataAtomic.withDataIdAndValue("ref",
				RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		// what does a metadata group contain
		addChildReferenceWithId(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithId(childReferences, DATA_ID);
		addChildReferenceWithId(childReferences, TEXT_ID);
		addChildReferenceWithId(childReferences, DEF_TEXT_ID);

		// attributeReferences
		DataGroup attributeReferences2 = DataGroup.withDataId(CHILD_REFERENCE);
		attributeReferences2.addChild(DataAtomic.withDataIdAndValue("ref", ATTRIBUTE_REFERENCES));
		attributeReferences2.addChild(DataAtomic.withDataIdAndValue(REPEAT_MIN, "1"));
		attributeReferences2.addChild(DataAtomic.withDataIdAndValue(REPEAT_MAX, "1"));
		childReferences.addChild(attributeReferences2);

		// childReferences
		addChildReferenceWithId(childReferences, CHILD_REFERENCES);

		recordStorage.create(METADATA, METADATA_NEW, dataGroup);
	}

	@Override
	public DataValidator getDataValidator() {
		return new DataValidatorImp(metadataStorage);
	}

}
