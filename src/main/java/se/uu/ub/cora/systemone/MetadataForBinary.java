/*
 * Copyright 2016 Uppsala University Library
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

public class MetadataForBinary {
	private static final String BINARY_GROUP = "binaryGroup";
	private static final String BINARY = "binary";
	private static final String IMAGE_GROUP = "imageGroup";
	private static final String ID_BINARY_TEXT_VAR = "idBinaryTextVar";
	private static final String RECORD_INFO_BINARY_GROUP = "recordInfoBinaryGroup";
	private static final String FILENAME_TEXT_VAR = "filenameTextVar";
	private static final String FILESIZE_TEXT_VAR = "filesizeTextVar";
	private static final String REF_PARENT_ID = "refParentId";
	private static final String NAME_FOR_ABSTRACT = "abstract";
	private static final String PARENT_ID = "parentId";
	private static final String RECORD_TYPE = "recordType";
	private DataGroup emptyLinkList = DataGroup.withNameInData("collectedDataLinks");
	private RecordStorage recordStorage;
	private MetadataCreator metadataCreator;

	public MetadataForBinary(RecordStorage recordStorage, MetadataCreator metadataCreator) {
		this.recordStorage = recordStorage;
		this.metadataCreator = metadataCreator;
	}

	public void createMetadataForBinary() {
		createTextVars();
		createRecordInfoBinaryGroup();
		createBinaryGroup();
		createImageGroup();
		addRecordTypeBinary();
		addRecordTypeImage();
	}

	private void createTextVars() {
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx(FILENAME_TEXT_VAR,
				"fileName", "(.*)");
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx(FILESIZE_TEXT_VAR,
				"fileSize", "(.*)");
	}

	private void createRecordInfoBinaryGroup() {
		metadataCreator.addMetadataTextVariableChildWithIdAndNameInDataAndRegExAndRefParentId(
				ID_BINARY_TEXT_VAR, "id", "(.*$)", "idTextVar");
		metadataCreator.addMetadataRecordInfoWithRecordInfoIdAndRefMetadataIdUsedAsId(
				RECORD_INFO_BINARY_GROUP, ID_BINARY_TEXT_VAR);
	}

	private void createBinaryGroup() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordIdAndNameInData(BINARY_GROUP, BINARY);

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, RECORD_INFO_BINARY_GROUP);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, FILENAME_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, FILESIZE_TEXT_VAR);

		recordStorage.create(MetadataTypes.GROUP.type, BINARY_GROUP, dataGroup, emptyLinkList);
	}

	private void createImageGroup() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordIdAndNameInData(IMAGE_GROUP, BINARY);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, RECORD_INFO_BINARY_GROUP);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, FILENAME_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, FILESIZE_TEXT_VAR);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, BINARY_GROUP));
		recordStorage.create(MetadataTypes.GROUP.type, IMAGE_GROUP, dataGroup, emptyLinkList);

		DataGroup dataGroup2 = metadataCreator
				.createDataGroupForMetadataWithRecordIdAndNameInData("imageNewGroup", BINARY);
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, IMAGE_GROUP));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, FILENAME_TEXT_VAR);
		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, FILESIZE_TEXT_VAR);
		recordStorage.create(MetadataTypes.GROUP.type, "imageNewGroup", dataGroup2, emptyLinkList);
	}

	private void addRecordTypeBinary() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId(BINARY);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "true"));
		recordStorage.create(RECORD_TYPE, BINARY, dataGroup, emptyLinkList);
	}

	private void addRecordTypeImage() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithIdAndUserSuppliedId("image",
				false);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "false"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, BINARY));
		recordStorage.create(RECORD_TYPE, "image", dataGroup, emptyLinkList);
	}
}
