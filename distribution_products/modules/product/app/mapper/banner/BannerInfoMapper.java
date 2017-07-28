package mapper.banner;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.banner.BannerInfo;

public interface BannerInfoMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(BannerInfo record);

	int insertSelective(BannerInfo record);

	BannerInfo selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(BannerInfo record);

	int updateByPrimaryKey(BannerInfo record);

	List<BannerInfo> getAllBanner(@Param("openSize") Integer openSize,
			@Param("pageSize") Integer pageSize, @Param("status") int status,@Param("type") int type,@Param("describe") String describe,@Param("sord") String sord,@Param("sidx") String sidx);

	int deleteBanner(Integer id);

	int getMaxSize(@Param("type") int type);

	public String getBannerImg(Integer id);
	
	public List<BannerInfo> selectAllBanner();
	
   int countBannerByParam(BannerInfo bannerInfo);
}