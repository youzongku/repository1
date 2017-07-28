package entity.dismember;

import java.util.Random;

import com.github.napp.kaptcha.Kaptcha;
import com.github.napp.kaptcha.KaptchaConfig;

public class KaptchaCacheBuilder {

	private static final char[] CHARACTERS = "0123456789".toCharArray();
	
	static {
		KaptchaConfig config = new KaptchaConfig();
		config.setHeight(30);
		config.setHeight(75);
		kaptcha = new Kaptcha();
	}

	private final static Kaptcha kaptcha;

	public static KaptchaCache create(int length) {
		KaptchaCache c = new KaptchaCache();
//		c.actualText = KaptchaTextCreator.getText(length);
		c.actualText = getText(length);
		c.image = kaptcha.createImage(c.actualText);
		return c;
	}

	private static String getText(int length) {
		StringBuffer text = new StringBuffer();
		Random rand = new Random();
		for (int i = 0; i < length; ++i) {
			int index = rand.nextInt(CHARACTERS.length);
			text.append(CHARACTERS[index]);
		}

		return text.toString();
	}

}
