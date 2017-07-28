package services.dismember.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.dismember.PaymentMethodDto;
import entity.dismember.DisMember;
import entity.dismember.PaymentCondition;
import entity.dismember.PaymentMapper;
import entity.dismember.PaymentMethod;
import mapper.dismember.PaymentConditionMapper;
import mapper.dismember.PaymentMapperMapper;
import mapper.dismember.PaymentMethodMapper;
import play.Logger;
import services.dismember.IPaymentMethodService;

/**
 * @author zbc
 * 2016年12月6日 上午11:40:27
 */
public class PaymentMethodService implements IPaymentMethodService {

	@Inject
	private PaymentMethodMapper methodMapper;
	@Inject 
	private PaymentConditionMapper conditMapper;
	@Inject
	private PaymentMapperMapper _mapper;
	
	@Override
	public List<PaymentMethod> readAllMethod() {
		return methodMapper.select();
	}

	@Override
	public Map<String, Object> create(PaymentMethodDto condit,String admin) {
		Map<String, Object> res = Maps.newHashMap();
		try {
			PaymentCondition payCondit = conditMapper.selectByParma(condit);
			String msg = null;
			if(payCondit != null){
				//删除原先的映射，插入新的映射关系
				_mapper.deleteBycondId(payCondit.getId());
				payCondit.setUpdateTime(new Date());
				conditMapper.updateByPrimaryKey(payCondit);
				msg = "更新";
			}else{
				payCondit = new PaymentCondition();
				BeanUtils.copyProperties(condit, payCondit);
				payCondit.setCreateUser(admin);
				conditMapper.insertSelective(payCondit);
				msg = "新增";
			}
			if(condit.getMethodids() != null){
				PaymentMapper mapper = null;
				for(Integer methodId:condit.getMethodids()){
					mapper =  new PaymentMapper();
					mapper.setConditionId(payCondit.getId());
					mapper.setMethodId(methodId);
					_mapper.insertSelective(mapper);
				}
			}
			res.put("suc", true);
			res.put("msg", msg+"数据成功");
		} catch (Exception e) {
			Logger.info("新增/更新异常",e);
			res.put("suc", false);
			res.put("msg", "新增/更新异常");
		}
		return res;
	}

	@Override
	public Map<String, Object> read(Integer purpose,DisMember member, Boolean isfront) {
		Map<String,Object> res = Maps.newHashMap();
		if(member == null){
			res.put("suc", false);
			res.put("msg", "用户不存在");
			return res;
		}
		
		try {
			PaymentCondition condit = new PaymentCondition();
			condit.setModel(member.getDistributionMode());
			condit.setDisType(member.getComsumerType());
			condit.setPurpose(purpose);
			if(isfront){
				condit.setForeground(true);
			}else{
				condit.setBackstage(true);
			}
			List<PaymentMethod> list  = methodMapper.selectByCondit(condit);
			res.put("suc", true);
			res.put("list", list);
			return res;
		} catch (Exception e) {
			Logger.info("获取支付方式异常",e);
			res.put("suc", false);
			res.put("msg", "获取支付方式异常");
			return res;
		}
	}

}
