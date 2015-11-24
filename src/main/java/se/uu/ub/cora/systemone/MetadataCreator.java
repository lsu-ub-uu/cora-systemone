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

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataTypes;
import se.uu.ub.cora.spider.record.storage.RecordStorage;

public class MetadataCreator {
	private RecordStorage recordStorage;
	private static final String TYPE = "type";
	private static final String COLLECTION_ITEM = "collectionItem";
	private static final String DEF_TEXT = "DefText";
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
	private static final String NEW_METADATA_ID = "newMetadataId";
	private static final String CHILD_REFERENCE = "childReference";
	private static final String GROUP = "group";
	private static final String NAME_IN_DATA = "nameInData";
	private static final String CHILD_REFERENCES = "childReferences";
	private static final String NAME_FOR_METADATA = "metadata";
	private static final String RECORD_INFO = "recordInfo";
	private static final String RECORD_TYPE = "recordType";
	private static final String REF_PARENT_ID = "refParentId";
	private static final String COLLECTION = "Collection";
	private static final String COLLECTION_ITEM_REFERENCES = "collectionItemReferences";
	private static final String REF_COLLECTION_ID = "refCollectionId";
	private static final String COLLECTION_VAR = "CollectionVar";
	private DataGroup emptyLinkList = DataGroup.withNameInData("collectedDataLinks");

	public MetadataCreator(RecordStorage recordStorage) {
		this.recordStorage = recordStorage;
	}

	public void createCollectionItem(String id, String nameInData) {
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

	public DataGroup createRecordInfoWithRecordTypeAndRecordId(String recordType, String id) {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue(TYPE, recordType));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("createdBy", "userId"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("updatedBy", "userId"));
		return recordInfo;
	}

	public void addMetadataTextVariableWithIdAndNameInDataAndRegEx(String id, String nameInData,
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

	public DataGroup createDataGroupForMetadataWithRecordId(final String name) {
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

	public void addChildReferenceWithRef1to1(DataGroup dataGroup, String ref) {
		addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, ref, "1", "1");
	}

	public void addChildReferenceWithRefRepeatMinRepeatMax(DataGroup dataGroup, String ref,
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

	public void addMetadataRecordInfoNewWithRecordInfoIdAndRefMetadataIdUsedAsId(
			String recordInfoId, String refMetadataIdUsedAsId) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(
				createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type, recordInfoId));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_INFO));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordInfoText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordInfoDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(dataGroup, refMetadataIdUsedAsId);

		recordStorage.create(MetadataTypes.GROUP.type, recordInfoId, dataGroup, emptyLinkList);
	}

	public void addMetadataRecordInfoWithRecordInfoIdAndRefMetadataIdUsedAsId(String recordInfoId,
			String refMetadataIdUsedAsId) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(
				createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type, recordInfoId));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_INFO));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordInfoText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordInfoDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(dataGroup, refMetadataIdUsedAsId);
		addChildReferenceWithRef1to1(dataGroup, "typeTextVar");
		addChildReferenceWithRef1to1(dataGroup, "createdByTextVar");

		recordStorage.create(MetadataTypes.GROUP.type, recordInfoId, dataGroup, emptyLinkList);
	}

	public DataGroup createRecordTypeWithId(String id) {
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

	public void addMetadataTextVariableWithIdAndNameInData(String id, String nameInData) {
		addMetadataTextVariableWithIdAndNameInDataAndRegEx(id, nameInData,
				"(^[0-9A-Za-z:-_]{2,50}$)");
	}

	public void addMetadataTextVariableChildWithIdAndNameInDataAndRegExAndRefParentId(String id,
			String nameInData, String regEx, String refParentId) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, "textVariable");
		dataGroup.addChild(
				createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.TEXTVARIABLE.type, id));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, nameInData));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, id + "Text"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, id + DEF_TEXT));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("regEx", regEx));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, refParentId));
		recordStorage.create(MetadataTypes.TEXTVARIABLE.type, id, dataGroup, emptyLinkList);
	}

	public DataGroup createDataGroupForItemCollectionWithId(String id) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, "itemCollection");
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.ITEMCOLLECTION.type, id + COLLECTION));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, id + COLLECTION));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, id + "CollectionTextId"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, id + "CollectionDefTextId"));

		DataGroup collectionItemReferences = DataGroup.withNameInData(COLLECTION_ITEM_REFERENCES);
		dataGroup.addChild(collectionItemReferences);
		return dataGroup;
	}

	public void addCollectionItemReferenceByCollectionItemId(DataGroup dataGroup,
			String collectionItemId) {
		DataGroup collectionItemReferences = dataGroup
				.getFirstGroupWithNameInData(COLLECTION_ITEM_REFERENCES);
		collectionItemReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", collectionItemId));
	}

	public DataGroup createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
			String collectionId, String refCollectionId, String nameInData) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, "collectionVariable");
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.COLLECTIONVARIABLE.type, collectionId + COLLECTION_VAR));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, nameInData));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(TEXT_ID, collectionId + "CollectionVarTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
				collectionId + "CollectionVarDefTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_COLLECTION_ID, refCollectionId));
		return dataGroup;
	}
}
