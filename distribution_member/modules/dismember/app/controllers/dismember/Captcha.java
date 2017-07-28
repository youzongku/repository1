package controllers.dismember;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.dismember.ICaptchaService;

public class Captcha extends Controller {

	@Inject
	ICaptchaService captchaService;

	public Result generate() throws IOException {
		Logger.info("CAPTCHA generate");
		response().setHeader("Cache-Control", "no-store, no-cache");
		byte[] tmp = null;
		try (ByteArrayOutputStream buffer = captchaService.createCaptcha(Context.current())) {
			tmp = buffer.toByteArray();
		} catch (IOException e) {
			Logger.error("Captcha.generate cause I/O error. ", e);
			throw e;
		}
		return ok(tmp).as("image/png");
	}

	public Result checkCaptcha() {
		if (request().body().asFormUrlEncoded().get("captcha") == null) {
			return ok(Json.toJson(false));
		}
		
		String captcha = request().body().asFormUrlEncoded().get("captcha")[0];
		boolean flag = captchaService.verify(captcha);
		return ok(Json.toJson(flag));
	}
}
