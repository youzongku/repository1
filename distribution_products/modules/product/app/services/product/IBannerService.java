package services.product;

import java.io.File;
import java.util.List;

import util.product.Page;
import entity.banner.BannerInfo;

public interface IBannerService {
	public boolean addBannerInfo(BannerInfo banner);

	public Page<BannerInfo> getAllBanner(Integer curr, Integer pageSize,
			int status,int type, String describe,String sord, String sidx);

	public boolean deleteBanner(Integer id);

	public File getBannerImg(Integer id);

	public boolean updateBannerInfo(BannerInfo banner);
	
	public BannerInfo selectByPrimaryKey(Integer id);
	
	public List<BannerInfo> selectAllBanner();

	int countBannerByParam(BannerInfo bannerInfo);
}
