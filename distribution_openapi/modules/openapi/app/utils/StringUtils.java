package utils;

public class StringUtils {
/**
 * @author hanfs
 * @param obj
 * @param isNull(true:对象为空时返回null false:对象为空时返回"")
 * @return
 */
public static String getString(Object obj,boolean isNull){
	if(obj!=null){
		return obj.toString();
	}
	if (isNull) {
		return null;
	}
	return ""; 
}
public static boolean isBlankOrNull(Object obj){
	if(obj==null||"".equals(obj.toString())){
		return true;
	}
	return false;
}
public static boolean isNotBlankOrNull(Object obj){
	return !isBlankOrNull(obj);
}
}
