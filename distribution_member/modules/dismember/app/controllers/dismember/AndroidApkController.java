package controllers.dismember;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import entity.dismember.ApkApplyQueue;
import entity.dismember.ApkVersion;
import events.dismember.ApkSourceUploadEvent;
import extensions.InjectorInstance;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.dismember.IApkApplyService;
import services.dismember.impl.LoginService;

/**
 * 安卓APK重新打包路由
 * @author duyuntao
 */
public class AndroidApkController extends Controller {

	private static String USER_SOURCECODE_PATH = "";//用户项目源代码路径
	private static String APP_FOLDER_NAME = "";//app项目目录的具体名字
	private static String APK_RELEASE_PATH = "";//打包生成后，项目内的位置
	
	static{
		
		if (StringUtils.isEmpty(APP_FOLDER_NAME)) {
			Configuration conf = Play.application().configuration()
					.getConfig("appfolder");
			APP_FOLDER_NAME = conf.getString("name");
		}
		
		if (StringUtils.isEmpty(USER_SOURCECODE_PATH)) {
			Configuration conf = Play.application().configuration()
					.getConfig("usersourcecode");
			USER_SOURCECODE_PATH = conf.getString("path");
		}
		
		if (StringUtils.isEmpty(APK_RELEASE_PATH)) {
			Configuration conf = Play.application().configuration()
					.getConfig("apkrelease");
			APK_RELEASE_PATH = conf.getString("path");
		}
		
	}
	
	@Inject
	private IApkApplyService apkApplyService;
	
	@Inject
	private LoginService loginService;
	
    /**
     * 申请安卓apk打包
     * @return
     * @throws IOException 
     */
    public Result applyForApk() throws IOException {
    	Map<String, Object> result = Maps.newHashMap();
    	JsonNode reqParam = request().body().asJson();
 
    	if (!loginService.isLogin(1)) {
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}
    	String identifier=null;
    	String appIconUrl=null;
    	String appStartIconUrl=null;
    	if(reqParam.has("identifier") && !"".equals(reqParam.get("identifier").asText()) && !"null".equals(reqParam.get("identifier").asText())){
    		identifier=reqParam.get("identifier").asText();
    	}
    	if(reqParam.has("appIconUrl") && !"".equals(reqParam.get("appIconUrl").asText()) && !"null".equals(reqParam.get("appIconUrl").asText())){
    		appIconUrl=reqParam.get("appIconUrl").asText();
    	}
    	if(reqParam.has("appStartIconUrl") && !"".equals(reqParam.get("appStartIconUrl").asText()) && !"null".equals(reqParam.get("appStartIconUrl").asText())){
    		appStartIconUrl=reqParam.get("appStartIconUrl").asText();
    	}
    
		
		ApkApplyQueue aaq = new ApkApplyQueue();
    	aaq.setIdentifier(identifier);
    	aaq.setAppIconUrl(appIconUrl);
    	aaq.setAppStartIconUrl(appStartIconUrl);
    	aaq.setAccount(loginService.getLoginContext(1).getEmail());
    	int res = apkApplyService.saveOrUpdateActive(aaq);
    	
    	if(res == 1){
    		//保存成功
    		result.put("success", true);
    		result.put("msg", apkApplyService.getApplyNeedToRebuiltBeforeYou(aaq.getAccount()));
    		return ok(Json.toJson(result));
    	}
    	
    	//保存失败
		result.put("success", false);
		result.put("msg", "保存失败，请稍后重试");
        return ok(Json.toJson(result));
    }
    
    /**
     * 验证是否已经提交过apk申请
     * @param account
     * @return
     */
    public Result getApplyAccount(String account){
    	ApkApplyQueue aaq = apkApplyService.selectByAccount(account);
		return ok(Json.toJson(aaq==null?new ApkApplyQueue():aaq));
    }
    
    /**
     * 检查前面有多少个人在排队打包
     * @return
     */
    public Result getApplyNeedToRebuiltBeforeYou(String account){
    	return ok(Json.toJson(apkApplyService.getApplyNeedToRebuiltBeforeYou(account)));
    }
    
