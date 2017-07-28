package util.timer;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by luwj on 2016/1/25.
 */
public class PriceFormatUtil {
	/**
	 * 保留两位小数
	 * @param bigDecimal
	 * @return
	 */
	public static Double toFix2(BigDecimal bigDecimal){
		if(bigDecimal==null){
			return 0.00;
		}
		
		return bigDecimal.setScale(2,
				BigDecimal.ROUND_HALF_UP).doubleValue();
	}

    /**
     * 保留两位小数
     * @param f
     * @return
     */
    public static Double keepTwoDecimalDouble(Double f) {
    	if(f != null){
	        DecimalFormat decimalFormat=new DecimalFormat(".00");
	        return Double.parseDouble(decimalFormat.format(f));
    	}
    	return 0.00;
    }
}
