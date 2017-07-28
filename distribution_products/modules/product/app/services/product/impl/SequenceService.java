package services.product.impl;

import com.google.inject.Inject;

import mapper.contract.SequenceMapper;
import services.product.ISequenceService;

public class SequenceService implements ISequenceService{

	@Inject
	private SequenceMapper sequenceMapper;

	@Override
	public String selectNextValue(String seqName) {
		synchronized (new byte[0]) {
	        sequenceMapper.updateCurrentValue(seqName);
	        return sequenceMapper.selectCurrentValue(seqName);
	    }
	}

}
