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

public class MetadataForPresentation {

	private static final String ID = "id";
	private static final String ID_TEXT_VAR = "idTextVar";
	private static final String REPEAT = "repeat";
	private static final String FALSE = "false";
	private static final String PRESENTATION_OF_TEXT_VAR = "presentationOfTextVar";
	private static final String REPEAT_COLLECTION_VAR = "repeatCollectionVar";
	private static final String MODE_COLLECTION_VAR = "modeCollectionVar";
	private static final String PRESENTATION_TYPE_COLLECTION_VAR = "presentationTypeCollectionVar";
	private static final String P_GROUP = "pGroup";
	private static final String CONTAINER = "container";
	private static final String PRESENTATION_CONTAINER_GROUP = "presentationContainerGroup";
	private static final String PRESENTATION_GROUP_GROUP = "presentationGroupGroup";
	private static final String PRESENTATION_VAR_GROUP = "presentationVarGroup";
	private static final String PRESENTATION_GROUP = "presentationGroup";
	private static final String PRESENTATION = "presentation";
	private static final String PRESENTATION_TYPE_COLLECTION = "presentationTypeCollection";
	private static final String PRESENTATION_CHILD_REFERENCES = "presentationChildReferences";
	private static final String PRESENTATION_CHILD_REFERENCE = "presentationChildReference";
	private static final String ID_P_VAR_TEXT_VAR = "idPVarTextVar";
	private static final String ID_CONTAINER_TEXT_VAR = "idContainerTextVar";
	private static final String ID_PGROUP_TEXT_VAR = "idPGroupTextVar";
	private static final String COLLECTION = "Collection";
	private static final String COLLECTION_VAR = "CollectionVar";
	private static final String REF_PARENT_ID = "refParentId";
	private static final String NAME_FOR_ABSTRACT = "abstract";
	private static final String RECORD_TYPE = "recordType";
	private static final String FINAL_VALUE = "finalValue";
	private static final String PARENT_ID = "parentId";
	private static final String ATTRIBUTE_REFERENCES = "attributeReferences";
	private DataGroup emptyLinkList = DataGroup.withNameInData("collectedDataLinks");
	private RecordStorage recordStorage;
	private MetadataCreator metadataCreator;

	public MetadataForPresentation(RecordStorage recordStorage, MetadataCreator metadataCreator) {
		this.recordStorage = recordStorage;
		this.metadataCreator = metadataCreator;
	}

	public void createMetadataForPresentation() {
		createRepeatCollectionItems();
		createRepeatItemCollection();
		createRepeatVar();

		createModeCollectionItems();
		createModeItemCollection();
		createModeVar();

		createRecordInfoPVarGroup();

		createRecordInfoPGroupGroup();

		createRecordInfoContainerGroup();

		addPresentationChildReferences();
		addPresentationChildReference();

		createPresentationTypeCollectionItems();
		createPresentationTypeItemCollection();
		createPresentationTypeVar();
		createPresentationTypePGroupVar();
		createPresentationTypePVarVar();
		createPresentationTypeContainerVar();

		createPresentationGroup();
		addRecordTypePresentation();
		createPresentationVarGroup();
		addRecordTypePresentationVar();
		createPresentationGroupGroup();
		addRecordTypePresentationGroup();
		createPresentationContainerGroup();
		addRecordTypePresentationContainer();
	}

	private void createRepeatCollectionItems() {
		metadataCreator.createCollectionItem("this", "this");
		metadataCreator.createCollectionItem("children", "children");
	}

