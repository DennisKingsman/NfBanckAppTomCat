package servlets.billServlets;

import bean.UserBill;
import dao.ConnectionBuilderImpl;
import dao.UserBillsDao;
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
import java.util.List;

@WebServlet("/billInfo")
public class BillInfoServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(BillInfoServlet.class);
    private UserBillsDao dao;

    @Override
    public void init() throws ServletException {
        logger.info("Init method in BillInfoServlet has started");
        dao = new UserBillsDao();
        dao.setConnectionBuilder(new ConnectionBuilderImpl());
        super.init();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("ALERT! SERVICE METHOD IN BillInfoServlet has started");
        super.service(req, resp);
        logger.info("SERVICE METHOD HAS FINISHED");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    //todo
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("do get method in Bill Info Servlet has started");
        HttpSession session = req.getSession();
        List<UserBill> bills = dao.getUserBillsByUserId(AppUtils.getLoginedUser(session).getId());
        session.setAttribute("billList", bills);
        logger.info("dataBase request has return: " + bills.toString());
        req.setAttribute("userBills", bills);
        RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/billInfo.jsp");
        requestDispatcher.forward(req, resp);
    }
}
