package filter;

import bean.UserAccount;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.UserRoleRequestWrapper;
import utils.AppUtils;
import utils.SecurityUtils;

@WebFilter({"/*"})
public class SecurityFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    public SecurityFilter() {
    }

    public void destroy() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        this.logger.info("entering to method doFilter in SecurityFilter.class");
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String servletPath = request.getServletPath();
        this.logger.info("Current path " + servletPath + " and current req " + request);
        UserAccount loginedUser = AppUtils.getLoginedUser(request.getSession());
        this.logger.info("logined user is: " + loginedUser);
        this.logger.info("boolean equals result: " + servletPath.equals("/login"));
        if (servletPath.equals("/login")) {
            this.logger.info("entering in first <<If>>, servletPath is: " + servletPath);
            this.logger.info("Attention !!! recursion alert");
            filterChain.doFilter(request, response);
        } else {
            HttpServletRequest wrapRequest = request;
            String requestUrl;
            if (loginedUser != null) {
                this.logger.info("entering in second <<If>>");
                String userLogin = loginedUser.getLogin();
                requestUrl = loginedUser.getRole();
                List<String> roles = new ArrayList();
                roles.add(requestUrl);
                wrapRequest = new UserRoleRequestWrapper(userLogin, roles, request);
                this.logger.info("see how ur wrapRequest has changed " + wrapRequest);
            }

            boolean isSecurityMethodResult = SecurityUtils.isSecurityPage(request);
            if (!isSecurityMethodResult) {
                this.logger.info("not secured page");
            }

            this.logger.info("SecurityUtils.isSecurityPage result: " + isSecurityMethodResult);
            if (isSecurityMethodResult) {
                this.logger.info("this page is secure");
                if (loginedUser == null) {
                    this.logger.info("Loggined user is null");
                    requestUrl = request.getRequestURI();
                    int redirectId = AppUtils.storeRedirectAfterLoginUrl(request.getSession(), requestUrl);
                    response.sendRedirect(((HttpServletRequest)wrapRequest).getContextPath() + "/login?redirectId=" + redirectId);
                    return;
                }
            }

            boolean hasPermission;
            if (!isSecurityMethodResult) {
                hasPermission = true;
            } else {
                hasPermission = SecurityUtils.hasPermission((HttpServletRequest)wrapRequest);
            }

            this.logger.info("hasPermission result: " + hasPermission);
            if (!hasPermission) {
                this.logger.info("user has't permission to this page");
                RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/WEB-INF/accessDenied.jsp");
                dispatcher.forward(request, response);
            } else {
                this.logger.info("ATTENTION !!! recursion alert two");
                filterChain.doFilter((ServletRequest)wrapRequest, response);
            }
        }
    }
}
