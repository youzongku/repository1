package controllers.marketing.promotion;

import play.api.mvc.Action;
import play.api.mvc.AnyContent;
import play.mvc.Controller;

/**
 * @author xuse
 *
 */
public class Assets extends Controller {

	public static Action<AnyContent> at(String path, String file) {
		return controllers.Assets.at(path, file, false);
	}
}
