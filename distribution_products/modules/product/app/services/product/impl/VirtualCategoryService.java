package services.product.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dto.category.CategorySearchParamDto;
import dto.category.VirCategoryDto;
import dto.product.PageResultDto;
import dto.product.ProductLite;
import dto.product.ProductSearchParamDto;
import dxo.category.VirtualCategoryDxo;
import entity.category.CategoryBase;
import entity.category.VirtualCategory;
import forms.category.VirtualCategoryForm;
import mapper.category.CategoryBaseMapper;
import mapper.category.VirtualCategoryMapper;
import mapper.product.ProductBaseMapper;
import play.Logger;
import play.libs.Json;
import services.product.IProductVcategoryMapService;
import services.product.IVirtualCategoryService;
import session.ISessionService;

public class VirtualCategoryService implements IVirtualCategoryService {
	
	@Inject
	private CategoryBaseMapper cateBaseMapper;
	@Inject
	private  VirtualCategoryMapper vCategoryMapper;
	@Inject
	private IProductVcategoryMapService vmapService;
	@Inject
	private ProductBaseMapper productBaseMapper;
	@Inject
	private ISessionService sessionService;
	@Inject
	private static final String VCATE_KEY = "VCATE_LIST";
	
	@Override
	public List<VirtualCategoryForm> query(CategorySearchParamDto paramDto) {
		List<VirtualCategory> vcList = vCategoryMapper.queryByParam(paramDto);
		return VirtualCategoryDxo.mutilTrans(vcList);
	}

	@Override
	public List<CategoryBase> realCateQuery(CategorySearchParamDto dto) {
		return cateBaseMapper.realCateQuery(dto);
	}

	@Override
	public void queryChild(List<Integer> catIds, Set<Integer> all) {
		if(null != catIds && catIds.size() > 0){
 			all.addAll(catIds);
			List<Integer> ids = cateBaseMapper.queryChild(catIds);
			if(null != ids && ids.size() >0){
				all.addAll(ids);
				queryChild(ids,all);
			}
		}	
	}
	
	@Override
	public List<String> getSkuLists(List<Integer> list) {
		return cateBaseMapper.getSkusMapper(list);
	}

	@Override
	public VirtualCategory categoryDetail(Integer vcId) {
		return vCategoryMapper.select(vcId);
	}

	@Override
	public List<VirtualCategoryForm> queryAll(CategorySearchParamDto dto) {
		initAll();
		ObjectMapper map;
		List<VirtualCategoryForm> vcates;
		try {
			map = new ObjectMapper();
			vcates = map.readValue(sessionService.get(VCATE_KEY).toString(), new TypeReference<List<VirtualCategoryForm>>() {});
			setChilds(vcates);
			return vcates.stream()
					.collect(Collectors.partitioningBy(s-> s.getParentId()!= null && dto.getParentId().equals(s.getParentId()))).get(true);
		} catch (IOException e) {
			Logger.info("获取缓存虚拟类目异常"+e);
			return Lists.newArrayList();
		}
	}

	@Override
	public List<VirtualCategoryForm> queryParent(Integer vcId) {
		VirtualCategory cate= vCategoryMapper.select(vcId);
		VirtualCategoryForm  vcate = VirtualCategoryDxo.trans(cate);
		getParent(vcate);
		List<VirtualCategoryForm> vcateList = Lists.newArrayList();
		vcateList.add(vcate);
		getVCagteList(vcateList,vcate);
		return vcateList;
	}

	private void getVCagteList(List<VirtualCategoryForm> vcateList,VirtualCategoryForm vcate) {
		if(vcate.getParentCate() != null){
			vcateList.add(vcate.getParentCate());
			getVCagteList(vcateList,vcate.getParentCate());
		}
	}

	private void getParent(VirtualCategoryForm vcate) {
		if(vcate.getParentId() != null){
			VirtualCategory cate= vCategoryMapper.select(vcate.getParentId());
			VirtualCategoryForm  vcateform = VirtualCategoryDxo.trans(cate);
			vcate.setParentCate(vcateform);
			getParent(vcateform);
		}
		
	}

