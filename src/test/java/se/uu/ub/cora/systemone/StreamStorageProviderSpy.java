package se.uu.ub.cora.systemone;

import java.util.Map;

import se.uu.ub.cora.storage.StreamStorage;
import se.uu.ub.cora.storage.StreamStorageProvider;

public class StreamStorageProviderSpy implements StreamStorageProvider {

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
	public StreamStorage getStreamStorage() {
		// TODO Auto-generated method stub
		return null;
	}

}
