package service.timer.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import mapper.timer.OperateRecordMapper;
import mapper.timer.SaleDetailMapper;
import mapper.timer.SaleMainMapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import entity.timer.OperateRecord;
import entity.timer.SaleMain;
import entity.timer.SalesToB2cDetail;
import entity.timer.SalesToB2cIterm;
import entity.timer.ShopDto;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import service.timer.ISaleOrderTaxesService;
import service.timer.ISalesPushToB2CService;
import util.timer.HttpUtil;
import util.timer.SaleOrderStatus;

/**
 * b2b 订单推送到 b2c Created by luwj on 2016/1/19.
 */
public class SalesPushToB2CService implements ISalesPushToB2CService {

	@Inject
	private SaleMainMapper saleMainMapper;
	@Inject
	private SaleDetailMapper saleDetailMapper;
	@Inject
	private ISaleOrderTaxesService saleOrderTaxesService;
	@Inject
	private OperateRecordMapper operateRecordMapper;

	@Override
	public void pushSales(String exeType) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("status", SaleOrderStatus.WAITING_DELIVERY_SIX);// 审核通过
		try {
			List<SalesToB2cIterm> iterms = saleMainMapper.getSalesInfo(map);
			Logger.debug(">>pushSales>>iterms>>>>" + iterms.size());
			// 计算税金
			if (iterms.size() <= 0) {
				return;
			}
			Logger.info("推送至hk前，进行税金计算");
			calculateTaxFeeBeforePush(iterms);
			List<SalesToB2cIterm> pushItems = Lists.newArrayList();
			ObjectMapper obj = new ObjectMapper();
			ShopDto shop = new ShopDto();
			// 获取所有店铺信息
			Map<Integer, ShopDto> shopMap = Maps.newHashMap();
			try {
				String shopRes = HttpUtil.get(Maps.newHashMap(), HttpUtil.B2BBASEURL + "/member/getallstore");
				List<ShopDto> shops = obj.readValue(shopRes, new TypeReference<List<ShopDto>>() {
				});
				shopMap = shops.stream().collect(Collectors.toMap(ShopDto::getId, (p) -> p));
			} catch (Exception e) {
				Logger.error("获取店铺信息错误", e);
			}

			// 添加业务、财务备注信息 数据从操作日志上获取
			List<Integer> orderIdList = iterms.stream().map(SalesToB2cIterm::getIid).collect(Collectors.toList());
			// 查询操作记录
			List<OperateRecord> operateRecordList = operateRecordMapper.selectByOrderIdList(orderIdList);
			// 按照order id分组
			Map<Integer, List<OperateRecord>> operateRecordsByOrderId = operateRecordList.stream()
					.collect(Collectors.groupingBy(OperateRecord::getOrderId));

			for (SalesToB2cIterm iterm : iterms) {
				try {
					// 设置财务备注与业务备注
					setRemarks(iterm, operateRecordsByOrderId.get(iterm.getIid()));
					shop = shopMap.get(iterm.getShopId());
					String receiveraddr = iterm.getReceiveraddr();
					String subRe = receiveraddr.substring(receiveraddr.indexOf("|") + 1, receiveraddr.length());
					if (StringUtils.isNotBlank(subRe)) {
						String[] args = subRe.split(" ");
						iterm.setCprovince(args[0]);
						iterm.setCcity(args[1]);
						iterm.setCarea(args[2]);
						iterm.setCstreetaddress(subRe.substring(subRe.indexOf(args[1]) + args[1].length() + 1));
					}

					List<SalesToB2cDetail> details = saleDetailMapper.getDetails(iterm.getSalesorderid());
					// 获取报关金额和平台收入
					Map<String, Object> amount = saleDetailMapper.getAmount(iterm.getSalesorderid());
					Integer wid = details.get(0).getWarehouseId();
					if (iterm.getPlatformamount() == null) {
						if (null != amount) {
							try {
								if (wid == 2024) {
									iterm.setClearanceamount(0.0);// 报关金额
								} else {
									iterm.setClearanceamount(Double.valueOf(amount.get("clearanceamount") + ""));// 实付款
								}
								if (Integer.valueOf(amount.get("distributortype") + "") == 3) {
									iterm.setPlatformamount(iterm.getFgrandtotal());
								} else {
									Double plat = Double.valueOf(amount.get("platformamount") + "");
									if (amount.get("bbcpostage") != null) {
										plat += Double.valueOf(amount.get("bbcpostage") + "");
									}
									iterm.setPlatformamount(plat);
								}
							} catch (Exception e) {
								Logger.error("" + e);
							}
						} else {
							iterm.setPlatformamount(iterm.getFgrandtotal());
						}
					}
					
					//add by xuse 营销单毛收入为0
					if(null != iterm.getOrderLevel() && 50 == iterm.getOrderLevel()) {
						iterm.setPlatformamount(0.0);
					}

					if (shop != null) {
						iterm.setShopName(shop.getShopName());
						iterm.setShopAddress(buildShopAddress(shop));
						iterm.setShopTelephone(shop.getTelphone() != null ? shop.getTelphone() : "13689528832");
						iterm.setShopKeeper(shop.getKeeperName() != null ? shop.getKeeperName() : "唐义和");
					}

					// add by xuse 内部分销商同步外部订单交易号 外部分销商如果是系统自动支付则同步采购交易
					if (iterm.getDistributorType() != null && iterm.getDistributorType() == 3) {
						iterm.setCtransactionid(iterm.getCtransactionid().startsWith("TT_BBC_") ? iterm.getTradeNo()
								: iterm.getCtransactionid());// 收款方式暂时没做
					} else {
						String transactionId = iterm.getCtransactionid().startsWith("TT_BBC_")
								&& iterm.getPurchasePayno() != null ? iterm.getPurchasePayno()
										: iterm.getCtransactionid();
						String paymentType = iterm.getCtransactionid().startsWith("TT_BBC_")
								&& iterm.getPurchasePayno() != null ? iterm.getPurchasePaytype()
										: iterm.getCpaymentid();
						iterm.setCtransactionid(transactionId);
						iterm.setCpaymentid(paymentType);
					}

					iterm.setDetails(details);
					pushItems.add(iterm);
				} catch (Exception e) {
					Logger.error("客户订单id:[{}]在构造推送数据时产生异常：{}", iterm.getSalesorderid(), e);
				}
			}

			if (pushItems.size() > 0) {
				Configuration config = Play.application().configuration().getConfig("sales");
				String url = config.getString("2Bbc") + "/checkout/receiverB2b";
				Logger.debug(">>>>>>url>>>>" + url);
				String returnStr = payUnifiedorder(url, Json.toJson(pushItems).toString());
				Logger.debug(">>>>returnStr>>>>>>>>>" + returnStr);
				JsonNode node = obj.readValue(returnStr, JsonNode.class);
				if (node.has("errorCode")) {
					String errorCode = node.get("errorCode").asText();
					if ("0".equals(errorCode)) {
						Logger.debug(">>>>销售订单 " + Json.toJson(pushItems).toString() + " 推送到b2c成功!>>>>");
						// 更新销售订单发送状态
						for (SalesToB2cIterm iterm : pushItems) {
							SaleMain saleMain = saleMainMapper.selectByOrderNo(iterm.getCordernumber());
							// 已推送1，未推送0
							saleMain.setIsPushed(1);
							// 推送至hk
							saleMain.setStatus(SaleOrderStatus.PUSHED_2_HK);
							saleMainMapper.updateByPrimaryKeySelective(saleMain);
						}
					} else {
						Logger.debug(">>>>销售订单 " + Json.toJson(pushItems).toString() + " 推送到b2c失败!>>>>"
								+ node.get("errorInfo").asText());
					}
				}
			}
		} catch (Exception e) {
			Logger.error("b2b同步订单到b2c异常：", e);
		}
	}

	private void setRemarks(SalesToB2cIterm iterm, List<OperateRecord> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			iterm.setFinanceRemark(list.size() >= 1 ? list.get(0).getComment() : "");
			iterm.setBusinessRemark(list.size() >= 2 ? list.get(1).getComment() : "");
		}
	}

	private String buildShopAddress(ShopDto shop) {
		return shop.getProvinceName() != null ? shop.getProvinceName()
				: "" + shop.getCityName() != null ? shop.getCityName()
						: "" + shop.getAreaName() != null ? shop.getAreaName()
								: "" + shop.getAddr() != null ? shop.getAddr() : "";
	}

	/**
	 * 再次计算税金（只有未计算过税金的才会去计算）
	 * 
	 * @param iterms2CalculateTaxFee
	 *            销售单集合
	 */
	private void calculateTaxFeeBeforePush(List<SalesToB2cIterm> iterms2CalculateTaxFee) {
		for (SalesToB2cIterm aSalesToB2cIterm : iterms2CalculateTaxFee) {
			if (aSalesToB2cIterm.getTaxFee() == null) {
				Integer mainId = aSalesToB2cIterm.getSalesorderid();
				String saleOrderNo = aSalesToB2cIterm.getCordernumber();
				SaleMain sm = saleOrderTaxesService.calculateTaxes(mainId, saleOrderNo);
				if (sm.getTaxFee() != null) {
					aSalesToB2cIterm.setTaxFee(sm.getTaxFee());
				}
			}
		}
	}

	/**
	 * http 请求
	 *
	 * @param url
	 * @param request_xml
	 * @return
	 */
	public String payUnifiedorder(String url, String request_xml) {
		Logger.debug(">>>>>>>>>>>" + url);
		String return_xml = "";
		HttpClient httpClient = new HttpClient();
		PostMethod post = new PostMethod(url);
		NameValuePair[] pare = new NameValuePair[] { new NameValuePair("data", request_xml) };
		post.setRequestBody(pare);
		post.setRequestHeader("content-type", "application/x-www-form-urlencoded;charset=utf-8");
		// 发送http请求
		try {
			String token = HttpUtil.getToken();
			if (StringUtils.isNotEmpty(token)) {
				post.setRequestHeader("token", token);
			}
			httpClient.executeMethod(post);
			// 打印返回的信息
			return_xml = post.getResponseBodyAsString();
			Logger.info(">>>>payUnifiedorder>>return_xml>>" + return_xml);
		} catch (HttpException e) {
			Logger.error(">>payUnifiedorder>>HttpException>>>", e);
			e.printStackTrace();
		} catch (IOException e) {
			Logger.error(">>payUnifiedorder>>IOException>>>", e);
			e.printStackTrace();
		} finally {
			// 释放连接
			post.releaseConnection();
		}
		return return_xml;
	}
}