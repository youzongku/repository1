package controllers.dismember;


import play.api.mvc.Action;
import play.api.mvc.AnyContent;
import play.mvc.Controller;

/**
 * Created by luwj on 2015/11/24.
 */
public class Assets extends Controller {

    public static Action<AnyContent> at(String path, String file) {
        return controllers.Assets.at(path, file, false);
    }

}
