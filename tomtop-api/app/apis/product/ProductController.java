package apis.product;

import annotations.ApiPermission;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.ReturnMessageForm;
import forms.order.PurchaseReadResponseForm;
import forms.product.ProductReadResponseForm;
import forms.product.ProductSearchParamForm;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Constans;
import utils.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * Created by ye_ziran on 2016/3/22.
 */
//@ApiPermission
public class ProductController extends Controller {

    /**
     * 查询产品及库存
     * @return
     */
    public Result read() throws IOException{

        Result res = noContent();

        Form<ProductSearchParamForm> f = Form.form(ProductSearchParamForm.class).bindFromRequest();
        Map<String,String> params = f.data();

        //通过memberEmail拿到折扣
        String memberInfo =HttpUtil.get(Constans.MEMBER_INFO + "?email=" + params.get("memberEmail"));
        Logger.info("memberInfo = {}", memberInfo);
        JsonNode jNode =Json.parse(memberInfo);
        String discount = jNode.get("discount").asText();
        params.put("disCount", discount);

        params.remove("memberEmail");

        ObjectNode data = Json.newObject();
        data.set("data", Json.toJson(params));

        String respStr = HttpUtil.post(Constans.PRODUCT_READ, data);
        if(respStr != null){
            JsonNode prodJn = Json.parse(respStr).get("data").get("result");
            ObjectMapper om = new ObjectMapper();
            try{
                List<ProductReadResponseForm> prodFormList = new ArrayList<>();
                for(int i=0,len=prodJn.size(); i<len; i++){
                    ProductReadResponseForm prodForm = Json.fromJson(prodJn.get(i), ProductReadResponseForm.class);
                    prodFormList.add(prodForm);
                }
                res = ok(Json.toJson(prodFormList));
            }catch (Exception e) {
                Logger.error("GET /products error, cause by ObjectMapper.readValue, msg={}", e.getMessage());
                ReturnMessageForm returnForm = new ReturnMessageForm();
                returnForm.setRes(false);
                returnForm.setMsg("GET\"/products\"响应数据解析错误！");
                res = internalServerError(Json.toJson(returnForm));
            }
        }else{
            ReturnMessageForm returnForm = new ReturnMessageForm();
            returnForm.setRes(false);
            returnForm.setMsg("服务器繁忙，请稍后再请求~");
            res = internalServerError(Json.toJson(returnForm));
        }

        return res;
    }
}
