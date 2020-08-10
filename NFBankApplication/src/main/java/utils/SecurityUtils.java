
package utils;

import config.SecurityConfig;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityUtils {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    public SecurityUtils() {
    }

    public static boolean isSecurityPage(HttpServletRequest request) {
        String urlPattern = UrlPatternUtils.getUrlPattern(request);
        logger.info("URL PATTERN IN SecurityUtils.class: " + urlPattern);
        logger.info("equals result in isSecurityPage method: " + urlPattern.equals("/"));
        if (!urlPattern.equals("/") && !urlPattern.equals("/register")) {
            logger.info("skipped first <<if>>, moving to roles");
            Set<String> roles = SecurityConfig.getAllAppRoles();
            logger.info("Roles from config: " + roles.toString());
            Iterator var3 = roles.iterator();

            List urlPatterns;
            do {
                if (!var3.hasNext()) {
                    logger.info("are about to return false anyway!!! isSecurityPage method result!");
                    return false;
                }

                String role = (String)var3.next();
                urlPatterns = SecurityConfig.getUrlPatternsForRole(role);
            } while(urlPatterns == null || !urlPatterns.contains(urlPattern));

            return true;
        } else {
            return false;
        }
    }

    public static boolean hasPermission(HttpServletRequest request) {
        logger.info("entering in hasPermission method in " + SecurityUtils.class);
        String urlPattern = UrlPatternUtils.getUrlPattern(request);
        logger.info("UrlPatternUtils.getUrlPattern(req) method result in hasPermission method: " + urlPattern);
        Set<String> allRoles = SecurityConfig.getAllAppRoles();
        logger.info("All roles: " + allRoles.toString());
        Iterator var3 = allRoles.iterator();

        while(var3.hasNext()) {
            String role = (String)var3.next();
            if (!request.isUserInRole(role)) {
                logger.info("req is Not in Role");
            } else {
                List<String> urlPatterns = SecurityConfig.getUrlPatternsForRole(role);
                if (urlPatterns != null && urlPatterns.contains(urlPattern)) {
                    return true;
                }
            }
        }

        return false;
    }
}
