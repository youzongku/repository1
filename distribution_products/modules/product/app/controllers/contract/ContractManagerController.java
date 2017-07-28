package controllers.contract;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.collect.Maps;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;

import controllers.annotation.ALogin;
import dto.product.FileInfo;
import entity.contract.ContractAttachment;
import play.Configuration;
import play.Logger;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.product.IContractManagerService;
import services.product.IUserService;
import services.product.impl.WebUploaderService;

/**
 *
 * 合同管理
 *
 * @author Administrator
 *
 */
@Api(value="/contract",description="合同管理")
public class ContractManagerController extends Controller {

	@Inject
	private IUserService userService;

	@Inject
	private IContractManagerService contractService;

	@Inject
	private WebUploaderService webUploaderService;

	private static String uploadFolder = "";

	static {
		if (StringUtils.isEmpty(uploadFolder)) {
			Configuration config = Play.application().configuration().getConfig("b2bSPA");
			uploadFolder = config.getString("imagePath") + File.separator + "md";
		}
	}

	/**
	 * 添加合同
	 *
	 * @return
	 */
	@ALogin
	public Result addContract() {
		MultipartFormData formData = request().body().asMultipartFormData();
		Map<String, String[]> params = formData.asFormUrlEncoded();
		Map<String, Object> result = Maps.newHashMap();
		if (null == params || !params.containsKey("email")) {
			result.put("suc", false);
			result.put("msg", "参数错误。");
			return ok(Json.toJson(result));
		}
		String opUser = userService.getAdminAccount();
		return ok(Json.toJson(contractService.insert(params, opUser)));
	}

	/**
	 * 上传附件接口
	 *
	 * @return
	 */
	public Result uploadContractAttachment() {
		Map<String, String> param = Form.form().bindFromRequest().data();
		MultipartFormData multipartFormData = request().body().asMultipartFormData();

		String cno = param.get("cno");
		List<File> files = Lists.newArrayList();
		String opUser = userService.getAdminAccount();

		if(multipartFormData != null) {
			List<Http.MultipartFormData.FilePart> parts = multipartFormData.getFiles();

			if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(parts)) {
				try {
					for (Http.MultipartFormData.FilePart part : parts) {
						File file = part.getFile();
						String status = param.get("status");

						Form<FileInfo> form = Form.form(FileInfo.class).bindFromRequest();
						FileInfo info = form.get();
						if (status == null) {    //分片上传
							if (file != null) {
								try {
									File target = webUploaderService.getReadySpace(info, uploadFolder);

									if (target == null) {
										Map map = Maps.newHashMap();
										map.put("status", 0);
										map.put("message", "上传文件有误!");
										return ok(Json.toJson(map));
									}
									Files.copy(file, target);//保存上传文件分片
								} catch (IOException ex) {
									Logger.error("保存文件分片失败", ex);
								}

								if(info.getChunks() <= 0){
									File target = webUploaderService.getReadySpace(info, uploadFolder);
									Logger.info("---------------------->target:{},{},{}", target.getPath(), info.toString(), cno);
									files.add(target);
									contractService.uploadFile(files, info.getCno(), opUser, target.getName(), info.getMd5());

									Map map = Maps.newHashMap();
									map.put("status", 1);
									map.put("message", "上传完成");
									return ok(Json.toJson(map));
								}
							}
						}
					}
				} catch (Exception e) {
					Logger.error("上传文件有误-final：{}",e);
				}
			}
		}

		String status = param.get("status");
		Form<FileInfo> form = Form.form(FileInfo.class).bindFromRequest();
		FileInfo info = form.get();

