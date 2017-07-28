package controllers.dismember;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.dismember.TransferAccountDto;
import entity.dismember.DisTransferAccount;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IDisTransferAccountService;
import services.dismember.ILoginService;
import vo.dismember.LoginContext;

public class DisTransferAccountController extends Controller {
	
	@Inject
	private ILoginService loginService;	 
	
	@Inject 
	private IDisTransferAccountService accountService;
	
	public Result getTransferAccount(){
		Map<String, Object> result = Maps.newHashMap();
		if(!loginService.isLogin(1)){
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}
		
		LoginContext loginContext = loginService.getLoginContext(1);//1前台用户
		TransferAccountDto dto = new TransferAccountDto();
		dto.setEmail(loginContext.getEmail());
		List<DisTransferAccount> accounts = accountService.getAccountByDto(dto);
		List<String> bankNames = accountService.getBankNameByEmail(loginContext.getEmail());
		if (accounts == null || accounts.size() == 0) {
			result.put("suc", false);
			result.put("msg", "付款方式为空，请点击添加");
			return ok(Json.toJson(result)); 
		}
		
		result.put("suc", true);
		result.put("accounts", accounts);
		result.put("bankNames", bankNames);
		return ok(Json.toJson(result)); 
	} 
	
	public Result editTransferAccount(){
		Map<String, Object> result = Maps.newHashMap();
		if(!loginService.isLogin(1)){
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}
		
		LoginContext loginContext = loginService.getLoginContext(1);//1前台用户
		Map<String, String> params = Form.form().bindFromRequest().data();
		DisTransferAccount account = new DisTransferAccount();
		account.setBankName(params.get("bankName"));
		account.setTransferCard(params.get("transferCard"));
		account.setPayerName(params.get("payerName"));
		account.setCustomStatus(null == params.get("customStatus")?null:Integer.parseInt(params.get("customStatus")));
		account.setId(StringUtils.isEmpty(params.get("id"))?null:Integer.parseInt(params.get("id")));
		account.setDistributorEmail(loginContext.getEmail());
		TransferAccountDto dto = new TransferAccountDto();
		dto.setEmail(account.getDistributorEmail());
		dto.setCard(account.getTransferCard());
		dto.setBankName(account.getBankName());
		List<DisTransferAccount> list= accountService.getAccountByDto(dto);
		/**************************目前需求，一个账号相同付款方式的账户只能一个！验证付款方式是否重复****************************/
		TransferAccountDto ckDto = new TransferAccountDto();
		ckDto.setEmail(account.getDistributorEmail());
		ckDto.setBankName(account.getBankName());
		boolean ck = accountService.checkBankNameByDto(ckDto,account.getId());
		if(ck){
			result.put("success", false);
			result.put("errorInfo","该付款方式已存在，请重新选择！");
			return ok(Json.toJson(result));
		}
		
		/************************************************************************************************/
		//判断是否有id 若无则新增，若有则修改
		if(null == account.getId()){
			boolean isOk = null == list || list.size() == 0;
			if (!isOk) {
				result.put("success",false);
				result.put("errorInfo","账户已存在");
				return ok(Json.toJson(result));
			}
			
			result.put("success", accountService.addTransferAccount(account)>0);
			result.put("msg","新增付款账户成功");
			return ok(Json.toJson(result));
		}
		
		boolean updateOk = null == list||list.size()==0;
		if(updateOk){
			result.put("msg","修改付款账户成功");
			result.put("success", accountService.editTransferAccount(account)>0);
			return ok(Json.toJson(result));
		}

		boolean flag = false;
		if(list.size() == 1){
			//修改付款账户账号与其他账户相同则重复
			if(list.get(0).getId().equals(account.getId())){
				flag = true;
			}
			if(flag){
				result.put("msg","修改付款账户成功");
				result.put("success", accountService.editTransferAccount(account)>0);
			}else{
				result.put("success",false);
				result.put("errorInfo","账号重复");	
			}
		}
		return ok(Json.toJson(result));
	}
	
	public Result delTransferAccount(Integer id){
		Map<String, Object> result = Maps.newHashMap();
		if(!loginService.isLogin(1)){
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}

		result.put("success", accountService.delTransferAccount(id)>0);
		LoginContext loginContext = loginService.getLoginContext(1);//1前台用户
		TransferAccountDto dto = new TransferAccountDto();
		dto.setEmail(loginContext.getEmail());
		List<DisTransferAccount> accounts = accountService.getAccountByDto(dto);
		if(null ==accounts||accounts.size()==0){
			result.put("msg","删除付款账户成功,无付款账户,请添加");
			result.put("empty",true);
			return ok(Json.toJson(result));			
		}

		result.put("msg","删除付款账户成功");
		return ok(Json.toJson(result));
	}
	
	public Result getTransferAccountByid(Integer id){
		Map<String, Object> result = Maps.newHashMap();
		if(!loginService.isLogin(1)){
			result.put("success", false);
			result.put("code", 2);// 未登录
			return ok(Json.toJson(result));
		}
		
		result.put("account",accountService.getTransferAccountByid(id));
		result.put("success", true);
		return ok(Json.toJson(result));
	}
}