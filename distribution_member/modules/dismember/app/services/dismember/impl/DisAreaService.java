package services.dismember.impl;

import com.google.inject.Inject;
import entity.dismember.DisArea;
import mapper.dismember.DisAreaMapper;
import services.dismember.IDisAreaService;

import java.util.List;

/**
 * Created by LSL on 2015/12/21.
 */
public class DisAreaService implements IDisAreaService {

    @Inject
    private DisAreaMapper disAreaMapper;

    @Override
    public List<DisArea> getAreasByCity(Integer cityId) {
        return disAreaMapper.getAreasByCity(cityId);
    }

	@Override
	public String addArea(String name, Integer cityId) {
		DisArea area = new DisArea();
		area.setAreaName(name);
		area.setCityId(cityId);
		disAreaMapper.insertSelective(area);
		return "success";
	}

	@Override
	public List<DisArea> getAllAreas() {
		return disAreaMapper.getAllAreas();
	}
}