		if(status != null) {
			if(status.equals("md5Check")){	//秒传验证
				String path = webUploaderService.md5Check(info.getMd5());

				if(path == null){
					Map map = Maps.newHashMap();
					map.put("ifExist", 0);
					return ok(Json.toJson(map));
				}else{
					files.add(new File(path));
					contractService.uploadFile(files, cno, opUser, info.getFileName(), info.getMd5());
					Map map = Maps.newHashMap();
					map.put("ifExist", 1);
					return ok(Json.toJson(map));
				}

			}else if (status.equals("chunkCheck")) {//文件分块验证
				//检查目标分片是否存在且完整
				if (webUploaderService.chunkCheck(uploadFolder + "/" + info.getName() + "/" + info.getChunkIndex(), Long.valueOf(info.getSize()))) {
					Map map = Maps.newHashMap();
					map.put("ifExist", 1);
					return ok(Json.toJson(map));
				} else {
					Map map = Maps.newHashMap();
					map.put("ifExist", 0);
					return ok(Json.toJson(map));
				}

			} else if (status.equals("chunksMerge")) {//执行文件分块合并

				String path = webUploaderService.chunksMerge(info.getName(), info.getExt(), info.getChunks(), info.getMd5(), uploadFolder, cno, info.getFileName());
				if (path == null) {
					Map map = Maps.newHashMap();
					map.put("status", 0);
					map.put("message", "执行分块合并出错");
					return ok(Json.toJson(map));
				}

				files.add(new File(uploadFolder + File.separator + path));
				contractService.uploadFile(files, cno, opUser, info.getFileName(), info.getMd5());
				Map map = Maps.newHashMap();
				map.put("status", 1);
				map.put("message", "上传完成");
				return ok(Json.toJson(map));
			}
		}
		Map map = Maps.newHashMap();
		map.put("status", 0);
		map.put("message", "请求错误");
		return ok(Json.toJson(map));
	}

	/**
	 * 查询合同
	 * 
	 * @return
	 */
	@ALogin
	public Result getContract() {
		JsonNode node = request().body().asJson();
		Map<String, Object> result = Maps.newHashMap();
		if (null == node || node.size() <= 0) {
			result.put("suc", false);
			result.put("msg", "查询合同失败，请检查参数是否正确。");
			return ok(Json.toJson(result));
		}
		result.put("suc", true);
		result.put("data", contractService.getContracts(node));
		return ok(Json.toJson(result));
	}
	
	/**
	 * 更新合同
	 * 
	 * @return
	 */
	@ALogin
	public Result updateContract() {
		MultipartFormData formData = request().body().asMultipartFormData();
		Map<String, String[]> params = formData.asFormUrlEncoded();
		List<FilePart> files = formData.getFiles();
		Map<String, Object> result = Maps.newHashMap();
		if (null == params || !params.containsKey("cid")) {
			result.put("suc", false);
			result.put("msg", "参数错误。");
			return ok(Json.toJson(result));
		}
		String opUser = userService.getAdminAccount();
		return ok(Json.toJson(contractService.update(params, files, opUser)));
	}

	/**
	 * 删除附件
	 * 
	 * @return
	 */
	@ALogin
	public Result deleteAttachment() {
		JsonNode node = request().body().asJson();
		Map<String, Object> result = Maps.newHashMap();
		if (null == node || node.size() <= 0) {
			result.put("suc", false);
			result.put("msg", "删除附件失败，请检查参数是否正确。");
			return ok(Json.toJson(result));
		}
		String opUser = userService.getAdminAccount();
		return ok(Json.toJson(contractService.deleteAttachment(node, opUser)));
	}

	/**
	 * 获取操作记录
	 * 
	 * @return
	 */
	public Result getOprecord(String cno) {
		return ok(Json.toJson(contractService.getOprecord(cno)));
	}

	/**
	 * 获取附件列表
	 * 
	 * @return
	 */
	public Result getAttachment(String cno) {
		List<ContractAttachment> list = contractService.getAttachment(cno);
		return ok(Json.toJson(list));
	}

	/**
	 * 展示图片
	 * 
	 * @return
	 */
	public Result pictureView(Integer aid) {
		if (aid == null) {
			return ok("");
		}
		return ok(contractService.pictureView(aid));
	}

	/**
	 * 下载附件
	 * 
	 * @return
	 */
	@ALogin
	public Result download(String cno) {
		try {
			return ok(contractService.download(cno));
		} catch (Exception e) {
			return ok("");
		}
	}
}
