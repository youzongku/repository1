package services.dismember.impl;


import mapper.dismember.SequenceMapper;
import services.dismember.ISequenceService;

import com.google.inject.Inject;


public class SequenceService implements ISequenceService{

	@Inject
	private SequenceMapper sequenceMapper;

	@Override
	public String selectNextValue(String seqName) {
		synchronized (new byte[0]) {
	        sequenceMapper.updateCurrentValue(seqName);
			String val = sequenceMapper.selectCurrentValue(seqName);
			if("99999999".equals(val))
				sequenceMapper.restoreCurrentValue(seqName);
	        return val;
	    }
	}

}
