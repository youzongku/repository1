package service.discart.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import dto.discart.CartInfoDto;
import dto.discart.DisCartDto;
import dto.discart.ProSearch;
import dto.discart.ProStockDto;
import dto.discart.ProductSelectedStateParam;
import entity.discart.DisCart;
import entity.discart.DisCartItem;
import mapper.discart.DisCartItemMapper;
import mapper.discart.DisCartMapper;
import play.Logger;
import play.libs.Json;
import service.discart.IDisCartItemService;
import service.discart.IDisCartService;
import service.discart.IHttpService;
import utils.discart.JsonCaseUtil;
import utils.discart.KeyUtil;
import utils.discart.PriceFormatUtil;
import utils.discart.Types;

/**
 * Created by LSL on 2015/12/7.
 */
public class DisCartItemService implements IDisCartItemService {

    @Inject
    private DisCartItemMapper disCartItemMapper;
    @Inject
    private IHttpService httpService;
    @Inject
    private IDisCartService disCartService;
    @Inject
    private DisCartMapper disCartMapper;
    
    @Override
    public CartInfoDto getDisCartData(String dismember) {
        try {
        	JsonNode login = Json.parse(dismember);
        	String email = login.get("email").asText(); 
        	DisCart disCart = disCartService.getUsableDisCart(email);
        	Integer mode = login.get("distributionMode").asInt();
        	// 查询原有的商品
        	List<DisCartDto> cartDtoList = getCartInfo(email, disCart, mode);
        	if (CollectionUtils.isEmpty(cartDtoList)) {
                return new CartInfoDto(false, "您的购物车为空");
            }
        	// 非卖品处理
        	processNotSalableProsSelectedState(cartDtoList);
        	//change by zbc  解决购物车查询慢的问题
        	// 再次查询新的     重新查询商品信息导致购物车查询效率变低，没事找事 @huangjc
        /*	List<DisCartDto> newCartDtos = getCartInfo(email, disCart, mode);
        	return new CartInfoDto(true, disCart.getId(),
        			newCartDtos,
        			newCartDtos.size(), 
        			newCartDtos.stream().mapToInt(e->{return e.getSelected()?e.getQty():0;}).sum(), 
        			newCartDtos.stream().mapToDouble(e->{return e.getSelected()?e.getSumprice():0.00d;}).sum(), 
        			disCartService.getActive(disCart.getId()));*/
        	return new CartInfoDto(true, disCart.getId(),
        			cartDtoList,
        			cartDtoList.size(), 
        			cartDtoList.stream().mapToInt(e->{return e.getSelected()?e.getQty():0;}).sum(), 
        			cartDtoList.stream().mapToDouble(e->{return e.getSelected()?e.getSumprice():0.00d;}).sum(), 
        			disCartService.getActive(disCart.getId()));
		}  catch (Exception e) {
			e.printStackTrace();
			return new CartInfoDto(false, "查询购物车信息异常");
		}
    }

    /**
     * 处理非卖品的勾选状态，将非卖品的选中状态改为false
     * @param cartDtoList
     */
	private void processNotSalableProsSelectedState(List<DisCartDto> cartDtoList) {
		// 获取选中的非卖品
		List<DisCartDto> notSalableSelectedList = cartDtoList.stream().filter(ele->{
			return ele.getSelected()!=null && ele.getSelected() && ele.getSalable()!=1;
		}).collect(Collectors.toList());
		// 将选中的非卖品改为不选中
		if (CollectionUtils.isNotEmpty(notSalableSelectedList)) {
			notSalableSelectedList.stream().map(dcd->{
				//更新为不选中
				dcd.setSelected(false);
				DisCartItem cartItem = new DisCartItem();
		        cartItem.setId(dcd.getItemId());
		        cartItem.setBselected(false);
				return cartItem;
			}).collect(Collectors.toList());
//			int lines = disCartItemMapper.batchUpdateByPrimaryKeySelective(toUpdateList);
//			Logger.info("processNotSalableProsSelectedState==>更新购物车里的非卖品选中状态为false，非卖品有={}，成功更新个数={}", toUpdateList, lines);
		}
	}

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月23日 下午2:22:48
	 */
	private List<DisCartDto> getCartInfo( String email, DisCart disCart, Integer mode)
			throws JsonProcessingException, IOException {
		List<DisCartItem> cartItems = disCartItemMapper.getDisCartItemsByCartId(disCart.getId());
		List<DisCartDto> cartDtos = setCartItemDetail(email, mode, cartItems);
		return cartDtos;
	}

