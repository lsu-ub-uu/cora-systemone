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
	private static final String REF_TEXT_VAR = "refTextVar";
	private static final String NAME_IN_DATA_TEXT_VAR = "nameInDataTextVar";
	private static final String REF_COLLECTION_ID_TEXT_VAR = "refCollectionIdTextVar";
	private static final String FINAL_VALUE_TEXT_VAR = "finalValueTextVar";
	private static final String REF_PARENT_ID_TEXT_VAR = "refParentIdTextVar";
	private static final String LINKED_RECORD_TYPE_TEXT_VAR = "linkedRecordTypeTextVar";
	private static final String EVERYTHING_REG_EXP_TEXT_VAR = "everythingRegExpTextVar";
	private static final String SELF_PRESENTATION_VIEW_ID_TEXT_VAR = "selfPresentationViewIdTextVar";
	private static final String PERMISSION_KEY_TEXT_VAR = "permissionKeyTextVar";
	private static final String USER_SUPPLIED_ID_TEXT_VAR = "userSuppliedIdTextVar";
	private static final String SEARCH_PRESENTATION_FORM_ID_TEXT_VAR = "searchPresentationFormIdTextVar";
	private static final String SEARCH_METADATA_ID_TEXT_VAR = "searchMetadataIdTextVar";
	private static final String LIST_PRESENTATION_VIEW_ID_TEXT_VAR = "listPresentationViewIdTextVar";
	private static final String NEW_PRESENTATION_FORM_ID_TEXT_VAR = "newPresentationFormIdTextVar";
	private static final String NEW_METADATA_ID_TEXT_VAR = "newMetadataIdTextVar";
	private static final String PRESENTATION_FORM_ID_TEXT_VAR = "presentationFormIdTextVar";
	private static final String PRESENTATION_VIEW_ID_TEXT_VAR = "presentationViewIdTextVar";
	private static final String ABSTRACT_TEXT_VAR = "abstractTextVar";
	private static final String PARENT_ID_TEXT_VAR = "parentIdTextVar";
	private static final String METADATA_ID_TEXT_VAR = "metadataIdTextVar";
	private static final String ID_TEXT_VAR = "idTextVar";
	private static final String REF_PARENT_ID = "refParentId";
	private static final String FINAL_VALUE = "finalValue";
	private static final String REF_COLLECTION_ID = "refCollectionId";
	private static final String COLLECTION_ITEM_REFERENCES = "collectionItemReferences";
	private static final String COLLECTION_ITEM_REFERENCE = "collectionItemReference";
	private static final String TYPE = "type";
	private static final String ATTRIBUTE = "attribute";
	private static final String ATTRIBUTES = "attributes";
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

	private RecordStorage recordStorage;
	private MetadataStorage metadataStorage;
	private Authorizator authorizator;
	private RecordIdGenerator idGenerator;
	private PermissionKeyCalculator keyCalculator;

	private DataGroup emptyLinkList = DataGroup.withNameInData("collectedDataLinks");
	private MetadataCreator metadataCreator;

	public SystemOneDependencyProvider() {
		Map<String, Map<String, DataGroup>> records = new HashMap<>();

		recordStorage = new RecordStorageInMemory(records);
		metadataStorage = (MetadataStorage) recordStorage;
		authorizator = new AuthorizatorImp();
		idGenerator = new TimeStampIdGenerator();
		keyCalculator = new RecordPermissionKeyCalculator();

		metadataCreator = new MetadataCreator(recordStorage);

		bootstrapSystemMetadata();
		MetadataForTexts metadataForTexts = new MetadataForTexts(recordStorage, metadataCreator);
		metadataForTexts.createMetadataForTexts();

		MetadataForPresentation metadataForPresentation = new MetadataForPresentation(recordStorage,
				metadataCreator);
		metadataForPresentation.createMetadataForPresentation();

		createDummyRecordLink();
	}

	private void createDummyRecordLink() {
		DataGroup dummyDTDL = DataGroup.withNameInData(NAME_FOR_METADATA);
		dummyDTDL.addAttributeByIdWithValue(TYPE, "recordLink");
		String id = "dummyRecordLink";
		dummyDTDL.addChild(metadataCreator
				.createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.RECORDLINK.type, id));

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
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(ID_TEXT_VAR, ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData("createdByTextVar", CREATED_BY);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(METADATA_ID_TEXT_VAR,
				METADATA_ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(PARENT_ID_TEXT_VAR, PARENT_ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx(ABSTRACT_TEXT_VAR,
				"abstract", TRUE_OR_FALSE);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(PRESENTATION_VIEW_ID_TEXT_VAR,
				PRESENTATION_VIEW_ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(PRESENTATION_FORM_ID_TEXT_VAR,
				PRESENTATION_FORM_ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(NEW_METADATA_ID_TEXT_VAR,
				NEW_METADATA_ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(
				NEW_PRESENTATION_FORM_ID_TEXT_VAR, NEW_PRESENTATION_FORM_ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(
				LIST_PRESENTATION_VIEW_ID_TEXT_VAR, LIST_PRESENTATION_VIEW_ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(SEARCH_METADATA_ID_TEXT_VAR,
				SEARCH_METADATA_ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(
				SEARCH_PRESENTATION_FORM_ID_TEXT_VAR, SEARCH_PRESENTATION_FORM_ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx(
				USER_SUPPLIED_ID_TEXT_VAR, USER_SUPPLIED_ID, TRUE_OR_FALSE);
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx(PERMISSION_KEY_TEXT_VAR,
				PERMISSION_KEY, A_Z);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(
				SELF_PRESENTATION_VIEW_ID_TEXT_VAR, SELF_PRESENTATION_VIEW_ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx(
				EVERYTHING_REG_EXP_TEXT_VAR, "regEx", ".+");
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(LINKED_RECORD_TYPE_TEXT_VAR,
				LINKED_RECORD_TYPE);
		addTopLevelMetadataGroupsForTextVariable();

		addTopLevelMetadataGroupsForCollectionVariable();
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(REF_PARENT_ID_TEXT_VAR,
				REF_PARENT_ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(FINAL_VALUE_TEXT_VAR,
				FINAL_VALUE);
		addTopLevelMetadataGroupsForItemCollection();
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(REF_COLLECTION_ID_TEXT_VAR,
				REF_COLLECTION_ID);
		addMetadataCollectionItemReferences();
		addMetadataCollectionItemReference();
		addTopLevelMetadataGroupsForCollectionItem();

		addTopLevelMetadataGroupsForRecordLink();
		addMetadataLinkedPath();
		addMetadataAttributes();
		addMetadataAttribute();
		metadataCreator.addMetadataTextVariableWithIdAndNameInData("attributeNameTextVar",
				"attributeName");
		metadataCreator.addMetadataTextVariableWithIdAndNameInData("attributeValueTextVar",
				"attributeValue");

		addMetadataRecordInfoNew();
		addMetadataRecordInfo();
		addMetadataRecordTypeNew();
		addMetadataRecordType();

		metadataCreator.addMetadataTextVariableWithIdAndNameInData(NAME_IN_DATA_TEXT_VAR,
				NAME_IN_DATA);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData("textIdTextVar", TEXT_ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData("defTextIdTextVar", DEF_TEXT_ID);
		metadataCreator.addMetadataTextVariableWithIdAndNameInData(REF_TEXT_VAR, "ref");
		addMetadataAttributeReferences();

		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx("repeatMinTextVar",
				REPEAT_MIN, "(^[0-9\\_]{1,3}$)");
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx("repeatMinKeyTextVar",
				"repeatMinKey", A_Z);
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx("repeatMaxTextVar",
				REPEAT_MAX, "(^[0-9|X\\_]{1,3}$)");
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx("secretTextVar",
				"secret", TRUE_OR_FALSE);
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx("secretKeyTextVar",
				"secretKey", A_Z);
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx("readOnlyTextVar",
				"readOnly", TRUE_OR_FALSE);
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx("readOnlyKeyTextVar",
				"readOnlyKey", A_Z);
		addMetadataChildReference();
		addMetadataChildReferences();
		addMetadataCollectionVariableMetadataType();
		metadataCreator.addMetadataTextVariableWithIdAndNameInData("typeTextVar", TYPE);
		addTopLevelMetadataGroupsForMetadataGroup();

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

	@Override
	public DataValidator getDataValidator() {
		return new DataValidatorImp(metadataStorage);
	}

	@Override
	public DataRecordLinkCollector getDataRecordLinkCollector() {
		return new DataRecordLinkCollectorImp(metadataStorage);
	}

	private void addTopLevelMetadataGroupsForTextVariable() {
		String id = "metadataTextVariableGroup";
		DataGroup dataGroup = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, false);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, EVERYTHING_REG_EXP_TEXT_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				REF_PARENT_ID_TEXT_VAR, "0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, FINAL_VALUE_TEXT_VAR,
				"0", "1");
		recordStorage.create(MetadataTypes.GROUP.type, id, dataGroup, emptyLinkList);

		String idNew = "metadataTextVariableNewGroup";
		DataGroup dataGroupNew = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(idNew, true);
		metadataCreator.addChildReferenceWithRef1to1(dataGroupNew, EVERYTHING_REG_EXP_TEXT_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroupNew,
				REF_PARENT_ID_TEXT_VAR, "0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroupNew,
				FINAL_VALUE_TEXT_VAR, "0", "1");
		recordStorage.create(MetadataTypes.GROUP.type, idNew, dataGroupNew, emptyLinkList);
	}

	private DataGroup createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(String metadataGroupId,
			boolean isNew) {
		DataGroup dataGroup;
		dataGroup = metadataCreator.createDataGroupForMetadataWithRecordId(metadataGroupId);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", METADATA_TYPE_COLLECTION_VAR));

		if (isNew) {
			metadataCreator.addChildReferenceWithRef1to1(dataGroup, RECORD_INFO_NEW);
		} else {
			metadataCreator.addChildReferenceWithRef1to1(dataGroup, RECORD_INFO);
		}
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, NAME_IN_DATA_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "textIdTextVar");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "defTextIdTextVar");

		return dataGroup;
	}

	private void addTopLevelMetadataGroupsForCollectionVariable() {
		String id = "metadataCollectionVariableGroup";
		DataGroup dataGroup = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, false);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, REF_COLLECTION_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				REF_PARENT_ID_TEXT_VAR, "0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, FINAL_VALUE_TEXT_VAR,
				"0", "1");
		recordStorage.create(MetadataTypes.GROUP.type, id, dataGroup, emptyLinkList);

		String idNew = "metadataCollectionVariableNewGroup";
		DataGroup dataGroupNew = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(idNew, true);
		metadataCreator.addChildReferenceWithRef1to1(dataGroupNew, REF_COLLECTION_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroupNew,
				REF_PARENT_ID_TEXT_VAR, "0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroupNew,
				FINAL_VALUE_TEXT_VAR, "0", "1");
		recordStorage.create(MetadataTypes.GROUP.type, idNew, dataGroupNew, emptyLinkList);
	}

	private void addTopLevelMetadataGroupsForItemCollection() {
		String id = "metadataItemCollectionGroup";
		DataGroup dataGroup = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, false);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, COLLECTION_ITEM_REFERENCES);
		recordStorage.create(MetadataTypes.GROUP.type, id, dataGroup, emptyLinkList);

		String idNew = "metadataItemCollectionNewGroup";
		DataGroup dataGroupNew = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(idNew, true);
		metadataCreator.addChildReferenceWithRef1to1(dataGroupNew, COLLECTION_ITEM_REFERENCES);
		recordStorage.create(MetadataTypes.GROUP.type, idNew, dataGroupNew, emptyLinkList);
	}

	private void addMetadataCollectionItemReferences() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId(COLLECTION_ITEM_REFERENCES);
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(NAME_IN_DATA, COLLECTION_ITEM_REFERENCES));

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, REF_TEXT_VAR, "1",
				"X");
		recordStorage.create(MetadataTypes.GROUP.type, COLLECTION_ITEM_REFERENCES, dataGroup,
				emptyLinkList);

	}

	private void addMetadataCollectionItemReference() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId(COLLECTION_ITEM_REFERENCE);
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(NAME_IN_DATA, COLLECTION_ITEM_REFERENCE));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, REF_TEXT_VAR);
		recordStorage.create(MetadataTypes.GROUP.type, COLLECTION_ITEM_REFERENCE, dataGroup,
				emptyLinkList);
	}

	private void addTopLevelMetadataGroupsForCollectionItem() {
		String id = "metadataCollectionItemGroup";
		DataGroup dataGroup = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, false);
		recordStorage.create(MetadataTypes.GROUP.type, id, dataGroup, emptyLinkList);

		String idNew = "metadataCollectionItemNewGroup";
		DataGroup dataGroupNew = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(idNew, true);
		recordStorage.create(MetadataTypes.GROUP.type, idNew, dataGroupNew, emptyLinkList);
	}

	private void addTopLevelMetadataGroupsForRecordLink() {
		String id = "metadataRecordLinkGroup";
		DataGroup dataGroup = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, false);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, LINKED_RECORD_TYPE_TEXT_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, LINKED_PATH, "0",
				"1");
		recordStorage.create(MetadataTypes.GROUP.type, id, dataGroup, emptyLinkList);

		String idNew = "metadataRecordLinkNewGroup";
		DataGroup dataGroupNew = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(idNew, true);
		metadataCreator.addChildReferenceWithRef1to1(dataGroupNew, LINKED_RECORD_TYPE_TEXT_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroupNew, LINKED_PATH, "0",
				"1");
		recordStorage.create(MetadataTypes.GROUP.type, idNew, dataGroupNew, emptyLinkList);
	}

	private void addMetadataLinkedPath() {
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordId(LINKED_PATH);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, LINKED_PATH));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, NAME_IN_DATA_TEXT_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, ATTRIBUTES, "0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, LINKED_PATH, "0",
				"1");

		recordStorage.create(MetadataTypes.GROUP.type, LINKED_PATH, dataGroup, emptyLinkList);
	}

	private void addMetadataAttributes() {
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordId(ATTRIBUTES);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, ATTRIBUTES));

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, ATTRIBUTE, "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, ATTRIBUTES, dataGroup, emptyLinkList);
	}

	private void addMetadataAttribute() {
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordId(ATTRIBUTE);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, ATTRIBUTE));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "attributeNameTextVar");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "attributeValueTextVar");

		recordStorage.create(MetadataTypes.GROUP.type, ATTRIBUTE, dataGroup, emptyLinkList);
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
		metadataCreator.createCollectionItem("metadataType" + idWithCapitalFirst, id);
	}

	private void addMetadataTypeItemCollection() {
		// collection
		DataGroup dataGroup2 = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup2.addAttributeByIdWithValue(TYPE, "itemCollection");
		dataGroup2.addChild(metadataCreator.createRecordInfoWithRecordTypeAndRecordId(
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
		dataGroup.addChild(metadataCreator.createRecordInfoWithRecordTypeAndRecordId(
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
		metadataCreator.addMetadataRecordInfoNewWithRecordInfoIdAndRefMetadataIdUsedAsId(
				RECORD_INFO_NEW, ID_TEXT_VAR);
	}

	private void addMetadataRecordInfo() {
		metadataCreator.addMetadataRecordInfoWithRecordInfoIdAndRefMetadataIdUsedAsId(RECORD_INFO,
				ID_TEXT_VAR);
	}

	private void addMetadataRecordTypeNew() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(metadataCreator.createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.GROUP.type, "recordTypeNewGroup"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordTypeDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, METADATA_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, ABSTRACT_TEXT_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, PARENT_ID_TEXT_VAR,
				"0", "1");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, RECORD_INFO_NEW);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, PRESENTATION_VIEW_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, PRESENTATION_FORM_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, NEW_METADATA_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, NEW_PRESENTATION_FORM_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, LIST_PRESENTATION_VIEW_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, SEARCH_METADATA_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup,
				SEARCH_PRESENTATION_FORM_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, USER_SUPPLIED_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, PERMISSION_KEY_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, SELF_PRESENTATION_VIEW_ID_TEXT_VAR);

		recordStorage.create(MetadataTypes.GROUP.type, "recordTypeNewGroup", dataGroup,
				emptyLinkList);
	}

	private void addMetadataRecordType() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(metadataCreator.createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.GROUP.type, "recordTypeGroup"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordTypeDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, METADATA_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, ABSTRACT_TEXT_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, PARENT_ID_TEXT_VAR,
				"0", "1");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, RECORD_INFO);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, PRESENTATION_VIEW_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, PRESENTATION_FORM_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, NEW_METADATA_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, NEW_PRESENTATION_FORM_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, LIST_PRESENTATION_VIEW_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, SEARCH_METADATA_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup,
				SEARCH_PRESENTATION_FORM_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, USER_SUPPLIED_ID_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, PERMISSION_KEY_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, SELF_PRESENTATION_VIEW_ID_TEXT_VAR);

		recordStorage.create(MetadataTypes.GROUP.type, "recordTypeGroup", dataGroup, emptyLinkList);
	}

	private void addMetadataAttributeReferences() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(metadataCreator.createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.GROUP.type, ATTRIBUTE_REFERENCES));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, ATTRIBUTE_REFERENCES));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "attributeReferencesText"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "attributeReferencesDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, REF_TEXT_VAR, "1",
				"X");

		recordStorage.create(MetadataTypes.GROUP.type, ATTRIBUTE_REFERENCES, dataGroup,
				emptyLinkList);
	}

	private void addMetadataChildReferences() {
		// DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		// dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		// dataGroup.addChild(metadataCreator.createRecordInfoWithRecordTypeAndRecordId(
		// MetadataTypes.GROUP.type, CHILD_REFERENCES));
		//
		// dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA,
		// CHILD_REFERENCES));
		// dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID,
		// "childReferencesText"));
		// dataGroup
		// .addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
		// "childReferencesDefText"));
		//
		// DataGroup childReferences =
		// DataGroup.withNameInData(CHILD_REFERENCES);
		// dataGroup.addChild(childReferences);

		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId(CHILD_REFERENCES);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, CHILD_REFERENCE, "1",
				"X");

		recordStorage.create(MetadataTypes.GROUP.type, CHILD_REFERENCES, dataGroup, emptyLinkList);
	}

	private void addMetadataChildReference() {
		// DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		// dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		// dataGroup.addChild(metadataCreator.createRecordInfoWithRecordTypeAndRecordId(
		// MetadataTypes.GROUP.type, CHILD_REFERENCE));
		//
		// dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA,
		// CHILD_REFERENCE));
		// dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID,
		// "childReferenceText"));
		// dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
		// "childReferenceDefText"));
		//
		// DataGroup childReferences =
		// DataGroup.withNameInData(CHILD_REFERENCES);
		// dataGroup.addChild(childReferences);
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId(CHILD_REFERENCE);

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, REF_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "repeatMinTextVar");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "repeatMinKeyTextVar",
				"0", "1");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "repeatMaxTextVar");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "secretTextVar", "0",
				"1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "secretKeyTextVar",
				"0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "readOnlyTextVar",
				"0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "readOnlyKeyTextVar",
				"0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, CHILD_REFERENCE, dataGroup, emptyLinkList);
	}

	private void addTopLevelMetadataGroupsForMetadataGroup() {
		String id = "metadataGroupGroup";
		DataGroup dataGroup = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(id, false);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				REF_PARENT_ID_TEXT_VAR, "0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, ATTRIBUTE_REFERENCES,
				"0", "1");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, CHILD_REFERENCES);
		recordStorage.create(MetadataTypes.GROUP.type, id, dataGroup, emptyLinkList);

		String idNew = "metadataGroupNewGroup";
		DataGroup dataGroupNew = createDataGroupForTopLevelMetadataGroupWithIdAndIsNew(idNew, true);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroupNew,
				REF_PARENT_ID_TEXT_VAR, "0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroupNew,
				ATTRIBUTE_REFERENCES, "0", "1");
		metadataCreator.addChildReferenceWithRef1to1(dataGroupNew, CHILD_REFERENCES);
		recordStorage.create(MetadataTypes.GROUP.type, idNew, dataGroupNew, emptyLinkList);
	}

	private void addRecordTypeRecordType() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId(RECORD_TYPE);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "false"));
		recordStorage.create(RECORD_TYPE, RECORD_TYPE, dataGroup, emptyLinkList);
	}

	private void addRecordTypeMetadata() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId(NAME_FOR_METADATA);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "true"));
		recordStorage.create(RECORD_TYPE, METADATA, dataGroup, emptyLinkList);
	}

	private void addRecordTypeForAllMetadataGroups() {
		for (MetadataTypes metadataType : MetadataTypes.values()) {
			String type = metadataType.type;
			DataGroup dataGroup = metadataCreator.createRecordTypeWithId(type);
			dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "false"));
			dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, METADATA));
			recordStorage.create(RECORD_TYPE, type, dataGroup, emptyLinkList);
		}
	}

}