package controllers.banner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import play.Configuration;
import play.Logger;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import services.product.IBannerService;
import play.mvc.Result;

import com.google.common.io.Files;
import com.google.inject.Inject;

import entity.banner.BannerInfo;

public class BannerController extends Controller {
	private static String imagePath = "";
	@Inject
	private IBannerService bannerService;

	static {
		if (StringUtils.isEmpty(imagePath)) {
			Configuration config = Play.application().configuration()
					.getConfig("b2bSPA");
			imagePath = config.getString("imagePath");
		}
	}

	// 上传banner
	public Result uploadBannerImg() {
		Map<String, String> param = Form.form().bindFromRequest().data();
		MultipartFormData mFD = request().body().asMultipartFormData();
        Map<String, Object> map = new HashMap<String, Object>();
		Logger.info("param:" + param);

		String fileId = param.get("id");
		String md5 = param.get(fileId + "_md5");
		String id = param.get("bId");
		BannerInfo banner;
		banner = new BannerInfo();
		banner.setDescribe(param.get("describe"));
		banner.setRelatedInterfaceUrl(param.get("relatedInterfaceUrl"));
		banner.setCreateUser(param.get("user"));
		banner.setSort(Integer.valueOf(param.get("sort")));

		Integer status = Integer.valueOf(param.get("status"));
		banner.setStatus(Integer.valueOf(param.get("status")));

		String bgColor = param.get("bgColor");
		banner.setBgColor(bgColor);

		Integer type = Integer.valueOf(param.get("adType"));
		Integer categoryId = Integer.valueOf(param.get("categoryId"));
		Integer parentId = Integer.valueOf(param.get("parentId"));
		if (type!=null && type.intValue() >= 2 && (categoryId == null || categoryId <= 0)) {
			map.put("result", false);
			map.put("msg", "必须要关联一个类目！");
			return ok(Json.toJson(map));
		}

		banner.setType(type);
		banner.setCategoryId(categoryId);
		banner.setParentId(parentId);

		if(mFD != null) {
			List<FilePart> parts = mFD.getFiles();
			if (CollectionUtils.isNotEmpty(parts)) {
				String contentType, fileName, fileMD5;
				File origin, folder, target;
				try {
					for (FilePart part : parts) {
						contentType = part.getContentType();
						fileName = part.getFilename();
						Logger.debug("uploadBannerImg    contentType----->" + contentType);
						Logger.debug("uploadBannerImg    fileName----->" + fileName);

						origin = part.getFile();
						fileMD5 = DigestUtils.md5Hex(new FileInputStream(origin));
						Logger.debug("uploadBannerImg    fileMD5----->" + fileMD5);
						if (fileMD5 != null && fileMD5.equals(md5)) {
							target = new File(imagePath + File.separator + fileName);
							folder = new File(imagePath);

							if (!folder.exists()) {
								folder.mkdirs();
							}
							target.createNewFile();
							Files.copy(origin, target);

							banner.setImgUrl(target.getAbsolutePath());
							Logger.info("id:" + id);
							if (id == null || "".equals(id)) {
								if (status == 1) {
									switch (type) {
										case 0:
											BannerInfo bannerInfo = new BannerInfo();
											bannerInfo.setStatus(1);
											bannerInfo.setType(0);
											int count = bannerService.countBannerByParam(bannerInfo);//Banner已启用个数
											if (count >= 10) {
												map.put("result", false);
												map.put("msg", "Banner最多能启用10个！");
												return ok(Json.toJson(map));
											}
											break;
										case 1:
											BannerInfo floatingBannerInfo = new BannerInfo();
											floatingBannerInfo.setStatus(1);
											floatingBannerInfo.setType(1);
											int floatingCount = bannerService.countBannerByParam(floatingBannerInfo);//浮窗图片已启用个数
											if (floatingCount >= 2) {
												map.put("result", false);
												map.put("msg", "浮窗图最多能启用2个！");
												return ok(Json.toJson(map));
											}
											break;
										case 2:
											BannerInfo originFloorBannerInfo = new BannerInfo();
											originFloorBannerInfo.setCategoryId(categoryId);
											originFloorBannerInfo.setParentId(parentId);
											originFloorBannerInfo.setStatus(1);
											originFloorBannerInfo.setType(2);
											int originFloorBannerCount = bannerService.countBannerByParam(originFloorBannerInfo);
											if (originFloorBannerCount > 0) {
												map.put("result", false);
												map.put("msg", "添加失败,该栏目已绑定相关广告图！");
												return ok(Json.toJson(map));
											}

											BannerInfo eachFloorBannerInfo = new BannerInfo();
											eachFloorBannerInfo.setParentId(parentId);
											eachFloorBannerInfo.setStatus(1);
											eachFloorBannerInfo.setType(2);
											int eachFloorBannerCount = bannerService.countBannerByParam(eachFloorBannerInfo);
											if (eachFloorBannerCount >= 10) {
												map.put("result", false);
												map.put("msg", "添加失败，每个楼层广告图最多能启用10个！");
												return ok(Json.toJson(map));
											}

											BannerInfo floorBannerInfo = new BannerInfo();
											floorBannerInfo.setStatus(1);
											floorBannerInfo.setType(2);
											int floorCount = bannerService.countBannerByParam(floorBannerInfo);//楼层广告图片已启用个数
											if (floorCount >= 50) {
												map.put("result", false);
												map.put("msg", "添加失败，楼层广告图最多能启用50个！");
												return ok(Json.toJson(map));
											}
											break;
										case 3:
											BannerInfo originAdvertisingBannerInfo = new BannerInfo();
											originAdvertisingBannerInfo.setCategoryId(categoryId);
											originAdvertisingBannerInfo.setStatus(1);
											originAdvertisingBannerInfo.setType(3);
											int originAdvertisingBannerInfoCount = bannerService.countBannerByParam(originAdvertisingBannerInfo);
											if (originAdvertisingBannerInfoCount > 0) {
												map.put("result", false);
												map.put("msg", "添加失败,该栏目已绑定相关广告图！");
												return ok(Json.toJson(map));
											}

											BannerInfo  commonAdvertisingBannerInfo = new BannerInfo();
											commonAdvertisingBannerInfo.setStatus(1);
											commonAdvertisingBannerInfo.setType(3);
											int commonAdvertisingCount = bannerService.countBannerByParam(commonAdvertisingBannerInfo);//通栏广告位已启用个数
											if (commonAdvertisingCount >= 5) {
												map.put("result", false);
												map.put("msg", "添加失败，通栏广告位最多能启用5个！");
												return ok(Json.toJson(map));
											}
											break;
									}
								}

								bannerService.addBannerInfo(banner);
								map.put("result", true);
								map.put("msg", "添加成功");
								return ok(Json.toJson(map));
							}
						} else {
							map.put("result", false);
							map.put("msg", "上传文件MD5值校验不通过");
							return ok(Json.toJson(map));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					map.put("result", false);
					map.put("msg", "系统异常");
					return ok(Json.toJson(map));
				}
			}
		}
		Logger.info("id:"+id);
		if (id != null && !"".equals(id)) {
			banner.setId(Integer.valueOf(id));
			if (status == 1) {
				switch (type) {
					case 0:
						BannerInfo bannerInfo = new BannerInfo();
						bannerInfo.setStatus(1);
						bannerInfo.setType(0);
						int count = bannerService.countBannerByParam(bannerInfo);//Banner已启用个数
						if (count >= 10) {
							map.put("result", false);
							map.put("msg", "更新失败，Banner最多能启用10个！");
							return ok(Json.toJson(map));
						}
						break;
					case 1:
						BannerInfo floatingBannerInfo = new BannerInfo();
						floatingBannerInfo.setStatus(1);
						floatingBannerInfo.setType(1);
						int floatingCount = bannerService.countBannerByParam(floatingBannerInfo);//浮窗图片已启用个数
						if (floatingCount >= 2) {
							map.put("result", false);
							map.put("msg", "更新失败，浮窗图最多能启用2个！");
							return ok(Json.toJson(map));
						}
						break;
					case 2:
						BannerInfo totalFloorAdvertisingBannerInfo = new BannerInfo();
						totalFloorAdvertisingBannerInfo.setCategoryId(categoryId);
						totalFloorAdvertisingBannerInfo.setParentId(parentId);
						totalFloorAdvertisingBannerInfo.setStatus(1);
						totalFloorAdvertisingBannerInfo.setType(2);
						int totalFloorAdvertisingBannerInfoCount = bannerService.countBannerByParam(totalFloorAdvertisingBannerInfo);
						if(totalFloorAdvertisingBannerInfoCount >= 1) {
							map.put("result", false);
							map.put("msg", "更新失败,该栏目已绑定相关广告图！");
							return ok(Json.toJson(map));
						}

						BannerInfo floorBannerInfo = bannerService.selectByPrimaryKey(Integer.valueOf(id));

						BannerInfo eachFloorBannerInfo = new BannerInfo();
						eachFloorBannerInfo.setParentId(parentId);
						eachFloorBannerInfo.setStatus(1);
						eachFloorBannerInfo.setType(2);
						int eachFloorBannerCount = bannerService.countBannerByParam(eachFloorBannerInfo);
						if (eachFloorBannerCount >= 10) {
							map.put("result", false);
							map.put("msg", "更新失败，每个楼层广告图最多能启用10个！");
							return ok(Json.toJson(map));
						}

						BannerInfo countfloorBannerInfo = new BannerInfo();
						countfloorBannerInfo.setStatus(1);
						countfloorBannerInfo.setType(2);
						int floorCount = bannerService.countBannerByParam(countfloorBannerInfo);//楼层广告图片已启用个数
						if (floorCount >= 50) {
							map.put("result", false);
							map.put("msg", "更新失败，楼层广告图最多能启用50个！");
							return ok(Json.toJson(map));
						}

						if(categoryId.intValue() != floorBannerInfo.getCategoryId().intValue()) {
							BannerInfo originFloorBannerInfo = new BannerInfo();
							originFloorBannerInfo.setCategoryId(categoryId);
							originFloorBannerInfo.setParentId(parentId);
							originFloorBannerInfo.setStatus(1);
							originFloorBannerInfo.setType(2);
							int originFloorBannerCount = bannerService.countBannerByParam(originFloorBannerInfo);
							if (originFloorBannerCount > 0) {
								map.put("result", false);
								map.put("msg", "更新失败,该栏目已绑定相关广告图！");
								return ok(Json.toJson(map));
							}
						}
						break;
					case 3:
						BannerInfo  totalCommonAdvertisingBannerInfo = new BannerInfo();
						totalCommonAdvertisingBannerInfo.setCategoryId(categoryId);
						totalCommonAdvertisingBannerInfo.setType(3);
						totalCommonAdvertisingBannerInfo.setStatus(1);
						int totalCommonAdvertisingBannerInfoCount = bannerService.countBannerByParam(totalCommonAdvertisingBannerInfo);
						if(totalCommonAdvertisingBannerInfoCount >= 1) {
							map.put("result", false);
							map.put("msg", "更新失败,该栏目已绑定相关广告图！");
							return ok(Json.toJson(map));
						}

						BannerInfo  countColumnBannerInfo = new BannerInfo();
						countColumnBannerInfo.setStatus(1);
						countColumnBannerInfo.setType(3);
						int commonAdvertisingCount = bannerService.countBannerByParam(countColumnBannerInfo);//通栏广告位已启用个数
						if (commonAdvertisingCount >= 5) {
							map.put("result", false);
							map.put("msg", "更新失败，通栏广告位最多能启用5个！");
							return ok(Json.toJson(map));
						}

						BannerInfo originBannerInfo = bannerService.selectByPrimaryKey(Integer.valueOf(id));
						if (originBannerInfo.getCategoryId().intValue() != categoryId.intValue()) {
							BannerInfo  commonAdvertisingBannerInfo = new BannerInfo();
							commonAdvertisingBannerInfo.setCategoryId(categoryId);
							commonAdvertisingBannerInfo.setType(3);
							commonAdvertisingBannerInfo.setStatus(1);
							int originCommonAdvertisingCount = bannerService.countBannerByParam(commonAdvertisingBannerInfo);//通栏广告位已启用个数
							if (originCommonAdvertisingCount > 0) {
								map.put("result", false);
								map.put("msg", "更新失败,该栏目已绑定相关广告图！");
								return ok(Json.toJson(map));
							}
						}
						break;
				}
			}
			if (bannerService.updateBannerInfo(banner)) {
				map.put("result", true);
				map.put("msg", "修改成功");
				return ok(Json.toJson(map));
			}
			map.put("result", false);
			map.put("msg", "修改失败");
			return ok(Json.toJson(map));
		}
		map.put("result", false);
		map.put("msg", "请求失败");
		return ok(Json.toJson(map));
	}

	// 得到所有的banner
	public Result getPartBanner() {
		Map<String, String> param = Form.form().bindFromRequest().data();

		if (param == null) {
			Logger.info("node:" + param);
			return ok(Json.toJson(""));
		}
		Logger.info("status:" + param.get("status"));
		return ok(Json.toJson(bannerService.getAllBanner(
				Integer.valueOf(param.get("curr")),
				Integer.valueOf(param.get("pageSize")),
				Integer.valueOf(param.get("status")),
				Integer.valueOf(param.get("type")),
				param.get("describe"),
				param.get("sord"),
				param.get("sidx"))));
	}

	// 删除banner
	public Result deleteBanner() {
		Map<String, String> param = Form.form().bindFromRequest().data();
		Logger.info("param:" + param);
		String idStr = param.get("id");
		Map<String, Object> map = new HashMap<String, Object>();
		if (idStr == null) {
			Logger.info("idStr为空");
			return ok("");
		}
		Integer id = Integer.valueOf(idStr);
		if (bannerService.deleteBanner(id)) {
			map.put("msg", "删除成功");
			return ok(Json.toJson(map));
		}
		map.put("msg", "删除失败");
		return ok(Json.toJson(map));
	}

	// 得到图片
	public Result getBannerImg() {
		Map<String, String> param = Form.form().bindFromRequest().data();
		String idStr = param.get("id");
		Map<String, Object> map = new HashMap<String, Object>();
		if ("".equals(idStr) || idStr == null) {
			return ok(Json.toJson(map.put("msg", "id为空")));
		}
		File bannerImg = bannerService.getBannerImg(Integer.valueOf(idStr));
		if(!bannerImg.exists()){
			return ok(Json.toJson(""));
		}
		return ok(bannerImg);
	}

	// 根据ID得到banner
	public Result getBannerById() {
		Map<String, String> param = Form.form().bindFromRequest().data();
		Logger.info("param:" + param);
		String idStr = param.get("id");
		if (idStr == null) {
			Logger.info("idStr:" + idStr);
			return ok(Json.toJson("id为空"));
		}
		
		return ok(Json.toJson(bannerService.selectByPrimaryKey(Integer
				.valueOf(idStr))));
	}

	// 得到所有的图片
	public Result getAllBanner() {
		return ok(Json.toJson(bannerService.selectAllBanner()));
	}
}
