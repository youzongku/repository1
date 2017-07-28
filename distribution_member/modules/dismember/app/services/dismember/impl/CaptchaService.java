package services.dismember.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import com.google.common.primitives.Bytes;

import context.WebContext;
import entity.dismember.KaptchaCache;
import entity.dismember.KaptchaCacheBuilder;
import play.mvc.Http.Context;
import session.ISessionService;

public class CaptchaService implements services.dismember.ICaptchaService {

	@Inject
	private ISessionService session;

	public boolean verify(String input) {
		String cache = (String) session.get(KAPTCHA_VARNAME);
		if (cache == null || input == null) {
			return false;
		}
		
		return cache.equalsIgnoreCase(input);
	}

	@Override
	public ByteArrayOutputStream createCaptcha(Context ctx) throws IOException {
		if (ctx == null) {
			throw new NullPointerException("context is null");
		}
		KaptchaCache cache = KaptchaCacheBuilder.create(DEFAULT_KAPTCHA_LEN);
		// save the text to session
		session.set(CaptchaService.KAPTCHA_VARNAME, cache.getActualText());
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ImageIO.write(cache.getImage(), "png", buffer);
		return buffer;
	}

	@Override
	public List<Byte> createCaptcha(WebContext webCtx) throws IOException {
		if (webCtx == null) {
			throw new NullPointerException("WebContext is null");
		}
		KaptchaCache cache = KaptchaCacheBuilder.create(DEFAULT_KAPTCHA_LEN);
		// save the text to session
		session.set(CaptchaService.KAPTCHA_VARNAME, cache.getActualText(),
				webCtx);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ImageIO.write(cache.getImage(), "png", buffer);
		List<Byte> captchaByte = Bytes.asList(buffer.toByteArray());
		return captchaByte;
	}

	@Override
	public boolean verify(String captcha, WebContext webCtx) {
		if (captcha == null || captcha.length() == 0 || webCtx == null) {
			return false;
		}
		String cache = (String) session.get(KAPTCHA_VARNAME, webCtx);
		if (cache == null) {
			return false;
		}
		return cache.equalsIgnoreCase(captcha);
	}
}
