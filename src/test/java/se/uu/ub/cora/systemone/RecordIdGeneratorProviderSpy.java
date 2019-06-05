package se.uu.ub.cora.systemone;

import java.util.Map;

import se.uu.ub.cora.storage.RecordIdGenerator;
import se.uu.ub.cora.storage.RecordIdGeneratorProvider;

public class RecordIdGeneratorProviderSpy implements RecordIdGeneratorProvider {

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
	public RecordIdGenerator getRecordIdGenerator() {
		// TODO Auto-generated method stub
		return null;
	}

}
