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

	private static final String PRESENTATION = "presentation";
	private static final String PRESENTATION_TYPE_COLLECTION = "presentationTypeCollection";
	private static final String PRESENTATION_CHILD_REFERENCES = "presentationChildReferences";
	private static final String PRESENTATION_CHILD_REFERENCE = "presentationChildReference";
	private static final String ID_P_VAR_TEXT_VAR = "idPVarTextVar";
	private static final String ID_CONTAINER_TEXT_VAR = "idContainerTextVar";
	private static final String ID_PGROUP_TEXT_VAR = "idPGroupTextVar";
	private static final String COLLECTION = "Collection";
	private static final String COLLECTION_VAR = "CollectionVar";
	private static final String NAME_IN_DATA = "nameInData";
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
		String id = "repeat";
		DataGroup dataGroup = metadataCreator.createDataGroupForItemCollectionWithId(id);
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "thisItem");
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "childrenItem");
		recordStorage.create(MetadataTypes.ITEMCOLLECTION.type, id + COLLECTION, dataGroup,
				emptyLinkList);
	}

	private void createRepeatVar() {
		String collectionId = "repeat";
		DataGroup dataGroup = metadataCreator
				.createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(collectionId,
						"repeatCollection", "repeat");
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
				ID_P_VAR_TEXT_VAR, "id", "(.*PVar$)", "idTextVar");
		metadataCreator.addMetadataRecordInfoNewWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoNewPVarGroup", ID_P_VAR_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoPVarGroup", ID_P_VAR_TEXT_VAR);
	}

	private void createRecordInfoPGroupGroup() {
		metadataCreator.addMetadataTextVariableChildWithIdAndNameInDataAndRegExAndRefParentId(
				ID_PGROUP_TEXT_VAR, "id", "(.*PGroup$)", "idTextVar");
		metadataCreator.addMetadataRecordInfoNewWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoNewPGroupGroup", ID_PGROUP_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoPGroupGroup", ID_PGROUP_TEXT_VAR);
	}

	private void createRecordInfoContainerGroup() {
		metadataCreator.addMetadataTextVariableChildWithIdAndNameInDataAndRegExAndRefParentId(
				ID_CONTAINER_TEXT_VAR, "id", "(.*Container$)", "idTextVar");
		metadataCreator.addMetadataRecordInfoNewWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoNewContainerGroup", ID_CONTAINER_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoContainerGroup", ID_CONTAINER_TEXT_VAR);
	}

	private void addPresentationChildReference() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId(PRESENTATION_CHILD_REFERENCE);

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "ref");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "refMinimized", "0",
				"1");

		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATION_CHILD_REFERENCE, dataGroup,
				emptyLinkList);
	}

	private void addPresentationChildReferences() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId(PRESENTATION_CHILD_REFERENCES);

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_CHILD_REFERENCE, "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATION_CHILD_REFERENCES, dataGroup,
				emptyLinkList);
	}

	private void createPresentationTypeCollectionItems() {
		metadataCreator.createCollectionItem("pGroup", "pGroup");
		metadataCreator.createCollectionItem("pVar", "pVar");
		metadataCreator.createCollectionItem("container", "container");
	}

	private void createPresentationTypeItemCollection() {
		String id = "presentationType";
		DataGroup dataGroup = metadataCreator.createDataGroupForItemCollectionWithId(id);
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "pGroup");
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "pVar");
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "container");
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
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "presentationTypeCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, "pGroup"));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type,
				collectionVarId + COLLECTION_VAR, dataGroup, emptyLinkList);
	}

	private void createPresentationTypePVarVar() {
		String collectionVarId = "presentationTypePVar";
		DataGroup dataGroup = metadataCreator
				.createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(collectionVarId,
						PRESENTATION_TYPE_COLLECTION, "type");

		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "presentationTypeCollectionVar"));
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
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "presentationTypeCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, "container"));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type,
				collectionVarId + COLLECTION_VAR, dataGroup, emptyLinkList);
	}

	private void createPresentationGroup() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId("presentationGroup");
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "presentationTypeCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, PRESENTATION));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "recordInfoGroup");

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				"presentationOfTextVar", "0", "X");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "modeCollectionVar",
				"0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, "repeatCollectionVar",
				"0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_CHILD_REFERENCES, "0", "1");
		recordStorage.create(MetadataTypes.GROUP.type, "presentationGroup", dataGroup,
				emptyLinkList);

	}

	private void addRecordTypePresentation() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId(PRESENTATION);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "true"));
		recordStorage.create(RECORD_TYPE, "text", dataGroup, emptyLinkList);
	}

	private void createPresentationVarGroup() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId("presentationVarGroup");
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "presentationTypePVarCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, PRESENTATION));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "presentationGroup"));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "recordInfoPVarGroup");

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "presentationOfTextVar");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "modeCollectionVar");
		recordStorage.create(MetadataTypes.GROUP.type, "presentationVarGroup", dataGroup,
				emptyLinkList);

		DataGroup dataGroup2 = metadataCreator
				.createDataGroupForMetadataWithRecordId("presentationVarNewGroup");
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, PRESENTATION));
		dataGroup2
				.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "presentationVarGroup"));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, "recordInfoNewPVarGroup");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, "presentationOfTextVar");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, "modeCollectionVar");

		recordStorage.create(MetadataTypes.GROUP.type, "presentationVarNewGroup", dataGroup2,
				emptyLinkList);
	}

	private void addRecordTypePresentationVar() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId("presentationVar");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "false"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, PRESENTATION));
		recordStorage.create(RECORD_TYPE, "text", dataGroup, emptyLinkList);
	}

	private void createPresentationGroupGroup() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId("presentationGroupGroup");
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "presentationTypePGroupCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, PRESENTATION));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "presentationGroup"));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "recordInfoPGroupGroup");

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "presentationOfTextVar");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_CHILD_REFERENCES, "1", "1");
		recordStorage.create(MetadataTypes.GROUP.type, "presentationGroupGroup", dataGroup,
				emptyLinkList);

		DataGroup dataGroup2 = metadataCreator
				.createDataGroupForMetadataWithRecordId("presentationGroupNewGroup");
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, PRESENTATION));
		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "presentationGroupGroup"));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, "recordInfoNewPGroupGroup");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, "presentationOfTextVar");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup2,
				PRESENTATION_CHILD_REFERENCES, "1", "1");

		recordStorage.create(MetadataTypes.GROUP.type, "presentationGroupNewGroup", dataGroup2,
				emptyLinkList);
	}

	private void addRecordTypePresentationGroup() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId("presentationGroup");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "false"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, PRESENTATION));
		recordStorage.create(RECORD_TYPE, "text", dataGroup, emptyLinkList);
	}

	private void createPresentationContainerGroup() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId("presentationContainerGroup");
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "presentationTypeContainerCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, PRESENTATION));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "presentationGroup"));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "recordInfoPContainerGroup");

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				"presentationOfTextVar", "1", "X");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "repeatCollectionVar");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_CHILD_REFERENCES, "1", "1");
		recordStorage.create(MetadataTypes.GROUP.type, "presentationContainerGroup", dataGroup,
				emptyLinkList);

		DataGroup dataGroup2 = metadataCreator
				.createDataGroupForMetadataWithRecordId("presentationContainerNewGroup");
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, PRESENTATION));
		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "presentationContainerGroup"));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, "recordInfoNewPContainerGroup");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup2,
				"presentationOfTextVar", "1", "X");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, "repeatCollectionVar");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup2,
				PRESENTATION_CHILD_REFERENCES, "1", "1");

		recordStorage.create(MetadataTypes.GROUP.type, "presentationContainerNewGroup", dataGroup2,
				emptyLinkList);
	}

	private void addRecordTypePresentationContainer() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId("presentationContainer");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "false"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, PRESENTATION));
		recordStorage.create(RECORD_TYPE, "text", dataGroup, emptyLinkList);
	}
}
