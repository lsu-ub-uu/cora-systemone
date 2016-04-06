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

	private static final String OUTPUT_FORMAT = "outputFormat";
	private static final String EMPTY_TEXT_ID_TEXT_VAR = "emptyTextIdTextVar";
	private static final String REPEAT_COLLECTION = "repeatCollection";
	private static final String CHILDREN = "children";
	private static final String REF_MINIMIZED = "refMinimized";
	private static final String PRESENTATION_TYPE_CONTAINER_COLLECTION_VAR = "presentationTypeContainerCollectionVar";
	private static final String PRESENTATION_SURROUNDING_CONTAINER = "presentationSurroundingContainer";
	private static final String SURROUNDING = "Surrounding";
	private static final String REPEATING = "Repeating";
	private static final String PRESENTATION_REPEATING_CONTAINER = "presentationRepeatingContainer";
	private static final String GROUP = "Group";
	private static final String ID_S_CONTAINER_TEXT_VAR = "idSContainerTextVar";
	private static final String ID_R_CONTAINER_TEXT_VAR = "idRContainerTextVar";
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
	private static final String PRESENTATION_CHILD_REFERENCES_GROUP = "presentationChildReferencesGroup";
	private static final String PRESENTATION_CHILD_REFERENCE_GROUP = "presentationChildReferenceGroup";
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
	private static final String PRESENTATIONS_OF_GROUP = "presentationsOfGroup";
	private static final String OUTPUT_FORMAT_COLLECTION_VAR = "outputFormatCollectionVar";
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
		createRepeatThisVar();
		createRepeatChildrenVar();

		createModeCollectionItems();
		createModeItemCollection();
		createModeVar();

		createOutputFormatCollectionItems();
		createOutputFormatItemCollection();
		createOutputFormatVar();

		createDefaultPresentationCollectionItems();
		createDefaultPresentationItemCollection();
		createDefaultPresentationVar();

		createRecordInfoPVarGroup();

		createRecordInfoPGroupGroup();

		createRecordInfoContainerGroup();
		createRecordInfoSurroundingContainerGroup();
		createRecordInfoRepeatingContainerGroup();

		addPresentationChildReferences();
		addPresentationChildReference();

		createPresentationTypeCollectionItems();
		createPresentationTypeItemCollection();
		createPresentationTypeVar();
		createPresentationTypePGroupVar();
		createPresentationTypePVarVar();
		createPresentationTypeContainerVar();

		addPresentationsOfGroup();
		createPresentationGroup();
		addRecordTypePresentation();
		createPresentationVarGroup();
		addRecordTypePresentationVar();
		createPresentationGroupGroup();
		addRecordTypePresentationGroup();
		createPresentationContainerGroups();
		addRecordTypePresentationSurroundingContainer();
		addRecordTypePresentationRepeatingContainer();
	}

	private void createRepeatCollectionItems() {
		metadataCreator.createCollectionItem("this", "this");
		metadataCreator.createCollectionItem(CHILDREN, CHILDREN);
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
						REPEAT_COLLECTION, REPEAT);
		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type, collectionId + COLLECTION_VAR,
				dataGroup, emptyLinkList);
	}

	private void createRepeatThisVar() {
		String collectionId = "repeatThis";
		DataGroup dataGroup = metadataCreator
				.createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(collectionId,
						REPEAT_COLLECTION, REPEAT);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, REPEAT_COLLECTION_VAR));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, "this"));
		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type, collectionId + COLLECTION_VAR,
				dataGroup, emptyLinkList);
	}

	private void createRepeatChildrenVar() {
		String collectionId = "repeatChildren";
		DataGroup dataGroup = metadataCreator
				.createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(collectionId,
						REPEAT_COLLECTION, REPEAT);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, REPEAT_COLLECTION_VAR));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, CHILDREN));
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

	private void createOutputFormatCollectionItems() {
		metadataCreator.createCollectionItem("text", "text");
		metadataCreator.createCollectionItem("image", "image");
		metadataCreator.createCollectionItem("video", "video");
		metadataCreator.createCollectionItem("sound", "sound");
		metadataCreator.createCollectionItem("download", "download");
	}

	private void createOutputFormatItemCollection() {
		String id = OUTPUT_FORMAT;
		DataGroup dataGroup = metadataCreator.createDataGroupForItemCollectionWithId(id);
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "textItem");
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "imageItem");
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "videoItem");
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "soundItem");
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "downloadItem");
		recordStorage.create(MetadataTypes.ITEMCOLLECTION.type, id + COLLECTION, dataGroup,
				emptyLinkList);
	}

	private void createOutputFormatVar() {
		String collectionId = OUTPUT_FORMAT;
		DataGroup dataGroup = metadataCreator
				.createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(collectionId,
						"outputFormatCollection", OUTPUT_FORMAT);
		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type, collectionId + COLLECTION_VAR,
				dataGroup, emptyLinkList);
	}

	private void createDefaultPresentationCollectionItems() {
		metadataCreator.createCollectionItem("ref", "ref");
		metadataCreator.createCollectionItem(REF_MINIMIZED, REF_MINIMIZED);
	}

	private void createDefaultPresentationItemCollection() {
		String id = "defaultPresentation";
		DataGroup dataGroup = metadataCreator.createDataGroupForItemCollectionWithId(id);
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "refItem");
		metadataCreator.addCollectionItemReferenceByCollectionItemId(dataGroup, "refMinimizedItem");
		recordStorage.create(MetadataTypes.ITEMCOLLECTION.type, id + COLLECTION, dataGroup,
				emptyLinkList);
	}

	private void createDefaultPresentationVar() {
		String collectionId = "defaultPresentation";
		DataGroup dataGroup = metadataCreator
				.createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(collectionId,
						"defaultPresentationCollection", "default");
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
		metadataCreator.addMetadataRecordInfoWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoContainerGroup", ID_CONTAINER_TEXT_VAR);
	}

	private void createRecordInfoSurroundingContainerGroup() {
		metadataCreator.addMetadataTextVariableChildWithIdAndNameInDataAndRegExAndRefParentId(
				ID_S_CONTAINER_TEXT_VAR, ID, "(.*SContainer$)", ID_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoNewWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoNewSurroundingContainerGroup", ID_S_CONTAINER_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoSurroundingContainerGroup", ID_S_CONTAINER_TEXT_VAR);
	}

	private void createRecordInfoRepeatingContainerGroup() {
		metadataCreator.addMetadataTextVariableChildWithIdAndNameInDataAndRegExAndRefParentId(
				ID_R_CONTAINER_TEXT_VAR, ID, "(.*RContainer$)", ID_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoNewWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoNewRepeatingContainerGroup", ID_R_CONTAINER_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoRepeatingContainerGroup", ID_R_CONTAINER_TEXT_VAR);
	}

	private void addPresentationChildReference() {
		metadataCreator.addMetadataTextVariableWithIdAndNameInData("presentationRef", "ref");
		metadataCreator.addMetadataTextVariableWithIdAndNameInData("presentationRefMinimized",
				REF_MINIMIZED);

		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				PRESENTATION_CHILD_REFERENCE_GROUP, "childReference");

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "presentationRef");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				"presentationRefMinimized", "0", "1");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "defaultPresentationCollectionVar");

		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATION_CHILD_REFERENCE_GROUP,
				dataGroup, emptyLinkList);
	}

	private void addPresentationChildReferences() {
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				PRESENTATION_CHILD_REFERENCES_GROUP, "childReferences");

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_CHILD_REFERENCE_GROUP, "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATION_CHILD_REFERENCES_GROUP,
				dataGroup, emptyLinkList);
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

	private void addPresentationsOfGroup() {
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				PRESENTATIONS_OF_GROUP, "presentationsOf");

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_OF_TEXT_VAR, "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATIONS_OF_GROUP, dataGroup,
				emptyLinkList);
	}

	private void createPresentationGroup() {
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx(PRESENTATION_OF_TEXT_VAR,
				"presentationOf", "(.*)");
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				PRESENTATION_GROUP, PRESENTATION);
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", PRESENTATION_TYPE_COLLECTION_VAR));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "recordInfoGroup");

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATIONS_OF_GROUP, "0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_OF_TEXT_VAR, "0", "X");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, MODE_COLLECTION_VAR,
				"0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, REPEAT_COLLECTION_VAR,
				"0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_CHILD_REFERENCES_GROUP, "0", "1");
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

		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx(EMPTY_TEXT_ID_TEXT_VAR,
				"emptyTextId", "(.*Text$)");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				EMPTY_TEXT_ID_TEXT_VAR, "0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				OUTPUT_FORMAT_COLLECTION_VAR, "0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATION_VAR_GROUP, dataGroup,
				emptyLinkList);

		DataGroup dataGroup2 = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				"presentationVarNewGroup", PRESENTATION);
		DataGroup attributeReferences2 = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup2.addChild(attributeReferences2);
		attributeReferences2.addChild(
				DataAtomic.withNameInDataAndValue("ref", "presentationTypePVarCollectionVar"));
		dataGroup2
				.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, PRESENTATION_VAR_GROUP));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, "recordInfoNewPVarGroup");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, PRESENTATION_OF_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, MODE_COLLECTION_VAR);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup2,
				EMPTY_TEXT_ID_TEXT_VAR, "0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup2,
				OUTPUT_FORMAT_COLLECTION_VAR, "0", "1");

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
		metadataCreator.addChildReferenceWithRef1to1(dataGroup,
				PRESENTATION_CHILD_REFERENCES_GROUP);
		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATION_GROUP_GROUP, dataGroup,
				emptyLinkList);

		DataGroup dataGroup2 = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				"presentationGroupNewGroup", PRESENTATION);
		DataGroup attributeReferences2 = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup2.addChild(attributeReferences2);
		attributeReferences2.addChild(
				DataAtomic.withNameInDataAndValue("ref", "presentationTypePGroupCollectionVar"));
		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, PRESENTATION_GROUP_GROUP));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, "recordInfoNewPGroupGroup");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, PRESENTATION_OF_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2,
				PRESENTATION_CHILD_REFERENCES_GROUP);

		recordStorage.create(MetadataTypes.GROUP.type, "presentationGroupNewGroup", dataGroup2,
				emptyLinkList);
	}

	private void addRecordTypePresentationGroup() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId(PRESENTATION_GROUP);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, FALSE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, PRESENTATION));
		recordStorage.create(RECORD_TYPE, PRESENTATION_GROUP, dataGroup, emptyLinkList);
	}

	private void createPresentationContainerGroups() {
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordIdAndNameInData(
				PRESENTATION_CONTAINER_GROUP, PRESENTATION);
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(DataAtomic.withNameInDataAndValue("ref",
				PRESENTATION_TYPE_CONTAINER_COLLECTION_VAR));
		attributeReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", REPEAT_COLLECTION_VAR));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, PRESENTATION_GROUP));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, "recordInfoContainerGroup");

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATIONS_OF_GROUP, "0", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				PRESENTATION_OF_TEXT_VAR, "0", "1");
		metadataCreator.addChildReferenceWithRef1to1(dataGroup,
				PRESENTATION_CHILD_REFERENCES_GROUP);
		recordStorage.create(MetadataTypes.GROUP.type, PRESENTATION_CONTAINER_GROUP, dataGroup,
				emptyLinkList);

		createPresentationContainerGroup(PRESENTATION_REPEATING_CONTAINER,
				"repeatThisCollectionVar", REPEATING);
		createPresentationContainerGroup(PRESENTATION_SURROUNDING_CONTAINER,
				"repeatChildrenCollectionVar", SURROUNDING);
	}

	private void createPresentationContainerGroup(String id, String repeat, String type) {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordIdAndNameInData(id + GROUP, PRESENTATION);
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(DataAtomic.withNameInDataAndValue("ref",
				PRESENTATION_TYPE_CONTAINER_COLLECTION_VAR));
		attributeReferences.addChild(DataAtomic.withNameInDataAndValue("ref", repeat));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, PRESENTATION_CONTAINER_GROUP));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup,
				"recordInfo" + type + "ContainerGroup");

		if (type.equals(SURROUNDING)) {
			metadataCreator.addChildReferenceWithRef1to1(dataGroup, PRESENTATIONS_OF_GROUP);
		} else {
			metadataCreator.addChildReferenceWithRef1to1(dataGroup, PRESENTATION_OF_TEXT_VAR);
		}
		metadataCreator.addChildReferenceWithRef1to1(dataGroup,
				PRESENTATION_CHILD_REFERENCES_GROUP);
		recordStorage.create(MetadataTypes.GROUP.type, id + GROUP, dataGroup, emptyLinkList);

		DataGroup dataGroup2 = metadataCreator
				.createDataGroupForMetadataWithRecordIdAndNameInData(id + "NewGroup", PRESENTATION);
		DataGroup attributeReferences2 = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup2.addChild(attributeReferences2);
		attributeReferences2.addChild(DataAtomic.withNameInDataAndValue("ref",
				PRESENTATION_TYPE_CONTAINER_COLLECTION_VAR));
		attributeReferences2.addChild(DataAtomic.withNameInDataAndValue("ref", repeat));
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, id + GROUP));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup2,
				"recordInfoNew" + type + "ContainerGroup");
		if (type.equals(SURROUNDING)) {
			metadataCreator.addChildReferenceWithRef1to1(dataGroup2, PRESENTATIONS_OF_GROUP);
		} else {
			metadataCreator.addChildReferenceWithRef1to1(dataGroup2, PRESENTATION_OF_TEXT_VAR);
		}
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2,
				PRESENTATION_CHILD_REFERENCES_GROUP);

		recordStorage.create(MetadataTypes.GROUP.type, id + "NewGroup", dataGroup2, emptyLinkList);
	}

	private void addRecordTypePresentationSurroundingContainer() {
		DataGroup dataGroup = metadataCreator
				.createRecordTypeWithId(PRESENTATION_SURROUNDING_CONTAINER);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, FALSE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, PRESENTATION));
		recordStorage.create(RECORD_TYPE, PRESENTATION_SURROUNDING_CONTAINER, dataGroup,
				emptyLinkList);
	}

	private void addRecordTypePresentationRepeatingContainer() {
		DataGroup dataGroup = metadataCreator
				.createRecordTypeWithId(PRESENTATION_REPEATING_CONTAINER);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, FALSE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, PRESENTATION));
		recordStorage.create(RECORD_TYPE, PRESENTATION_REPEATING_CONTAINER, dataGroup,
				emptyLinkList);
	}
}
