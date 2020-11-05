
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
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.AppUtils;

@WebServlet({"/editUserProfile"})
public class EditUserProfileServlet extends HttpServlet {
    private UserAccountDao dao;

    private final Logger logger = LoggerFactory.getLogger(EditUserProfileServlet.class);

    public void init() throws ServletException {
        logger.info("init method in EditUserProfileServlet has started");
        this.dao = new UserAccountDao();
        this.dao.setConnectionBuilder(new ConnectionBuilderImpl());
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession();
        String userLogin = AppUtils.getLoginedUser(session).getLogin();
        UserAccount userAccount = dao.getProfile(userLogin);
        req.setAttribute("userAccount", userAccount);

        RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/editUserProfile.jsp");
        requestDispatcher.forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("doPost method called");

        String userName = req.getParameter("userName");
        String secondName = req.getParameter("secondName");
        //without date for now
        logger.info("Date view like: " + req.getParameter("dateOfBirth"));
        String parseDate = req.getParameter("dateOfBirth");
        logger.info("Started string of date: " + parseDate);
        int i = parseDate.lastIndexOf('T');
        parseDate = parseDate.substring(0, i);
        logger.info("get away time part: " + parseDate);
        //year-mouth-day
        int year, mounth, day, last, first;
        last = parseDate.lastIndexOf('-');
        first = parseDate.indexOf('-');
        year = Integer.parseInt(parseDate.substring(0, first));
        mounth = Integer.parseInt(parseDate.substring(++first, last));
        day = Integer.parseInt(parseDate.substring(++last));
        logger.info("received date: "
                + year
                + " "
                + mounth
                + " "
                + day);

        Date date = new Date(year - 1900, mounth, day);

        this.dao.setUserProfile(AppUtils.getLoginedUser(req.getSession()).getLogin(), date, userName, secondName);
        resp.sendRedirect(req.getContextPath() + "/userProfile");
    }
}
