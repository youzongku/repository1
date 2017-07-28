package services.sales.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

import entity.marketing.MarketingOrder;
import entity.sales.Receiver;
import entity.sales.SaleBase;
import entity.sales.SaleMain;
import mapper.sales.ReceiverMapper;
import play.Logger;
import services.sales.ISaleReceiverService;

public class SaleReceiverService implements ISaleReceiverService {

	@Inject private ReceiverMapper receiverMapper;
	
	@Override
	public boolean saveReceiver(Receiver receiver) {
		return receiverMapper.insert(receiver)>0;
	}

	@Override
	public boolean udpateReceiver(Receiver receiver) {
		return receiverMapper.updateByPrimaryKeySelective(receiver)>0;
	}

	@Override
	public Receiver getReceiverByOrderId(Integer orderId) {
		return receiverMapper.selectByOrderId(orderId);
	}

	@Override
	public int deleteReceiver(Integer rid, String account) {
		Receiver receiver = receiverMapper.selectByPrimaryKey(rid);
		if (receiver==null || !Objects.equals(account, receiver.getEmail())) {
			return 0;
		}
		return receiverMapper.deleteByPrimaryKey(rid);
	}

	@Override
	public Receiver addReceiver(MarketingOrder mo) {
		Logger.info("通过营销单录入-->保存收货人信息，mo：{}", mo);
		Receiver re = new Receiver();
		re.setReceiverName(mo.getReceiver());
		re.setReceiverTel(mo.getReceiverTel());
//		re.setReceiverIdcard();
		re.setEmail(mo.getEmail());
		// 地址
		re.setProvinceName(mo.getProvinceName());
		re.setCityName(mo.getCityName());
		re.setAreaName(mo.getRegionName());
		re.setReceiverAddr(mo.getAddressDetail());
		if (StringUtils.isNotBlank(mo.getReceiverPostcode())) {
			re.setPostCode(Integer.valueOf(mo.getReceiverPostcode()));	
		}
		
		return commomAddReceiver(re);
	}
	
	@Override
	public Receiver addReceiver(SaleMain main, SaleBase base) {
		Logger.info("通过发货单录入-->保存收货人信息");
		Receiver re = new Receiver();
		re.setReceiverName(base.getReceiver());
		re.setReceiverTel(base.getTel());
		re.setReceiverIdcard(base.getIdcard());
		re.setEmail(main.getEmail());
		// 地址
		String[] addresses = base.getAddress().split(" ");
		re.setProvinceName(addresses[0]);
		re.setCityName(addresses[1]);
		re.setAreaName(addresses[2]);
		re.setReceiverAddr(addresses[3]);
		if (StringUtils.isNotBlank(base.getPostCode())) {
			re.setPostCode(Integer.valueOf(base.getPostCode()));	
		}
		
		return commomAddReceiver(re);
	}
	
	private Receiver commomAddReceiver(Receiver re) {
		List<Receiver> addressList = receiverMapper.queryAllByAccount(re.getEmail());
		long count = addressList.stream()
				.filter(e->{
					return Objects.equals(buildAddressInfoStr(e), buildAddressInfoStr(re));
				}).count();
		if (count>0) {
			// 已结存在，不要保存
			Logger.info("addReceiver==地址已存在，不需要保存收货人地址，account={}，address={}",re.getEmail(),buildFullAddress(re));
			return null;
		}
		
		int line = receiverMapper.insertSelective(re);
		Logger.info("addReceiver==保存收货人信息={}", line==1);
		return re;
	}
	
	/**
	 * 将省市区详细地址拼接起来：深圳市龙岗区平湖街道华南城
	 * @param receiver
	 * @return
	 */
	private String buildFullAddress(Receiver receiver) {
		return String.join("", receiver.getProvinceName(), receiver.getCityName(), receiver.getAreaName(), receiver.getReceiverAddr());
	}
	/**
	 * 将收货人、收货人手机号、地址拼接起来：刘德华13545685236深圳市龙岗区平湖街道华南城
	 * @param receiver
	 * @return
	 */
	private String buildAddressInfoStr(Receiver receiver) {
		String receiverName = StringUtils.defaultIfBlank(receiver.getReceiverName(), "");
		String receiverTel = StringUtils.defaultIfBlank(receiver.getReceiverTel(), "");
		return String.join("", receiverName, receiverTel, buildFullAddress(receiver));
	}

	@Override
	public List<Receiver> query(String account, String searchText) {
		// receiver_name province_namecity_namearea_namereceiver_addr receiver_idcard post_code
		List<Receiver> list = receiverMapper.query(account, searchText);
		for (Receiver receiver : list) {
			receiver.setStringMsg(
					String.join(" ", getStringValue(receiver.getReceiverName(),""), 
							buildFullAddress(receiver), 
							getStringValue(receiver.getReceiverIdcard(),""), 
									getStringValue(String.valueOf(receiver.getPostCode()),"")
							)
					);
		}
		return list;
	}
	
	private String getStringValue(String val, String defaultVal) {
		if (StringUtils.isBlank(val)) {
			return defaultVal;
		}
		return val;
	}

}
