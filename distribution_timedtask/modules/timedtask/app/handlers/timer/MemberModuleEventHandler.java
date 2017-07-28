package handlers.timer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import entity.timer.ApkApplyQueue;
import entity.timer.Constant;
import entity.timer.DisAccount;
import entity.timer.DisApply;
import entity.timer.DisBill;
import entity.timer.ShopSite;
import events.timer.AccountPerioEvent;
import events.timer.ActiveStateEvent;
import events.timer.ApkApplyEvent;
import events.timer.WithdrawApplyEvent;
import mapper.timer.DisAccountMapper;
import mapper.timer.DisApplyMapper;
import mapper.timer.ShopSiteMapper;
import play.Configuration;
import play.Logger;
import play.Play;
import service.timer.IAccountPeriodService;
import service.timer.IActiveService;
import service.timer.IApkApplyService;
import service.timer.IDisBillService;
import service.timer.ISequenceService;
import util.timer.HttpUtil;
import util.timer.IDUtils;

public class MemberModuleEventHandler {

	@Inject
	private IActiveService activeService;

	@Inject
	private DisApplyMapper applyMapper;

	@Inject
	private ISequenceService sequenceService;

	@Inject
	private DisAccountMapper disAccountMapper;

	@Inject
	private IDisBillService billService;

	@Inject
	private IApkApplyService apkApplyService;

	@Inject
	private ShopSiteMapper shopSiteMapper;
	
	@Inject
	private IAccountPeriodService apService;

	private static String APP_FOLDER_NAME = "";// app项目目录的具体名字
	private static String STRINGS_RESOURCE_FILE = "";// strings.xml文件位置
	private static String APP_NAME = "";// 应用目录名
	private static Integer REBUILT_PER_MINUTE;// 每分钟打包的个数

	private static String ICON_FILE_NAME = "";// 图标文件的名字
	private static String ICON_FILE_PATH = "";// 需要替换的Icon文件的目录
	private static String ICON_FILE_TARGET_PATH = "";// 待替换图标所在的文位置

	private static String APP_CHANNEL_KEY = "";// 渠道属性key
	private static String APP_DEFAULT_HOST = "";// APK打开后默认的URL

	private static String SOURCECODE_PATH = "";// 项目源代码路径
	private static String USER_SOURCECODE_PATH = "";// 用户项目源代码路径
	private static String ANDROIDMANIFEST_PATH = "";// 项目下清单文件所在路径

