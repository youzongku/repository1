package extensions.dismember;

import java.util.List;
import java.util.Set;

import org.apache.camel.builder.RouteBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

import extensions.IModule;
import extensions.ModuleSupport;
import extensions.camel.ICamelExtension;
import extensions.common.CommonModule;
import extensions.event.IEventExtension;
import extensions.filter.IFilter;
import extensions.filter.IFilterExtension;
import extensions.runtime.IApplication;
import handlers.dismember.AccessLogHandler;
import handlers.dismember.AccountPeriodHandler;
import handlers.dismember.AccountPeriodTextReminderHandler;
import handlers.dismember.ActiveHandler;
import handlers.dismember.AkpApplyHandler;
import handlers.dismember.BackUpApOrderDetailHandler;
import handlers.dismember.DefaultAccountPeriodReminderHandler;
import handlers.dismember.OrderHandler;
import mapper.ap.ApOrderDetailMapper;
import mapper.dismember.AccountOperationRecordMapper;
import mapper.dismember.AccountPeriodMasterMapper;
import mapper.dismember.AccountPeriodSlaveMapper;
import mapper.dismember.AdminOperateRecordMapper;
import mapper.dismember.ApBillMapper;
import mapper.dismember.ApBillOrderMappingMapper;
import mapper.dismember.ApChangeMapper;
import mapper.dismember.ApChangeMappingMapper;
import mapper.dismember.ApOptRecordMapper;
import mapper.dismember.ApReminderSettingLogMapper;
import mapper.dismember.ApReminderSettingMapper;
import mapper.dismember.ApkApplyQueueMapper;
import mapper.dismember.ApkVersionMapper;
import mapper.dismember.CommonFieldMapper;
import mapper.dismember.CreditOperationRecordMapper;
import mapper.dismember.CustomerCreditMapper;
import mapper.dismember.CustomerTypeMapper;
import mapper.dismember.DisAccountMapper;
import mapper.dismember.DisActiveMapper;
import mapper.dismember.DisAddressMapper;
import mapper.dismember.DisApplyFileMapper;
import mapper.dismember.DisApplyMapper;
import mapper.dismember.DisAreaMapper;
import mapper.dismember.DisBankMapper;
import mapper.dismember.DisBillMapper;
import mapper.dismember.DisCityMapper;
import mapper.dismember.DisCouponsMapper;
import mapper.dismember.DisCreditMapper;
import mapper.dismember.DisEmailVerifyMapper;
import mapper.dismember.DisHeaderMapper;
import mapper.dismember.DisHeaderSalesmanMapper;
import mapper.dismember.DisMemberMapper;
import mapper.dismember.DisMemberMenuMapper;
import mapper.dismember.DisMenuMapper;
import mapper.dismember.DisModeMapper;
import mapper.dismember.DisProvinceMapper;
import mapper.dismember.DisRankMapper;
import mapper.dismember.DisRegisterApplyMapper;
import mapper.dismember.DisRoleMapper;
import mapper.dismember.DisSalesmanMapper;
import mapper.dismember.DisSalesmanMemberMapper;
import mapper.dismember.DisShopDpLogMapper;
import mapper.dismember.DisShopMapper;
import mapper.dismember.DisTransferAccountMapper;
import mapper.dismember.DisWithdrawAccountMapper;
import mapper.dismember.DisWithdrawLimitMapper;
import mapper.dismember.EmailAccountMapper;
import mapper.dismember.EmailTemplateMapper;
import mapper.dismember.EmailTypeMapper;
import mapper.dismember.EmailVariableMapper;
import mapper.dismember.EmpSalesManMapperMapper;
import mapper.dismember.ExportModelMapper;
import mapper.dismember.FileOperationRecordMapper;
import mapper.dismember.FindPasswordRecordMapper;
import mapper.dismember.HeadImgMapper;
import mapper.dismember.InvoiceMapper;
import mapper.dismember.LoginHistoryMapper;
import mapper.dismember.NodeHeaderMapper;
import mapper.dismember.OperationRecordMapper;
import mapper.dismember.OrderByApMapper;
import mapper.dismember.OrderConfigMapper;
import mapper.dismember.OrganizationHeaderMapper;
import mapper.dismember.OrganizationMapper;
import mapper.dismember.PackageMailLogMapper;
import mapper.dismember.PaymentConditionMapper;
import mapper.dismember.PaymentMapperMapper;
import mapper.dismember.PaymentMethodMapper;
import mapper.dismember.PhoneVerifyMapper;
import mapper.dismember.ReceiptModeMapper;
import mapper.dismember.RoleMenusMapper;
import mapper.dismember.SequenceMapper;
import mapper.dismember.ShopCategoryMapper;
import mapper.dismember.ShopPlatformMapper;
import mapper.dismember.ShopSiteMapper;
import mapper.dismember.UserRankHistoryMapper;
import mapper.dismember.VipInviteCodeMapper;
import mybatis.MyBatisExtension;
import mybatis.MyBatisService;
import services.dismember.IAccountPeriodService;
import services.dismember.IActiveService;
import services.dismember.IApkApplyService;
import services.dismember.IApplyService;
import services.dismember.ICaptchaService;
import services.dismember.ICommonExportService;
import services.dismember.ICommonFieldService;
import services.dismember.ICreditOperationRecordService;
import services.dismember.ICreditService;
import services.dismember.IDisAccountService;
import services.dismember.IDisAddressService;
import services.dismember.IDisAreaService;
import services.dismember.IDisBankService;
import services.dismember.IDisBillService;
import services.dismember.IDisCityService;
import services.dismember.IDisMemberService;
import services.dismember.IDisProvinceService;
import services.dismember.IDisRankService;
import services.dismember.IDisRoleService;
import services.dismember.IDisSalesmanService;
import services.dismember.IDisShopService;
import services.dismember.IDisTransferAccountService;
import services.dismember.IEmailService;
import services.dismember.IFindPasswordByEmailService;
import services.dismember.IHttpService;
import services.dismember.ILoginService;
import services.dismember.IMenuService;
import services.dismember.IOrganizationService;
import services.dismember.IPackageMailLogService;
import services.dismember.IPaymentMethodService;
import services.dismember.ISequenceService;
import services.dismember.IVipService;
import services.dismember.IWAccountService;
import services.dismember.impl.AccountPeriodService;
import services.dismember.impl.ActiveService;
import services.dismember.impl.ApkApplyService;
import services.dismember.impl.ApplyService;
import services.dismember.impl.CaptchaService;
import services.dismember.impl.CommonExportService;
import services.dismember.impl.CommonFieldService;
import services.dismember.impl.CreditOperationRecordService;
import services.dismember.impl.CreditService;
import services.dismember.impl.DisAccountService;
import services.dismember.impl.DisAddressService;
import services.dismember.impl.DisAreaService;
import services.dismember.impl.DisBankService;
import services.dismember.impl.DisBillService;
import services.dismember.impl.DisCityService;
import services.dismember.impl.DisMemberService;
import services.dismember.impl.DisProvinceService;
import services.dismember.impl.DisRankService;
import services.dismember.impl.DisRoleService;
import services.dismember.impl.DisSalesmanService;
import services.dismember.impl.DisShopService;
import services.dismember.impl.DisTransferAccountService;
import services.dismember.impl.EmailService;
import services.dismember.impl.FindPasswordByEmailService;
import services.dismember.impl.HttpServiceImpl;
import services.dismember.impl.LoginService;
import services.dismember.impl.MenuServiceImpl;
import services.dismember.impl.OrganizationService;
import services.dismember.impl.PackageMailLogService;
import services.dismember.impl.PaymentMethodService;
import services.dismember.impl.SequenceService;
import services.dismember.impl.VipService;
import services.dismember.impl.WAccountService;
import timer.dismember.ActiveTimerTrigger;

