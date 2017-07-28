package utils.discart;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by luwj on 2016/1/25.
 */
public class PriceFormatUtil {
	/**
	 * 简化操作：bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP)
	 * 
	 * @param bigDecimal
	 * @return 返回一个保留两位小数点的BigDecimal
	 */
	public static BigDecimal setScale2(BigDecimal bigDecimal){
		if(bigDecimal==null){
			return null;
		}
		
		return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
    
	/**
	 * 保留两位小数
	 * @param bigDecimal
	 * @return
	 */
	public static Double toFix2(BigDecimal bigDecimal){
		if(bigDecimal==null){
			return 0.00;
		}
		
		return (double) Math.round(bigDecimal.doubleValue()*100)/100;
	}
	
    /**
     * 保留两位小数
     * @param f
     * @return
     */
    public static Double toFix2(Double val) {
    	if(val != null){
	        return (double) Math.round(val*100)/100;
    	}
    	return 0.00;
    }
}