	static {
		if (SOURCECODE_PATH == "") {
			Configuration conf = Play.application().configuration()
					.getConfig("sourcecode");
			SOURCECODE_PATH = conf.getString("path");
		}
		if (USER_SOURCECODE_PATH == "") {
			Configuration conf = Play.application().configuration()
					.getConfig("usersourcecode");
			USER_SOURCECODE_PATH = conf.getString("path");
		}
		if (ANDROIDMANIFEST_PATH == "") {
			Configuration conf = Play.application().configuration()
					.getConfig("androidmanifest");
			ANDROIDMANIFEST_PATH = conf.getString("path");
		}
		if (APP_FOLDER_NAME == "") {
			Configuration conf = Play.application().configuration()
					.getConfig("appfolder");
			APP_FOLDER_NAME = conf.getString("name");
		}
		if (STRINGS_RESOURCE_FILE == "") {
			Configuration conf = Play.application().configuration()
					.getConfig("stringresourcefile");
			STRINGS_RESOURCE_FILE = conf.getString("path");
		}
		if (APP_NAME == "") {
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
		if (ICON_FILE_NAME == "") {
			Configuration conf = Play.application().configuration()
					.getConfig("appicon");
			ICON_FILE_NAME = conf.getString("filename");
		}
		if (ICON_FILE_PATH == "") {
			Configuration conf = Play.application().configuration()
					.getConfig("appicon");
			ICON_FILE_PATH = conf.getString("path");
		}
		if (ICON_FILE_TARGET_PATH == "") {
			Configuration conf = Play.application().configuration()
					.getConfig("appicon");
			ICON_FILE_TARGET_PATH = conf.getString("targetdir");
		}

	}

	/**
	 * 销售订单推送到b2c
	 * 
	 * @param event
	 */
	@Subscribe
	public void execute(ActiveStateEvent event) {
		if (SystemEventHandler.run_timed_task) {
			Logger.info("[memberEvent]========	update activity information at every 24:00	========[memberEvent]");
			activeService.execute();
		}
	}

	/**
	 * 定时发送提现申请到M站
	 * 
	 * @Author LSL on 2016-09-28 09:52:32
	 */
	@Subscribe
	public void sendWithdrawApplyToMsite(WithdrawApplyEvent event) {
		if (SystemEventHandler.run_timed_task) {
			Logger.info("[memberEvent]========	sendWithdrawApplyToMsite start	========[memberEvent]");
			// 查询申请类型为提现、申请状态为处理中、提现账户类型为M站的提现申请
			List<DisApply> das = applyMapper.findWithdrawToMsiteApply();
			if (!CollectionUtils.isEmpty(das)) {
				JSONObject temp = null;
				String res = null;
				DisApply apply = null;
				int line = 0;
				DisAccount account = null;
				BigDecimal frozenAmount = null;
				DisBill bill = null;
				for (DisApply da : das) {
					// Logger.debug("sendWithdrawApplyToMsite    WithdrawApplyNo----->"
					// + da.getOnlineApplyNo());
					Logger.info(
							"[memberEvent]========	sendWithdrawApplyToMsite    WithdrawApplyNo[{}]	========[memberEvent]",
							da.getOnlineApplyNo());
					// Logger.debug("sendWithdrawApplyToMsite    disemail----->"
					// + da.getEmail());
					// 发送提现申请信息到M站
					temp = new JSONObject();
					temp.put("disemail", String.valueOf(da.getEmail()));
					temp.put("orderNo", String.valueOf(da.getOnlineApplyNo()));
					temp.put(
							"amount",
							da.getWithdrawAmount()
									.setScale(2, BigDecimal.ROUND_HALF_UP)
									.toString());
					temp.put("timestamp", String.valueOf(new Date().getTime()));
					res = HttpUtil.sendWithdrawApply(temp);
					Logger.debug("sendWithdrawApplyToMsite    res----->" + res);
					if (JSON.parseObject(res).getBoolean("result")) {
						// 更新申请状态为已完成
						apply = applyMapper.selectByPrimaryKey(da.getId());
						apply.setAuditState(Constant.AUDIT_PASS);
						apply.setAuditReasons("已确认");
						apply.setUpdatedate(new Date());
						line = applyMapper.updateByPrimaryKeySelective(apply);
						// Logger.debug("sendWithdrawApplyToMsite    [update DisApply]line----->"
						// + line);

						if (line == 1) {
							// 扣减冻结余额
							account = disAccountMapper.getDisAccountByEmail(da
									.getEmail());
							frozenAmount = account.getFrozenAmount();
							account.setFrozenAmount(frozenAmount.subtract(da
									.getWithdrawAmount()));
							line = disAccountMapper
									.updateByPrimaryKeySelective(account);
							// Logger.debug("sendWithdrawApplyToMsite    [update DisAccount]line----->"
							// + line);

							// 新增交易记录
							bill = new DisBill();
							bill.setAmount(da.getWithdrawAmount());
							bill.setPurpose("2");
							String serialNumber = IDUtils
									.getOnlineTopUpCode(
											"SN",
											sequenceService
													.selectNextValue("WITHDRAW_AMOUNT_NO"));
							bill.setSerialNumber(serialNumber);
							bill.setPaymentType("余额提现");
							bill.setApplyId(apply.getId());
							bill.setSourceCard(apply.getEmail());
							bill.setBalance(account.getBalance());
							bill.setAccountId(account.getId());
							bill.setSources(0);// 子交易记录
							line = billService.save(bill);
							bill.setId(null);
							bill.setSources(3);// 总交易记录
							line = billService.save(bill);
						}
					}
				}
			}
		}
	}

	/**
	 * 定时检查Akp打包申请，对最先的申请进行打包
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	@Subscribe
	public void apkApplyCheck(ApkApplyEvent event) {
		if (SystemEventHandler.run_timed_task) {
			List<ApkApplyQueue> aqqs = apkApplyService
					.getPriorApply(REBUILT_PER_MINUTE);
			for (ApkApplyQueue aqq : aqqs) {

				Logger.info(
						"=======================[APK Build START] current apply is ：[{},identifier：{}]=======================",
						aqq.getAccount(), aqq.getIdentifier());

				try {

					// #0复制原始代码到用户目录指定目录
					File desc = new File(USER_SOURCECODE_PATH + File.separator
							+ aqq.getAccount());
					desc.mkdirs();

					File src = new File(SOURCECODE_PATH);
					src.mkdirs();

					FileUtils.copyDirectoryToDirectory(src, desc);

					// #1定位到用户目录下，清单文件替换
					String androidManifestPath = USER_SOURCECODE_PATH
							+ File.separator + aqq.getAccount()
							+ File.separator + APP_FOLDER_NAME + File.separator
							+ ANDROIDMANIFEST_PATH;

					// 解析XML资源文件，替换目标值。
					File xmlResource = new File(androidManifestPath);
					SAXBuilder builder = new SAXBuilder();
					Document doc = builder.build(xmlResource);
					Element rootEl = doc.getRootElement();
					List<Element> list = rootEl.getChildren("application");

					List<Element> metas = list.get(0).getChildren("meta-data");
					Namespace type = Namespace.getNamespace("android",
							"http://schemas.android.com/apk/res/android");

					// 获取店铺的URL
					ShopSite ss = new ShopSite();
					ss.setDisemail(aqq.getAccount());
					ss = shopSiteMapper.selectByCondition(ss);

					for (Element element : metas) {
						// 遍历查找，替换渠道为用户的账户名
						if (APP_CHANNEL_KEY.equals(element.getAttributeValue(
								"name", type))) {
							element.setAttribute("value",
									"ACCOUNT_" + aqq.getAccount(), type);
						}
						// 遍历查找，替换默认请求连接
						if (APP_DEFAULT_HOST.equals(element.getAttributeValue(
								"name", type))) {
							element.setAttribute("value",ss.getSiteurl(), type);
						}
					}

					OutputStream os = new FileOutputStream(androidManifestPath); // 定义文件输出流
					Format format = Format.getPrettyFormat(); // 定义Format格式
																// xml格式
					format.setEncoding("UTF-8"); // 设置xml为 UTF-8编码
					XMLOutputter output = new XMLOutputter(format); // 创建
					// XMLOutput对象
					// 通过制定格式
					output.output(doc, os); // 将Document对象输出到xml文档中
					os.flush();
					Logger.info("[APK Build] modify target file [{}] complete",
							androidManifestPath);

					// #2图标文件替换
					// 获取已上传的icon文件
					String sourceIcon = ICON_FILE_PATH + aqq.getAccount()
							+ File.separator + ICON_FILE_NAME;
					Logger.info(
							"[APK Build] replacing app icon file,source icon file:{}......",
							sourceIcon);
					File iconFile = new File(sourceIcon);

					// 用户上传了icon就进行替换
					if (iconFile.exists()) {
						// 替换目标文件
						String targetIconFile = USER_SOURCECODE_PATH
								+ File.separator + aqq.getAccount()
								+ File.separator + APP_FOLDER_NAME
								+ File.separator + ICON_FILE_TARGET_PATH;
						FileUtils.copyFileToDirectory(iconFile, new File(
								targetIconFile));
						Logger.info(
								"[APK Build] replacing app icon file complete,path:[{}]",
								targetIconFile);
					} else {
						Logger.info("[APK Build] user's icon doesn't exist,use default app icon");
					}

					// #3 strings.xml文件内容替换
					// 目标文件路径
					String targetFilePath = USER_SOURCECODE_PATH
							+ File.separator + aqq.getAccount()
							+ File.separator + APP_FOLDER_NAME + File.separator
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
						if (APP_NAME.equals(element.getAttribute("name")
								.getValue())) {
							Logger.info(
									"[APK Build] app name:{} update complete",
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
					Logger.info("[APK Build] modify target file [{}] complete",
							targetFilePath);

					// #4打包
					GradleConnector connector = GradleConnector.newConnector();
					String projectPath = USER_SOURCECODE_PATH + File.separator
							+ aqq.getAccount() + File.separator
							+ APP_FOLDER_NAME;

					Logger.info(
							"[APK Build] Project path is [{}],now we are building...",
							projectPath);

					connector.forProjectDirectory(new File(projectPath));
					ProjectConnection connection = connector.connect();
					try {
						// 配置构建
						BuildLauncher launcher = connection.newBuild();
						launcher.forTasks("build");
						// launcher.setStandardOutput(System.out); 忽略掉打包过程中的输出
						launcher.setStandardError(System.err); // 打印异常输出

						// 执行构建
						launcher.run();
					} catch (Exception e) {
						Logger.info(
								"[APK Build] Project build fail!account is [{}] ",
								aqq.getAccount());
						Logger.error(e.getMessage());
						return;
					} finally {
						// 清理工作空间，并且修改申请的状态
						connection.close();
					}

					aqq.setIsSuccess(true);
					apkApplyService.saveOrUpdateActive(aqq);

				} catch (Exception e) {
					e.printStackTrace();
					Logger.error(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 更新账期状态：
	 *  1 未生效 次日生效
	 * @author zbc
	 * @since 2016年11月20日 下午5:31:39
	 */
	@Subscribe
	public void dealAccountPeriod(AccountPerioEvent event ){
		Logger.info("凌晨更新账期状态:[{}]"+new Date(),apService.dealAccountPeriod());
	}
	
}
