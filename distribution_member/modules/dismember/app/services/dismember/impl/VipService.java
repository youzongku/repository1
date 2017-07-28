package services.dismember.impl;

import java.util.List;

import mapper.dismember.VipInviteCodeMapper;
import services.dismember.IVipService;
import utils.dismember.IDUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class VipService implements IVipService {
	
	private static final Integer LENGHT = 8;
	
	@Inject
	private VipInviteCodeMapper mapper;
	
	/* *
	  {
	  	"num":100,
	  	"lenght":8,
	  }
	 */
	@Override
	public String create(Integer num) {
		List<String> list =   Lists.newArrayList();
		for(int i = 0;i<num;i++){
			list.add(IDUtils.getStringRandom(LENGHT));
		}
		//填充相同的值
		return mapper.batchInsert(list)>0?"SUCCESS":"FAIL";
	}
	
}
