package utils;

import java.text.DecimalFormat;

/**
 * Created by luwj on 2016/1/25.
 */
public class PriceFormatUtil {

    /**
     * 保留两位小数
     * @param f
     * @return
     */
    public static Double keepTwoDecimalDouble(Double f) {
        DecimalFormat decimalFormat=new DecimalFormat(".00");
        return Double.parseDouble(decimalFormat.format(f));
    }
}
