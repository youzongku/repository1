package handlers.dismember;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import entity.dismember.ApkApplyQueue;
import entity.dismember.ApkVersion;
import entity.dismember.ShopSite;
import events.dismember.ApkApplyEvent;
import events.dismember.ApkSourceUploadEvent;
import mapper.dismember.ApkVersionMapper;
import mapper.dismember.ShopSiteMapper;
import play.Configuration;
import play.Logger;
import play.Play;
import services.dismember.IApkApplyService;

public class AkpApplyHandler {

	private static String APP_FOLDER_NAME = "";//app项目目录的具体名字
	private static String STRINGS_RESOURCE_FILE = "";//strings.xml文件位置
	private static String APP_NAME = "";//应用目录名
	private static Integer REBUILT_PER_MINUTE;//每分钟打包的个数
	
	private static String ICON_FILE_NAME = "";//图标文件的名字
	private static String START_ICON_FILE_NAME="";//app启动时欢迎图片的名字
	private static String ICON_FILE_PATH = "";//需要替换的Icon文件的目录
	private static String ICON_FILE_TARGET_PATH = "";//待替换图标所在的文位置
	
	private static String APP_CHANNEL_KEY = "";//渠道属性key
	private static String APP_DEFAULT_HOST = "";//APK打开后默认的URL
	
	private static String SOURCECODE_PATH = "";//项目源代码路径
	private static String USER_SOURCECODE_PATH = "";//用户项目源代码路径
	private static String ANDROIDMANIFEST_PATH = "";//项目下清单文件所在路径

	static {
		if (StringUtils.isEmpty(SOURCECODE_PATH)) {
			Configuration conf = Play.application().configuration()
					.getConfig("sourcecode");
			SOURCECODE_PATH = conf.getString("path");
		}
		if (StringUtils.isEmpty(USER_SOURCECODE_PATH)) {
			Configuration conf = Play.application().configuration()
					.getConfig("usersourcecode");
			USER_SOURCECODE_PATH = conf.getString("path");
		}
		if (StringUtils.isEmpty(ANDROIDMANIFEST_PATH)) {
			Configuration conf = Play.application().configuration()
					.getConfig("androidmanifest");
			ANDROIDMANIFEST_PATH = conf.getString("path");
		}
		if (StringUtils.isEmpty(APP_FOLDER_NAME)) {
			Configuration conf = Play.application().configuration()
					.getConfig("appfolder");
			APP_FOLDER_NAME = conf.getString("name");
		}
		if (StringUtils.isEmpty(STRINGS_RESOURCE_FILE)) {
			Configuration conf = Play.application().configuration()
					.getConfig("stringresourcefile");
			STRINGS_RESOURCE_FILE = conf.getString("path");
		}
		if (StringUtils.isEmpty(APP_NAME)) {
			Configuration conf = Play.application().configuration()
					.getConfig("attr");
			APP_NAME = conf.getString("appname");
			APP_CHANNEL_KEY = conf.getString("channel");
			APP_DEFAULT_HOST = conf.getString("host");
		}
		if (REBUILT_PER_MINUTE == null) {
			Configuration conf = Play.application().configuration()
					.getConfig("rebuilt");
			REBUILT_PER_MINUTE = conf.getInt("perMinuteQty");
		}
		if (StringUtils.isEmpty(ICON_FILE_NAME)) {
			Configuration conf = Play.application().configuration()
					.getConfig("appicon");
			ICON_FILE_NAME = conf.getString("filename");
		}
		if (StringUtils.isEmpty(START_ICON_FILE_NAME)) {
			Configuration conf = Play.application().configuration()
					.getConfig("appicon");
			START_ICON_FILE_NAME = conf.getString("startfilename");
		}
		if (StringUtils.isEmpty(ICON_FILE_PATH)) {
			Configuration conf = Play.application().configuration()
					.getConfig("appicon");
			ICON_FILE_PATH = conf.getString("path");
		}
		if (StringUtils.isEmpty(ICON_FILE_TARGET_PATH)) {
			Configuration conf = Play.application().configuration()
					.getConfig("appicon");
			ICON_FILE_TARGET_PATH = conf.getString("targetdir");
		}

	}

	@Inject
	private IApkApplyService apkApplyService;
	
	@Inject
	private ShopSiteMapper shopSiteMapper;

