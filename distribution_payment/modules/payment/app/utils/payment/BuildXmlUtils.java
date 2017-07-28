package utils.payment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * xml与bean转换工具类
 * @author luwj
 *
 */
public abstract class BuildXmlUtils<T> {


	/**
	 * 获得泛型实体
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public T getT() throws InstantiationException, IllegalAccessException {
		Type sType = getClass().getGenericSuperclass();
		Type[] generics = ((ParameterizedType) sType).getActualTypeArguments();
		Class<T> mTClass = (Class<T>) (generics[0]);
		return mTClass.newInstance();
	}

	/**
	 * 实体bean转成xml
	 * @param message 实体bean
	 * @return String
	 */
	public String bean2xml(T message) {
		String result = null;
		JAXBContext context = null;
		Marshaller marshaller = null;
		String __defult_encoding = "UTF-8";
		try {
			context = JAXBContext.newInstance(message.getClass());
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, __defult_encoding);

			StringWriter writer = new StringWriter();
			marshaller.marshal(message, writer);
			result = writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * xml转实体bean
	 * @param xmlStr xml字符窜
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T xml2bean(String xmlStr) {

		T message = null;
		Reader reader = null;
		JAXBContext context = null;
		Unmarshaller unmarshaller = null;
		try {
			reader = new StringReader(xmlStr);
			context = JAXBContext.newInstance(getT().getClass());
			unmarshaller = context.createUnmarshaller();
			message = (T) unmarshaller.unmarshal(reader);
			reader.close();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				reader = null;
				e.printStackTrace();
			}
		}
		return message;
	}
	
	public static String  transformXMLToString(Document doc){
		TransformerFactory   tf   =   TransformerFactory.newInstance();
		Transformer t;
		try {
			t = tf.newTransformer();
			ByteArrayOutputStream   bos   =   new   ByteArrayOutputStream();
			t.setOutputProperty("encoding", "GB2312");
			t.transform(new DOMSource(doc), new StreamResult(bos));
			return bos.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
