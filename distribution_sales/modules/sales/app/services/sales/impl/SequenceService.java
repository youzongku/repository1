package services.sales.impl;

import mapper.sales.SequenceMapper;

import com.google.inject.Inject;

import services.sales.ISequenceService;

public class SequenceService implements ISequenceService{

	@Inject private	SequenceMapper sequenceMapper;

	@Override
	public String selectNextValue(String seqName) {
		synchronized (new byte[0]) {
	        sequenceMapper.updateCurrentValue(seqName);
	        return sequenceMapper.selectCurrentValue(seqName);
	    }
	}

}
