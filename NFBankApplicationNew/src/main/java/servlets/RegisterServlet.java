
package servlets;

import dao.ConnectionBuilderImpl;
import dao.UserAccountDao;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.XmlHistoryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet({"/register"})
public class RegisterServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    private UserAccountDao dao;
    private XmlHistoryDao xmlDao;

    public RegisterServlet() {
    }

    public void init() throws ServletException {
        this.logger.info("init method in RegisterServlet.class has started");
        this.dao = new UserAccountDao();
        this.dao.setConnectionBuilder(new ConnectionBuilderImpl());

        xmlDao = new XmlHistoryDao();
        xmlDao.setConnectionBuilder(new ConnectionBuilderImpl());
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.logger.info("Get method in RegisterServlet has started");
        RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/register.jsp");
        requestDispatcher.forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.logger.info("Post method in RegisterServlet has started");
        req.setCharacterEncoding("UTF-8");
        String userLogin = req.getParameter("userLogin");
        String userPassword = req.getParameter("userPassword");
        this.logger.info("userName and Password from request: " + userLogin + " " + userPassword);
        boolean has = this.dao.hasSameLoginInDataBase(userLogin);
        if (has) {
            String errorMessage = "This user Login is already in use";
            req.setAttribute("errorMessage", errorMessage);
            RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/register.jsp");
            requestDispatcher.forward(req, resp);
        } else {
            this.logger.info("After searching same user: " + has);
            Long userId = this.dao.registerUserAccount(userLogin, userPassword);
            if(userId > 0L){
                xmlDao.startHistoryCollect(userId, userLogin);
                logger.info("history initiated");
            }
            this.logger.info("registration complete");
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