	private List<DisCartDto> setCartItemDetail(String email, Integer mode, List<DisCartItem> cartItemList)
			throws JsonProcessingException, IOException {
		List<DisCartDto> cartDtos = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(cartItemList)) {
			// 实时获取商品信息
			Set<String> skuSet = Sets.newHashSet(Lists.transform(cartItemList, e->e.getCsku()));
			JsonNode productsNode = httpService.getProducts(new ProSearch(Lists.newArrayList(skuSet), null, mode, email));
			Map<String, JsonNode> skuWarehouseId2ProNode = Maps.newHashMap();
			productsNode.get("data").get("result").forEach(proNode -> {
				skuWarehouseId2ProNode.put(KeyUtil.getKey(proNode.get("csku").asText(), proNode.get("warehouseId").asInt()), proNode);
			});
			
			JsonNode productNode = null;
			for (DisCartItem cartItem : cartItemList) {
				productNode = skuWarehouseId2ProNode.get(KeyUtil.getKey(cartItem.getCsku(), cartItem.getWarehouseId()));
				if(productNode != null){
					DisCartDto cartDto = new DisCartDto();
					cartDto.setItemId(cartItem.getId());
					cartDto.setSku(cartItem.getCsku());
					cartDto.setQty(cartItem.getIqty());
					cartDto.setIsOrder(cartItem.getIsOrder());
					cartDto.setWarehouseId(cartItem.getWarehouseId());
					cartDto.setStorageName(cartItem.getWarehouseName());
					cartDto.setTitle(cartItem.getTitle());
					cartDto.setInterBarCode(cartItem.getInterBarCode());
					cartDto.setImage(cartItem.getPublicImg());
					cartDto.setCategoryId(cartItem.getCategoryId());
					cartDto.setSelected(cartItem.getBselected());
					cartDto.setCategoryName(cartItem.getCategoryName());
					cartDto.setDisPrice(JsonCaseUtil.jsonCase(productNode, "disPrice",Types.DOU));
					cartDto.setMarketPrice(JsonCaseUtil.jsonCase(productNode, "proposalRetailPrice",Types.DOU));
					Double sumprice = 0.00d;
					if (cartItem.getIqty() != null && cartDto.getDisPrice() != null) {
						sumprice = PriceFormatUtil.toFix2(
								new BigDecimal(cartItem.getIqty()).multiply(new BigDecimal(cartDto.getDisPrice())));
					}
					cartDto.setIstatus(JsonCaseUtil.jsonCase(productNode, "istatus", Types.INT));
					cartDto.setSalable(JsonCaseUtil.jsonCase(productNode, "salable", Types.INT));
					cartDto.setSumprice(sumprice);
					cartDto.setContractNo(JsonCaseUtil.jsonCase(productNode, "contractNo",Types.STR));
					Integer batchNum = JsonCaseUtil.jsonCase(productNode, "batchNumber", Types.INT);
					cartDto.setBatchnum(batchNum != null && batchNum > 0 ? batchNum : 1);
					cartDto.setClearancePrice(JsonCaseUtil.jsonCase(productNode, "clearancePrice", Types.DOU));
					cartDtos.add(cartDto);
				}
			}
		}
		return cartDtos;
	}

    @Override
    public Map<String, Object> deleteDisCartItemById(String email, String cartItemId) {
    	Map<String, Object> result = Maps.newHashMap();
    	DisCartItem item = disCartItemMapper.selectByPrimaryKey(Integer.parseInt(cartItemId));
    	if (item==null) {
    		result.put("result", false);
            result.put("msg", "购物车不存在此商品");
            return result;
		}
    	if (!isMyCart(item.getCartId(), email)) {
    		result.put("result", false);
            result.put("msg", "购物车不存在此商品");
            return result;
		}
    	
        int line = disCartItemMapper.deleteByPrimaryKey(Integer.parseInt(cartItemId));
        Logger.debug("deleteDisCartItemById line-->" + line);
        result.put("result", (line == 1));
        result.put("msg", (line == 1)?"购物车不存在此商品":"删除商品成功");
        return result;
    }

    @Override
    public Map<String, Object> batchUpdateSelectStateOfCartItem(ProductSelectedStateParam param) {
        Logger.info("batchUpdateSelectStateOfCartItem params-->{}",param);
        Map<String, Object> result = Maps.newHashMap();
        
        // 获取所有购物车的商品
        List<DisCartItem> allItemsInCart = Lists.newArrayList();
        // 分销商的购物车
        DisCart disCart = disCartService.getUsableDisCart(param.getEmail());
        //有cart参数表示批量更新，无则表示单个更新
        if (param.isAllInCart()) {
        	Logger.info("更新购物车商品状态：获取购物车里的所有商品，购物车id为{}",disCart.getId());
            List<DisCartItem> itemList = disCartItemMapper.getDisCartItemsByCartId(disCart.getId());
            allItemsInCart.addAll(itemList);
        } else {
        	// 要检查此商品是不是这个分销商的
        	Logger.info("更新购物车商品状态：获取购物车里的单个商品，itemId为{}",param.getItemId());
        	DisCartItem item = disCartItemMapper.selectByPrimaryKey(param.getItemId());
        	// 此商品所属的购物车
        	Integer cartId = item.getCartId();
        	if (cartId.intValue() != disCart.getId().intValue()) {
        		Logger.info("更新购物车商品状态：商品{}不存在与分销商{}的购物车里【id为{}】，而是存在购物车【id为{}】",
        				param.getItemId(), param.getEmail(), disCart.getId(), cartId);
    			result.put("suc", false);
    			result.put("msg", "购物车不存在此商品");
                return result;
			}
        	allItemsInCart.add(item);
        }
        
        // 区分非卖品和可卖品
        List<DisCartDto> disCartDtoList = null;
        try {
        	disCartDtoList = setCartItemDetail(param.getEmail(), param.getDistributionMode(), allItemsInCart);
		} catch (IOException e) {
			Logger.info("更新购物车商品状态：获取异常");
			result.put("suc", false);
			result.put("msg", "选择商品失败");
            return result;
		}
        if (CollectionUtils.isEmpty(disCartDtoList)) {
        	Logger.info("更新购物车商品状态：购物车里没有商品");
        	result.put("suc", false);
			result.put("msg", "选择商品失败");
            return result;
		}
        
        Map<Integer, DisCartDto> itemId2DisCartDto  = Maps.uniqueIndex(disCartDtoList, dcd->dcd.getItemId());
        Lists.transform(disCartDtoList, item->item.getItemId());
		Map<Boolean, List<DisCartDto>> listBySalable = disCartDtoList.stream()
				.collect(Collectors.partitioningBy(dcd -> dcd.getSalable() == 1));
        // 可卖品
        List<DisCartDto> salableList = listBySalable.get(Boolean.TRUE);
        // 非卖品
        List<DisCartDto> unsalableList = listBySalable.get(Boolean.FALSE);
        
        // 非卖品不能被选中
        processNotSalableProsSelectedState(unsalableList);
        
        List<DisCartItem> toUpdateList = Lists.newArrayList();
        //有cart参数表示批量更新，无则表示单个更新
        if (param.isAllInCart()) {
            // 更改可卖品的选择状态
    		List<DisCartItem> updateSelectedList = Lists.transform(salableList, dcd->{
    			DisCartItem cartItem = new DisCartItem();
    	        cartItem.setId(dcd.getItemId());
    	        cartItem.setBselected(param.isSelected());
    	        return cartItem;
    		});
    		toUpdateList.addAll(updateSelectedList);
        } else {
        	// 更新单个的商品
            DisCartDto disCartDto = itemId2DisCartDto.get(param.getItemId());
            if (disCartDto.getSalable()!=1) {// 不是可卖的
            	result.put("suc", false);
    			result.put("msg", "此商品为非卖品");
                return result;
			}
            DisCartItem cartItem = new DisCartItem();
            cartItem.setId(param.getItemId());
            cartItem.setBselected(param.isSelected());
            toUpdateList.add(cartItem);
        }
        
        if (CollectionUtils.isEmpty(toUpdateList)) {
        	Logger.info("更新购物车商品状态：没有要更新状态的商品，直接返回=>更新购物车中商品的选中状态成功");
        	result.put("suc", false);
			result.put("msg", "请选择商品");
            return result;
		}
        
        int lines = disCartItemMapper.batchUpdateByPrimaryKeySelective(toUpdateList);
        Logger.info("batchUpdateByPrimaryKeySelective lines-->" + lines);
        result.put("suc", lines > 0);
		result.put("msg", lines > 0 ? "更新购物车中商品的选中状态成功" : "更新购物车中商品的选中状态失败");
        return result;
    }

	@Override
	public String deleteDisCartItemByEmail(String email) {
		Map<String, Object> result = Maps.newHashMap();
		if(StringUtils.isEmpty(email)){
			result.put("success", false);
    		result.put("msg", "数据有误");
    		return Json.toJson(result).toString();
		}
		
		int count = disCartItemMapper.deleteDisCartItemsByEmail(email);
		Logger.info("删除购物车数据{}",count);
		result.put("success", true);
		result.put("msg", "购物车数据删除成功！");
		return Json.toJson(result).toString();
	}

	@Override
	public Map<String, Object> updatePurchaseQties(String email, Integer itemId, Integer qty) {
		Map<String,Object> result = Maps.newHashMap();
		
		DisCartItem item = disCartItemMapper.selectByPrimaryKey(itemId);
		if (item==null) {
			result.put("result", false);
			result.put("msg", "购物车不存在此商品");
	        return result;
		}
		if (!isMyCart(item.getCartId(), email)) {
			result.put("result", false);
			result.put("msg", "购物车不存在此商品");
	        return result;
		}
		
		try {
			Map<String, ProStockDto> stoMap = getStock(item);
			ProStockDto proStockDto = stoMap.get(KeyUtil.getKey(item.getCsku(), item.getWarehouseId()));
			boolean stockEnough = proStockDto != null && proStockDto.getCloudInventory() != null && proStockDto.getCloudInventory() >= qty;
			if (!stockEnough) {
				result.put("result", false);
				result.put("msg", "云仓库存不足");
				return result;
			}

			item.setIqty(qty);
			boolean updateResult = disCartItemMapper.updateByPrimaryKeySelective(item) > 0;
			result.put("result", updateResult);
			result.put("msg", updateResult ? "更新商品购买数量成功" : "更新商品购买数量失败");
			return result;
		} catch (Exception e) {
			Logger.info("更新购物车数量异常", e);
			result.put("result", false);
			result.put("msg", "更新购物车数量异常");
			return result;
		}
	}

	@Override
	public Integer getItemsCount(String email) {
		DisCart cart = disCartMapper.getUsableDisCart(email);
		if (cart == null) {
			return 0;
		}else{
			Integer count = disCartItemMapper.getDisCartItemsCountByCartId(cart.getId());
			return count == null? 0 : count;
		}
	}

	private Map<String, ProStockDto> getStock(DisCartItem item)
			throws Exception, IOException, JsonParseException, JsonMappingException {
		Map<String, ProStockDto> stoMap = Maps.newHashMap();
		String proStr = httpService.getProStock(Lists.newArrayList(item.getCsku()));
		JsonNode proNode = Json.parse(proStr);
		ObjectMapper map = new ObjectMapper();
		List<ProStockDto> stockList = map.readValue(proNode.toString(), new TypeReference<List<ProStockDto>>() {
		});
		for (ProStockDto s : stockList) {
			if (s.getSku() != null && s.getWarehouseId() != null) {
				stoMap.put(KeyUtil.getKey(s.getSku(), s.getWarehouseId()), s);
			}
		}
		return stoMap;
	}

	/**
	 * 是我的购物车？
	 * @param cartId 购物车id
	 * @param email 当前用户
	 * @return true：说明购物车是email的；false：说明购物车不是email的
	 */
	private boolean isMyCart(Integer cartId, String email){
		DisCart cart = disCartMapper.selectByPrimaryKey(cartId);
		return Objects.equal(cart.getEmail(), email);
	}
}
