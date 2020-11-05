
package utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.jms.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlPatternUtils {
    private static final Logger logger = LoggerFactory.getLogger(UrlPatternUtils.class);

    public UrlPatternUtils() {
    }

    private static boolean hasUrlPattern(ServletContext servletContext, String urlPattern) {

        logger.info("method hasUrlPattern has called");
        logger.info("urlPattern is: " + urlPattern);
        int i = urlPattern.lastIndexOf(46); //taking '.'
        if (i != -1) {
            String tail = urlPattern.substring(i + 1);
            if (tail.equals("html")) {
                urlPattern = urlPattern.substring(0, i);
            }
        }

        logger.info("new urlPattern is: " + urlPattern);

        logger.info("configuring map of servletRegistrations");

        //filled map of servletRegistrations by req.getServletContext
        Map<String, ? extends ServletRegistration> map = servletContext.getServletRegistrations();
        Iterator var4 = map.keySet().iterator();

        Collection mappings;
        do {
            if (!var4.hasNext()) {
                return false;
            }

            //ket as servletName
            String servletName = (String)var4.next();
            //var by key as ServletRegistration
            ServletRegistration servletRegistration = (ServletRegistration)map.get(servletName);
            logger.info("servlet name is " + servletName + " and accompanying ServletRegistration is " + servletRegistration.toString());
            mappings = servletRegistration.getMappings();
            logger.info("mappings of Servlet Registration is" + mappings.toString());
            //is it equal to our req?
            logger.info("boolean contains result: " + mappings.contains(urlPattern));
        } while(!mappings.contains(urlPattern));

        return true;
    }

    public static String getUrlPattern(HttpServletRequest request) {

        logger.info("method getUrlPattern has called. Getting Servlet Context from HttpServletRequest");
        ServletContext servletContext = request.getServletContext();
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        logger.info("Servlet Context is " + servletContext.toString());
        logger.info("accompanying servletPath " + servletPath + " and pathInfo " + pathInfo);
        String urlPattern = null;

        logger.info("Current path info: " + pathInfo);
        if (pathInfo != null) {
            logger.info("if pathInfo not null");
            boolean ultimatePath = pathInfo.equals("/*");
            logger.info("does path info equals /* : " + ultimatePath);
            if(!ultimatePath) {
                //Working with session!!!ALERT!!!
                logger.info("start working with session");
                String savedPathInfoLabel = "savedPathInfo";
                HttpSession session = request.getSession();
                String savedPathInfo = (String) session.getAttribute(savedPathInfoLabel);
                try {
                    if (savedPathInfo == null) {
                        logger.info("savedPathInfo is null!!!");
                        session.setAttribute(savedPathInfoLabel, pathInfo);
                        logger.info("path info is settled: " + session.getAttribute(savedPathInfoLabel));
                    } else {
                        logger.info("Path info in the session is already settled. Old path info in the session: "
                                + savedPathInfo
                                + "and new path info is: " + pathInfo);
                        session.setAttribute(savedPathInfoLabel, pathInfo);
                        logger.info("new path info has settled: " + session.getAttribute(savedPathInfoLabel));

                    }
                } catch (NullPointerException e) {
                    logger.info("NPE caught");
                    e.printStackTrace();
                }
                logger.info("stop working with session");
            }
            urlPattern = servletPath + "/*";
            return urlPattern;
        } else {
            logger.info("if pathInfo is null");
            //iterate to servletMappings
            boolean has = hasUrlPattern(servletContext, servletPath);

            logger.info("has urlPattern? : " + has + " then return servletPath: " + servletPath);
            if (has) {
                return servletPath;
            } else {
                logger.info("No url pattern was found");
                logger.info("trying to modify servletPath");
                int i = servletPath.lastIndexOf(46);
                logger.info("servletPath.lastIndexOf('.'): " + i);
                if (i != -1) {
                    String ext = servletPath.substring(i + 1);
                    logger.info("servletPath.substring(i + 1) result is: " + ext);
                    urlPattern = "*." + ext;
                    logger.info("new url pattern to hasUrlPattern method: " + urlPattern);
                    has = hasUrlPattern(servletContext, urlPattern);
                    logger.info("calling hasUrlPattern method again and taken result is: " + has);
                    if (has) {
                        return urlPattern;
                    }
                }

                // /editUserParam -> /
                //return "/";
                return servletPath;
            }
        }
    }
}
