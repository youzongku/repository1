package utils.payment.test;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.springframework.util.ReflectionUtils;



/**
 * 字符串工具类
 * @description 提供操作字符串的常用工具方法
 * @author Lincoln
 */
public class StringUtils {
	static Map<String,String> weekName=new HashMap<String,String>();
    static{
    	weekName.put("2", "星期一");
    	weekName.put("3", "星期二");
    	weekName.put("4", "星期三");
    	weekName.put("5", "星期四");
    	weekName.put("6", "星期五");
    	weekName.put("7", "星期六");
    	weekName.put("1", "星期日");
    }
    
    
	/**
	 * 将对象数组拼接成字符串 以 "," 号分隔 返回String 
	 * @param objArr
	 * @return
	 */
    public static String getString(Object[] objArr){
		return org.apache.commons.lang3.StringUtils.join(objArr, ",");
	}
    
    /**
     * 将数组每个元素前后添加指定标示位，然后拼装为字符串
     * startAndEndAddSeparator(["a", "b", "c"], "'")  = "'a','b','c'"
     * @param target
     * @param separator
     * @return
     */
    public static String startAndEndAddSeparator(String[] target,String separator){
        for(int i=0;i<target.length;i++){
            target[i] = separator+target[i]+separator;
        }
        return getString(target);
    }
   
	/**
	 * 将对象数组转换为可显字符串
	 * 
	 * @param objArr
	 * @return
	 */
	public static String toString(Object[] objArr) {
		if (objArr == null) {
			return null;
		}

		StringBuffer buf = new StringBuffer("[");
		for (int i = 0; i < objArr.length; i++) {
			buf.append((i > 0 ? "," : "") + objArr[i]);
		}
		buf.append("]");
		return buf.toString();
	}
	/**
	 * 获取星期几的名称
	 * 
	 * @param str
	 * @return
	 */
	public static String getWeekName(String str){
		return weekName.get(str);
	}
	/**
	 * 将单个对象转换为可显字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {
		if (obj instanceof String) {
			return "\"" + obj + "\"";
		}
		if (obj instanceof Object[]) {
			return toString((Object[]) obj);
		} else {
			return String.valueOf(obj);
		}
	}

	public static void main(String[] args) {
		System.out.println(formatString("lin{0}dong{1}cheng{2}", new Object[] {56789096709790l,"abadafd",new Date()}));
	}

	/**
	 * 使用正则表达式验证字符串格式是否合法
	 * 
	 * @param pattern
	 * @param str
	 * @return
	 */
	public static boolean patternValidate(String pattern, String str) {
		if (pattern == null || str == null) {
			throw new IllegalArgumentException("参数格式不合法[patternValidate(String " + pattern + ", String " + str + ")]");
		}
		return Pattern.matches(pattern, str);
	}

