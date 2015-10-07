package se.uu.ub.cora.systemone;

import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.beefeater.Authorizator;
import se.uu.ub.cora.beefeater.AuthorizatorImp;
import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.MetadataTypes;
import se.uu.ub.cora.metadataformat.storage.MetadataStorage;
import se.uu.ub.cora.metadataformat.validator.DataValidator;
import se.uu.ub.cora.metadataformat.validator.DataValidatorImp;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.record.PermissionKeyCalculator;
import se.uu.ub.cora.spider.record.RecordPermissionKeyCalculator;
import se.uu.ub.cora.spider.record.storage.RecordIdGenerator;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.record.storage.RecordStorageInMemory;
import se.uu.ub.cora.spider.record.storage.TimeStampIdGenerator;

/**
 * SystemOneDependencyProvider wires up the system for use in "production", as
 * this is in SystemOne production currently means using all in memory storage,
 * so do NOT use this class in production as it is written today. :)
 *
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 * @since 0.1
 */
public class SystemOneDependencyProvider implements SpiderDependencyProvider {
	private static final String TARGET_PATH = "targetPath";
	private static final String TARGET_RECORD_TYPE = "targetRecordType";
	private static final String METADATA = "metadata";
	private static final String NAME_FOR_ABSTRACT = "abstract";
	private static final String PARENT_ID = "parentId";
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
	private static final String ATTRIBUTE_REFERENCES = "attributeReferences";
	private static final String NEW_METADATA_ID = "newMetadataId";
	private static final String CHILD_REFERENCE = "childReference";
	private static final String GROUP = "group";
	private static final String RECORD_TYPE_TYPE_GROUP = "recordTypeTypeGroup";
	private static final String RECORD_TYPE_TYPE_COLLECTION = "recordTypeTypeCollection";
	private static final String RECORD_TYPE_TYPE_COLLECTION_VAR = "recordTypeTypeCollectionVar";
	private static final String RECORD_TYPE_TYPE_TEXT_VARIABLE = "recordTypeTypeTextVariable";
	private static final String RECORD_TYPE_TYPE_DATA_TO_DATA_LINK = "recordTypeTypeDataToDataLink";
	private static final String ID = "id";
	private static final String NAME_IN_DATA = "nameInData";
	private static final String CHILD_REFERENCES = "childReferences";
	private static final String RECORD_INFO_NEW = "recordInfoNew";
	private static final String NAME_FOR_METADATA = "metadata";
	private static final String CREATED_BY = "createdBy";
	private static final String RECORD_INFO = "recordInfo";
	private static final String RECORD_TYPE = "recordType";
	private static final String EVERYTHING_REG_EXP = "everythingRegExp";
	private static final String METADATA_TEXT_VARIABLE = "metadataTextVariable";
	private static final String METADATA_TEXT_VARIABLE_NEW = "metadataTextVariableNew";

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

		bootstrapSystemMetadata();

