package services.product.impl;

import java.io.File;
import java.util.List;

import play.Logger;
import services.product.IBannerService;
import mapper.banner.BannerInfoMapper;
import util.product.Page;

import com.google.inject.Inject;

import entity.banner.BannerInfo;

public class BannerService implements IBannerService {
	@Inject
	private BannerInfoMapper bannerInfoMapper;

	@Override
	public boolean addBannerInfo(BannerInfo banner) {
		return bannerInfoMapper.insertSelective(banner) == 1;
	}

	@Override
	public Page<BannerInfo> getAllBanner(Integer curr, Integer pageSize,
			int status,int type,String describe, String sord, String sidx) {
		int openSize = (curr - 1) * pageSize;
		List<BannerInfo> bannerList = bannerInfoMapper.getAllBanner(openSize,
				pageSize, status, type,describe, sord, sidx);
		Page<BannerInfo> page = new Page<BannerInfo>(bannerList,
				bannerInfoMapper.getMaxSize(type), curr, pageSize);
		return page;
	}

	@Override
	public boolean deleteBanner(Integer id) {
		return bannerInfoMapper.deleteBanner(id) == 1;
	}

	@Override
	public File getBannerImg(Integer id) {
		return new File(bannerInfoMapper.getBannerImg(id));
	}

	@Override
	public boolean updateBannerInfo(BannerInfo banner) {
	   Logger.info("banner："+banner);
		return  bannerInfoMapper.updateByPrimaryKeySelective(banner)==1;
	}

	@Override
	public BannerInfo selectByPrimaryKey(Integer id) {
		return bannerInfoMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<BannerInfo> selectAllBanner() {
		return bannerInfoMapper.selectAllBanner();
	}

	@Override
	public int countBannerByParam(BannerInfo bannerInfo) {
		return bannerInfoMapper.countBannerByParam(bannerInfo);
	}

}
