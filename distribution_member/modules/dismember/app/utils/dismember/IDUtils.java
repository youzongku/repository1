package utils.dismember;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;

import java.util.Date;
import java.util.Random;
import java.util.UUID;


/**
 * Created by luwj on 2015/11/26.
 */
public class IDUtils {

    /**
     * 生成随机窜
     * @return
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成时间加随机数字符窜
     * @return
     */
    public static String buildRefundNo(){
        String str = DateUtils.date2string(new Date(), DateUtils.FORMAT_DATETIME_BACKEND);
        int x=(int)(Math.random()*1000);
        return str + String.valueOf(x);
    }

    /**
     * 生成6位随机数字
     * @param length
     * @return
     */
    public static String randomNumber(int length) {
        Random random = new Random();
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buff.append(random.nextInt(10));

        }
        return buff.toString();
    }
    
    /**
     * 生成随机数字和字母,
     * @param length
     * @return
     */
    public static String getStringRandom(int length) {  
          
        String val = "";  
        Random random = new Random();  
        //参数length，表示生成几位随机数  
        for(int i = 0; i < length; i++) {  
              
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";  
            //输出字母还是数字  
            if( "char".equalsIgnoreCase(charOrNum) ) {  
                //输出是大写字母还是小写字母  
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;  
                val += (char)(random.nextInt(26) + temp);  
            } else if( "num".equalsIgnoreCase(charOrNum) ) {  
                val += String.valueOf(random.nextInt(10));  
            }  
        }  
        return val;  
    } 
    
    /**
     * 数据库序列值
     * @param seqvalue
     * @return
     */
    public static String getOnlineTopUpCode(String str, String seqvalue){
    	return str+getCode(seqvalue,DateUtils.FORMAT_DATETIME_BACKEND,8);
    }

    /**
     * eg.2015112700000025
     * @param seqvalue
     * @return
     */
    public static String getCode(String seqvalue,String format,int size){
        String cal = DateUtils.date2string(new Date() , format);
        return cal + paddingLeft(seqvalue,size);
    }
    
    /**
     * 左侧补全
     * @param seqvalue
     * @param paddingLength
     * @return
     */
    public static String paddingLeft(String seqvalue,int paddingLength){
    	if(seqvalue.length() < paddingLength){
    		StringBuilder sb = new StringBuilder();
    		for(int i = 0;i< paddingLength - seqvalue.length();i++){
    			sb.append("0");
    		}
    		return sb.append(seqvalue).toString();
    	}else{
    		return seqvalue;
    	}
    }

    /**
     * 获取指定JSON key的JSON value
     * @Author LSL on 2016-09-23 12:06:13
     */
    public static String getText(JsonNode node, String key) {
        return node == null || key == null ? null :
            !node.has(key) ? null : node.get(key).isNull() ? null :
            node.get(key).size() == 0 ? null :
            Strings.isNullOrEmpty(node.get(key).asText()) ? null :
            node.get(key).asText();
    }

	public static String getShopNo(String string, String selectNextValue) {
        return string + paddingLeft(selectNextValue,5);
	}

}
