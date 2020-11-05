package servlets.billServlets;

import bean.UserAccount;
import dao.ConnectionBuilderImpl;
import dao.UserBillsDao;
import dao.XmlHistoryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ActionName;
import utils.AppUtils;
import utils.Status;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/closeBill/*")
public class CloseBillServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(CreateBillServlet.class);
    private UserBillsDao userBillsDao;
    private XmlHistoryDao xmlDao;

    @Override
    public void init() throws ServletException {
        logger.info("init method in " + CloseBillServlet.class); //
        userBillsDao = new UserBillsDao();
        userBillsDao.setConnectionBuilder(new ConnectionBuilderImpl());

        xmlDao = new XmlHistoryDao();
        xmlDao.setConnectionBuilder(new ConnectionBuilderImpl());

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("doGet method has started");

        String tail = req.getPathInfo();
        this.logger.info("tail(needed path info) of servletPath is: " + tail);
        tail = tail.substring(1);
        Long billId = Long.parseLong(tail); //!!!

        HttpSession session = req.getSession();
        UserAccount loginedUser = AppUtils.getLoginedUser(session);

        LocalDate closeDate = LocalDate.now();
        int res = userBillsDao.closeUserBill(billId, closeDate);
        if (res > 0){
            xmlDao.auditInitUserBill(loginedUser.getId(), billId, ActionName.REPLACEMENT.name(), Status.CLOSED.name());
        }


        logger.info("doGet method has finished");
        resp.sendRedirect(req.getContextPath() + "/billInfo");
    }
}
