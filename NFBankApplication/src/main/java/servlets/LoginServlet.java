
package servlets;

import bean.UserAccount;
import dao.ConnectionBuilderImpl;
import dao.UserAccountDao;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.AppUtils;

@WebServlet({"/login"})
public class LoginServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private static final long serialVersionUID = 1L;
    private UserAccountDao dao;

    public LoginServlet() {
    }

    public void init() throws ServletException {
        this.logger.info("Servlet LoginServlet has started");
        this.dao = new UserAccountDao();
        this.dao.setConnectionBuilder(new ConnectionBuilderImpl());
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.logger.info("Called doGet method in LoginServlet.class");
        RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp");
        requestDispatcher.forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.logger.info("doPost method has started in LoginServlet.class");
        String userLogin = req.getParameter("userLogin");
        String password = req.getParameter("userPassword");
        this.logger.info("userLogin and Password from request: " + userLogin + " " + password);
        UserAccount userAccount = new UserAccount();

        userAccount = dao.loginPerson(userLogin, password);

        logger.info("again in doPOst");

        if (userAccount.getLogin() == null) {
            String errorMessage = "Invalid user name or password";
            logger.info(errorMessage);
            req.setAttribute("errorMessage", errorMessage);
            RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp");
            requestDispatcher.forward(req, resp);
        } else {
            this.logger.info("storing logined user...");
            AppUtils.storeLoginedUser(req.getSession(), userAccount);
            int redirectId = -1;

            try {
                redirectId = Integer.parseInt(req.getParameter("redirectId"));
            } catch (Exception var8) {
                var8.printStackTrace();
            }

            String requestUrl = AppUtils.getRedirectAfterLoginUrl(req.getSession(), redirectId);
            if (requestUrl != null) {
                resp.sendRedirect(requestUrl);
            } else {
                resp.sendRedirect(req.getContextPath() + "/userInfo");
            }

        }
    }
}

