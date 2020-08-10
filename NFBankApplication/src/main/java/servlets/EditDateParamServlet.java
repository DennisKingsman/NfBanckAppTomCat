package servlets;

import dao.ConnectionBuilderImpl;
import dao.UserAccountDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.AppUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/dateParam/*")
public class EditDateParamServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(EditDateParamServlet.class);
    private UserAccountDao dao;
    //add bills dao

    @Override
    public void init() throws ServletException {
        logger.info("EditDateParamServlet has started");
        super.init();
        dao = new UserAccountDao();
        //init bills dao
        dao.setConnectionBuilder(new ConnectionBuilderImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("doGet in " + EditDateParamServlet.class.getSimpleName() + " started");
        RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/editDateParam.jsp");
        requestDispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("doPost method has started");

        String servletPath = req.getServletPath();
        this.logger.info("currentPath: " + servletPath);

        //start working with session
        HttpSession session = req.getSession();
        String savedPathInfo = (String) session.getAttribute("savedPathInfo");
        boolean compare = savedPathInfo == null;
        //== => true != => false
        logger.info("Compare: " + compare);
        String parseDate = req.getParameter("dateParam");
        String userLogin = AppUtils.getLoginedUser(req.getSession()).getLogin();
        this.logger.info("posted param: " + parseDate + " and user login is: " + userLogin);

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

        try {
            if(compare){
                logger.info("Alert!!!Path info in Edit param servlet is null: no param found. Path info is : " + savedPathInfo);
                resp.sendRedirect(req.getContextPath() + "/userInfo");

            }else {
                logger.info("Saved Path info is: " + savedPathInfo);
                switch (savedPathInfo){
                    case "/dateOfBirth" :
                        logger.info("date of Birth case");
                        dao.updateDateParam(userLogin, date, savedPathInfo);
                        this.logger.info("Update completed, sending redirect to user profile page");
                        resp.sendRedirect(req.getContextPath() + "/userProfile");
                }
                //todo add case with bill date and operation date
                logger.info("quit try block");
            }
        }catch (NullPointerException e){
            logger.info("NPE !!!");
            e.printStackTrace();
        }

    }
}
