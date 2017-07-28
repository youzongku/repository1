package controllers.dismember;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import dto.dismember.ReceiptModeDto;
import entity.dismember.DisBank;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IDisBankService;

import java.util.List;
import java.util.Map;

/**
 * Created by LSL on 2016/1/7.
 */
public class BankController extends Controller {

    @Inject
    private IDisBankService disBankService;

    /**
     * 获取所有银行
     * @return
     */
    public Result getAllBanks() {
        Map<String, Object> result = Maps.newHashMap();
        List<DisBank> banks = disBankService.getAllBanks();
        if(banks==null || banks.size()==0){
        	result.put("suc", false);
            result.put("msg", "银行数据为空");
            return ok(Json.toJson(result));
        }
        
        result.put("suc", true);
        result.put("list", banks);
        return ok(Json.toJson(result));
    }

    /**
     * 获取所有收款方式
     * @return
     */
    public Result getAllReceiptModes() {
        Map<String, Object> result = Maps.newHashMap();
        List<ReceiptModeDto> rmds = disBankService.getAllReceiptModes();
        if (rmds==null || rmds.size()==0) {
        	result.put("suc", false);
            result.put("msg", "收款方式数据为空");
            return ok(Json.toJson(result));
        }
        
        result.put("suc", true);
        result.put("list", rmds);
        return ok(Json.toJson(result));
    }

}