	@Override
	public void initAll() {
		if(sessionService.get(VCATE_KEY) == null){
			List<VirtualCategoryForm> vcates =  VirtualCategoryDxo.mutilTrans(vCategoryMapper.queryAll());;
			sessionService.set(VCATE_KEY,Json.toJson(Lists.newArrayList(vcates)).toString());
		}
	}

	/**
	 * 循环设置子类目
	 * @author zbc
	 * @since 2017年4月1日 上午10:21:15
	 */
	private void setChilds(List<VirtualCategoryForm> vcates) {
		for(VirtualCategoryForm form:vcates){
			Map<Boolean, List<VirtualCategoryForm>> childList = vcates.stream()
					.collect(Collectors.partitioningBy(e -> e.getParentId()!= null && form.getVcId().equals(e.getParentId())));
			form.setForm(childList.get(true));
		}
	}
	
	@Override
	public void emptyAll() {
		sessionService.remove(VCATE_KEY);
	}

	public void queryChild(VirtualCategoryForm form,Set<Integer> all){
		CategorySearchParamDto dto = new CategorySearchParamDto();
		dto.setParentId(form.getVcId());
		List<VirtualCategoryForm>  list = queryAll(dto);
		for(VirtualCategoryForm f:list){
			all.add(f.getVcId());
		}
	}
	
	@Override
	public List<Map<String, Object>> getProInfo(CategorySearchParamDto cateDto) {
		ProductSearchParamDto proDto = new ProductSearchParamDto();
		List<Map<String,Object>> res = Lists.newArrayList();
		Integer currpage = cateDto.getCurrPage();
		Integer pageSize = cateDto.getPageSize();
		List<VirtualCategoryForm> vcList = query(cateDto);
		for(VirtualCategoryForm vc:vcList){
			CategorySearchParamDto sonCateDto = new CategorySearchParamDto();
			sonCateDto.setParentId(vc.getVcId());
			List<VirtualCategoryForm> sonList = queryAll(sonCateDto);
		    for(VirtualCategoryForm son:sonList){
		    	Map<String,Object> result = Maps.newHashMap();
				proDto = new ProductSearchParamDto();
				proDto.setCategoryId(son.getVcId());
				proDto.setCurrPage(currpage);
				proDto.setPageSize(pageSize);
				proDto.setModel(cateDto.getModel());
				Set<Integer> all = new HashSet<>();
				all.add(proDto.getCategoryId());
				all.addAll(son.getChildIds());
				PageResultDto page =vmapService.getSkuLists(new ArrayList<>(all),proDto);
				result.put("cate", son);
				result.put("list",page);
				res.add(result);
		    }
		}
		return res;
	}

	@Override
	public List<String> getSkuList(Integer catId) {
		if(catId == null){
			return new ArrayList<String>();
		}
		Set<Integer> all = new HashSet<>();
		List<Integer> list = new ArrayList<>();
		list.add(catId);
		queryChild(list, all);
		return cateBaseMapper.getSkusMapper(list);
	}

	@Override
	public PageResultDto getSkuLists(ProductSearchParamDto searchDto) {
		searchDto.setIstatus(1);
		searchDto.setvCategoryId(Lists.newArrayList(searchDto.getCategoryId()));
		searchDto.setCategoryId(null);
		searchDto.setModel(searchDto.getModel());
		List<ProductLite> productList = productBaseMapper.products(searchDto);
		Integer pageSize = searchDto.getPageSize();
		Integer currPage = searchDto.getCurrPage();
		int total = productList.size();
		//分页
		if(null != pageSize && productList.size() > pageSize){
			int startIdx = (currPage - 1) *  pageSize;
			int toIdx = startIdx + pageSize;
			if(toIdx > (productList.size() - 1)){
				toIdx = productList.size();
			}
			productList = productList.subList( startIdx, toIdx);
		}
		return new PageResultDto(searchDto.getPageSize(), total, searchDto.getCurrPage(), productList);
	}

	@Override
	public List<VirCategoryDto> getAllSubsByParentId(Integer parentId) {
		return vCategoryMapper.allSubsByParentId(parentId);
	}
	
}
