
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

        logger.info("isSecurityPage method has called");
        String urlPattern = UrlPatternUtils.getUrlPattern(request);
        //finally got our urlPattern
        //here we start using securityConfig
        logger.info("URL PATTERN IN SecurityUtils.class: " + urlPattern); // "/"
        logger.info("if url pattern equals '/' : " + urlPattern.equals("/"));
        if (!urlPattern.equals("/") && !urlPattern.equals("/register")) { //notSecured pages
            logger.info("if it supposed to be a secured page then moving to roles into our securityConfig");
            Set<String> roles = SecurityConfig.getAllAppRoles();
            logger.info("Roles from config: " + roles.toString());

            for (String role: roles) {
                List<String> urlPatterns = SecurityConfig.getUrlPatternsForRole(role);
                logger.info("considered role: " + role + "appropriate urlPatterns: " + urlPatterns.toString());
                if(urlPatterns != null && urlPatterns.contains(urlPattern)){
                    logger.info("urlPatterns is not null and contains considered urlPattern");
                    return true;
                }
            }

            logger.info("no such role or urlPattern in SecurityConfig");
            return false;

            //Iterator var3 = roles.iterator();
            //List urlPatterns;
            //do {
              //  if (!var3.hasNext()) {
                //    logger.info("No roles has found. Are about to return false anyway!!! isSecurityPage method result!");
                  //  return false;
                //}

                //String role = (String)var3.next();
                //urlPatterns = SecurityConfig.getUrlPatternsForRole(role);
            //} while(urlPatterns == null || !urlPatterns.contains(urlPattern));

            //return true;
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
        for (String role: allRoles) {
            if(!request.isUserInRole(role)){
                //вероятнотно проверяет пользователя затем проверяет его роль затем сопоставляет его роль с ролью параметром
                logger.info("user not in role: continue iterate");
            continue;
            }

            //user in role
            List<String> urlPatterns = SecurityConfig.getUrlPatternsForRole(role);
            if(urlPatterns != null && urlPatterns.contains(urlPattern)){
                logger.info("considered role: " + role + " and urlPattern " + urlPattern);
                return true;
            }

//        Iterator var3 = allRoles.iterator();
  //      while(var3.hasNext()) {
          //  String role = (String)var3.next();
            //if (!request.isUserInRole(role)) {
              //  logger.info("req is Not in Role");
            //} else {
              //  List<String> urlPatterns = SecurityConfig.getUrlPatternsForRole(role);
                //if (urlPatterns != null && urlPatterns.contains(urlPattern)) {
                  //  return true;
                //}
            //}
        }

        return false;
    }
}
