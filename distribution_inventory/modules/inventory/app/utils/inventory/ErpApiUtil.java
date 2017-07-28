package utils.inventory;

import java.util.Date;

import org.joda.time.LocalDate;

import play.Configuration;
import play.Play;

public class ErpApiUtil {
	public static String ERP_API_KEY = "";//访问erp需要的权限密钥
	public static String ERP_HOST = "";//erp数据源host
	public static String STOCK_INIT_API = "";//Erp库存数据api，带到期日期的
	public static String EXTERNAL_WAREHOUSE_API="";//Erp库存数据api，不带到期日期
	public static final Date EXPIRATION_DATE = new LocalDate(2116,2,7).toDate();//查询不到到期日期商品设置为2116-02-07到期
	public static final String EXPIRATION_STR="2116-02-07";
	public static int EXTERNAL_WAREHOUSE_STOCK=-1;
	static {
		Configuration config = Play.application().configuration().getConfig("erp");
		if (ERP_API_KEY .equals("")) {
			ERP_API_KEY = config.getString("apiKey");
		}
		if (ERP_HOST .equals("")) {
			ERP_HOST = config.getString("host");
		}
		if (STOCK_INIT_API.equals("")) {
			STOCK_INIT_API = config.getString("stockInitApi");
		}
		if(EXTERNAL_WAREHOUSE_API.equals("")){
			EXTERNAL_WAREHOUSE_API = config.getString("externalWarehouseApi");
		}
		if(EXTERNAL_WAREHOUSE_STOCK==-1){
			EXTERNAL_WAREHOUSE_STOCK = config.getInt("externalWarehouseStock")==null?-1:config.getInt("externalWarehouseStock");
		}
	}
}