/**
 * Created by luwj on 2015/11/20.
 */
public class DisMemberModule extends ModuleSupport
		implements MyBatisExtension, IEventExtension, ICamelExtension, IFilterExtension {

	@SuppressWarnings("unchecked")
	public Set<Class<? extends IModule>> getDependentModules() {
		return Sets.newHashSet(CommonModule.class);
	}

	@Override
	public Module getModule(IApplication iApplication) {
		return new AbstractModule() {
			@Override
			protected void configure() {
				bind(IDisMemberService.class).to(DisMemberService.class);
				bind(ICaptchaService.class).to(CaptchaService.class);
				bind(IDisShopService.class).to(DisShopService.class);
				bind(IFindPasswordByEmailService.class).to(FindPasswordByEmailService.class);
				bind(IApplyService.class).to(ApplyService.class);
				bind(IDisProvinceService.class).to(DisProvinceService.class);
				bind(IDisCityService.class).to(DisCityService.class);
				bind(IDisAreaService.class).to(DisAreaService.class);
				bind(IDisRoleService.class).to(DisRoleService.class);
				bind(ILoginService.class).to(LoginService.class);
				bind(IDisAddressService.class).to(DisAddressService.class);
				bind(IDisAccountService.class).to(DisAccountService.class);
				bind(IDisBillService.class).to(DisBillService.class);
				bind(IDisBankService.class).to(DisBankService.class);
				bind(IDisRankService.class).to(DisRankService.class);
				bind(IMenuService.class).to(MenuServiceImpl.class);
				bind(ICommonFieldService.class).to(CommonFieldService.class);
				bind(IDisTransferAccountService.class).to(DisTransferAccountService.class);
				bind(ICreditService.class).to(CreditService.class);
				bind(ICreditOperationRecordService.class).to(CreditOperationRecordService.class);
				bind(IActiveService.class).to(ActiveService.class);
				bind(ISequenceService.class).to(SequenceService.class);
				bind(IOrganizationService.class).to(OrganizationService.class);
				bind(IDisSalesmanService.class).to(DisSalesmanService.class);
				bind(IWAccountService.class).to(WAccountService.class);
				bind(IEmailService.class).to(EmailService.class);
				bind(IApkApplyService.class).to(ApkApplyService.class);
				bind(IPaymentMethodService.class).to(PaymentMethodService.class);
				bind(IVipService.class).to(VipService.class);
				bind(IAccountPeriodService.class).to(AccountPeriodService.class);
				bind(IPackageMailLogService.class).to(PackageMailLogService.class);
				bind(IHttpService.class).to(HttpServiceImpl.class);
				bind(ICommonExportService.class).to(CommonExportService.class);
			}
		};
	}

	@Override
	public void processConfiguration(MyBatisService myBatisService) {
		myBatisService.addMapperClass("dismember", DisAccountMapper.class);
		myBatisService.addMapperClass("dismember", DisAddressMapper.class);
		myBatisService.addMapperClass("dismember", DisApplyMapper.class);
		myBatisService.addMapperClass("dismember", DisAreaMapper.class);
		myBatisService.addMapperClass("dismember", DisBillMapper.class);
		myBatisService.addMapperClass("dismember", DisCityMapper.class);
		myBatisService.addMapperClass("dismember", DisEmailVerifyMapper.class);
		myBatisService.addMapperClass("dismember", DisMemberMapper.class);
		myBatisService.addMapperClass("dismember", DisProvinceMapper.class);
		myBatisService.addMapperClass("dismember", DisShopMapper.class);
		myBatisService.addMapperClass("dismember", FindPasswordRecordMapper.class);
		myBatisService.addMapperClass("dismember", LoginHistoryMapper.class);
		myBatisService.addMapperClass("dismember", DisRoleMapper.class);
		myBatisService.addMapperClass("dismember", ShopPlatformMapper.class);
		myBatisService.addMapperClass("dismember", ShopCategoryMapper.class);
		myBatisService.addMapperClass("dismember", DisBankMapper.class);
		myBatisService.addMapperClass("dismember", ReceiptModeMapper.class);
		myBatisService.addMapperClass("dismember", HeadImgMapper.class);
		myBatisService.addMapperClass("dismember", OperationRecordMapper.class);
		myBatisService.addMapperClass("dismember", DisRankMapper.class);
		myBatisService.addMapperClass("dismember", UserRankHistoryMapper.class);
		myBatisService.addMapperClass("dismember", DisTransferAccountMapper.class);
		myBatisService.addMapperClass("dismember", DisMenuMapper.class);
		myBatisService.addMapperClass("dismember", RoleMenusMapper.class);
		myBatisService.addMapperClass("dismember", CommonFieldMapper.class);
		myBatisService.addMapperClass("dismember", EmailAccountMapper.class);// 邮件账号
		myBatisService.addMapperClass("dismember", EmailTemplateMapper.class);// 邮件模板
		myBatisService.addMapperClass("dismember", EmailTypeMapper.class);// 邮件类型：激活、改密码、、、、
		myBatisService.addMapperClass("dismember", EmailVariableMapper.class);// 邮件变量
		myBatisService.addMapperClass("dismember", DisCreditMapper.class);// 用户信用额度
		myBatisService.addMapperClass("dismember", CreditOperationRecordMapper.class);// 用户信用额度操作记录
		myBatisService.addMapperClass("dismember", DisActiveMapper.class);// 优惠活动表
		myBatisService.addMapperClass("dismember", DisCouponsMapper.class); // 优惠码表
		myBatisService.addMapperClass("dismember", OrderConfigMapper.class); // API对接配置
		myBatisService.addMapperClass("dismember", ShopSiteMapper.class);
		myBatisService.addMapperClass("dismember", SequenceMapper.class);
		myBatisService.addMapperClass("dismember", OrganizationMapper.class);// 分销商组织结构
		myBatisService.addMapperClass("dismember", NodeHeaderMapper.class);
		myBatisService.addMapperClass("dismember", DisHeaderMapper.class);
		myBatisService.addMapperClass("dismember", OrganizationHeaderMapper.class);
		myBatisService.addMapperClass("dismember", DisSalesmanMapper.class);// 业务员
		myBatisService.addMapperClass("dismember", DisHeaderSalesmanMapper.class);
		myBatisService.addMapperClass("dismember", DisSalesmanMemberMapper.class);
		myBatisService.addMapperClass("dismember", DisModeMapper.class);
		myBatisService.addMapperClass("dismember", DisMemberMenuMapper.class);
		myBatisService.addMapperClass("dismember", DisWithdrawAccountMapper.class);
		myBatisService.addMapperClass("dismember", DisWithdrawLimitMapper.class);
		myBatisService.addMapperClass("dismember", PhoneVerifyMapper.class);
		myBatisService.addMapperClass("dismember", CustomerTypeMapper.class);// 分销商类型
		myBatisService.addMapperClass("dismember", CustomerCreditMapper.class);// 分销商类型和模式所拥有的额度
		myBatisService.addMapperClass("dismember", DisRegisterApplyMapper.class);// 注册申请
		myBatisService.addMapperClass("dismember", DisApplyFileMapper.class);// 注册申请时上传的文件
		myBatisService.addMapperClass("dismember", FileOperationRecordMapper.class);// 注册申请时上传的文件
		myBatisService.addMapperClass("dismember", EmpSalesManMapperMapper.class);// 注册申请时上传的文件
		myBatisService.addMapperClass("dismember", ApkApplyQueueMapper.class);// apk打包申请
		myBatisService.addMapperClass("dismember", PaymentConditionMapper.class);// 支付方式条件表
		myBatisService.addMapperClass("dismember", PaymentMapperMapper.class);// 条件与支付方式
																				// 映射
		myBatisService.addMapperClass("dismember", PaymentMethodMapper.class);// 系统支付方式
		myBatisService.addMapperClass("dismember", VipInviteCodeMapper.class);// vip邀请码
		myBatisService.addMapperClass("dismember", AccountOperationRecordMapper.class);// 账户核销日志记录
		myBatisService.addMapperClass("dismember", ApkVersionMapper.class);// apk版本升级
		myBatisService.addMapperClass("dismember", InvoiceMapper.class);// 分销商发票信息
		myBatisService.addMapperClass("dismember", ApBillMapper.class);
		myBatisService.addMapperClass("dismember", ApBillOrderMappingMapper.class);
		myBatisService.addMapperClass("dismember", ApChangeMapper.class);
		myBatisService.addMapperClass("dismember", ApChangeMappingMapper.class);
		myBatisService.addMapperClass("dismember", OrderByApMapper.class);
		myBatisService.addMapperClass("dismember", AccountPeriodMasterMapper.class);
		myBatisService.addMapperClass("dismember", AccountPeriodSlaveMapper.class);
		myBatisService.addMapperClass("dismember", ApOptRecordMapper.class);
		myBatisService.addMapperClass("dismember", DisShopDpLogMapper.class);
		myBatisService.addMapperClass("dismember", ApReminderSettingMapper.class);
		myBatisService.addMapperClass("dismember", ApReminderSettingLogMapper.class);
		myBatisService.addMapperClass("dismember", AdminOperateRecordMapper.class);
		myBatisService.addMapperClass("dismember", PackageMailLogMapper.class);
		myBatisService.addMapperClass("dismember", ApOrderDetailMapper.class);
		myBatisService.addMapperClass("dismember", ExportModelMapper.class);
	}

	@Override
	public void registerListener(EventBus eventBus, Injector injector) {
		eventBus.register(injector.getInstance(ActiveHandler.class));
		eventBus.register(injector.getInstance(AccessLogHandler.class));
		eventBus.register(injector.getInstance(AkpApplyHandler.class));
		eventBus.register(injector.getInstance(OrderHandler.class));
		eventBus.register(injector.getInstance(AccountPeriodHandler.class));
		eventBus.register(injector.getInstance(AccountPeriodTextReminderHandler.class));
		eventBus.register(injector.getInstance(DefaultAccountPeriodReminderHandler.class));
		eventBus.register(injector.getInstance(BackUpApOrderDetailHandler.class));
	}

	@Override
	public List<RouteBuilder> getRouteBuilders() {
		// return Lists.newArrayList();
		return Lists.newArrayList(new ActiveTimerTrigger());
	}

	@Override
	public void registerFilter(Multibinder<IFilter> filters) {
		filters.addBinding().to(AccessLogFilter.class);
	}
}
