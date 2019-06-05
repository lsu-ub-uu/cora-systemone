package se.uu.ub.cora.systemone;

import java.util.Map;

import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.RecordStorageProvider;

public class RecordStorageProviderSpy implements RecordStorageProvider {

	@Override
	public int getOrderToSelectImplementionsBy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void startUsingInitInfo(Map<String, String> initInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public RecordStorage getRecordStorage() {
		// TODO Auto-generated method stub
		return null;
	}

}
