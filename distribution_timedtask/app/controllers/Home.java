package controllers;

import filters.common.CookieTrackingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;

public class Home extends Controller {

	public Result home() {
		return ok("Your new application is ready.");
	}

	public static Promise<Result> notFoundPromiseResult() {
		return Promise.pure(notFoundResult());
	}

	public static Promise<Result> errorPromiseResult(RequestHeader request,
			Throwable t) {
		return Promise.pure(errorResult(request, t));
	}

	public static Result notFoundResult() {
		String header = request().getHeader("Referer");
		return notFound("");
	}

	public static Result errorResult(RequestHeader request, Throwable t) {
		String errorCode = RandomStringUtils.random(8,
				"abcdefghijkmnpqrstuvwxyz23456789");
		Logger.error(generateErrorLine(errorCode, request), t);
		if (t != null && t.getCause() != null) {
			Logger.error("Further cause", t.getCause());
		}
		return internalServerError("");
	}

	private static String generateErrorLine(String errorCode,
			RequestHeader request) {
		StringBuilder sb = new StringBuilder();
		sb.append("Application Error #");
		sb.append(errorCode);
		sb.append(" IP=");
		sb.append(request.remoteAddress());
		sb.append(" LTC=");
		sb.append(request.cookie(CookieTrackingFilter.TT_LTC) != null ? request
				.cookie(CookieTrackingFilter.TT_LTC).value() : "<null>");
		sb.append("\nRequest (");
		sb.append(request.method());
		sb.append(") [");
		sb.append(request.uri());
		sb.append("]");
		return sb.toString();
	}
}
