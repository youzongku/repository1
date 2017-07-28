package util.sales;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanUtils {

	public static Map<String, Object> beanToMap(Object bean){
		Map<String, Object> map = new HashMap<String, Object>();  
        Field[] fields = null;  
        fields = bean.getClass().getDeclaredFields();  
        for (Field field : fields) {  
            field.setAccessible(true);  
            String proName = field.getName();  
            Object proValue = null;
			try {
				proValue = field.get(bean);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
            map.put(proName.toUpperCase(), proValue);  
        }
        return map;
	}
}