	private void createRepeatItemCollection() {
		String id = REPEAT;
		DataGroup dataGroup = metadataCreator.createDataGroupForItemCollectionWithId(id);
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "thisItem");
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "childrenItem");
		recordStorage.create(MetadataTypes.ITEMCOLLECTION.type, id + COLLECTION, dataGroup,
				emptyLinkList);
	}

	private void createRepeatVar() {
		String collectionId = REPEAT;
		DataGroup dataGroup = metadataCreator
				.createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(collectionId,
						"repeatCollection", REPEAT);
		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type, collectionId + COLLECTION_VAR,
				dataGroup, emptyLinkList);
	}

	private void createModeCollectionItems() {
		metadataCreator.createCollectionItem("input", "input");
		metadataCreator.createCollectionItem("output", "output");
	}

	private void createModeItemCollection() {
		String id = "mode";
		DataGroup dataGroup = metadataCreator.createDataGroupForItemCollectionWithId(id);
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "inputItem");
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "outputItem");
		recordStorage.create(MetadataTypes.ITEMCOLLECTION.type, id + COLLECTION, dataGroup,
				emptyLinkList);
	}

	private void createModeVar() {
		String collectionId = "mode";
		DataGroup dataGroup = metadataCreator
				.createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(collectionId,
						"modeCollection", "mode");
		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type, collectionId + COLLECTION_VAR,
				dataGroup, emptyLinkList);
	}

	private void createRecordInfoPVarGroup() {
		metadataCreator.addMetadataTextVariableChildWithIdAndNameInDataAndRegExAndRefParentId(
				ID_P_VAR_TEXT_VAR, ID, "(.*PVar$)", ID_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoNewWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoNewPVarGroup", ID_P_VAR_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoPVarGroup", ID_P_VAR_TEXT_VAR);
	}

	private void createRecordInfoPGroupGroup() {
		metadataCreator.addMetadataTextVariableChildWithIdAndNameInDataAndRegExAndRefParentId(
				ID_PGROUP_TEXT_VAR, ID, "(.*PGroup$)", ID_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoNewWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoNewPGroupGroup", ID_PGROUP_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoPGroupGroup", ID_PGROUP_TEXT_VAR);
	}

	private void createRecordInfoContainerGroup() {
		metadataCreator.addMetadataTextVariableChildWithIdAndNameInDataAndRegExAndRefParentId(
				ID_CONTAINER_TEXT_VAR, ID, "(.*Container$)", ID_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoNewWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoNewContainerGroup", ID_CONTAINER_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoContainerGroup", ID_CONTAINER_TEXT_VAR);
	}

	private void addPresentationChildReference() {
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				PRESENTATION_CHILD_REFERENCE, "childReference");

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "ref");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "refMinimized", "0",
				"1");

		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATION_CHILD_REFERENCE, dataGroup,
				emptyLinkList);
	}

	private void addPresentationChildReferences() {
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				PRESENTATION_CHILD_REFERENCES, "childReferences");

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_CHILD_REFERENCE, "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATION_CHILD_REFERENCES, dataGroup,
				emptyLinkList);
	}

	private void createPresentationTypeCollectionItems() {
		metadataCreator.createCollectionItem(P_GROUP, P_GROUP);
		metadataCreator.createCollectionItem("pVar", "pVar");
		metadataCreator.createCollectionItem(CONTAINER, CONTAINER);
	}

	private void createPresentationTypeItemCollection() {
		String id = "presentationType";
		DataGroup dataGroup = metadataCreator.createDataGroupForItemCollectionWithId(id);
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, P_GROUP);
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "pVar");
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, CONTAINER);
		recordStorage.create(MetadataTypes.ITEMCOLLECTION.type, id + COLLECTION, dataGroup,
				emptyLinkList);
	}

	private void createPresentationTypeVar() {
		String collectionId = "presentationType";
		DataGroup dataGroup = metadataCreator
				.createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(collectionId,
						PRESENTATION_TYPE_COLLECTION, "type");
		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type, collectionId + COLLECTION_VAR,
				dataGroup, emptyLinkList);
	}

	private void createPresentationTypePGroupVar() {
		String collectionVarId = "presentationTypePGroup";
		DataGroup dataGroup = metadataCreator
				.createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(collectionVarId,
						PRESENTATION_TYPE_COLLECTION, "type");

		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, PRESENTATION_TYPE_COLLECTION_VAR));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, P_GROUP));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type,
				collectionVarId + COLLECTION_VAR, dataGroup, emptyLinkList);
	}

	private void createPresentationTypePVarVar() {
		String collectionVarId = "presentationTypePVar";
		DataGroup dataGroup = metadataCreator
				.createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(collectionVarId,
						PRESENTATION_TYPE_COLLECTION, "type");

		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, PRESENTATION_TYPE_COLLECTION_VAR));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, "pVar"));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type,
				collectionVarId + COLLECTION_VAR, dataGroup, emptyLinkList);
	}

	private void createPresentationTypeContainerVar() {
		String collectionVarId = "presentationTypeContainer";
		DataGroup dataGroup = metadataCreator
				.createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(collectionVarId,
						PRESENTATION_TYPE_COLLECTION, "type");

		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, PRESENTATION_TYPE_COLLECTION_VAR));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, CONTAINER));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type,
				collectionVarId + COLLECTION_VAR, dataGroup, emptyLinkList);
	}

	private void createPresentationGroup() {
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				PRESENTATION_GROUP, PRESENTATION);
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", PRESENTATION_TYPE_COLLECTION_VAR));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "recordInfoGroup");

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_OF_TEXT_VAR, "0", "X");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, MODE_COLLECTION_VAR,
				"0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, REPEAT_COLLECTION_VAR,
				"0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_CHILD_REFERENCES, "0", "1");
		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATION_GROUP, dataGroup,
				emptyLinkList);

	}

	private void addRecordTypePresentation() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId(PRESENTATION);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "true"));
		recordStorage.create(RECORD_TYPE, "presentation", dataGroup, emptyLinkList);
	}

	private void createPresentationVarGroup() {
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				PRESENTATION_VAR_GROUP, PRESENTATION);
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "presentationTypePVarCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, PRESENTATION_GROUP));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "recordInfoPVarGroup");

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, PRESENTATION_OF_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, MODE_COLLECTION_VAR);
		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATION_VAR_GROUP, dataGroup,
				emptyLinkList);

		DataGroup dataGroup2 = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				"presentationVarNewGroup", PRESENTATION);
		dataGroup2
				.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, PRESENTATION_VAR_GROUP));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, "recordInfoNewPVarGroup");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, PRESENTATION_OF_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, MODE_COLLECTION_VAR);

		recordStorage.create(MetadataTypes.GROUP.type, "presentationVarNewGroup", dataGroup2,
				emptyLinkList);
	}

	private void addRecordTypePresentationVar() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId("presentationVar");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, FALSE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, PRESENTATION));
		recordStorage.create(RECORD_TYPE, "presentationVar", dataGroup, emptyLinkList);
	}

	private void createPresentationGroupGroup() {
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				PRESENTATION_GROUP_GROUP, PRESENTATION);
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "presentationTypePGroupCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, PRESENTATION_GROUP));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "recordInfoPGroupGroup");

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, PRESENTATION_OF_TEXT_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_CHILD_REFERENCES, "1", "1");
		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATION_GROUP_GROUP, dataGroup,
				emptyLinkList);

		DataGroup dataGroup2 = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				"presentationGroupNewGroup", PRESENTATION);
		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, PRESENTATION_GROUP_GROUP));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, "recordInfoNewPGroupGroup");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, PRESENTATION_OF_TEXT_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup2,
				PRESENTATION_CHILD_REFERENCES, "1", "1");

		recordStorage.create(MetadataTypes.GROUP.type, "presentationGroupNewGroup", dataGroup2,
				emptyLinkList);
	}

	private void addRecordTypePresentationGroup() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId(PRESENTATION_GROUP);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, FALSE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, PRESENTATION));
		recordStorage.create(RECORD_TYPE, PRESENTATION_GROUP, dataGroup, emptyLinkList);
	}

	private void createPresentationContainerGroup() {
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				PRESENTATION_CONTAINER_GROUP, PRESENTATION);
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "presentationTypeContainerCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, PRESENTATION_GROUP));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "recordInfoPContainerGroup");

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_OF_TEXT_VAR, "1", "X");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, REPEAT_COLLECTION_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_CHILD_REFERENCES, "1", "1");
		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATION_CONTAINER_GROUP, dataGroup,
				emptyLinkList);

		DataGroup dataGroup2 = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				"presentationContainerNewGroup", PRESENTATION);
		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, PRESENTATION_CONTAINER_GROUP));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, "recordInfoNewPContainerGroup");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup2,
				PRESENTATION_OF_TEXT_VAR, "1", "X");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, REPEAT_COLLECTION_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup2,
				PRESENTATION_CHILD_REFERENCES, "1", "1");

		recordStorage.create(MetadataTypes.GROUP.type, "presentationContainerNewGroup", dataGroup2,
				emptyLinkList);
	}

	private void addRecordTypePresentationContainer() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId("presentationContainer");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, FALSE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, PRESENTATION));
		recordStorage.create(RECORD_TYPE, "presentationContainer", dataGroup, emptyLinkList);
	}
}
