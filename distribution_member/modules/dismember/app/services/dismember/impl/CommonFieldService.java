package services.dismember.impl;

import com.google.inject.Inject;

import entity.dismember.CommonField;
import mapper.dismember.CommonFieldMapper;
import services.dismember.ICommonFieldService;

public class CommonFieldService implements ICommonFieldService {
	
	@Inject
	private CommonFieldMapper commonFieldMapper;

	@Override
	public boolean saveOrUpdateField(CommonField commonField) {
		if (null != commonField) {
			int result = commonFieldMapper.updateByPrimaryKeySelective(commonField);
			return result > 0;
		} 
		
		int result = commonFieldMapper.insert(commonField);
		return result > 0;
	}

	@Override
	public CommonField getCommonFieldById(Integer id) {
		return commonFieldMapper.selectByPrimaryKey(id);
	}

	@Override
	public boolean deleteCommonFieldById(Integer id) {
		int result = commonFieldMapper.deleteByPrimaryKey(id);
		return result>0;
	}

	@Override
	public CommonField getCommonFieldByName(String name) {
		return commonFieldMapper.selectByName(name);
	}

}
