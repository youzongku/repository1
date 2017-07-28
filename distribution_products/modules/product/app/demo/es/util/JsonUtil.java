package demo.es.util;

import java.io.IOException;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class JsonUtil {
	public static XContentBuilder jsonBuilder() {
		XContentBuilder jsonBuilder = null;
		try {
			jsonBuilder = XContentFactory.jsonBuilder();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("创建XContentBuilder对象失败");
		}
		return jsonBuilder;
	}

}
