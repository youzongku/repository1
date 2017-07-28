package utils.inventory;

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
    private String randomNumber(int length) {
        Random random = new Random();
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buff.append(random.nextInt(10));

        }
        return buff.toString();
    }
}
