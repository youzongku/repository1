package controllers.rabbitmq;


import play.api.mvc.Action;
import play.api.mvc.AnyContent;
import play.mvc.Controller;

/**
 * @author wujirui
 */
public class Assets extends Controller {

    public static Action<AnyContent> at(String path, String file) {
        return controllers.Assets.at(path, file, false);
    }

}
