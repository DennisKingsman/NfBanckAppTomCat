
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
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.AppUtils;

@WebServlet({"/editUserParam/*"})
public class EditUserParamServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(EditUserParamServlet.class);
    private UserAccountDao dao;

    public EditUserParamServlet() {
    }

    public void init() throws ServletException {
        super.init();
        this.logger.info("EditUserParamServlet has started");
        this.dao = new UserAccountDao();
        this.dao.setConnectionBuilder(new ConnectionBuilderImpl());
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.logger.info("Post method in EditUserParamServlet has started");
        String servletPath = req.getServletPath();
        this.logger.info("currentPath: " + servletPath);

        //start working with session
        HttpSession session = req.getSession();
        String savedPathInfo = (String) session.getAttribute("savedPathInfo");
        boolean compare = savedPathInfo == null;
        //== => true != => false
        logger.info("Compare: " + compare);
        String tail = req.getPathInfo();

        try {
            if(compare){
                logger.info("Alert!!!Path info in Edit param servlet is null: no param found. Tail is: " + tail);
                //todo redirect to user profile

                resp.sendRedirect(req.getContextPath() + "/userInfo");
            }else {
                tail = savedPathInfo;
                logger.info("Path info for edited param is: " + tail);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }


        this.logger.info("tail(needed path info) of servletPath is: " + tail);
        String paramStrring = req.getParameter("paramString");

        if(paramStrring == null){
            resp.sendRedirect(req.getContextPath() + "/userProfile");
            return;
        }

        String userLogin = AppUtils.getLoginedUser(req.getSession()).getLogin();
        this.logger.info("posted param: " + paramStrring + " and user login is: " + userLogin);
        this.dao.updateUserProfileParam(userLogin, paramStrring, tail);
        this.logger.info("Update completed, sending redirect to user profile page");
        resp.sendRedirect(req.getContextPath() + "/userProfile");
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.logger.info("Get method in EditUserParamServlet has started");
        RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/editUserParam.jsp");
        requestDispatcher.forward(req, resp);
    }
}