	@Inject
	private ApkVersionMapper apkVersionMapper;
	/**
	 * 定时检查Akp打包申请，对最先的申请进行打包
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	@Subscribe
	public void apkApplyCheck(ApkApplyEvent event) {
		List<ApkApplyQueue> aqqs = apkApplyService.getPriorApply(REBUILT_PER_MINUTE);
		for (ApkApplyQueue aqq : aqqs) {
			
			Logger.info("=======================[APK Build START] current apply is ：[{},identifier：{}]=======================",
					aqq.getAccount(),aqq.getIdentifier());
			try {
				// #0复制原始代码到用户目录指定目录
				File desc = new File(USER_SOURCECODE_PATH + File.separator + aqq.getAccount());
				if(desc.exists()){
					FileUtils.deleteDirectory(desc);
				}
				desc.mkdirs();
					
				File src = new File(SOURCECODE_PATH);
				src.mkdirs();
			
				FileUtils.copyDirectoryToDirectory(src, desc);
					
				// #1定位到用户目录下，清单文件替换
				String androidManifestPath = USER_SOURCECODE_PATH + File.separator + aqq.getAccount() + File.separator + APP_FOLDER_NAME + File.separator + ANDROIDMANIFEST_PATH;
				
				// 解析XML资源文件，替换目标值。
				File xmlResource = new File(androidManifestPath);
				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build(xmlResource);
				Element rootEl = doc.getRootElement();
				List<Element> list = rootEl.getChildren("application");
				
				List<Element> metas = list.get(0).getChildren("meta-data");
				Namespace type = Namespace.getNamespace("android","http://schemas.android.com/apk/res/android");
				
				//获取店铺的URL
				ShopSite ss = new ShopSite();
				ss.setDisemail(aqq.getAccount());
				ss = shopSiteMapper.selectByCondition(ss);
				
				for (Element element : metas) {
					// 遍历查找，替换渠道为用户的账户名
					if(APP_CHANNEL_KEY.equals(element.getAttributeValue("name", type))){
						element.setAttribute("value", "ACCOUNT_" + aqq.getAccount(), type);
					}
					// 遍历查找，替换默认请求连接
					if(APP_DEFAULT_HOST.equals(element.getAttributeValue("name", type))){
						element.setAttribute("value", ss.getSiteurl(), type);
					}
				}
				
				OutputStream os = new FileOutputStream(androidManifestPath); // 定义文件输出流
				Format format = Format.getPrettyFormat(); // 定义Format格式 xml格式
				format.setEncoding("UTF-8"); // 设置xml为 UTF-8编码
				XMLOutputter output = new XMLOutputter(format); // 创建
												   // XMLOutput对象
												   // 通过制定格式
				output.output(doc, os); // 将Document对象输出到xml文档中
				os.flush();
				os.close();
				Logger.info("[APK Build] modify target file [{}] complete",androidManifestPath);
				
				//#2判断用户是否上传了app图标图片,上传了就进行下载替换
				String iconTarget=USER_SOURCECODE_PATH + File.separator + aqq.getAccount() + File.separator+APP_FOLDER_NAME;
				if(aqq.getAppIconUrl()!=null){
					this.downLoadIcon(aqq.getAppIconUrl(),ICON_FILE_NAME,iconTarget);
					Logger.info("[APK Build] replacing app icon file complete,path:[{}]",aqq.getAppIconUrl());
				}
				if(aqq.getAppStartIconUrl()!=null){
					this.downLoadIcon(aqq.getAppStartIconUrl(),START_ICON_FILE_NAME,iconTarget);
					Logger.info("[APK Build] replacing app start icon file complete,path:[{}]",aqq.getAppStartIconUrl());
				}
				
				/*// #2图标文件替换
				//获取已上传的icon文件
				String sourceIcon = ICON_FILE_PATH + aqq.getAccount() + File.separator +  ICON_FILE_NAME;
				Logger.info("[APK Build] replacing app icon file,source icon file:{}......",sourceIcon);
				File iconFile = new File(sourceIcon);
				
				//用户上传了icon就进行替换
				if(iconFile.exists()){
					//替换目标文件
					String targetIconFile = USER_SOURCECODE_PATH + File.separator + aqq.getAccount() + File.separator + APP_FOLDER_NAME + File.separator + ICON_FILE_TARGET_PATH;
					FileUtils.copyFileToDirectory(iconFile, new File(targetIconFile));
					Logger.info("[APK Build] replacing app icon file complete,path:[{}]",targetIconFile);
				}else{
					Logger.info("[APK Build] user's icon doesn't exist,use default app icon");
				}*/
				
