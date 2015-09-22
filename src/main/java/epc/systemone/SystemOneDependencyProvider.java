package epc.systemone;

import java.util.HashMap;
import java.util.Map;

import epc.beefeater.Authorizator;
import epc.beefeater.AuthorizatorImp;
import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.MetadataTypes;
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
 * SystemOneDependencyProvider wires up the system for use in "production", as
 * this is in SystemOne production currently means using all in memory storage,
 * so do NOT use this class in production as it is written today. :)
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
	private static final String CREATED_BY = "createdBy";
	private static final String RECORD_INFO = "recordInfo";
	private static final String RECORD_TYPE = "recordType";
	private RecordStorage recordStorage;
	private MetadataStorage metadataStorage;
	private Authorizator authorizator;
	private RecordIdGenerator idGenerator;
	private PermissionKeyCalculator keyCalculator;

	public SystemOneDependencyProvider() {
		Map<String, Map<String, DataGroup>> records = new HashMap<>();

		recordStorage = new RecordStorageInMemory(records);
		metadataStorage = (MetadataStorage) recordStorage;
		authorizator = new AuthorizatorImp();
		idGenerator = new TimeStampIdGenerator();
		keyCalculator = new RecordPermissionKeyCalculator();

		addMetadataTextVariableWithId(ID);
		addMetadataTextVariableWithId(CREATED_BY);
		addMetadataTextVariableWithId(METADATA_ID);
		addMetadataTextVariableWithId("parentId");
		addMetadataTextVariableWithIdAndRegEx("abstract", TRUE_OR_FALSE);
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
		addMetadataMetadataGroupNew();

		addRecordTypeRecordType();
		addRecordTypeMetadata();
		addRecordTypeMetadataGroup();
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

	private DataGroup createRecordInfoWithIdAndRecordType(String id, String recordType) {
		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", id));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", recordType));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("createdBy", "userId"));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("updatedBy", "userId"));
		return recordInfo;
	}

	private void addMetadataTextVariableWithId(String id) {
		addMetadataTextVariableWithIdAndRegEx(id, "(^[0-9A-Za-z:-_]{2,50}$)");
	}

	private void addMetadataTextVariableWithIdAndRegEx(String id, String regEx) {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", "textVariable");
		dataGroup.addChild(createRecordInfoWithIdAndRecordType(id, METADATA));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, id));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, id + "Text"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, id + "DeffText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("regEx", regEx));
		recordStorage.create(METADATA, id, dataGroup);
	}

	private void addMetadataCollectionVariableMetadataType() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", "collectionVariable");
		dataGroup.addChild(
				createRecordInfoWithIdAndRecordType(RECORD_TYPE_TYPE_COLLECTION_VAR, METADATA));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, "type"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue(TEXT_ID, "recordTypeTypeCollectionVarTextId"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID,
				"recordTypeTypeCollectionVarDeffTextId"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue("refCollectionId", RECORD_TYPE_TYPE_COLLECTION));

		recordStorage.create(METADATA, RECORD_TYPE_TYPE_COLLECTION_VAR, dataGroup);

		// collection
		DataGroup dataGroup2 = DataGroup.withDataId(METADATA);
		dataGroup2.addAttributeByIdWithValue("type", "itemCollection");
		dataGroup2.addChild(
				createRecordInfoWithIdAndRecordType(RECORD_TYPE_TYPE_COLLECTION, METADATA));

		dataGroup2.addChild(DataAtomic.withDataIdAndValue(DATA_ID, RECORD_TYPE_TYPE_COLLECTION));
		dataGroup2
				.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "recordTypeTypeCollectionTextId"));
		dataGroup2.addChild(
				DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "recordTypeTypeCollectionDeffTextId"));

		DataGroup collectionItemReferences = DataGroup.withDataId("collectionItemReferences");
		collectionItemReferences
				.addChild(DataAtomic.withDataIdAndValue("ref", RECORD_TYPE_TYPE_GROUP));
		collectionItemReferences
				.addChild(DataAtomic.withDataIdAndValue("ref", "recordTypeTypeTextVariable"));
		dataGroup2.addChild(collectionItemReferences);

		recordStorage.create(METADATA, RECORD_TYPE_TYPE_COLLECTION, dataGroup2);

		// item1
		DataGroup dataGroup3 = DataGroup.withDataId(METADATA);
		dataGroup3.addAttributeByIdWithValue("type", "collectionItem");
		dataGroup3.addChild(createRecordInfoWithIdAndRecordType(RECORD_TYPE_TYPE_GROUP, METADATA));

		dataGroup3.addChild(DataAtomic.withDataIdAndValue(DATA_ID, GROUP));
		dataGroup3.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "recordTypeTypeGroupTextId"));
		dataGroup3.addChild(
				DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "recordTypeTypeGroupDeffTextId"));
		recordStorage.create(METADATA, RECORD_TYPE_TYPE_GROUP, dataGroup3);

		// item2
		DataGroup dataGroup4 = DataGroup.withDataId(METADATA);
		dataGroup4.addAttributeByIdWithValue("type", "collectionItem");
		dataGroup4.addChild(createRecordInfoWithIdAndRecordType(RECORD_TYPE_TYPE_GROUP, METADATA));

		dataGroup4.addChild(DataAtomic.withDataIdAndValue(DATA_ID, "textVariable"));
		dataGroup4.addChild(
				DataAtomic.withDataIdAndValue(TEXT_ID, "recordTypeTypeTextVariableTextId"));
		dataGroup4.addChild(
				DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "recordTypeTypeTextVariableDeffTextId"));
		recordStorage.create(METADATA, "recordTypeTypeTextVariable", dataGroup4);

	}

	private void addMetadataRecordInfoNew() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(createRecordInfoWithIdAndRecordType(RECORD_INFO_NEW, METADATA));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, RECORD_INFO));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "recordInfoText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "recordInfoDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, ID);

		recordStorage.create(METADATA, RECORD_INFO_NEW, dataGroup);
	}

	private void addChildReferenceWithRef1to1(DataGroup childReferences, String ref) {
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, ref, "1", "1");
	}

	private void addChildReferenceWithRefRepeatMinRepeatMax(DataGroup childReferences, String ref,
			String repeatMin, String repeatMax) {
		DataGroup childReference = DataGroup.withDataId(CHILD_REFERENCE);
		childReference.addChild(DataAtomic.withDataIdAndValue("ref", ref));
		childReference.addChild(DataAtomic.withDataIdAndValue(REPEAT_MIN, repeatMin));
		childReference.addChild(DataAtomic.withDataIdAndValue(REPEAT_MAX, repeatMax));
		childReferences.addChild(childReference);
	}

	private void addMetadataRecordInfo() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(createRecordInfoWithIdAndRecordType(RECORD_INFO, METADATA));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, RECORD_INFO));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "recordInfoText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "recordInfoDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, ID);
		addChildReferenceWithRef1to1(childReferences, "type");
		addChildReferenceWithRef1to1(childReferences, CREATED_BY);

		recordStorage.create(METADATA, RECORD_INFO, dataGroup);
	}

	private void addMetadataRecordTypeNew() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(createRecordInfoWithIdAndRecordType(RECORD_TYPE_NEW, METADATA));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "recordTypeText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "recordTypeDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, "abstract");
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "parentId", "0", "1");
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(childReferences, PRESENTATION_VIEW_ID);
		addChildReferenceWithRef1to1(childReferences, PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(childReferences, NEW_METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, NEW_PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(childReferences, LIST_PRESENTATION_VIEW_ID);
		addChildReferenceWithRef1to1(childReferences, SEARCH_METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, SEARCH_PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(childReferences, USER_SUPPLIED_ID);
		addChildReferenceWithRef1to1(childReferences, PERMISSION_KEY);
		addChildReferenceWithRef1to1(childReferences, SELF_PRESENTATION_VIEW_ID);

		recordStorage.create(METADATA, RECORD_TYPE_NEW, dataGroup);
	}

	private void addMetadataRecordType() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(createRecordInfoWithIdAndRecordType(RECORD_TYPE, METADATA));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "recordTypeText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "recordTypeDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, "abstract");
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "parentId", "0", "1");
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO);
		addChildReferenceWithRef1to1(childReferences, PRESENTATION_VIEW_ID);
		addChildReferenceWithRef1to1(childReferences, PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(childReferences, NEW_METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, NEW_PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(childReferences, LIST_PRESENTATION_VIEW_ID);
		addChildReferenceWithRef1to1(childReferences, SEARCH_METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, SEARCH_PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(childReferences, USER_SUPPLIED_ID);
		addChildReferenceWithRef1to1(childReferences, PERMISSION_KEY);
		addChildReferenceWithRef1to1(childReferences, SELF_PRESENTATION_VIEW_ID);

		recordStorage.create(METADATA, RECORD_TYPE, dataGroup);
	}

	private void addMetadataAttributeReferences() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(createRecordInfoWithIdAndRecordType(ATTRIBUTE_REFERENCES, METADATA));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, ATTRIBUTE_REFERENCES));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "attributeReferencesText"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "attributeReferencesDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "ref", "1", "X");

		recordStorage.create(METADATA, ATTRIBUTE_REFERENCES, dataGroup);
	}

	private void addMetadataChildReferences() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(createRecordInfoWithIdAndRecordType(CHILD_REFERENCES, METADATA));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, CHILD_REFERENCES));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "childReferencesText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "childReferencesDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, CHILD_REFERENCE, "1", "X");

		recordStorage.create(METADATA, CHILD_REFERENCES, dataGroup);
	}

	private void addMetadataChildReference() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(createRecordInfoWithIdAndRecordType(CHILD_REFERENCE, METADATA));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, CHILD_REFERENCE));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "childReferenceText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "childReferenceDeffText"));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, "ref");
		addChildReferenceWithRef1to1(childReferences, REPEAT_MIN);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "repeatMinKey", "0", "1");
		addChildReferenceWithRef1to1(childReferences, REPEAT_MAX);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "secret", "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "secretKey", "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "readOnly", "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "readOnlyKey", "0", "1");

		recordStorage.create(METADATA, CHILD_REFERENCE, dataGroup);
	}

	private void addMetadataMetadataGroupNew() {
		DataGroup dataGroup = DataGroup.withDataId(METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(createRecordInfoWithIdAndRecordType("metadataGroupNew",
				MetadataTypes.METADATA_GROUP.type));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(DATA_ID, METADATA));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(TEXT_ID, "metadataText"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(DEF_TEXT_ID, "metadataDeffText"));

		DataGroup attributeReferences = DataGroup.withDataId(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences
				.addChild(DataAtomic.withDataIdAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withDataId(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		// what does a metadata group contain
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(childReferences, DATA_ID);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);

		// childReferences
		addChildReferenceWithRef1to1(childReferences, CHILD_REFERENCES);

		recordStorage.create(MetadataTypes.METADATA_GROUP.type, "metadataGroupNew", dataGroup);
	}

	@Override
	public DataValidator getDataValidator() {
		return new DataValidatorImp(metadataStorage);
	}

	private DataGroup createRecordTypeWithIdAndMetadataId(String id) {
		String idWithCapitalFirst = id.substring(0, 1).toUpperCase() + id.substring(1);

		DataGroup dataGroup = DataGroup.withDataId(RECORD_TYPE);
		dataGroup.addChild(createRecordInfoWithIdAndRecordType(id, RECORD_TYPE));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(METADATA_ID, id));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(PRESENTATION_VIEW_ID,
				"pg" + idWithCapitalFirst + "View"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "Form"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(NEW_METADATA_ID, id + "New"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(NEW_PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "FormNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(LIST_PRESENTATION_VIEW_ID,
				"pg" + idWithCapitalFirst + "List"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(SEARCH_METADATA_ID, id + "Search"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(SEARCH_PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "SearchForm"));

		dataGroup.addChild(DataAtomic.withDataIdAndValue(USER_SUPPLIED_ID, "true"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue(PERMISSION_KEY, "RECORDTYPE_" + id.toUpperCase()));
		dataGroup.addChild(DataAtomic.withDataIdAndValue(SELF_PRESENTATION_VIEW_ID,
				"pg" + idWithCapitalFirst + "Self"));
		return dataGroup;
	}

	private void addRecordTypeRecordType() {
		DataGroup dataGroup = createRecordTypeWithIdAndMetadataId(RECORD_TYPE);
		dataGroup.addChild(DataAtomic.withDataIdAndValue("abstract", "false"));
		recordStorage.create(RECORD_TYPE, RECORD_TYPE, dataGroup);
	}

	private void addRecordTypeMetadata() {
		DataGroup dataGroup = createRecordTypeWithIdAndMetadataId("metadata");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("abstract", "true"));
		recordStorage.create(RECORD_TYPE, "metadata", dataGroup);
	}

	private void addRecordTypeMetadataGroup() {
		DataGroup dataGroup = createRecordTypeWithIdAndMetadataId("metadataGroup");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("abstract", "false"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("parentId", "metadata"));
		recordStorage.create(RECORD_TYPE, "metadataGroup", dataGroup);
	}
}