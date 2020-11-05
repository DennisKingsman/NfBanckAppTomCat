
package utils;

import bean.UserAccount;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppUtils {
    private static final Logger logger = LoggerFactory.getLogger(AppUtils.class);
    private static int REDIRECT_ID = 0;
    private static final Map<Integer, String> idUrlMap = new HashMap();
    private static final Map<String, Integer> urlIdMap = new HashMap();

    public AppUtils() {
    }

    public static void storeLoginedUser(HttpSession session, UserAccount loginedUser) {
        logger.info("in storeLoginedUser method. Logined user login and password are: " + loginedUser.getLogin() + " " + loginedUser.getPassword());
        session.setAttribute("loginedUser", loginedUser);
        logger.info("storing complete");
    }

    public static UserAccount getLoginedUser(HttpSession session) {

        logger.info("getLoginedUser has called");
        UserAccount loginedUser = (UserAccount)session.getAttribute("loginedUser");
        return loginedUser;
    }

    public static int storeRedirectAfterLoginUrl(HttpSession session, String requestUrl) {
        logger.info("method storeRedirectAfterLoginUrl in AppUtils.class has started with url = " + requestUrl);
        Integer id = (Integer)urlIdMap.get(requestUrl);
        if (id == null) {
            id = REDIRECT_ID++;
            logger.info("redirect id now is " + REDIRECT_ID + " so as Id of dis url");
            urlIdMap.put(requestUrl, id);
            idUrlMap.put(id, requestUrl);
            return id;
        } else {
            return id;
        }
    }

    public static String getRedirectAfterLoginUrl(HttpSession session, int redirectId) {
        String url = (String)idUrlMap.get(redirectId);
        if (url != null) {
            return url;
        } else {
            logger.info("ATTENTION !!! in getRedirectAfterLoginUrl method in AppUtils.class required url is null");
            return null;
        }
    }
}