				// #3 strings.xml文件内容替换
				// 目标文件路径
				String targetFilePath = USER_SOURCECODE_PATH + File.separator + aqq.getAccount() + File.separator + APP_FOLDER_NAME + File.separator
						+ STRINGS_RESOURCE_FILE;
				Logger.info("[APK Build] target file path:[{}]",
						targetFilePath);
				// 解析XML资源文件，替换目标值。
				xmlResource = new File(targetFilePath);

				builder = new SAXBuilder();
				doc = builder.build(xmlResource);
				rootEl = doc.getRootElement();
				list = rootEl.getChildren("string");
				// 遍历查找，找到匹配的属性就替换属性值对应的内容
				for (Element element : list) {
					if (APP_NAME
							.equals(element.getAttribute("name").getValue())) {
						Logger.info("[APK Build] app name:{} update complete",
								APP_NAME);
						element.setText(aqq.getIdentifier());
						break;
					}
				}
				os = new FileOutputStream(targetFilePath); // 定义文件输出流
				format = Format.getPrettyFormat(); // 定义Format格式 xml格式
				format.setEncoding("UTF-8"); // 设置xml为 UTF-8编码
				output = new XMLOutputter(format); // 创建
																// XMLOutput对象
																// 通过制定格式
				output.output(doc, os); // 将Document对象输出到xml文档中
				os.flush();
				os.close();
				Logger.info("[APK Build] modify target file [{}] complete",targetFilePath);
				
				// #4打包
				GradleConnector connector = GradleConnector.newConnector();
				String projectPath = USER_SOURCECODE_PATH + File.separator + aqq.getAccount() + File.separator + APP_FOLDER_NAME;
				
				Logger.info("[APK Build] Project path is [{}],now we are building...",projectPath);
				
