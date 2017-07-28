package services.dismember.impl;

import com.google.inject.Inject;
import entity.dismember.DisCity;
import mapper.dismember.DisCityMapper;
import services.dismember.IDisCityService;

import java.util.List;

/**
 * Created by LSL on 2015/12/21.
 */
public class DisCityService implements IDisCityService {

    @Inject
    private DisCityMapper disCityMapper;

    @Override
    public List<DisCity> getCitiesByProvince(Integer proId) {
        return disCityMapper.getCitiesByProvince(proId);
    }

	@Override
	public List<DisCity> getAllCities() {
		
		return disCityMapper.getAllCities();
	}
}
