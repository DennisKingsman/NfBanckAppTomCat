package servlets.billServlets;

import bean.UserAccount;
import bean.UserBill;
import dao.ConnectionBuilderImpl;
import dao.UserBillsDao;
import dao.XmlHistoryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ActionName;
import utils.AppUtils;
import utils.Status;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@WebServlet("/createBill")
public class CreateBillServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(CreateBillServlet.class);

    private UserBillsDao userBillsDao;
    private XmlHistoryDao xmlDao;


    @Override
    public void init() throws ServletException {
        logger.info("init method in " + CreateBillServlet.class + "called");
        userBillsDao = new UserBillsDao();
        userBillsDao.setConnectionBuilder(new ConnectionBuilderImpl());
        super.init();
        xmlDao = new XmlHistoryDao();
        xmlDao.setConnectionBuilder(new ConnectionBuilderImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("doGet method in CreateBillServlet called");
        RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/createBill.jsp");
        requestDispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("doPOst method in CreateBillServlet has called");
        req.setCharacterEncoding("UTF-8");

        String hiddenCreate = req.getParameter("hiddenCreate");
        String submitCreate = req.getParameter("create");
        //String radio = req.getParameter("answer");
        logger.info("Expected values: hiddenCreate: " + hiddenCreate + " submitCreate " + submitCreate);

        UserBill userBill = new UserBill();
        //take user login
        HttpSession session = req.getSession();
        UserAccount loginedUser = AppUtils.getLoginedUser(session);
        Long userInSessionId = loginedUser.getId();
        logger.info(" user id for new bill: " + userInSessionId);
        userBill.setResponsible_user_id(userInSessionId);
        //take current date
        //using java 8 java.time (стильно модно молодежно)
        LocalDate currentDate = LocalDate.now();
        logger.info(" current date for bill: " + currentDate);
        userBill.setOpenDate(currentDate);
        //set status
        userBill.setStatus(Status.OPEN);
        //set def balance
        BigDecimal initMoney = BigDecimal.valueOf(999.9);

        userBill.setBalance(initMoney);

        Long billId = 0L;
        billId = userBillsDao.openUserBill(userBill);

        if(billId != 0L){
            xmlDao.auditInitUserBill(userInSessionId, billId, ActionName.INITIALISATION.name(), Status.OPEN.name());
            //save money increasing history
            xmlDao.auditMoneyIncrease(userInSessionId, billId, initMoney, ActionName.INCREASE.name());
        }

        logger.info("redirecting to Bill Info page");
        resp.sendRedirect(req.getContextPath() + "/billInfo");
    }
}
