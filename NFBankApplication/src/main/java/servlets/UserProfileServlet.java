package servlets;

import bean.UserAccount;
import dao.ConnectionBuilderImpl;
import dao.UserAccountDao;
import java.io.IOException;
import java.sql.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.AppUtils;

@WebServlet({"/userProfile"})
public class UserProfileServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(UserProfileServlet.class);

    private UserAccountDao dao;

    public void init() throws ServletException {
        this.dao = new UserAccountDao();
        this.dao.setConnectionBuilder(new ConnectionBuilderImpl());
        logger.info("UserProfileServlet initiated");
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userLogin = AppUtils.getLoginedUser(req.getSession()).getLogin();
        UserAccount userAccount = this.dao.getProfile(userLogin);

        logger.info("UserProfile from DB: " + userAccount.toString());

        //NPE??
        Date dateFromDB = userAccount.getDate();

        logger.info("User Date of birth is: " + dateFromDB);

        logger.info("trying to costume date");
        AppUtils.getLoginedUser(req.getSession()).setName(userAccount.getName());
        AppUtils.getLoginedUser(req.getSession()).setSecondName(userAccount.getSecondName());

        if(dateFromDB == null) {
            AppUtils.getLoginedUser(req.getSession()).setDate(null);
        }else AppUtils.getLoginedUser(req.getSession()).setDate(dateFromDB);


        logger.info("User in the session now: " + AppUtils.getLoginedUser(req.getSession()).toString());

        RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/userProfile.jsp");
        requestDispatcher.forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