		        connector.forProjectDirectory(new File(projectPath));
		        ProjectConnection connection = connector.connect();
		        try {
		            //配置构建
		            BuildLauncher launcher = connection.newBuild();
		            launcher.forTasks("build");
		            //launcher.setStandardOutput(System.out); 忽略掉打包过程中的输出
		            launcher.setStandardError(System.err); //打印异常输出

		            //执行构建
		            launcher.run();
		        } catch(Exception e){
		        	Logger.info("[APK Build] Project build fail!account is [{}] ",aqq.getAccount());
		        	Logger.error(e.getMessage());
		        	continue;
		        }finally {
		            //清理工作空间，并且修改申请的状态
		            connection.close();
		        }
		        aqq.setIsSuccess(true);
				apkApplyService.saveOrUpdateActive(aqq);
			}catch(Exception e){
				Logger.error("app打包发生异常{}",e);
				continue;
			}
		}
	}
	private void downLoadIcon(String downLoadUrl, String iconFileName, String iconTarget) throws HttpException, IOException {
		GetMethod getMethod= new GetMethod(downLoadUrl);;
		FileOutputStream fos=null;
		HttpClient httpClient = new HttpClient();  
		try {
			httpClient.executeMethod(getMethod);
			
			InputStream inputStream = getMethod.getResponseBodyAsStream();
			File tempFile=new File(iconTarget+File.separator+ICON_FILE_TARGET_PATH+File.separator+iconFileName);
			if(tempFile.exists()){//先删除源码中自带的图片,替换为下载的图片
				FileUtils.deleteQuietly(tempFile);
			}
			fos = new FileOutputStream(iconTarget+File.separator+ICON_FILE_TARGET_PATH+File.separator+iconFileName);  
			byte[] data = new byte[1024];  
			int len = 0;  
			while ((len = inputStream.read(data)) != -1) {  
			    fos.write(data, 0, len);  
			}
		} catch (Exception e) {
			Logger.error("app打包下载图片发生异常!参数{},信息{}", downLoadUrl+","+iconFileName,e);
		}finally{
			getMethod.releaseConnection();
			if(null != fos) {
				fos.close();				
			}
		}
        
	}
	
	@Subscribe
	public void apkSoruceUpLoad(ApkSourceUploadEvent event) {
		 byte[] buffer = new byte[1024];
		//解压文件
		try {  
            ZipInputStream apkSourceZip=new ZipInputStream(new FileInputStream(SOURCECODE_PATH+"//"+APP_FOLDER_NAME+".zip"));//输入源zip路径  
            String Parent=SOURCECODE_PATH; //输出路径（文件夹目录）  
            ZipEntry apkSouceEntry=apkSourceZip.getNextEntry();  
            File oldFile=new File(SOURCECODE_PATH+"//"+APP_FOLDER_NAME);
            if(oldFile.exists() && oldFile.isDirectory()){
            	FileUtils.deleteDirectory(oldFile);
            }
            while(apkSouceEntry!=null){
            	String fileName = apkSouceEntry.getName();
                File newFile = new File(Parent + File.separator + fileName);
                if(apkSouceEntry.isDirectory()){
                	new File(newFile.getParent()).mkdirs();
                }else{
                	new File(newFile.getParent()).mkdirs();
                	 FileOutputStream fos = new FileOutputStream(newFile);
                	  
                     int len;
                     while ((len = apkSourceZip.read(buffer)) > 0) {
                         fos.write(buffer, 0, len);
                     }
                     fos.close();
                     
                }
                apkSouceEntry = apkSourceZip.getNextEntry();
            }
            Logger.info("apk上传源码解压成功！"); 
            apkSourceZip.close();
          //删除压缩包
            File zip=new File(SOURCECODE_PATH+File.separator+APP_FOLDER_NAME+".zip");
            if(zip.exists()){
            	zip.delete();
            }
            this.apkUpgradePackage(event);
        } catch (FileNotFoundException e) {
        	Logger.info("apk上传源码解压异常1{}", e); 
        } catch (IOException e) {
        	Logger.info("apk上传源码解压异常2{}", e); 
		}  

	}
	@SuppressWarnings("unchecked")
	private void apkUpgradePackage(ApkSourceUploadEvent event) {
		ApkVersion apkVersion = event.getApkVersion();
		//查询apk_version表中所有记录
		List<ApkVersion> apkVersionList= apkVersionMapper.getAll();
		if(apkVersionList==null || apkVersionList.size()<=0){
			return;
		}
		for(ApkVersion oldApkVersion:apkVersionList){
			String channelName = oldApkVersion.getChannelName();
			String account=channelName.substring(8);
			ApkApplyQueue apkApplyRecord = apkApplyService.selectByAccount(account);
			
			try{
				// #0复制原始代码到用户目录指定目录
				File desc = new File(USER_SOURCECODE_PATH + File.separator + apkApplyRecord.getAccount());
				if(desc.exists()){
					FileUtils.deleteDirectory(desc);
				}
				desc.mkdirs();
				
				File src = new File(SOURCECODE_PATH);
				src.mkdirs();
				FileUtils.copyDirectoryToDirectory(src, desc);
				// #1定位到用户目录下，清单文件替换
				String androidManifestPath = USER_SOURCECODE_PATH + File.separator + apkApplyRecord.getAccount() + File.separator + APP_FOLDER_NAME + File.separator + ANDROIDMANIFEST_PATH;
				
				// 解析XML资源文件，替换目标值。
				File xmlResource = new File(androidManifestPath);
				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build(xmlResource);
				Element rootEl = doc.getRootElement();
				List<Element> list = rootEl.getChildren("application");
				
				List<Element> metas = list.get(0).getChildren("meta-data");
				Namespace type = Namespace.getNamespace("android","http://schemas.android.com/apk/res/android");
				
				//获取店铺的URL
				ShopSite ss = new ShopSite();
				ss.setDisemail(apkApplyRecord.getAccount());
				ss = shopSiteMapper.selectByCondition(ss);
				
				for (Element element : metas) {
					// 遍历查找，替换渠道为用户的账户名
					if(APP_CHANNEL_KEY.equals(element.getAttributeValue("name", type))){
						element.setAttribute("value", "ACCOUNT_" + apkApplyRecord.getAccount(), type);
					}
					// 遍历查找，替换默认请求连接
					if(APP_DEFAULT_HOST.equals(element.getAttributeValue("name", type))){
						element.setAttribute("value", ss.getSiteurl(), type);
					}
				}
				
				OutputStream os = new FileOutputStream(androidManifestPath); // 定义文件输出流
				Format format = Format.getPrettyFormat(); // 定义Format格式 xml格式
				format.setEncoding("UTF-8"); // 设置xml为 UTF-8编码
				XMLOutputter output = new XMLOutputter(format); // 创建
												   // XMLOutput对象
												   // 通过制定格式
				output.output(doc, os); // 将Document对象输出到xml文档中
				os.flush();
				os.close();
				Logger.info("[APK UpgradeBuild] modify target file [{}] complete",androidManifestPath);
				
				//#2判断用户是否上传了app图标图片,上传了就进行下载替换
				String iconTargetPath=USER_SOURCECODE_PATH + File.separator + apkApplyRecord.getAccount() + File.separator + APP_FOLDER_NAME;
				if(apkApplyRecord.getAppIconUrl()!=null){
					this.downLoadIcon(apkApplyRecord.getAppIconUrl(),ICON_FILE_NAME,iconTargetPath);
					Logger.info("[APK Build] replacing app icon file complete,path:[{}]",apkApplyRecord.getAppIconUrl());
				}
				if(apkApplyRecord.getAppStartIconUrl()!=null){
					this.downLoadIcon(apkApplyRecord.getAppStartIconUrl(),START_ICON_FILE_NAME,iconTargetPath);
					Logger.info("[APK Build] replacing app start icon file complete,path:[{}]",apkApplyRecord.getAppStartIconUrl());
				}
				
				// #3 strings.xml文件内容替换
				// 目标文件路径
				String targetFilePath = USER_SOURCECODE_PATH + File.separator + apkApplyRecord.getAccount() + File.separator + APP_FOLDER_NAME + File.separator
						+ STRINGS_RESOURCE_FILE;
				Logger.info("[APK UpgradeBuild] target file path:[{}]",targetFilePath);
				// 解析XML资源文件，替换目标值。
				xmlResource = new File(targetFilePath);
	
				builder = new SAXBuilder();
				doc = builder.build(xmlResource);
				rootEl = doc.getRootElement();
				list = rootEl.getChildren("string");
				// 遍历查找，找到匹配的属性就替换属性值对应的内容
				for (Element element : list) {
					if (APP_NAME
							.equals(element.getAttribute("name").getValue())) {
						Logger.info("[APK UpgradeBuild] app name:{} update complete",APP_NAME);
						element.setText(apkApplyRecord.getIdentifier());
						break;
					}
				}
				os = new FileOutputStream(targetFilePath); // 定义文件输出流
				format = Format.getPrettyFormat(); // 定义Format格式 xml格式
				format.setEncoding("UTF-8"); // 设置xml为 UTF-8编码
				output = new XMLOutputter(format); // 创建
																// XMLOutput对象
																// 通过制定格式
				output.output(doc, os); // 将Document对象输出到xml文档中
				os.flush();
				os.close();
				Logger.info("[APK UpgradeBuild] modify target file [{}] complete",targetFilePath);
				
				// #4打包
				GradleConnector connector = GradleConnector.newConnector();
				String projectPath = USER_SOURCECODE_PATH + File.separator + apkApplyRecord.getAccount() + File.separator + APP_FOLDER_NAME;
				Logger.info("[APK UpgradeBuild] Project path is [{}],now we are building...",projectPath);
				
		        connector.forProjectDirectory(new File(projectPath));
		        ProjectConnection connection = connector.connect();
		        try {
		            //配置构建
		            BuildLauncher launcher = connection.newBuild();
		            launcher.forTasks("build");
		            //launcher.setStandardOutput(System.out); 忽略掉打包过程中的输出
		            launcher.setStandardError(System.err); //打印异常输出
		            //执行构建
		            launcher.run();
		            
		            //更新apkVersion信息
		            oldApkVersion.setCode(apkVersion.getCode());   
			        oldApkVersion.setDescription(apkVersion.getDescription());
			        oldApkVersion.setSize(apkVersion.getSize());
			        oldApkVersion.setVersion(apkVersion.getVersion());
			        oldApkVersion.setForceUpdate(apkVersion.getForceUpdate());
			        oldApkVersion.setUpdateTime(new Date());
			        oldApkVersion.setApkName(apkApplyRecord.getIdentifier());
			        apkVersionMapper.updateByPrimaryKeySelective(oldApkVersion);
		        } catch(Exception e){
		        	Logger.info("[APK UpgradeBuild] Project build fail!account is [{}] ",apkApplyRecord.getAccount());
		        	Logger.error(e.getMessage());
		        	continue;
		        }finally {
		            //清理工作空间，并且修改申请的状态
		            connection.close();
		        }
			}catch(Exception e){
	        	Logger.info("[APK UpgradeBuild] Project build fail!account is [{}] ",apkApplyRecord.getAccount());
	        	Logger.error(e.getMessage());
	        	continue;
			}
		}
	}
}
