package services.product.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dto.product.ProductDispriceSearch;
import entity.product.TypeBase;
import mapper.product.ProductDispriceMapper;
import mapper.product.TypeBaseMapper;
import play.libs.Json;
import services.product.ITypeBaseService;

public class TypeBaseService implements ITypeBaseService{
	
	@Inject
	private TypeBaseMapper typeBaseMapper;
	
	@Inject
	private ProductDispriceMapper productDispriceMapper;

	@Override
	public String addProductType(String param) {
		ObjectNode result = Json.newObject();
		JsonNode node = Json.parse(param);
		TypeBase typeBase = new TypeBase();
		typeBase.setName(node.get("name") == null ? null : node.get("name").asText());
		typeBase.setCreateDate(new Date());
		int flag = typeBaseMapper.insertSelective(typeBase);
		
		if (flag == 0) {
			result.put("suc", false);
			result.put("msg", "添加类别失败");
			return result.toString();
		}
		
		result.put("suc", true);
		return result.toString();
	}

	@Override
	public String updateProductType(String param) {
		ObjectNode result = Json.newObject();
		JsonNode node = Json.parse(param);
		TypeBase typeBase = new TypeBase();
		typeBase.setId(node.get("tid").asInt());
		typeBase.setName(node.get("name") == null ? null : node.get("name").asText());
		typeBase.setUpdateDate(new Date());
		ProductDispriceSearch search = new ProductDispriceSearch();
		search.setTypeId(typeBase.getId());
		int count = productDispriceMapper.getProductDisPriceCount(search);
		if (count > 0) {
			result.put("suc", false);
			result.put("msg", "该类别下存在"+count+"个商品，请先确认之后，再修改");
			return result.toString();
		}
		
		int flag = typeBaseMapper.updateByPrimaryKeySelective(typeBase);
		if (flag == 0) {
			result.put("suc", false);
			result.put("msg", "添加类别失败");
			return result.toString();
		}
		
		result.put("suc", true);
		return result.toString();
	}

	@Override
	public List<TypeBase> getAllTypes() {
		return typeBaseMapper.selectAll();
	}

	@Override
	public String deleteType(Integer tid) {
		ObjectNode result = Json.newObject();
		ProductDispriceSearch search = new ProductDispriceSearch();
		search.setTypeId(tid);
		int count = productDispriceMapper.getProductDisPriceCount(search);
		if (count > 0) {
			result.put("suc", false);
			result.put("msg", "该类别下存在"+count+"个商品，请先确认之后，再删除");
			return result.toString();
		}
		
		int flag = typeBaseMapper.deleteByPrimaryKey(tid);
		if (flag == 0) {
			result.put("suc", false);
			result.put("msg", "删除失败");
			return result.toString();
		}
		
		result.put("suc", true);
		result.put("msg", "删除成功");
		return result.toString();
	}
	
}
