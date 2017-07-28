package services.product.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.elasticsearch.common.collect.Lists;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import dto.JsonResult;
import dto.product.CloudExpirationFormatResult;
import dto.product.InvetoryLockNumDto;
import dto.product.PageResultDto;
import dto.product.PostInventoryDetail;
import dto.product.PostInventoryLockDto;
import dto.product.ProductLite;
import dto.product.ProductSearchParamDto;
import dto.product.inventory.CloudLockPro;
import dto.product.inventory.CreateSaleOrderResult;
import dto.product.inventory.InventoryCloudLockDto;
import dto.product.inventory.InventoryLockStock;
import dto.product.inventory.LockResetDetialDto;
import dto.product.inventory.LockResetDto;
import dto.product.inventory.OrderDetail;
import dto.product.inventory.ProductCloudInventoryResult;
import dto.product.inventory.ProductInventoryDetail;
import dto.product.inventory.ProductMicroInventoryDetail;
import dto.product.inventory.SaleLockDetailDto;
import dto.product.inventory.SaleLockDto;
import dto.product.search.InventoryLockDeSearch;
import dto.product.search.InventoryLockSearch;
import entity.product.InventoryLock;
import entity.product.InventoryLockDetail;
import entity.product.InventoryOrder;
import entity.product.IvyOprecord;
import entity.product.IvyOptDetail;
import mapper.product.InventoryLockDetailMapper;
import mapper.product.InventoryLockMapper;
import mapper.product.InventoryOrderMapper;
import mapper.product.IvyOprecordMapper;
import mapper.product.IvyOptDetailMapper;
import play.Logger;
import play.libs.Json;
import services.base.utils.JsonFormatUtils;
import services.product.IHttpService;
import services.product.IInventoryLockService;
import services.product.IProductBaseService;
import services.product.ISequenceService;
import util.product.DateUtils;
import util.product.IDUtils;
import util.product.JsonCaseUtil;

/**
 * @author zbc
 * 2017年4月18日 下午3:29:10
 */
public class InventoryLockService implements IInventoryLockService {
	
	private static final String LOCK_NO = "LOCK_NO";
	
	@Inject
	private IHttpService httpService;
	@Inject
	private ISequenceService sequenceService;
	@Inject
	private IProductBaseService productBaseService;
	@Inject
	private InventoryLockMapper lockMapper;
	@Inject
	private InventoryLockDetailMapper lockDeMapper;
	@Inject
	private InventoryOrderMapper orderMapper;
	
	@Inject
	private IvyOprecordMapper recordMapper;
	
	@Inject
	private IvyOptDetailMapper optDetailMapper;
	
	//用于同步锁互斥
	private  static final byte[] lock = new byte[0];

	@Override
	public JsonResult<?> create(String string,String admin) {
		try {
			PostInventoryLockDto lockDto = JsonFormatUtils.jsonToBean(string, PostInventoryLockDto.class);
			if(lockDto == null){
				return JsonResult.newIns().result(false).msg("参数错误");
			}
			JsonNode memberResult = httpService.getMemberInfo(lockDto.getAccount());
			if (null == memberResult || !memberResult.get("suc").asBoolean()) {
				return JsonResult.newIns().result(false).msg("未查询到用户信息");
			}
			List<PostInventoryDetail> details = lockDto.getDetails();
			if(details == null || details.size()==0 ){
				return JsonResult.newIns().result(false).msg("详情不能为空");
			}
			JsonNode member = memberResult.get("result");
			InventoryLock lock = new InventoryLock(lockDto.getAccount(), IDUtils.getKALockNo(sequenceService.selectNextValue(LOCK_NO)),
					JsonCaseUtil.jsonToString(member.get("nickName")), JsonCaseUtil.jsonToString(member.get("salesmanErp")), 
					admin, lockDto.getRemark(), DateUtils.string2date(lockDto.getEstimatedShippingTime(),DateUtils.FORMAT_DATE_PAGE ));
			JsonResult<List<InventoryLockDetail>> resolutionRes = resolution(lock,details);
			if(resolutionRes.getResult()){
				//保存锁库数据
				lockMapper.insertSelective(lock);
				//保存锁库详情
				resolutionRes.getData().forEach(d->{
					d.setLockId(lock.getId());
					lockDeMapper.insertSelective(d);
				});
				return JsonResult.newIns().result(true).msg("创建KA锁库成功");
			}
			return resolutionRes;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.info("创建KA锁库记录异常"+e);
			return JsonResult.newIns().result(false).msg("创建KA锁库记录异常");
		}
	}

