package services.dismember.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import entity.dismember.DisChinaArea;
import entity.dismember.DisProvince;
import mapper.dismember.DisProvinceMapper;
import play.libs.Json;
import services.dismember.IDisProvinceService;

import java.util.List;
import java.util.Map;

/**
 * Created by LSL on 2015/12/21.
 */
public class DisProvinceService implements IDisProvinceService {

    @Inject
    private DisProvinceMapper disProvinceMapper;

    @Override
    public List<DisProvince> getAllProvinces() {
        return disProvinceMapper.getAllProvinces();
    }

	@Override
	public DisProvince getProvinces(String key) {
		return disProvinceMapper.getProvince(key);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getChinaArea(String param) {
		ObjectNode result = Json.newObject();
		JsonNode node = Json.parse(param);
	    String smodifydate = node.has("smodifydate") ? node.get("smodifydate").asText() : "";   
	    String emodifydate = node.has("emodifydate") ? node.get("emodifydate").asText() : "";
	    Map<String,Object> search = Maps.newHashMap();
	    if(node.has("areaId")) {
	    	search.put("areaId", node.get("areaId").asInt());
	    } else {
	    	search.put("smodifydate", smodifydate);
	    	search.put("emodifydate", emodifydate);	    	
	    }
	    List<DisChinaArea> lists = disProvinceMapper.getChinaArea(search);
	    result.put("suc", true);
	    result.put("data", Json.toJson(lists));
		return result.toString();
	}
}