	/**
	 * 验证字符串是否为空字符
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (null == str || "".equals(str)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串不为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean notEmpty(String str) {
		return !isEmpty(str);
	}
	
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is
     *  not empty and not null and not whitespace
     * @since 2.0
     * @since 3.0 Changed signature from isNotBlank(String) to isNotBlank(CharSequence)
     */
    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }
	/**
	 * 如果为空,将字符串转换为NULL
	 * 
	 * @param str
	 * @return
	 */
	public static String trimToNull(String str) {
		String s = null;
		if (isEmpty(str)) {
			return s;
		}
		s = str.trim();
		return s;
	}

	/**
	 * 设置空字符串为null
	 * 
	 * @param str
	 * @return
	 */
	public static String emptyAsNull(String str){
	    if(isEmpty(str)){
	        return null;
	    }
	    else{
	        return str;
	    }
	}
	
	/**
	 * 转换对象的空字符串成null
	 * 
	 * @param object
	 */
	@SuppressWarnings("rawtypes")
	public static void emptyAsNull(Object object){
	      Class clazz = object.getClass();
	      Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
	      for(Method method : methods){
	          if(method.getName().equals("getClass")){
	              continue;
	          }
	          if(method.getName().startsWith("get")){
	              if(method.getParameterTypes().length>0)
	                  continue;
	                  try {
	                    String setMethod = method.getName().replaceFirst("get", "set");
	                  
	                    Object value = method.invoke(object, null);
	                    if(String.class.isAssignableFrom((Class)method.getGenericReturnType())){
	                        if("".equals(value)){
	                            Method setter = ReflectionUtils.findMethod(clazz, setMethod, new Class[]{(Class)method.getGenericReturnType()});
	                            setter.invoke(object, (String)null);
	                        }
	                    }
	                } catch (Exception e) {
	            } 
	          }
	      }
	  }
	  
	/**
	 * 字符编码转换器
	 * 
	 * @param str
	 * @param newCharset
	 * @return
	 * @throws Exception
	 */
	public static String changeCharset(String str, String newCharset) throws Exception {
		if (str != null) {
			byte[] bs = str.getBytes();
			return new String(bs, newCharset);
		}
		return null;
	}

	/**
	 * 判断一个字符串是否为boolean信息
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBooleanStr(String str) {
		try {
			Boolean.parseBoolean(str);
			return true;
		} catch (Throwable t) {
			return false;

		}
	}

	/**
	 * 取得指定长度的字符串(如果长度过长,将截取后半部分特定长度,如果长度太短,则使用指定字符进行左补齐)
	 * 
	 * @param str 原始字符串
	 * @param length 要求的长度
	 * @param c 用于补位的支付
	 * @return 指定长度的字符串
	 */
	public static String getLengthStr(String str, int length, char c) {
		if (str == null) {
			str = "";
		}
		int strPaymentIdLength = str.length();
		if (strPaymentIdLength > length) {
			str = str.substring(strPaymentIdLength - length);
		} else {
			str = org.apache.commons.lang3.StringUtils.leftPad(str, length, c);
		}
		return str;
	}
	
	/**
     * 
    * : convlToLong
    * @Description: TODO String 作非空处理
    * @param @param orgStr
    * @param @param convertStr
    * @param @return    
    * @return Long    
    * @throws
     */
    public static String convertNullToString(Object orgStr, String convertStr){
        if(orgStr == null){
            return convertStr;
        }
        return orgStr.toString();
    }

    /**
     * 
    * : convertNulg
    * @Description: TODO Long 作非空处理
    * @param @param orgStr
    * @param @param convertStr
    * @param @return    
    * @return Long    
    * @throws
     */
    public static Long convertNullToLong(Object orgStr, Long convertStr){
        if(orgStr == null || Long.parseLong(orgStr.toString()) == 0){
            return convertStr;
        }else{
            return Long.valueOf(orgStr.toString());
        }
    }
    
    /**
     * 
    * : convertNullTolon * @Description: TODO long 作非空处理
    * @param @param orgStr
    * @param @param convertStr
    * @param @return    
    * @return long    
    * @throws
     */
    public static long convertNullTolong(Object orgStr, long convertStr){
        if(orgStr == null || Long.parseLong(orgStr.toString()) == 0){
            return convertStr;
        }else{
            return Long.parseLong(orgStr.toString());
        }
    }
    
    /**
     * 
    * : convertNullToInt
    cription: TODO Int 作非空处理
    * @param @param orgStr
    * @param @param convertStr
    * @param @return    
    * @return int    
    * @throws
     */
    public static int convertNullToInt(Object orgStr, int convertStr){
        if(orgStr == null || Long.parseLong(orgStr.toString())== 0){
            return convertStr;
        }else{
            return Integer.parseInt(orgStr.toString());
        }
    }
    
    /**
     * 
    * : convertNullToInt
    * @Deson: TODO Integer 作非空处理
    * @param @param orgStr
    * @param @param convertStr
    * @param @return    
    * @return int    
    * @throws
     */
    public static int convertNullToInteger(Object orgStr, int convertStr){
        if(orgStr == null){
            return convertStr;
        }else{
            return Integer.valueOf(orgStr.toString());
        }
    }
    
   /**
    * 
    * : convertNullToDate
    * @DescriptODO Date 作非空处理
    * @param @param orgStr
    * @param @return    
    * @return Date    
    * @throws
     */
    public static Date convertNullToDate(Object orgStr){
        if(orgStr == null || orgStr.toString().equals("")){
            return new Date();
        }else{
            return (Date)(orgStr);
        }
    }
    
    /**
     * 
     * : convertNullToDate
     * @Description: ate 作非空处理
     * @param @param orgStr
     * @param @return    
     * @return Date    
     * @throws
      */
     public static BigDecimal convertNullToBigDecimal(Object orgStr){
         if(orgStr == null || orgStr.toString().equals("")){
             return new BigDecimal("0");
         }else{
             return (BigDecimal)(orgStr);
         }
     }
     
     /**
      * 对字符串 - 在左边填充指定符号
      * @param s
      * @param fullLength
      * @param addSymbol
      * @return
      */
     public static String addSymbolAtLeft(String s, int fullLength,
             char addSymbol) {
         if (s == null) {
             return null;
         }

         int distance = 0;
         String result = s;
         int length = s.length();
         distance = fullLength - length;

         if (distance <= 0) {
             System.out.println("StringTools:addSymbolAtleft() --> Warinning ,the length is equal or larger than fullLength!");
         }

         else {
             char[] newChars = new char[fullLength];
             for (int i = 0; i < length; i++) {
                 newChars[i + distance] = s.charAt(i);
             }

             for (int j = 0; j < distance; j++) {
                 newChars[j] = addSymbol;
             }

             result = new String(newChars);
         }

         return result;
     }
     
     /**
      * 对字符串 - 在右边填充指定符号
      * @param s
      * @param fullLength
      * @param addSymbol
      * @return
      */
     public static String addSymbolAtRight(String s, int fullLength,
             char addSymbol) {
         if (s == null) {
             return null;
         }

         String result = s;
         int length = s.length();

         if (length >= fullLength) {
             System.out
                     .println("StringTools:addSymbolAtRight() --> Warinning ,the length is equal or larger than fullLength!");
         }

         else {
             char[] newChars = new char[fullLength];

             for (int i = 0; i < length; i++) {
                 newChars[i] = s.charAt(i);
             }

             for (int j = length; j < fullLength; j++) {
                 newChars[j] = addSymbol;
             }
             result = new String(newChars);
         }

         return result;
     }

	/**
	 * 判断两个字符串是否相同
	 * @param str1 :参数
	 * @param str2 :参数
	 * @return : 布尔
	 */
	public static boolean isEquals(String str1, String str2) {
		if(str1==null){
			return str2==null;
		}else{
			return str1.equals(str2);
		}
	}
	
	/**
	 * 判断两个字符串是否不同
	 * @param str1 : 参数
	 * @param str2 : 参数
	 * @return
	 */
	public static boolean notEquals(String str1, String str2) {
		return !isEquals(str1,str2);
	}
	
	/**
	 * 分隔字符串
	 * @param srcStr 被分隔的字符串
	 * @param splitChars	多个分隔符
	 * @return 分隔结果
	 */
	public static List<String> splitString(String srcStr,String splitChars){
		if(isEmpty(srcStr)){
			return null;
		}
		
		List<String> strList=new ArrayList<String>();
		StringTokenizer tok=new StringTokenizer(srcStr,splitChars);
		while(tok.hasMoreTokens()){
			strList.add(tok.nextToken());
		}
		return strList;
	}

	/**
	 * 格式化字符串
	 * @param src
	 * @param params
	 * @return
	 */
	public static String formatString(String src,Object... params) {
		String[] paramsStrArr=params!=null?new String[params.length]:null;
		for(int i=0;params!=null && i<params.length;i++) {
			paramsStrArr[i]=String.valueOf(params[i]);
		}
		
		return MessageFormat.format(src,paramsStrArr);
	}
	
	/**
	 * 比较两个字符串是否相等
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean equals(String str1, String str2) {
		if (str1 == null) {
			return str2 == null;
		}

		return str1.equals(str2);
	}
	
	/**
	 * 比较字符串，不区分大小写 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {
		if (str1 == null) {
			return str2 == null;
		}

		return str1.equalsIgnoreCase(str2);
	}
	
	public static String substring(String str, int start) {
		if (str == null) {
			return null;
		}

		if (start < 0) {
			start = str.length() + start;
		}

		if (start < 0) {
			start = 0;
		}

		if (start > str.length()) {
			return "";
		}

		return str.substring(start);
	}

	public static String substring(String str, int start, int end) {
		if (str == null) {
			return null;
		}

		if (end < 0) {
			end = str.length() + end;
		}

		if (start < 0) {
			start = str.length() + start;
		}

		if (end > str.length()) {
			end = str.length();
		}

		if (start > end) {
			return "";
		}

		if (start < 0) {
			start = 0;
		}

		if (end < 0) {
			end = 0;
		}

		return str.substring(start, end);
	}
	public static String truncString(String orginStr,int limitLength){
        if (!StringUtils.isEmpty(orginStr)) {
            byte[] msgByte = orginStr.getBytes();
            int length = msgByte.length;
            if (length < limitLength) {
                return orginStr;
            } else {
                byte[] temp = Arrays.copyOfRange(msgByte, 0, limitLength);
                return new String(temp);
            }
        }        
        return orginStr;
    }	  
	
    /**
     * 保留字符串第一个字母
     * */
    public static String concealStringWithHead(String originalSource) {
        String result = "";
        if (!StringUtils.isEmpty(originalSource)) {
            for (int i = 0; i < originalSource.length(); i++) {
                if (i == 0) {
                    result += originalSource.charAt(i);
                } else {
                    result += "*";
                }

            }
        }
        return result;
    }

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		 if (str == null) {
	            return false;
	        }
	        int sz = str.length();
	        for (int i = 0; i < sz; i++) {
	            if (Character.isDigit(str.charAt(i)) == false) {
	                return false;
	            }
	        }
	        return true;
	}
	
	/**
     * 使用给定的 charset 将此 String 编码到 byte 序列，并将结果存储到新的 byte 数组。
     * 
     * @param content 字符串对象
     * 
     * @param charset 编码方式
     * 
     * @return 所得 byte 数组
     */
    public static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }

        try {
            return content.getBytes(charset);
        }
        catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Not support:" + charset, ex);
        }
    }
    
}
