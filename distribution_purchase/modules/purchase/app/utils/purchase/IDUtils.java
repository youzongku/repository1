package utils.purchase;

import java.text.DecimalFormat;
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
    private static String randomNumber(int length) {
        Random random = new Random();
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buff.append(random.nextInt(10));

        }
        return buff.toString();
    }

    /**
     * eg.201511270000000025
     * @param nextval
     * @return
     */
    public static String getCode(int nextval, String format){
        String cal = DateUtils.date2string(new Date() , DateUtils.FORMAT_DATE_DB);
        DecimalFormat nf = new DecimalFormat(format);
        String ret = nf.format(nextval);
        return cal + ret;
    }
}