	/**
	 * 拆分数据
	 * @author zbc
	 * @param lock 
	 * @throws Exception 
	 * @since 2017年4月19日 上午11:32:45
	 */
	private JsonResult<List<InventoryLockDetail>> resolution(InventoryLock lock, List<PostInventoryDetail> details
			) throws Exception {
		// 锁定库存，分布到每个到期日期  调用接口查询到期日志对应库存,校验是否能够锁住，并把详情分布到每一个到期日期
		List<ObjectNode> getCloudParam = Lists.transform(details, p->{
			return 	Json.newObject().put("sku", p.getSku()).put("warehouseId", p.getWarehouseId()); 
		});
		JsonResult<List<InventoryLockDetail>> res = JsonResult.newIns();
		List<CloudExpirationFormatResult> cloudStocks = getCloud(null,getCloudParam);
		Map<String, ProductLite> proMap = getProMap(lock.getAccount(), Lists.transform(details, e->{return e.getSku();}),null);
		ProductLite product;String key;
		List<InventoryLockDetail> lockDes = Lists.newArrayList();
		Map<Boolean,List<PostInventoryDetail>> detailsMap = details.stream().collect(Collectors.partitioningBy(d->d.getExpirationDate()!= null));
		List<PostInventoryDetail> subDetails = null;
		int leftNum, num;
		//指定到期日期
		subDetails =  detailsMap.get(true);
		if(subDetails != null && subDetails.size()>0){
			Map<String,CloudExpirationFormatResult> cloudStockExMap = Maps.newHashMap();
			for(CloudExpirationFormatResult c:cloudStocks){
				cloudStockExMap.put(key(c.getSku(),c.getWarehouseId(),c.getExpirationDate()), c);
			}
			CloudExpirationFormatResult cloud;
			for(PostInventoryDetail de:subDetails){
				cloud = cloudStockExMap.get(key(de.getSku(),de.getWarehouseId(),de.getExpirationDate()));
				product = proMap.get(key(de.getSku(),de.getWarehouseId()));
				leftNum = de.getNum();
				if(cloud == null){
					return res.result(false).msg(de.getSku()+"未查询到云仓库存信息");
				}
				if(product == null){
					return res.result(false).msg("商品"+de.getSku()+"不存在");
				}
				if(cloud.getStock()>= de.getNum()){
					num = de.getNum();
					cloud.setStock(cloud.getStock() - de.getNum());
					lockDes.add(new InventoryLockDetail(de.getSku(),
							DateUtils.string2date(cloud.getExpirationDate(), DateUtils.FORMAT_DATE_PAGE), num,
							de.getWarehouseId(), product.getWarehouseName(), product.getCtitle(), product.getInterBarCode(),
							num, null, lock.getLockNo()));
				}else{
					return res.result(false).msg("商品"+key(de.getSku(),de.getExpirationDate())+"库存缺少"+ (de.getNum()-cloud.getStock()));
				}
			}
		}
		//不指定到期日期
		subDetails =  detailsMap.get(false);
		if(subDetails != null && subDetails.size()>0){
			//sku 仓库id 分组
			Map<String,List<CloudExpirationFormatResult>> cloudStockMap = 
					cloudStocks.stream().collect(Collectors.groupingBy(e->key(e.getSku(),e.getWarehouseId())));
			List<CloudExpirationFormatResult> clouds;
			for(PostInventoryDetail de:subDetails){
				key = key(de.getSku(),de.getWarehouseId());
				clouds = cloudStockMap.get(key);
				product = proMap.get(key);
				leftNum = de.getNum();
				if(clouds == null){
					return res.result(false).msg(de.getSku()+"未查询到云仓库存信息");
				}
				if(product == null){
					return res.result(false).msg("商品"+de.getSku()+"不存在");
				}
				for(CloudExpirationFormatResult cloud:clouds){
					if(leftNum <= 0){
						break;
					}else if(leftNum <= cloud.getStock() ){
						num = leftNum;
					}else{
						num = cloud.getStock();
					}
					leftNum -= num;
					lockDes.add(new InventoryLockDetail(de.getSku(),
							DateUtils.string2date(cloud.getExpirationDate(), DateUtils.FORMAT_DATE_PAGE), num,
							de.getWarehouseId(), product.getWarehouseName(), product.getCtitle(), product.getInterBarCode(),
							num, null, lock.getLockNo()));
				}
				if(leftNum > 0){
					return res.result(false).msg("商品"+de.getSku()+"库存缺少"+leftNum);
				}
			}
		}
		return res.result(true).data(lockDes);
	}

	@SuppressWarnings("unchecked")
	private Map<String, ProductLite> getProMap(String email, Collection<String> skus,Integer model) {
		ProductSearchParamDto searchDto = new ProductSearchParamDto();
		searchDto.setEmail(email);
		searchDto.setSkuList(Lists.newArrayList(skus));
		//查询商品数据
		List<ProductLite> list = (List<ProductLite>) productBaseService.products(searchDto).getResult();
		Map<String,ProductLite> proMap = Maps.uniqueIndex(list, e->key(e.getCsku(),e.getWarehouseId()));
		return proMap;
	}
	@Override
	public JsonResult<?> get(Integer id) {
		InventoryLock lock = lockMapper.selectByPrimaryKey(id);
		if(lock != null){
			return JsonResult.newIns().result(true).data(lock);
		}
		return JsonResult.newIns().result(false).msg("锁库数据不存在");
	}

	@Override
	public JsonResult<?> release(Integer id) {
		InventoryLock lock = lockMapper.selectByPrimaryKey(id);
		if(lock != null){
			lock.setStatus(InventoryLock.BE_RELEASE);
			lock.setIsLeftStock(InventoryLock.BE_RELEASE);
			lockMapper.updateByPrimaryKeySelective(lock);
			return JsonResult.newIns().result(true).msg("释放锁库成功");
		}
		return JsonResult.newIns().result(false).msg("锁库数据不存在");
	}

	@Override
	public JsonResult<?> detailPages(String string) {
		try {
			InventoryLockDeSearch search = JsonFormatUtils.jsonToBean(string, InventoryLockDeSearch.class);
			if(search != null){
				return JsonResult.newIns().result(true).data(new PageResultDto<InventoryLockDetail>(
						search.getRows(),
						lockDeMapper.pageCount(search),
						search.getPage(), 
						lockDeMapper.pageSearch(search)));
			}
			return JsonResult.newIns().result(false).msg("分页查询参数异常");
		} catch (Exception e) {
			Logger.info("分页查询锁库详情异常{}",e);
			return JsonResult.newIns().result(false).msg("分页查询锁库详情异常");
		}
	}