    /**
     * 下载已经打包调整完毕的APK
     * @param account
     */
    public Result downloadApk(String account){
		ApkApplyQueue aaq = apkApplyService.selectByAccount(account);
    	String targetFile = USER_SOURCECODE_PATH + File.separator + aaq.getAccount() + File.separator + APP_FOLDER_NAME + File.separator + APK_RELEASE_PATH;
    	return ok(new File(targetFile));
    }
    
    /**
     * apk升级验证
     * @return
     */
    public Result apkUpgrade(){
    	Map<String,Object> retData=Maps.newHashMap();
    	JsonNode oldApkData = request().body().asJson();
    	if(oldApkData==null || oldApkData.get("appName")==null || oldApkData.get("code")==null
    			|| oldApkData.get("channelName")==null){
    		retData.put("ret", 0);
    		retData.put("msg", "参数输入不正确");
    		return ok(Json.toJson(retData));
    	}
    	Logger.info("渠道商升级信息[{}]", oldApkData);
    	String appName = oldApkData.get("appName").asText();
    	int code = oldApkData.get("code").asInt();
    	String channelName = oldApkData.get("channelName").asText();
    	ApkVersion apkVersionDate = apkApplyService.apkUpgrade(appName,code,channelName);
    	if(apkVersionDate==null){
    		retData.put("ret", 0);
    		retData.put("msg", "该版本已是最新版或渠道商不存在");
    		return ok(Json.toJson(retData));
    	}
    	retData.put("ret", 1);
		retData.put("data", apkVersionDate);
    	return ok(Json.toJson(retData));
    }
    
    public Result uploadApkSourceForUpgrade(){
    	//接收上传的app源码文件
    	MultipartFormData reqMultipart = request().body().asMultipartFormData();
    	Map<String, String[]> params = reqMultipart.asFormUrlEncoded();
    	Map<String, Object> result = Maps.newHashMap();
    	
    	//保存源码文件
		FilePart filePart = reqMultipart.getFile("sourceCode");
		File sourceCodeFile=filePart.getFile();
		String fileName=filePart.getFilename();
		ApkVersion apkVersion=new ApkVersion();
		String[] tempCode = params.get("code");
    	Integer code = Integer.valueOf(tempCode[0]);
    	apkVersion.setCode(code);
    	String[] tempDescription = params.get("description");
    	String description = tempDescription[0];
    	apkVersion.setDescription(description);
    	String[] tempSize = params.get("size");
    	String size = tempSize[0];
    	apkVersion.setSize(size);
    	String[] tempVersion = params.get("version");
    	String version = tempVersion[0];
    	apkVersion.setVersion(version);
    	String[] tempForceUpdater = params.get("forceUpdate");
    	Integer forceUpdate = Integer.valueOf(tempForceUpdater[0]);
    	apkVersion.setForceUpdate(forceUpdate);
    	
		try {
			BufferedInputStream in=new BufferedInputStream(new FileInputStream(sourceCodeFile));  
			//保存路径
			Configuration conf = Play.application().configuration().getConfig("sourcecode");
			String filePath = conf.getString("path");
			
			File newFile=new File(filePath);//要保存的文件路径  
			if(!newFile.exists()&& !newFile.isDirectory()){
				newFile.mkdirs();
			}
			File target=new File(filePath+"//"+fileName);
			if(target.exists()){
				target.delete();
			}
			target.createNewFile();
			BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(target));  
			byte[] b = new byte[1024];
			while(in.read(b)!=-1){  
			    out.write(b);  
			}  
			in.close();  
			out.close();
			
			ApkSourceUploadEvent apkSourceUploadEvent = new ApkSourceUploadEvent();
			apkSourceUploadEvent.setApkVersion(apkVersion);
			InjectorInstance.getInstance(EventBus.class).post(apkSourceUploadEvent);
			result.put("result", 0);
			result.put("msg", "源码上传成功！");
		} catch (IOException e) {
			Logger.info("apk源码上传异常{}",e);
			result.put("result", 1);
			result.put("msg", "源码上传发生异常");
		}  
        return ok(Json.toJson(result));
    }

}
