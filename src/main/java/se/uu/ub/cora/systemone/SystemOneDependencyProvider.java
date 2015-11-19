/*
 * Copyright 2015 Uppsala University Library
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

import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.beefeater.Authorizator;
import se.uu.ub.cora.beefeater.AuthorizatorImp;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollectorImp;
import se.uu.ub.cora.bookkeeper.metadata.MetadataTypes;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.bookkeeper.validator.DataValidatorImp;
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
	private static final String REF_PARENT_ID = "refParentId";
	private static final String FINAL_VALUE = "finalValue";
	private static final String REF_COLLECTION_ID = "refCollectionId";
	private static final String COLLECTION_ITEM_REFERENCES = "collectionItemReferences";
	private static final String COLLECTION_ITEM_REFERENCE = "collectionItemReference";
	private static final String TYPE = "type";
	private static final String ATTRIBUTE = "attribute";
	private static final String ATTRIBUTES = "attributes";
	private static final String COLLECTION_ITEM = "collectionItem";
	private static final String DEF_TEXT = "DefText";
	private static final String LINKED_PATH = "linkedPath";
	private static final String LINKED_RECORD_TYPE = "linkedRecordType";
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
	private static final String METADATA_TYPE_COLLECTION = "metadataTypeCollection";
	private static final String METADATA_TYPE_COLLECTION_VAR = "metadataTypeCollectionVar";
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

	private RecordStorage recordStorage;
	private MetadataStorage metadataStorage;
	private Authorizator authorizator;
	private RecordIdGenerator idGenerator;
	private PermissionKeyCalculator keyCalculator;

	private DataGroup emptyLinkList = DataGroup.withNameInData("collectedDataLinks");

	public SystemOneDependencyProvider() {
		Map<String, Map<String, DataGroup>> records = new HashMap<>();

		recordStorage = new RecordStorageInMemory(records);
		metadataStorage = (MetadataStorage) recordStorage;
		authorizator = new AuthorizatorImp();
		idGenerator = new TimeStampIdGenerator();
		keyCalculator = new RecordPermissionKeyCalculator();

		bootstrapSystemMetadata();

		createDummyRecordLink();
	}

	private void createDummyRecordLink() {
		DataGroup dummyDTDL = DataGroup.withNameInData(NAME_FOR_METADATA);
		dummyDTDL.addAttributeByIdWithValue(TYPE, "recordLink");
		String id = "dummyRecordLink";
		dummyDTDL.addChild(
				createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.RECORDLINK.type, id));

		dummyDTDL.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "dummyLink"));
		dummyDTDL.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, id + "Text"));
		dummyDTDL.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, id + DEF_TEXT));
		dummyDTDL.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "someRecordType"));
		recordStorage.create(MetadataTypes.RECORDLINK.type, id, dummyDTDL, emptyLinkList);
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
		addMetadataTextVariableWithIdAndNameInDataAndRegEx(EVERYTHING_REG_EXP, "regEx", ".+");
		addMetadataTextVariableWithId(LINKED_RECORD_TYPE);
		addTopLevelMetadataGroupsForTextVariable();

		addTopLevelMetadataGroupsForCollectionVariable();
		addMetadataTextVariableWithId(REF_PARENT_ID);
		addMetadataTextVariableWithId(FINAL_VALUE);
		addTopLevelMetadataGroupsForItemCollection();
		addMetadataTextVariableWithId(REF_COLLECTION_ID);
		addMetadataCollectionItemReferences();
		addMetadataCollectionItemReference();
		addTopLevelMetadataGroupsForCollectionItem();

		addTopLevelMetadataGroupsForRecordLink();
		addMetadataLinkedPath();
		addMetadataAttributes();
		addMetadataAttribute();
		addMetadataTextVariableWithId("attributeName");
		addMetadataTextVariableWithId("attributeValue");

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
		addMetadataTextVariableWithId(TYPE);
		addMetadataMetadataGroupNew();

		addRecordTypeRecordType();
		addRecordTypeMetadata();
		addRecordTypeForAllMetadataGroups();

		createLanguageCollectionItems();
		createSystemLanguageItemCollection();
		createSystemLanguageVar();
		createSystemLanguageSvVar();
		createSystemLanguageEnVar();
		createTextVar();
		createTextPartTypeCollectionItems();
		createTextPartTypeItemCollection();
		createTextPartTypeVar();
		createTextPartGroup();
		createTextPartTypeDefaultVar();
		createTextPartTypeAlternativeVar();
		createTextPartDefaultGroup();
		createTextPartAlternativeGroup();
		createTextPartSvGroup();
		createTextPartEnGroup();
		createTextGroup();
		createTextDefaultAlternativeGroup();
		createTextSystemOneGroup();
		addRecordTypeText();
		addRecordTypeTextSystemOne();
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

	private DataGroup createRecordInfoWithRecordTypeAndRecordId(String recordType, String id) {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue(TYPE, recordType));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("createdBy", "userId"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("updatedBy", "userId"));
		return recordInfo;
	}

	private void addMetadataTextVariableWithId(String id) {
		addMetadataTextVariableWithIdAndRegEx(id, "(^[0-9A-Za-z:-_]{2,50}$)");
	}

	private void addMetadataTextVariableWithIdAndRegEx(String id, String regEx) {
		addMetadataTextVariableWithIdAndNameInDataAndRegEx(id, id, regEx);
	}

	private void addMetadataTextVariableWithIdAndNameInDataAndRegEx(String id, String nameInData,
			String regEx) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, "textVariable");
		dataGroup.addChild(
				createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.TEXTVARIABLE.type, id));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, nameInData));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, id + "Text"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, id + DEF_TEXT));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("regEx", regEx));
		recordStorage.create(MetadataTypes.TEXTVARIABLE.type, id, dataGroup, emptyLinkList);
	}

	private void addTopLevelMetadataGroupsForTextVariable() {
		String id = METADATA_TEXT_VARIABLE;
		DataGroup dataGroup = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, false);
		addChildReferenceWithRef1to1(dataGroup, EVERYTHING_REG_EXP);
		recordStorage.create(MetadataTypes.GROUP.type, id, dataGroup, emptyLinkList);

		DataGroup dataGroupNew = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, true);
		addChildReferenceWithRef1to1(dataGroupNew, EVERYTHING_REG_EXP);
		recordStorage.create(MetadataTypes.GROUP.type, id + "New", dataGroupNew, emptyLinkList);
	}

	private DataGroup createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(String metadataGroupId,
			boolean isNew) {
		DataGroup dataGroup;
		if (isNew) {
			dataGroup = createDataGroupForMetadataWithRecordId(metadataGroupId + "NewGroup");
		} else {
			dataGroup = createDataGroupForMetadataWithRecordId(metadataGroupId + "Group");
		}
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", METADATA_TYPE_COLLECTION_VAR));

		if (isNew) {
			addChildReferenceWithRef1to1(dataGroup, RECORD_INFO_NEW);
		} else {
			addChildReferenceWithRef1to1(dataGroup, RECORD_INFO);
		}
		addChildReferenceWithRef1to1(dataGroup, NAME_IN_DATA);
		addChildReferenceWithRef1to1(dataGroup, TEXT_ID);
		addChildReferenceWithRef1to1(dataGroup, DEF_TEXT_ID);

		return dataGroup;
	}

	private void addTopLevelMetadataGroupsForCollectionVariable() {
		String id = "metadataCollectionVariable";
		DataGroup dataGroup = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, false);
		addChildReferenceWithRef1to1(dataGroup, REF_COLLECTION_ID);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, REF_PARENT_ID, "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, FINAL_VALUE, "0", "1");
		recordStorage.create(MetadataTypes.GROUP.type, id, dataGroup, emptyLinkList);

		DataGroup dataGroupNew = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, true);
		addChildReferenceWithRef1to1(dataGroupNew, REF_COLLECTION_ID);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroupNew, REF_PARENT_ID, "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroupNew, FINAL_VALUE, "0", "1");
		recordStorage.create(MetadataTypes.GROUP.type, id + "New", dataGroupNew, emptyLinkList);
	}

	private void addTopLevelMetadataGroupsForItemCollection() {
		String id = "metadataItemCollection";
		DataGroup dataGroup = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, false);
		addChildReferenceWithRef1to1(dataGroup, COLLECTION_ITEM_REFERENCES);
		recordStorage.create(MetadataTypes.GROUP.type, id, dataGroup, emptyLinkList);

		DataGroup dataGroupNew = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, true);
		addChildReferenceWithRef1to1(dataGroupNew, COLLECTION_ITEM_REFERENCES);
		recordStorage.create(MetadataTypes.GROUP.type, id + "New", dataGroupNew, emptyLinkList);
	}

	private void addMetadataCollectionItemReferences() {
		DataGroup dataGroup = createDataGroupForMetadataWithRecordId(COLLECTION_ITEM_REFERENCES);
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(NAME_IN_DATA, COLLECTION_ITEM_REFERENCES));

		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "ref", "1", "X");
		recordStorage.create(MetadataTypes.GROUP.type, COLLECTION_ITEM_REFERENCES, dataGroup,
				emptyLinkList);

	}

	private void addMetadataCollectionItemReference() {
		DataGroup dataGroup = createDataGroupForMetadataWithRecordId(COLLECTION_ITEM_REFERENCE);
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(NAME_IN_DATA, COLLECTION_ITEM_REFERENCE));

		addChildReferenceWithRef1to1(dataGroup, "ref");
		recordStorage.create(MetadataTypes.GROUP.type, COLLECTION_ITEM_REFERENCE, dataGroup,
				emptyLinkList);
	}

	private void addTopLevelMetadataGroupsForCollectionItem() {
		String id = "metadataCollectionItem";
		DataGroup dataGroup = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, false);
		recordStorage.create(MetadataTypes.GROUP.type, id, dataGroup, emptyLinkList);

		DataGroup dataGroupNew = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, true);
		recordStorage.create(MetadataTypes.GROUP.type, id + "New", dataGroupNew, emptyLinkList);
	}

	private void addTopLevelMetadataGroupsForRecordLink() {
		String id = "metadataRecordLink";
		DataGroup dataGroup = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, false);
		addChildReferenceWithRef1to1(dataGroup, LINKED_RECORD_TYPE);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, LINKED_PATH, "0", "1");
		recordStorage.create(MetadataTypes.GROUP.type, id, dataGroup, emptyLinkList);

		DataGroup dataGroupNew = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, true);
		addChildReferenceWithRef1to1(dataGroupNew, LINKED_RECORD_TYPE);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroupNew, LINKED_PATH, "0", "1");
		recordStorage.create(MetadataTypes.GROUP.type, id + "New", dataGroupNew, emptyLinkList);
	}

	private void addMetadataLinkedPath() {
		DataGroup dataGroup = createDataGroupForMetadataWithRecordId(LINKED_PATH);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, LINKED_PATH));

		addChildReferenceWithRef1to1(dataGroup, NAME_IN_DATA);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, ATTRIBUTES, "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, LINKED_PATH, "0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, LINKED_PATH, dataGroup, emptyLinkList);
	}

	private void addMetadataAttributes() {
		DataGroup dataGroup = createDataGroupForMetadataWithRecordId(ATTRIBUTES);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, ATTRIBUTES));

		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, ATTRIBUTE, "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, ATTRIBUTES, dataGroup, emptyLinkList);
	}

	private void addMetadataAttribute() {
		DataGroup dataGroup = createDataGroupForMetadataWithRecordId(ATTRIBUTE);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, ATTRIBUTE));

		addChildReferenceWithRef1to1(dataGroup, "attributeName");
		addChildReferenceWithRef1to1(dataGroup, "attributeValue");

		recordStorage.create(MetadataTypes.GROUP.type, ATTRIBUTE, dataGroup, emptyLinkList);
	}

	private DataGroup createDataGroupForMetadataWithRecordId(final String name) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);

		dataGroup.addChild(
				createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type, name));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, name + "Text"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, name + DEF_TEXT));
		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		return dataGroup;
	}

	private void addMetadataCollectionVariableMetadataType() {
		addMetadataTypeCollectionItems();
		addMetadataTypeItemCollection();
		addMetadataTypeCollectionVariable();
	}

	private void addMetadataTypeCollectionItems() {
		createMetadataTypeCollectionItem("group");
		createMetadataTypeCollectionItem("textVariable");
		createMetadataTypeCollectionItem("recordLink");
		createMetadataTypeCollectionItem("itemCollection");
		createMetadataTypeCollectionItem("collectionItem");
		createMetadataTypeCollectionItem("collectionVariable");
	}

	private void createMetadataTypeCollectionItem(String id) {
		String idWithCapitalFirst = id.substring(0, 1).toUpperCase() + id.substring(1);
		createCollectionItem("metadataType" + idWithCapitalFirst, id);
	}

	private void createCollectionItem(String id, String nameInData) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, COLLECTION_ITEM);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.COLLECTIONITEM.type, id + "Item"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, nameInData));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, id + "ItemTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, id + "ItemDefTextId"));

		recordStorage.create(MetadataTypes.COLLECTIONITEM.type, id + "Item", dataGroup,
				emptyLinkList);
	}

	private void addMetadataTypeItemCollection() {
		// collection
		DataGroup dataGroup2 = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup2.addAttributeByIdWithValue(TYPE, "itemCollection");
		dataGroup2.addChild(createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.ITEMCOLLECTION.type, METADATA_TYPE_COLLECTION));

		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue(NAME_IN_DATA, METADATA_TYPE_COLLECTION));
		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeTypeCollectionTextId"));
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
				"recordTypeTypeCollectionDeffTextId"));

		DataGroup collectionItemReferences = DataGroup.withNameInData(COLLECTION_ITEM_REFERENCES);
		dataGroup2.addChild(collectionItemReferences);
		collectionItemReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", "metadataTypeGroupItem"));
		collectionItemReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", "metadataTypeTextVariableItem"));
		collectionItemReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", "metadataTypeRecordLinkItem"));
		collectionItemReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "metadataTypeItemCollectionItem"));
		collectionItemReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "metadataTypeCollectionItemItem"));
		collectionItemReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "metadataTypeCollectionVariableItem"));
		collectionItemReferences.addChild(DataAtomic.withNameInDataAndValue("ref",
				"metadataTypeCollectionVariableChildItem"));

		recordStorage.create(MetadataTypes.ITEMCOLLECTION.type, METADATA_TYPE_COLLECTION,
				dataGroup2, emptyLinkList);
	}

	private void addMetadataTypeCollectionVariable() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, "collectionVariable");
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.COLLECTIONVARIABLE.type, METADATA_TYPE_COLLECTION_VAR));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, TYPE));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeTypeCollectionVarTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
				"recordTypeTypeCollectionVarDeffTextId"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_COLLECTION_ID, METADATA_TYPE_COLLECTION));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type, METADATA_TYPE_COLLECTION_VAR,
				dataGroup, emptyLinkList);
	}

	private void addMetadataRecordInfoNew() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type,
				RECORD_INFO_NEW));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_INFO));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordInfoText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordInfoDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(dataGroup, ID);

		recordStorage.create(MetadataTypes.GROUP.type, RECORD_INFO_NEW, dataGroup, emptyLinkList);
	}

	private void addChildReferenceWithRef1to1(DataGroup dataGroup, String ref) {
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, ref, "1", "1");
	}

	private void addChildReferenceWithRefRepeatMinRepeatMax(DataGroup dataGroup, String ref,
			String repeatMin, String repeatMax) {
		DataGroup childReferences = (DataGroup) dataGroup
				.getFirstChildWithNameInData(CHILD_REFERENCES);
		DataGroup childReference = DataGroup.withNameInData(CHILD_REFERENCE);
		childReference.setRepeatId(String.valueOf(childReferences.getChildren().size() + 1));
		childReference.addChild(DataAtomic.withNameInDataAndValue("ref", ref));
		childReference.addChild(DataAtomic.withNameInDataAndValue(REPEAT_MIN, repeatMin));
		childReference.addChild(DataAtomic.withNameInDataAndValue(REPEAT_MAX, repeatMax));
		childReferences.addChild(childReference);
	}

	private void addMetadataRecordInfo() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(
				createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type, RECORD_INFO));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_INFO));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordInfoText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordInfoDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(dataGroup, ID);
		addChildReferenceWithRef1to1(dataGroup, TYPE);
		addChildReferenceWithRef1to1(dataGroup, CREATED_BY);

		recordStorage.create(MetadataTypes.GROUP.type, RECORD_INFO, dataGroup, emptyLinkList);
	}

	private void addMetadataRecordTypeNew() {
		// TODO: use top level
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type,
				"recordTypeNewGroup"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordTypeDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(dataGroup, METADATA_ID);
		addChildReferenceWithRef1to1(dataGroup, NAME_FOR_ABSTRACT);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, PARENT_ID, "0", "1");
		addChildReferenceWithRef1to1(dataGroup, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(dataGroup, PRESENTATION_VIEW_ID);
		addChildReferenceWithRef1to1(dataGroup, PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(dataGroup, NEW_METADATA_ID);
		addChildReferenceWithRef1to1(dataGroup, NEW_PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(dataGroup, LIST_PRESENTATION_VIEW_ID);
		addChildReferenceWithRef1to1(dataGroup, SEARCH_METADATA_ID);
		addChildReferenceWithRef1to1(dataGroup, SEARCH_PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(dataGroup, USER_SUPPLIED_ID);
		addChildReferenceWithRef1to1(dataGroup, PERMISSION_KEY);
		addChildReferenceWithRef1to1(dataGroup, SELF_PRESENTATION_VIEW_ID);

		recordStorage.create(MetadataTypes.GROUP.type, "recordTypeNewGroup", dataGroup,
				emptyLinkList);
	}

	private void addMetadataRecordType() {
		// TODO: use top level
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type,
				"recordTypeGroup"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordTypeDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(dataGroup, METADATA_ID);
		addChildReferenceWithRef1to1(dataGroup, NAME_FOR_ABSTRACT);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, PARENT_ID, "0", "1");
		addChildReferenceWithRef1to1(dataGroup, RECORD_INFO);
		addChildReferenceWithRef1to1(dataGroup, PRESENTATION_VIEW_ID);
		addChildReferenceWithRef1to1(dataGroup, PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(dataGroup, NEW_METADATA_ID);
		addChildReferenceWithRef1to1(dataGroup, NEW_PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(dataGroup, LIST_PRESENTATION_VIEW_ID);
		addChildReferenceWithRef1to1(dataGroup, SEARCH_METADATA_ID);
		addChildReferenceWithRef1to1(dataGroup, SEARCH_PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(dataGroup, USER_SUPPLIED_ID);
		addChildReferenceWithRef1to1(dataGroup, PERMISSION_KEY);
		addChildReferenceWithRef1to1(dataGroup, SELF_PRESENTATION_VIEW_ID);

		recordStorage.create(MetadataTypes.GROUP.type, "recordTypeGroup", dataGroup, emptyLinkList);
	}

	private void addMetadataAttributeReferences() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type,
				ATTRIBUTE_REFERENCES));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, ATTRIBUTE_REFERENCES));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "attributeReferencesText"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "attributeReferencesDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "ref", "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, ATTRIBUTE_REFERENCES, dataGroup,
				emptyLinkList);
	}

	private void addMetadataChildReferences() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type,
				CHILD_REFERENCES));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, CHILD_REFERENCES));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "childReferencesText"));
		dataGroup
				.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "childReferencesDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, CHILD_REFERENCE, "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, CHILD_REFERENCES, dataGroup, emptyLinkList);
	}

	private void addMetadataChildReference() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type,
				CHILD_REFERENCE));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, CHILD_REFERENCE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "childReferenceText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "childReferenceDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(dataGroup, "ref");
		addChildReferenceWithRef1to1(dataGroup, REPEAT_MIN);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "repeatMinKey", "0", "1");
		addChildReferenceWithRef1to1(dataGroup, REPEAT_MAX);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "secret", "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "secretKey", "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "readOnly", "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "readOnlyKey", "0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, CHILD_REFERENCE, dataGroup, emptyLinkList);
	}

	private void addMetadataMetadataGroupNew() {
		// TODO: use top level
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type,
				"metadataGroupNewGroup"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "metadataText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "metadataDefText"));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", METADATA_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		// what does a metadata group contain
		addChildReferenceWithRef1to1(dataGroup, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(dataGroup, NAME_IN_DATA);
		addChildReferenceWithRef1to1(dataGroup, TEXT_ID);
		addChildReferenceWithRef1to1(dataGroup, DEF_TEXT_ID);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, REF_PARENT_ID, "0", "1");

		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, ATTRIBUTE_REFERENCES, "0", "1");
		// dataGroup
		addChildReferenceWithRef1to1(dataGroup, CHILD_REFERENCES);

		recordStorage.create(MetadataTypes.GROUP.type, "metadataGroupNewGroup", dataGroup,
				emptyLinkList);
	}

	private DataGroup createRecordTypeWithId(String id) {
		String idWithCapitalFirst = id.substring(0, 1).toUpperCase() + id.substring(1);

		DataGroup dataGroup = DataGroup.withNameInData(RECORD_TYPE);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(RECORD_TYPE, id));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(METADATA_ID, id + "Group"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PRESENTATION_VIEW_ID,
				"pg" + idWithCapitalFirst + "View"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "Form"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NEW_METADATA_ID, id + "NewGroup"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NEW_PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "FormNew"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(LIST_PRESENTATION_VIEW_ID,
				"pg" + idWithCapitalFirst + "List"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(SEARCH_METADATA_ID, id + "SearchGroup"));
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
		recordStorage.create(RECORD_TYPE, RECORD_TYPE, dataGroup, emptyLinkList);
	}

	private void addRecordTypeMetadata() {
		DataGroup dataGroup = createRecordTypeWithId(NAME_FOR_METADATA);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "true"));
		recordStorage.create(RECORD_TYPE, METADATA, dataGroup, emptyLinkList);
	}

	private void addRecordTypeForAllMetadataGroups() {
		for (MetadataTypes metadataType : MetadataTypes.values()) {
			String type = metadataType.type;
			DataGroup dataGroup = createRecordTypeWithId(type);
			dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "false"));
			dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, METADATA));
			recordStorage.create(RECORD_TYPE, type, dataGroup, emptyLinkList);
		}
	}

	private void createLanguageCollectionItems() {
		createCollectionItem("sv", "sv");
		createCollectionItem("en", "en");
		createCollectionItem("es", "es");
		createCollectionItem("no", "no");
	}

	private void createSystemLanguageItemCollection() {
		String id = "systemLanguages";
		DataGroup dataGroup = createDataGroupForItemCollectionWithId(id);
		// note, only sv and en are currently used in the system
		addCollectionItemReferenceByCollectionItemId(dataGroup, "svItem");
		addCollectionItemReferenceByCollectionItemId(dataGroup, "enItem");
		recordStorage.create(MetadataTypes.ITEMCOLLECTION.type, id + "Collection", dataGroup,
				emptyLinkList);
	}

	private DataGroup createDataGroupForItemCollectionWithId(String id) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, "itemCollection");
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.ITEMCOLLECTION.type, id + "Collection"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, id + "Collection"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, id + "CollectionTextId"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, id + "CollectionDefTextId"));

		DataGroup collectionItemReferences = DataGroup.withNameInData(COLLECTION_ITEM_REFERENCES);
		dataGroup.addChild(collectionItemReferences);
		return dataGroup;
	}

	private void addCollectionItemReferenceByCollectionItemId(DataGroup dataGroup,
			String collectionItemId) {
		DataGroup collectionItemReferences = dataGroup
				.getFirstGroupWithNameInData(COLLECTION_ITEM_REFERENCES);
		collectionItemReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", collectionItemId));
	}

	private void createSystemLanguageVar() {
		String collectionId = "systemLanguages";
		DataGroup dataGroup = createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
				collectionId, "systemLanguagesCollection", "lang");
		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type, collectionId + "CollectionVar",
				dataGroup, emptyLinkList);
	}

	private DataGroup createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
			String collectionId, String refCollectionId, String nameInData) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, "collectionVariable");
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.COLLECTIONVARIABLE.type, collectionId + "CollectionVar"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, nameInData));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(TEXT_ID, collectionId + "CollectionVarTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
				collectionId + "CollectionVarDefTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_COLLECTION_ID, refCollectionId));
		return dataGroup;
	}

	private void createSystemLanguageSvVar() {
		String collectionVarId = "systemLanguageSv";
		DataGroup dataGroup = createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
				collectionVarId, "systemLanguagesCollection", "lang");

		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "systemLanguagesCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, "sv"));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type,
				collectionVarId + "CollectionVar", dataGroup, emptyLinkList);
	}

	private void createSystemLanguageEnVar() {
		String collectionVarId = "systemLanguageEn";
		DataGroup dataGroup = createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
				collectionVarId, "systemLanguagesCollection", "lang");

		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "systemLanguagesCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, "en"));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type,
				collectionVarId + "CollectionVar", dataGroup, emptyLinkList);
	}

	private void createTextVar() {
		addMetadataTextVariableWithIdAndNameInDataAndRegEx("textTextVar", "text", "(.*)");
	}

	private void createTextPartTypeCollectionItems() {
		createCollectionItem("default", "default");
		createCollectionItem("alternative", "alternative");
	}

	private void createTextPartTypeItemCollection() {
		String id = "textPartType";
		DataGroup dataGroup = createDataGroupForItemCollectionWithId(id);
		addCollectionItemReferenceByCollectionItemId(dataGroup, "default");
		addCollectionItemReferenceByCollectionItemId(dataGroup, "alternative");
		recordStorage.create(MetadataTypes.ITEMCOLLECTION.type, id + "Collection", dataGroup,
				emptyLinkList);
	}

	private void createTextPartTypeVar() {
		String collectionId = "textPartType";
		DataGroup dataGroup = createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
				collectionId, "textPartTypeCollection", "type");
		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type, collectionId + "CollectionVar",
				dataGroup, emptyLinkList);
	}

	private void createTextPartGroup() {
		DataGroup dataGroup = createDataGroupForMetadataWithRecordId("textPartGroup");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "textPart"));
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", "textPartTypeCollectionVar"));
		attributeReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", "systemLanguageCollectionVar"));

		addChildReferenceWithRef1to1(dataGroup, "textTextVar");

		recordStorage.create(MetadataTypes.GROUP.type, "textPartGroup", dataGroup, emptyLinkList);
	}

	private void createTextPartTypeDefaultVar() {
		String collectionVarId = "textPartTypeDefault";
		DataGroup dataGroup = createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
				collectionVarId, "textPartTypeCollection", "type");

		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "textPartTypeCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, "default"));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type,
				collectionVarId + "CollectionVar", dataGroup, emptyLinkList);
	}

	private void createTextPartTypeAlternativeVar() {
		String collectionVarId = "textPartTypeAlternative";
		DataGroup dataGroup = createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
				collectionVarId, "textPartTypeCollection", "type");

		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "textPartTypeCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, "alternative"));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type,
				collectionVarId + "CollectionVar", dataGroup, emptyLinkList);
	}

	private void createTextPartDefaultGroup() {
		DataGroup dataGroup = createDataGroupForMetadataWithRecordId("textPartDefaultGroup");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "textPart"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "textPartGroup"));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "textPartTypeDefaultCollectionVar"));
		attributeReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", "systemLanguageCollectionVar"));

		addChildReferenceWithRef1to1(dataGroup, "textTextVar");

		recordStorage.create(MetadataTypes.GROUP.type, "textPartDefaultGroup", dataGroup,
				emptyLinkList);
	}

	private void createTextPartAlternativeGroup() {
		DataGroup dataGroup = createDataGroupForMetadataWithRecordId("textPartAlternativeGroup");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "textPart"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "textPartGroup"));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "textPartTypeAlternativeCollectionVar"));
		attributeReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", "systemLanguageCollectionVar"));

		addChildReferenceWithRef1to1(dataGroup, "textTextVar");

		recordStorage.create(MetadataTypes.GROUP.type, "textPartAlternativeGroup", dataGroup,
				emptyLinkList);
	}

	private void createTextPartSvGroup() {
		DataGroup dataGroup = createDataGroupForMetadataWithRecordId("textPartSvGroup");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "textPart"));
		dataGroup
				.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "textPartDefaultGroup"));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "textPartTypeDefaultCollectionVar"));
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "systemLanguageSvCollectionVar"));

		addChildReferenceWithRef1to1(dataGroup, "textTextVar");

		recordStorage.create(MetadataTypes.GROUP.type, "textPartSvGroup", dataGroup, emptyLinkList);
	}

	private void createTextPartEnGroup() {
		DataGroup dataGroup = createDataGroupForMetadataWithRecordId("textPartEnGroup");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "textPart"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "textPartAlternativeGroup"));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "textPartTypeAlternativeCollectionVar"));
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "systemLanguageEnCollectionVar"));

		addChildReferenceWithRef1to1(dataGroup, "textTextVar");

		recordStorage.create(MetadataTypes.GROUP.type, "textPartEnGroup", dataGroup, emptyLinkList);
	}

	private void createTextGroup() {
		DataGroup dataGroup = createDataGroupForMetadataWithRecordId("textGroup");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "text"));

		addChildReferenceWithRef1to1(dataGroup, RECORD_INFO);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "textPartGroup", "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, "textGroup", dataGroup, emptyLinkList);

	}

	private void createTextDefaultAlternativeGroup() {
		DataGroup dataGroup = createDataGroupForMetadataWithRecordId("textDefaultAlternativeGroup");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "text"));
		addChildReferenceWithRef1to1(dataGroup, RECORD_INFO);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "text"));

		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "textPartDefaultGroup", "1", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "textPartAlternativeGroup", "0", "X");

		recordStorage.create(MetadataTypes.GROUP.type, "textDefaultAlternativeGroup", dataGroup,
				emptyLinkList);
	}

	private void createTextSystemOneGroup() {
		DataGroup dataGroup = createDataGroupForMetadataWithRecordId("textSystemOneGroup");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "text"));
		addChildReferenceWithRef1to1(dataGroup, RECORD_INFO);
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "textDefaultAlternativeGroup"));

		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "textPartSvGroup", "1", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "textPartEnGroup", "0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, "textSystemOneGroup", dataGroup,
				emptyLinkList);

		DataGroup dataGroup2 = createDataGroupForMetadataWithRecordId("textSystemOneNewGroup");
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "text"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "textSystemOneGroup"));

		addChildReferenceWithRef1to1(dataGroup2, RECORD_INFO_NEW);
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup2, "textPartSvGroup", "1", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup2, "textPartEnGroup", "0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, "textSystemOneNewGroup", dataGroup2,
				emptyLinkList);
	}

	private void addRecordTypeText() {
		DataGroup dataGroup = createRecordTypeWithId("text");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "true"));
		recordStorage.create(RECORD_TYPE, "text", dataGroup, emptyLinkList);
	}

	private void addRecordTypeTextSystemOne() {
		DataGroup dataGroup = createRecordTypeWithId("textSystemOne");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "false"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, "text"));
		recordStorage.create(RECORD_TYPE, "textSystemOne", dataGroup, emptyLinkList);
	}

}