	@Override
	public JsonResult<?> page(String string) {
		try {
			InventoryLockSearch search = JsonFormatUtils.jsonToBean(string, InventoryLockSearch.class);
			if(search != null){
				return JsonResult.newIns().result(true).data(new PageResultDto<InventoryLock>(
						search.getRows(),
						lockMapper.pageCount(search),
						search.getPage(), 
						lockMapper.
						pageSearch(search)));
			}
			return JsonResult.newIns().result(false).msg("分页查询参数异常");
		} catch (Exception e) {
			Logger.info("分页查询锁库详情异常{}",e);
			return JsonResult.newIns().result(false).msg("分页查询锁库详情异常");
		}
	}
	@Override
	public List<ProductLite> substock(String email,List<ProductLite>  list){
		try {
			if(list.size()>0){
				List<String> skus = Lists.transform(list, p->{return p.getCsku();});
				Map<String, InvetoryLockNumDto> lockNumMap = getLockNumMap(email, skus);
				InvetoryLockNumDto lockNum;int stock;
				for(ProductLite product:list){
					lockNum = lockNumMap.get(key(product.getCsku(),product.getWarehouseId()));
					if(lockNum != null){
						stock = product.getStock() -lockNum.getSubstock();
						product.setStock(stock>0?stock:0);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.info("扣减KA锁库异常:{}",e);
		}
		return list;
	}

	/**
	 * @author zbc
	 * @since 2017年4月20日 下午5:34:36
	 */
	private Map<String, InvetoryLockNumDto> getLockNumMap(String email, List<String> skus) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("account", email);
		param.put("skus", skus);
		Map<String,InvetoryLockNumDto> lockNumMap = Maps.uniqueIndex(lockDeMapper.querySubstock(param), e->key(e.getSku(),e.getWarehouseId()));
		return lockNumMap;
	}

	@Override
	public ProductLite substock(String email, ProductLite product) {
		try {
			Map<String, InvetoryLockNumDto> lockNumMap = getLockNumMap(email, Lists.newArrayList(product.getCsku()));
			InvetoryLockNumDto lockNum = lockNumMap.get(key(product.getCsku(),product.getWarehouseId()));
			if(lockNum != null){
				int stock = product.getStock() -lockNum.getSubstock();
				product.setStock(stock>0?stock:0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.info("扣减KA锁库异常:{}",e);
		}
		return product;
	}

	@Override
	public List<CloudExpirationFormatResult> dealCloud(String string) {
		try {
			JsonNode node = Json.parse(string);
			String email = node.get("email").asText();
			ObjectMapper map = new ObjectMapper();
			List<ObjectNode>  getCloudParam = map.readValue(node.get("skuWarehouseIdArray").toString(), new TypeReference<List<ObjectNode>>() {});
			return getCloud(email,getCloudParam);
		} catch (Exception e) {
			Logger.info("KA锁库处理云仓库存异常{}",e);
			return Lists.newArrayList();
		}
	}
	
	/**
	 * 
	 * @author zbc
	 * @since 2017年4月21日 上午11:08:47
	 */
	public  List<CloudExpirationFormatResult> getCloud(String email,List<ObjectNode> getCloudParam)
			throws IOException, JsonParseException, JsonMappingException, JsonProcessingException {
		ObjectMapper map = new ObjectMapper();
		List<CloudExpirationFormatResult> cloudStocks = map.readValue(httpService.getCloudProductsExpirationDate(getCloudParam).toString(), new TypeReference<List<CloudExpirationFormatResult>>(){});
		//change by zbc 只查询云仓库存大于0的商品
		List<CloudExpirationFormatResult> newCloudStocks = Lists.newArrayList();
		if(cloudStocks.size()>0){
			Map<String,Object> param = Maps.newHashMap();
			param.put("account", email);
			param.put("skus", Lists.transform(getCloudParam, e->{return e.get("sku").asText();}));
			List<InvetoryLockNumDto> locks = lockDeMapper.querySubstockByExpirationDate(param);
			Map<String,InvetoryLockNumDto> lockMap = Maps.uniqueIndex(locks, l->key(l.getSku(),l.getWarehouseId(),l.getExpirationDate()));
			InvetoryLockNumDto lock = null;
			for(CloudExpirationFormatResult cloud:cloudStocks){
				lock = lockMap.get(key(cloud.getSku(),cloud.getWarehouseId(),cloud.getExpirationDate()));
				if(lock != null){
					cloud.setStock(cloud.getStock() - lock.getSubstock());
				}
				if(cloud.getStock() > 0){
					newCloudStocks.add(cloud);
				}
			}
		}
		return newCloudStocks;
	}
	
	private String key(Object... keys){
		StringBuilder builer = new StringBuilder();
		for(int i = 0;i<keys.length;i++){
			builer.append(keys[i]);
			if(i < keys.length - 1){
				builer.append("||");
			}
		}
		return builer.toString();
	}

	@Override
	public ProductCloudInventoryResult<ProductInventoryDetail> cloudlock(String string) {
		synchronized (lock) {
			try {
				ObjectMapper map = new ObjectMapper();
				InventoryCloudLockDto cloudLockDto = map.readValue(string, InventoryCloudLockDto.class);
				JsonNode node = httpService.getOrderDetails(cloudLockDto.getOrderNo());
				// 查询改锁库数据是否已经存在如果存在，则不往下走
				// 更新订单详情
				List<InventoryLockDetail> uLockDes = Lists.newArrayList();
				// 订单使用记录
				List<InventoryOrder> orders = Lists.newArrayList();
				// 库存短缺商品
				List<ProductInventoryDetail> sortList = Lists.newArrayList();
				List<CloudLockPro> newPros = Lists.newArrayList();
				ProductCloudInventoryResult<ProductInventoryDetail> res;
				if (node != null && node.size() > 0) {
					// 选赠品
					if (cloudLockDto.getChange() != null && cloudLockDto.getChange().size() > 0) {
						// 指定到期日期
						res = dealCloudLock(cloudLockDto, cloudLockDto.getChange(), uLockDes, orders, newPros,
								sortList);
						if (!res.isResult()) {
							return res;
						}
						cloudLockDto.setChange(newPros);
						// 均摊价map
						Map<String, Object> capFeeMap = Maps.newHashMap();
						for (CloudLockPro pro : cloudLockDto.getPros()) {
							capFeeMap.put(key(pro.getSku(), pro.getWarehouseId()), pro.getCapfee());
						}
						List<OrderDetail> orderDetails = map.readValue(node.toString(), new TypeReference<List<OrderDetail>>() {
						});
						List<CloudLockPro> list = Lists.newArrayList(Lists.transform(orderDetails, d -> {
							return new CloudLockPro(d);
						}));
						list.addAll(newPros);
						list.forEach(p -> {
							p.setCapfee((Double) capFeeMap.get(key(p.getSku(), p.getWarehouseId())));
						});
						// 更新所有详情
					}
					return map.readValue(httpService.cloudLock(cloudLockDto).toString(),
							new TypeReference<ProductCloudInventoryResult<ProductInventoryDetail>>() {
							});
				}
				List<InventoryOrder> uOrders = Lists.newArrayList();
				if (cloudLockDto.getSaleOrderNo() != null) {
					// 如果包含发货单号，则说明之前已经发货了
					// 更新锁库详情
					uOrders = orderMapper.selectByOrderNo(cloudLockDto.getSaleOrderNo());
					uOrders.forEach(o -> {
						o.setOrderNo(cloudLockDto.getOrderNo());
					});
				} else {
					// 指定到期日期
					res = dealCloudLock(cloudLockDto, cloudLockDto.getPros(), uLockDes, orders, newPros, sortList);
					if (!res.isResult()) {
						return res;
					}
					cloudLockDto.setPros(newPros);
				}
				//赠品增量处理逻辑
				if (sortList.size() > 0) {
					return new ProductCloudInventoryResult<>(false, "库存不足", sortList, null);
				}
				JsonNode lockRes = httpService.cloudLock(cloudLockDto);
				ProductCloudInventoryResult<ProductInventoryDetail> result = map.readValue(lockRes.toString(),
						new TypeReference<ProductCloudInventoryResult<ProductInventoryDetail>>() {
						});
				if (result.isResult()) {
					uLockDes.forEach(de -> {
						lockDeMapper.updateByPrimaryKeySelective(de);
					});
					orders.forEach(od -> {
						orderMapper.insertSelective(od);
					});
					// 更新
					uOrders.forEach(od -> {
						orderMapper.updateByPrimaryKeySelective(od);
					});
					autoChangeIsLeftStock();
				}
				return result;

			} catch (Exception e) {
				Logger.info("KA云仓锁库异常：{}", e);
				return new ProductCloudInventoryResult<>(false, "KA云仓锁库异常");
			}
		}
	}
	/**
	 * 
	 * @author zbc
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @since 2017年4月24日 下午7:55:11
	 */
	private ProductCloudInventoryResult<ProductInventoryDetail> dealCloudLock(InventoryCloudLockDto cloudLockDto,List<CloudLockPro> pros, List<InventoryLockDetail> uLockDes,
			List<InventoryOrder> orders, List<CloudLockPro> newPros, List<ProductInventoryDetail> sortList
			) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		List<CloudExpirationFormatResult> cloudStocks = getCloud(cloudLockDto.getAccount(), Lists.transform(cloudLockDto.getPros(), e->{
				return Json.newObject().put("sku",e.getSku()).put("warehouseId", e.getWarehouseId());
		}));
		Map<Boolean,List<CloudLockPro>> prosMap = pros.stream().collect(Collectors.partitioningBy(e->e.getExpirationDate() != null));
		//指定到期日期的商品
		List<CloudLockPro> subPros = prosMap.get(true);
		//指定到期日期，拆分后的新详情
		Map<String,CloudExpirationFormatResult> cloudExMap = Maps.newHashMap();
		for(CloudExpirationFormatResult c:cloudStocks){
			cloudExMap.put(key(c.getSku(),c.getWarehouseId(),c.getExpirationDate()), c);
		}
		Date now = new Date();
		//锁库详情
		List<InventoryLockDetail>  lockDes = lockDeMapper.getDetail(cloudLockDto.getAccount()); 
		//对应到期日期的锁库详情map
		Map<String,List<InventoryLockDetail>> lockDeExMap = lockDes.stream().collect(Collectors.groupingBy(e->key(e.getSku(),e.getWarehouseId(),e.expirationDateStr())));
		//锁库详情
		List<InventoryLockDetail> subLockDes;
		String key;
		CloudExpirationFormatResult cloud = null;
		if(subPros != null && subPros.size()>0 ){
			for(CloudLockPro pro:subPros){
				key = key(pro.getSku(),pro.getWarehouseId(),pro.getExpirationDate());
				cloud = cloudExMap.get(key);
				if(cloud == null){
					return new  ProductCloudInventoryResult<>(false, pro.getSku()+"库存未查到");
				}
				if(cloud.getStock()>= pro.getQty()){
					cloud.setStock(cloud.getStock() - pro.getQty());
					subLockDes = lockDeExMap.get(key);
					if(subLockDes != null && subLockDes.size()>0){
						// 更新ＫＡ锁库详情
						generOrders(cloudLockDto,pro,subLockDes,orders,uLockDes,now);
					}
					newPros.add(pro);
				}else{
					sortList.add(new ProductInventoryDetail(cloud, pro.getQty()-cloud.getStock()));
				}
			}
		}
		//未指定到期日期
		subPros = prosMap.get(false);
		CloudLockPro newPro = null;
		if(subPros != null && subPros.size()>0 ){
			List<CloudExpirationFormatResult> subClouds = null;
			//系统自动分配
			Map<String,List<CloudExpirationFormatResult>> cloudMap = cloudStocks.stream().collect(Collectors.groupingBy(c->key(c.getSku(),c.getWarehouseId())));
			for(CloudLockPro pro:subPros){
				//指定到期日期
				subClouds = cloudMap.get(key(pro.getSku(),pro.getWarehouseId()));
				if(subClouds != null && subClouds.size()>0){
					int leftNum = pro.getQty(),num;
					for(CloudExpirationFormatResult c:subClouds){
						cloud = c;
						//解决锁库数量为零
						if(c.getStock() <= 0){
							continue;
						}else if(leftNum <=0){
							break;
						}else if(leftNum < c.getStock()){
							num = leftNum;
						}else{
							num = c.getStock();
						}
						//重置数量，防止赠品到期日期数量错误
						c.setStock(c.getStock() - num);
						newPro = new CloudLockPro();
						BeanUtils.copyProperties(pro, newPro);
						newPro.setExpirationDate(c.getExpirationDate());
						newPro.setQty(num);
						subLockDes = lockDeExMap.get(key(newPro.getSku(),newPro.getWarehouseId(),newPro.getExpirationDate()));
						if(subLockDes != null && subLockDes.size()>0){
							// 更新ＫＡ锁库详情
							generOrders(cloudLockDto,newPro,subLockDes,orders,uLockDes,now);
						}
						newPros.add(newPro);
						leftNum -= num;
					}
					if(leftNum > 0){
						sortList.add(new ProductInventoryDetail(cloud, pro.getQty()-cloud.getStock()));
					}
				}else{
					return  new  ProductCloudInventoryResult<>(false, pro.getSku()+"库存未查到");
				}
			}
		}
		return  new  ProductCloudInventoryResult<>(true,null);
	}

	/**
	 * 生成订单锁库记录，以及更新锁库详情
	 * @author zbc
	 * @since 2017年4月21日 下午7:42:44
	 */
	private void generOrders(InventoryCloudLockDto cloudLockDto,CloudLockPro pro,
			List<InventoryLockDetail> lockDes,List<InventoryOrder> orders,List<InventoryLockDetail>  uLockDes,Date now) {
		Integer leftNum = pro.getQty(), num;
		for(InventoryLockDetail lockDe:lockDes){
			if(lockDe.getLeftNum() <=0 ){
				continue;
			}else if(leftNum <= 0){
				break;
			}else if(leftNum < lockDe.getLeftNum()){
				num = leftNum;
			}else{
				num = lockDe.getLeftNum();
			}
			lockDe.setLeftNum(lockDe.getLeftNum() - num);
			uLockDes.add(lockDe);
			leftNum -= num;
			orders.add(new InventoryOrder(now,num, cloudLockDto.getOrderNo(), lockDe, pro,
					cloudLockDto.getAccount()));
		}
	}
	
	
	private void generOrders(SaleLockDto microOut,SaleLockDetailDto pro,
			List<InventoryLockDetail> lockDes,List<InventoryOrder> orders,List<InventoryLockDetail>  uLockDes,Date now) {
		Integer leftNum = pro.getQty(), num;
		for(InventoryLockDetail lockDe:lockDes){
			if(lockDe.getLeftNum() <= 0){
				continue;
			}else if(leftNum <= 0){
				break;
			}else if(leftNum < lockDe.getLeftNum()){
				num = leftNum;
			}else{
				num = lockDe.getLeftNum();
			}
			lockDe.setLeftNum(lockDe.getLeftNum() - num);
			uLockDes.add(lockDe);
			leftNum -= num;
			orders.add(new InventoryOrder(now,num, microOut.getOrderNo(), lockDe, pro,
					microOut.getAccount()));
		}
	}

	@Override
	public CreateSaleOrderResult microlock(String string) {
		synchronized (lock) {
			try {

				// 查询微仓库存
				SaleLockDto microOut = JsonFormatUtils.jsonToBean(string, SaleLockDto.class);
				// 判断是否已经锁库过
				// 指定采购单,则不走云仓锁库，所以不做处理
				// 更新订单详情
				List<InventoryLockDetail> uLockDes = Lists.newArrayList();
				// 订单使用记录
				List<InventoryOrder> orders = Lists.newArrayList();
				if (microOut.getPurchaseNo() == null) {
					List<SaleLockDetailDto> newPros = Lists.newArrayList();
					// 将微仓部分和缺货采购部分拆分，微仓部分按照逻辑赋予到期日期，采购单部分
					// 根据是否指定到期日期分组
					// 需要云仓锁库
					List<SaleLockDetailDto> cloudLocks = Lists.newArrayList();
					if (microOut.getLockCloud() == null || !microOut.getLockCloud()) {
						// KA 根据微仓库存，更新详情
						dealMircros(microOut, newPros, cloudLocks);
					} else {
						cloudLocks = microOut.getPros();
					}
					// 如果需要发云仓数量大于0
					if (!dealClouds(microOut, newPros, cloudLocks, uLockDes, orders)) {
						return new CreateSaleOrderResult(3, null, null);
					}
					microOut.setPros(newPros);
				}
				CreateSaleOrderResult res = JsonFormatUtils.jsonToBean(httpService.microLock(microOut).toString(),
						CreateSaleOrderResult.class);
				if (res.getType() != null && (res.getType() == 2 || res.getType() == 5)) {
					// ＫＡ锁库处理
					// 批量更新
					uLockDes.forEach(de -> {
						lockDeMapper.updateByPrimaryKeySelective(de);
					});
					// 批量插入
					orders.forEach(od -> {
						orderMapper.insertSelective(od);
					});
					//如果可用库存为0，自动更新为已释放
					autoChangeIsLeftStock();
				}
				return res;

			} catch (Exception e) {
				Logger.info("KA微仓锁库异常:{}", e);
				return new CreateSaleOrderResult(4, null, null);
			}
		}
		// 校验库存是否充足，如果不足 拦住
		// 如果充足，进行数据处理，然后锁库
	}

	/**
	 * @author zbc
	 * @since 2017年4月24日 下午2:15:46
	 */
	private boolean dealClouds(SaleLockDto microOut, List<SaleLockDetailDto> newPros,
			List<SaleLockDetailDto> cloudLocks,List<InventoryLockDetail> uLockDes,List<InventoryOrder> orders)
			throws IOException, JsonParseException, JsonMappingException, JsonProcessingException {
		Integer warehouseId = microOut.getWarehouseId();
		if(cloudLocks.size()>0){
			//锁库详情
			List<InventoryLockDetail>  lockDes = lockDeMapper.getDetail(microOut.getAccount()); 
			//对应到期日期的锁库详情map
			Map<String,List<InventoryLockDetail>> lockDeExMap = lockDes.stream().collect(Collectors.groupingBy(e->key(e.getSku(),e.getWarehouseId(),e.expirationDateStr())));
			//锁库详情
			List<InventoryLockDetail> subLockDes;
			//分配云仓库存
			List<CloudExpirationFormatResult> cloudStocks = getCloud(microOut.getAccount(), Lists.transform(cloudLocks, p->{
				return Json.newObject().put("sku",p.getSku()).put("warehouseId",warehouseId);
			}));
			Map<Boolean,List<SaleLockDetailDto>> proMap = cloudLocks.stream().collect(Collectors.groupingBy(c->c.getExpirationDate() != null));
			//指定到期日期
			List<SaleLockDetailDto> subPros = proMap.get(true);
			String key = null;
			Date now = new Date();
			if(subPros != null && subPros.size()>0){
				Map<String,CloudExpirationFormatResult> cloudExMap = Maps.newHashMap();
				for(CloudExpirationFormatResult c:cloudStocks){
					cloudExMap.put(key(c.getSku(),c.getWarehouseId(),c.getExpirationDate()), c);
				}
				CloudExpirationFormatResult cloud = null;
				for(SaleLockDetailDto pro:subPros){
					key = key(pro.getSku(),warehouseId,pro.getExpirationDate());
					cloud = cloudExMap.get(key);
					if(cloud != null && cloud.getStock() >= pro.getQty()){
						cloud.setStock(cloud.getStock() - pro.getQty());
						// 更新KA锁库详情，后续云仓锁库替换掉该详情
						newPros.add(pro);
						subLockDes = lockDeExMap.get(key);
						if(subLockDes != null && subLockDes.size() > 0){
							generOrders(microOut, pro, subLockDes, orders, uLockDes, now);
						}
					}else{
						return false;
					}
				}
			}
			//未指定到期日期
			subPros = proMap.get(false);
			if(subPros != null && subPros.size()>0 ){
				List<CloudExpirationFormatResult> subClouds = null;
				//系统自动分配
				Map<String,List<CloudExpirationFormatResult>> cloudMap = cloudStocks.stream().collect(Collectors.groupingBy(c->key(c.getSku(),c.getWarehouseId())));
				for(SaleLockDetailDto pro:subPros){
					key = key(pro.getSku(),warehouseId);
					//指定到期日期
					subClouds = cloudMap.get(key);
					if(subClouds != null && subClouds.size()>0){
						int leftNum = pro.getQty(),num;
						for(CloudExpirationFormatResult cloud:subClouds){
							if(cloud.getStock() <= 0){
								continue;
							}else if(leftNum <=0){
								break;
							}else if(leftNum < cloud.getStock()){
								num = leftNum;
							}else{
								num = cloud.getStock();
							}
							cloud.setStock(cloud.getStock() - num);
							//更新KA锁库详情，后续云仓锁库替换掉该详情
							pro = new SaleLockDetailDto(pro, num, cloud.getExpirationDate());
							newPros.add(pro);
							subLockDes = lockDeExMap.get(key(pro.getSku(),warehouseId,pro.getExpirationDate()));
							if(subLockDes != null && subLockDes.size() > 0){
								generOrders(microOut, pro, subLockDes, orders, uLockDes, now);
							}
							leftNum -= num;
						}
						if(leftNum > 0){
							return false;
						}
					}else{
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * KA 根据微仓库存，更新详情
	 * @author zbc
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @since 2017年4月24日 上午11:40:38
	 */
	private void dealMircros(SaleLockDto microOut,List<SaleLockDetailDto> newPros, List<SaleLockDetailDto> cloudLocks) throws JsonProcessingException, IOException {
		List<ProductMicroInventoryDetail> subMicros;
		ObjectMapper map = new ObjectMapper();
		List<SaleLockDetailDto> pros = microOut.getPros();
		Integer warehouseId = microOut.getWarehouseId();
		JsonNode microNode = httpService.getMicroProductsExpirationDate(microOut.getAccount(), Lists.transform(microOut.getPros(), p->{
			return Json.newObject().put("sku",p.getSku()).put("warehouseId",warehouseId);
		}));
		List<ProductMicroInventoryDetail> micros = map.readValue(microNode.toString(), new TypeReference<List<ProductMicroInventoryDetail>>() {});
		if(micros.size()>0){
			Map<Boolean,List<SaleLockDetailDto>> proMap =  pros.stream().collect(Collectors.partitioningBy(p->p.getExpirationDate() != null));
			Map<String,List<ProductMicroInventoryDetail>> microsExMap = micros.stream().collect(Collectors.groupingBy(e->key(e.getSku(),e.getWarehouseId(),e.expirationDateStr())));
			String key;int leftNum,num;
			List<SaleLockDetailDto> subPros = proMap.get(true);
			if(subPros != null && subPros.size() > 0){
				for(SaleLockDetailDto pro:subPros){
					key = key(pro.getSku(),warehouseId,pro.getExpirationDate());
					subMicros = microsExMap.get(key);
					leftNum = pro.getQty();
					if(subMicros != null && subMicros.size() > 0){
						for(ProductMicroInventoryDetail micro:subMicros){
							if(micro.getStock() <= 0){
								continue;
							}else if(leftNum <= 0){
								break;
							}else if(leftNum < micro.getStock()){
								num = leftNum;
							}else{
								num = micro.getStock();
							}
							//扣除库存
							micro.setStock(micro.getStock() - num);
							//消耗数量
							leftNum -= num;
							newPros.add(new SaleLockDetailDto(pro,num));
						}
					}
					//剩余数量大于零说明缺货
					if(leftNum > 0){
						cloudLocks.add(new SaleLockDetailDto(pro,leftNum));
					}
				}
			}
			//不指定到期日期
			subPros = proMap.get(false);
			if(subPros != null && subPros.size() > 0){
				Map<String,List<ProductMicroInventoryDetail>> microsMap = micros.stream().collect(Collectors.groupingBy(e->key(e.getSku(),e.getWarehouseId())));
				//自动分配到期日期
				for(SaleLockDetailDto pro:subPros){
					key = key(pro.getSku(),warehouseId);
					subMicros = microsMap.get(key);
					leftNum = pro.getQty();
					if(subMicros != null && subMicros.size() > 0){
						for(ProductMicroInventoryDetail micro:subMicros){
							if(micro.getStock() <= 0){
								continue;
							}else if(leftNum <= 0){
								break;
							}else if(leftNum < micro.getStock()){
								num = leftNum;
							}else{
								num = micro.getStock();
							}
							//扣除库存
							micro.setStock(micro.getStock() - num);
							//消耗数量
							leftNum -= num;
							newPros.add(new SaleLockDetailDto(pro,num,micro.expirationDateStr()));
						}
					}
					//剩余数量大于零说明缺货
					if(leftNum > 0){
						cloudLocks.add(new SaleLockDetailDto(pro,leftNum));
					}
				}
			}
		}else{
			cloudLocks.addAll(pros);
		}
	}
	
	/**
	 * 自动更新是否剩余库存
	 * @author zbc
	 * @since 2017年4月25日 上午10:09:23
	 */
	private void autoChangeIsLeftStock(){
		lockDeMapper.autoChange();
	}

	@Override
	public InventoryLockStock stock(String sku, Integer wareId) {
		InventoryLockStock stock = lockDeMapper.getLockStock(sku, wareId);
		if(stock == null){
			stock = new InventoryLockStock(sku, wareId, 0); 
		}
		return stock;
	}

	@Override
	public JsonResult<?> cloudSelectedExpirationDates(String string) {
		try {
			JsonNode node = Json.parse(string);
			JsonNode memberResult = httpService.getMemberInfo(JsonCaseUtil.jsonToString(node.get("email")));
			if (null == memberResult || !memberResult.get("suc").asBoolean()) {
				return JsonResult.newIns().result(false).msg("未查询到用户信息");
			}
			JsonNode member = memberResult.get("result");
			Integer model = JsonCaseUtil.jsonToInteger(member.get("distributionMode"));
			List<CloudExpirationFormatResult> clouds = getCloud(null,
					new ObjectMapper().readValue(node.get("pros").toString(), new TypeReference<List<ObjectNode>>() {
					}));
			if(clouds.size()>0){
				List<ProductLite> products = Lists.newArrayList();
				ProductLite product = null;
				ProductLite newPro = null;
				Map<String,ProductLite> proMap = getProMap(null,
						Sets.newHashSet(Lists.transform(clouds, c->{return c.getSku();})),model);
				for(CloudExpirationFormatResult cloud:clouds){
					product = proMap.get(key(cloud.getSku(),cloud.getWarehouseId()));
					if(product == null){
						return JsonResult.newIns().result(false).msg("商品："+cloud.getSku()+"未查到");
					}
					if(cloud.getStock() >0 ){
						newPro = new ProductLite();
						BeanUtils.copyProperties(product, newPro);
						newPro.setStock(cloud.getStock());
						newPro.setExpirationDate(cloud.getExpirationDate());
						products.add(newPro);
					}
				}
				return JsonResult.newIns().result(true).data(products);
			}else{
				return JsonResult.newIns().result(false).msg("未查询到库存信息");
			}
		} catch (Exception e) {
			Logger.info("KA锁库获取各到期日期商品异常{}",e);
			return JsonResult.newIns().result(false).msg("KA锁库获取各到期日期商品异常");
		}
	}

	@Override
	public JsonResult<?> reSetInventoryLock(String string, String adminAccount) {
		synchronized (lock) {
			try {
				LockResetDto reset = JsonFormatUtils.jsonToBean(string, LockResetDto.class);
				if(reset == null){
					return JsonResult.newIns().result(false).msg("参数格式错误");
				}
				List<LockResetDetialDto> details = reset.getDetails();
				Integer lockId = reset.getLockId();
				String remark = reset.getRemark();
				InventoryLock lock = lockMapper.selectByPrimaryKey(lockId);
				if (lock == null) {
					return JsonResult.newIns().result(false).msg("锁库信息不存在");
				}
				//更新详情集合
				List<InventoryLockDetail> ulockDetails = Lists.newArrayList();
				//释放库存集合
				List<IvyOptDetail> optDetails = Lists.newArrayList();
				//所有详情集合
				List<InventoryLockDetail> lockDetails = lockDeMapper.getDetailByLockId(lockId);
				//释放全部
				if(reset.getIsAll() != null && reset.getIsAll() ){
					lockDetails = lockDetails.stream().filter(e->{return e.getLeftNum()>0;}).collect(Collectors.toList());
					if(lockDetails.size() <= 0){
						autoChangeIsLeftStock();
						return JsonResult.newIns().result(false).msg("没有剩余库存可以释放");
					}
					lockDetails.forEach(d->{
						optDetails.add(new IvyOptDetail(d, d.getLeftNum()));
						d.setLeftNum(0);
						ulockDetails.add(d);
					});
				}else{
					if (details.size() <= 0) {
						return JsonResult.newIns().result(false).msg("详情不能为空");
					}
					if(lock.getStatus() == InventoryLock.BE_RELEASE){
						return JsonResult.newIns().result(false).msg("该锁库已经释放，不能再次释放");
					}
					int size = details.stream().map(detail->detail.getDetailId()).collect(Collectors.toCollection(HashSet::new)).size();
					if(size < details.size()){
						return JsonResult.newIns().result(false).msg("释放锁库详情不能重复");
					}
					Map<Integer,InventoryLockDetail> lockDeMap = Maps.uniqueIndex(lockDetails, e->e.getId());
					InventoryLockDetail uDetail = null;
					for(LockResetDetialDto de:details){
						uDetail = lockDeMap.get(de.getDetailId());
						if(uDetail == null){
							return JsonResult.newIns().result(false).msg("详情id:["+de.getDetailId()+"]不存在");
						}
						if(de.getNum()== null|| de.getNum()<=0 ||de.getNum()>uDetail.getLeftNum()){
							return JsonResult.newIns().result(false).msg("详情:["+uDetail.getSku()+"]["+uDetail.getExpirationDate()+"]释放数量不正确：不能为空，小于等于0，或者大于剩余数量");
						}
						uDetail.setLeftNum(uDetail.getLeftNum() - de.getNum());
						ulockDetails.add(uDetail);
						optDetails.add(new IvyOptDetail(uDetail, de.getNum()));
					}
				}
				//更新库存
				ulockDetails.forEach(d->{
					lockDeMapper.updateByPrimaryKeySelective(d);
				});
				IvyOprecord record = new IvyOprecord(lock.getId(),adminAccount, new Date(), "释放库存", remark);
				if(recordMapper.insertSelective(record)>0){
					optDetails.forEach(d->{
						d.setOprecordId(record.getId());
						optDetailMapper.insertSelective(d);
					});
				}
				//如果可用库存为0，自动更新为已释放
				autoChangeIsLeftStock();
				return JsonResult.newIns().result(true).msg("释放库存成功");
			} catch (Exception e) {
				Logger.info("释放锁库异常：{}", e);
				return JsonResult.newIns().result(false).msg("释放锁库异常");
			}
		}
	}
	
	
	public static void main(String[] args) {
		try {
			String str = 
				"{\"pros\":[{\"sku\":\"IF639\",\"qty\":3,\"purchasePrice\":43.8,\"isGift\":0,\"warehouseId\":2024,\"warehouseName\":\"深圳仓\",\"imgUrl\":\"https://static.tomtop.com.cn/images/I/9/IF639/IF639-1-3a9d-S36h.jpg\",\"productTitle\":\"麦蒂斯金装啤酒500ml\",\"capfee\":43.8,\"arriveWarePrice\":2.19,\"categoryId\":4706,\"categoryName\":\"进口食品\",\"contractNo\":null,\"expirationDate\":null,\"clearancePrice\":0.0}],\"orderNo\":\"CG201704220000016355\",\"accountName\":\"sdfdsfds\",\"account\":\"zhengbc@qq.com\"}";
			InventoryCloudLockDto cloudLockDto = JsonFormatUtils.jsonToBean(str, InventoryCloudLockDto.class);
			System.out.println(Json.toJson(cloudLockDto));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public JsonResult<?> getRecords(String string) {
		try {
			InventoryLockDeSearch search = JsonFormatUtils.jsonToBean(string, InventoryLockDeSearch.class);
			if(search != null){
				return JsonResult.newIns().result(true).data(new PageResultDto<IvyOprecord>(
						search.getRows(),
						recordMapper.pageCount(search),
						search.getPage(), 
						recordMapper.pageSearch(search)));
			}
			return JsonResult.newIns().result(false).msg("分页查询参数异常");
		} catch (Exception e) {
			Logger.info("分页查询锁库释放异常：{}",e);
			return JsonResult.newIns().result(false).msg("分页查询锁库释放异常");
		}
	}

	@Override
	public JsonResult<?> getResetDetails(String string) {
		try {
			
			InventoryLockDeSearch search = JsonFormatUtils.jsonToBean(string, InventoryLockDeSearch.class);
			if(search != null){
				return JsonResult.newIns().result(true).data(new PageResultDto<IvyOptDetail>(
						search.getRows(),
						optDetailMapper.pageCount(search),
						search.getPage(), 
						optDetailMapper.pageSearch(search)));
			}
			return JsonResult.newIns().result(false).msg("分页查询参数异常");
		} catch (Exception e) {
			Logger.info("分页查询释放明细异常：{}",e);
			return JsonResult.newIns().result(false).msg("分页查询释放明细异常");
		}
	}
	
}