		createDummyDataToDataLink();
	}

	private void createDummyDataToDataLink() {
		DataGroup dummyDTDL = DataGroup.withNameInData(NAME_FOR_METADATA);
		dummyDTDL.addAttributeByIdWithValue("type", "dataToDataLink");
		String id = "dummyDataToDataLink";
		dummyDTDL.addChild(
				createRecordInfoWithIdAndRecordType(id, MetadataTypes.DATATODATALINK.type));

		dummyDTDL.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "dummyLink"));
		dummyDTDL.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, id + "Text"));
		dummyDTDL.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, id + "DefText"));
		dummyDTDL.addChild(DataAtomic.withNameInDataAndValue("targetRecordType", "someRecordType"));
		recordStorage.create(MetadataTypes.DATATODATALINK.type, id, dummyDTDL);
	}

	/**
	 * This metadata and record types needs to be in place when the system
	 * starts
	 */
	private void bootstrapSystemMetadata() {
		addMetadataTextVariableWithId(ID);
		addMetadataTextVariableWithId(CREATED_BY);
		addMetadataTextVariableWithId(METADATA_ID);
		addMetadataTextVariableWithId(PARENT_ID);
		addMetadataTextVariableWithIdAndRegEx(NAME_FOR_ABSTRACT, TRUE_OR_FALSE);
		addMetadataTextVariableWithId(PRESENTATION_VIEW_ID);
		addMetadataTextVariableWithId(PRESENTATION_FORM_ID);
		addMetadataTextVariableWithId(NEW_METADATA_ID);
		addMetadataTextVariableWithId(NEW_PRESENTATION_FORM_ID);
		addMetadataTextVariableWithId(LIST_PRESENTATION_VIEW_ID);
		addMetadataTextVariableWithId(SEARCH_METADATA_ID);
		addMetadataTextVariableWithId(SEARCH_PRESENTATION_FORM_ID);
		addMetadataTextVariableWithIdAndRegEx(USER_SUPPLIED_ID, TRUE_OR_FALSE);
		addMetadataTextVariableWithIdAndRegEx(PERMISSION_KEY, A_Z);
		addMetadataTextVariableWithId(SELF_PRESENTATION_VIEW_ID);
		addMetadataTextVariableWithIdAndRegEx(EVERYTHING_REG_EXP, "regEx", ".+");
		addMetadataTextVariableWithId(TARGET_RECORD_TYPE);
		addMetadataTextVariableWithId(TARGET_PATH);
		addMetadataTextVariable();
		addMetadataTextVariableNew();
		addMetadataDataToDataLink();
		addMetadataDataToDataLinkNew();
		addMetadataRecordInfoNew();
		addMetadataRecordInfo();
		addMetadataRecordTypeNew();
		addMetadataRecordType();

		addMetadataTextVariableWithId(NAME_IN_DATA);
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
		addRecordTypeForAllMetadataGroups();
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
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", recordType));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("createdBy", "userId"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("updatedBy", "userId"));
		return recordInfo;
	}

	private void addMetadataTextVariableWithId(String id) {
		addMetadataTextVariableWithIdAndRegEx(id, "(^[0-9A-Za-z:-_]{2,50}$)");
	}

	private void addMetadataTextVariableWithIdAndRegEx(String id, String regEx) {
		addMetadataTextVariableWithIdAndRegEx(id, id, regEx);
	}

	private void addMetadataTextVariableWithIdAndRegEx(String id, String nameInData, String regEx) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue("type", "textVariable");
		dataGroup
				.addChild(createRecordInfoWithIdAndRecordType(id, MetadataTypes.TEXTVARIABLE.type));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, nameInData));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, id + "Text"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, id + "DefText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("regEx", regEx));
		recordStorage.create(MetadataTypes.TEXTVARIABLE.type, id, dataGroup);
	}

	private void addMetadataTextVariable() {
		DataGroup dataGroup = createDataGroupOfTypeGroup(METADATA_TEXT_VARIABLE);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, EVERYTHING_REG_EXP);

		recordStorage.create(MetadataTypes.GROUP.type, METADATA_TEXT_VARIABLE, dataGroup);
	}

	private void addMetadataTextVariableNew() {
		DataGroup dataGroup = createDataGroupOfTypeGroup(METADATA_TEXT_VARIABLE_NEW);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, EVERYTHING_REG_EXP);

		recordStorage.create(MetadataTypes.GROUP.type, METADATA_TEXT_VARIABLE_NEW, dataGroup);
	}

	private void addMetadataDataToDataLinkNew() {
		DataGroup dataGroup = createDataGroupOfTypeGroup("metadataDataToDataLinkNew");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, TARGET_RECORD_TYPE);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, TARGET_PATH, "0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, "metadataDataToDataLinkNew", dataGroup);
	}

	private void addMetadataDataToDataLink() {
		DataGroup dataGroup = createDataGroupOfTypeGroup("metadataDataToDataLink");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, TARGET_RECORD_TYPE);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, TARGET_PATH, "0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, "metadataDataToDataLink", dataGroup);
	}

	private DataGroup createDataGroupOfTypeGroup(final String name) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);

		dataGroup.addChild(createRecordInfoWithIdAndRecordType(name, MetadataTypes.GROUP.type));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, name + "Text"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, name + "DefText"));
		return dataGroup;
	}

	private void addMetadataCollectionVariableMetadataType() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue("type", "collectionVariable");
		dataGroup.addChild(createRecordInfoWithIdAndRecordType(RECORD_TYPE_TYPE_COLLECTION_VAR,
				MetadataTypes.COLLECTIONVARIABLE.type));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "type"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeTypeCollectionVarTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
				"recordTypeTypeCollectionVarDeffTextId"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue("refCollectionId", RECORD_TYPE_TYPE_COLLECTION));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type, RECORD_TYPE_TYPE_COLLECTION_VAR,
				dataGroup);

		// collection
		DataGroup dataGroup2 = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup2.addAttributeByIdWithValue("type", "itemCollection");
		dataGroup2.addChild(createRecordInfoWithIdAndRecordType(RECORD_TYPE_TYPE_COLLECTION,
				MetadataTypes.ITEMCOLLECTION.type));

		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_TYPE_TYPE_COLLECTION));
		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeTypeCollectionTextId"));
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
				"recordTypeTypeCollectionDeffTextId"));

		DataGroup collectionItemReferences = DataGroup.withNameInData("collectionItemReferences");
		dataGroup2.addChild(collectionItemReferences);
		collectionItemReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_GROUP));
		collectionItemReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_TEXT_VARIABLE));
		collectionItemReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_DATA_TO_DATA_LINK));

		recordStorage.create(MetadataTypes.ITEMCOLLECTION.type, RECORD_TYPE_TYPE_COLLECTION,
				dataGroup2);

		// item1
		DataGroup dataGroup3 = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup3.addAttributeByIdWithValue("type", "collectionItem");
		dataGroup3.addChild(createRecordInfoWithIdAndRecordType(RECORD_TYPE_TYPE_GROUP,
				MetadataTypes.COLLECTIONITEM.type));

		dataGroup3.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, GROUP));
		dataGroup3
				.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeTypeGroupTextId"));
		dataGroup3.addChild(
				DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordTypeTypeGroupDeffTextId"));
		recordStorage.create(MetadataTypes.COLLECTIONITEM.type, RECORD_TYPE_TYPE_GROUP, dataGroup3);

		// item2
		DataGroup dataGroup4 = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup4.addAttributeByIdWithValue("type", "collectionItem");
		dataGroup4.addChild(createRecordInfoWithIdAndRecordType(RECORD_TYPE_TYPE_TEXT_VARIABLE,
				MetadataTypes.COLLECTIONITEM.type));

		dataGroup4.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "textVariable"));
		dataGroup4.addChild(
				DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeTypeTextVariableTextId"));
		dataGroup4.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
				"recordTypeTypeTextVariableDeffTextId"));
		recordStorage.create(MetadataTypes.COLLECTIONITEM.type, RECORD_TYPE_TYPE_TEXT_VARIABLE,
				dataGroup4);

		// item3 dataToDataLink
		DataGroup dataGroup5 = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup5.addAttributeByIdWithValue("type", "collectionItem");
		dataGroup5.addChild(createRecordInfoWithIdAndRecordType(RECORD_TYPE_TYPE_DATA_TO_DATA_LINK,
				MetadataTypes.COLLECTIONITEM.type));

		dataGroup5.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "dataToDataLink"));
		dataGroup5.addChild(
				DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeTypeDataToDataLinkTextId"));
		dataGroup5.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
				"recordTypeTypeDataToDataLinkDeffTextId"));
		recordStorage.create(MetadataTypes.COLLECTIONITEM.type, RECORD_TYPE_TYPE_DATA_TO_DATA_LINK,
				dataGroup5);

	}

	private void addMetadataRecordInfoNew() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(
				createRecordInfoWithIdAndRecordType(RECORD_INFO_NEW, MetadataTypes.GROUP.type));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_INFO));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordInfoText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordInfoDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, ID);

		recordStorage.create(MetadataTypes.GROUP.type, RECORD_INFO_NEW, dataGroup);
	}

	private void addChildReferenceWithRef1to1(DataGroup childReferences, String ref) {
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, ref, "1", "1");
	}

	private void addChildReferenceWithRefRepeatMinRepeatMax(DataGroup childReferences, String ref,
			String repeatMin, String repeatMax) {
		DataGroup childReference = DataGroup.withNameInData(CHILD_REFERENCE);
		childReference.addChild(DataAtomic.withNameInDataAndValue("ref", ref));
		childReference.addChild(DataAtomic.withNameInDataAndValue(REPEAT_MIN, repeatMin));
		childReference.addChild(DataAtomic.withNameInDataAndValue(REPEAT_MAX, repeatMax));
		childReferences.addChild(childReference);
	}

	private void addMetadataRecordInfo() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(
				createRecordInfoWithIdAndRecordType(RECORD_INFO, MetadataTypes.GROUP.type));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_INFO));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordInfoText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordInfoDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, ID);
		addChildReferenceWithRef1to1(childReferences, "type");
		addChildReferenceWithRef1to1(childReferences, CREATED_BY);

		recordStorage.create(MetadataTypes.GROUP.type, RECORD_INFO, dataGroup);
	}

	private void addMetadataRecordTypeNew() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(
				createRecordInfoWithIdAndRecordType(RECORD_TYPE_NEW, MetadataTypes.GROUP.type));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordTypeDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, NAME_FOR_ABSTRACT);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, PARENT_ID, "0", "1");
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

		recordStorage.create(MetadataTypes.GROUP.type, RECORD_TYPE_NEW, dataGroup);
	}

	private void addMetadataRecordType() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(
				createRecordInfoWithIdAndRecordType(RECORD_TYPE, MetadataTypes.GROUP.type));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordTypeDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, NAME_FOR_ABSTRACT);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, PARENT_ID, "0", "1");
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

		recordStorage.create(MetadataTypes.GROUP.type, RECORD_TYPE, dataGroup);
	}

	private void addMetadataAttributeReferences() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(createRecordInfoWithIdAndRecordType(ATTRIBUTE_REFERENCES,
				MetadataTypes.GROUP.type));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, ATTRIBUTE_REFERENCES));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "attributeReferencesText"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "attributeReferencesDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "ref", "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, ATTRIBUTE_REFERENCES, dataGroup);
	}

	private void addMetadataChildReferences() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(
				createRecordInfoWithIdAndRecordType(CHILD_REFERENCES, MetadataTypes.GROUP.type));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, CHILD_REFERENCES));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "childReferencesText"));
		dataGroup
				.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "childReferencesDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, CHILD_REFERENCE, "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, CHILD_REFERENCES, dataGroup);
	}

	private void addMetadataChildReference() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(
				createRecordInfoWithIdAndRecordType(CHILD_REFERENCE, MetadataTypes.GROUP.type));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, CHILD_REFERENCE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "childReferenceText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "childReferenceDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, "ref");
		addChildReferenceWithRef1to1(childReferences, REPEAT_MIN);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "repeatMinKey", "0", "1");
		addChildReferenceWithRef1to1(childReferences, REPEAT_MAX);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "secret", "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "secretKey", "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "readOnly", "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "readOnlyKey", "0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, CHILD_REFERENCE, dataGroup);
	}

	private void addMetadataMetadataGroupNew() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue("type", GROUP);
		dataGroup.addChild(
				createRecordInfoWithIdAndRecordType("metadataGroupNew", MetadataTypes.GROUP.type));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "metadataText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "metadataDefText"));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		// what does a metadata group contain
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);

		// childReferences
		addChildReferenceWithRef1to1(childReferences, CHILD_REFERENCES);

		recordStorage.create(MetadataTypes.GROUP.type, "metadataGroupNew", dataGroup);
	}

	@Override
	public DataValidator getDataValidator() {
		return new DataValidatorImp(metadataStorage);
	}

	private DataGroup createRecordTypeWithId(String id) {
		String idWithCapitalFirst = id.substring(0, 1).toUpperCase() + id.substring(1);

		DataGroup dataGroup = DataGroup.withNameInData(RECORD_TYPE);
		dataGroup.addChild(createRecordInfoWithIdAndRecordType(id, RECORD_TYPE));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(METADATA_ID, id));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PRESENTATION_VIEW_ID,
				"pg" + idWithCapitalFirst + "View"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "Form"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NEW_METADATA_ID, id + "New"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NEW_PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "FormNew"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(LIST_PRESENTATION_VIEW_ID,
				"pg" + idWithCapitalFirst + "List"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(SEARCH_METADATA_ID, id + "Search"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(SEARCH_PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "SearchForm"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(USER_SUPPLIED_ID, "true"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PERMISSION_KEY,
				"RECORDTYPE_" + id.toUpperCase()));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(SELF_PRESENTATION_VIEW_ID,
				"pg" + idWithCapitalFirst + "Self"));
		return dataGroup;
	}

	private void addRecordTypeRecordType() {
		DataGroup dataGroup = createRecordTypeWithId(RECORD_TYPE);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "false"));
		recordStorage.create(RECORD_TYPE, RECORD_TYPE, dataGroup);
	}

	private void addRecordTypeMetadata() {
		DataGroup dataGroup = createRecordTypeWithId(NAME_FOR_METADATA);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "true"));
		recordStorage.create(RECORD_TYPE, METADATA, dataGroup);
	}

	private void addRecordTypeForAllMetadataGroups() {
		for (MetadataTypes metadataType : MetadataTypes.values()) {
			String type = metadataType.type;
			DataGroup dataGroup = createRecordTypeWithId(type);
			dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "false"));
			dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, METADATA));
			recordStorage.create(RECORD_TYPE, type, dataGroup);
		}
	}
}