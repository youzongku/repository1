package services.product.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.collect.Maps;

import com.google.inject.Inject;

import entity.product.store.AttrMultivalue;
import entity.product.store.AttributeType;
import entity.product.store.BbcAttribute;
import entity.product.store.StoreBase;
import entity.product.store.StorePage;
import mapper.product.store.AttributeTypeMapper;
import mapper.product.store.BbcAttributeMapper;
import play.Logger;
import services.base.utils.JsonFormatUtils;
import services.product.IAttributeService;

public class AttributeService implements IAttributeService{

	@Inject
	private BbcAttributeMapper bbcAttributeMapper;
	
	@Inject 
	private AttributeTypeMapper typeMapper;
	
	@Override
	public Map<String, Object> create(StoreBase entity) {
		Map<String, Object> res = Maps.newHashMap();
		boolean flag = false;
		try {
			flag = bbcAttributeMapper.insertSelective((BbcAttribute)entity) > 0;
		} catch (Exception e) {
			Logger.info("新增属性异常",e);
		}
		res.put("suc", flag);
		res.put("msg","新增属性"+(flag?"成功":"失败"));
		return res;
	}

	@Override
	public Map<String, Object> delete(StoreBase entity) {
		Map<String, Object> res = Maps.newHashMap();
		boolean flag = false;
		try {
			flag = bbcAttributeMapper.deleteByPrimaryKey(((BbcAttribute)entity).getId()) > 0;
		} catch (Exception e) {
			Logger.info("删除属性异常",e);
		}
		res.put("suc", flag);
		res.put("msg","删除属性"+(flag?"成功":"失败"));
		return res;
	}

	@Override
	public Map<String, Object> update(StoreBase entity) {
		Map<String, Object> res = Maps.newHashMap();
		boolean flag = false;
		try {
			flag = bbcAttributeMapper.updateByPrimaryKey((BbcAttribute)entity) > 0;
		} catch (Exception e) {
			Logger.info("更新属性异常",e);
		}
		res.put("suc", flag);
		res.put("msg","更新属性"+(flag?"成功":"失败"));
		return res;
	}

	@Override
	public List<AttrMultivalue> selectMaintenanceOptions(StoreBase entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addMaintenanceOptions(StoreBase entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteMaintenanceOptions(StoreBase entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AttributeType> selectAttrTypes() {
		return typeMapper.selectAll();
	}

	@Override
	@SuppressWarnings("unchecked")
	public StorePage search(String string) {
		Map<String,Object> map = JsonFormatUtils.jsonToBean(string, HashMap.class);
		List<BbcAttribute> list = bbcAttributeMapper.queryPage(map);
		return new StorePage(toInt(map.get("pageSize")) ,bbcAttributeMapper.queryCount(map),toInt(map.get("currPage")),list);
	}
	private Integer toInt(Object str){
		try {
			return Integer.valueOf(str.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	
}
