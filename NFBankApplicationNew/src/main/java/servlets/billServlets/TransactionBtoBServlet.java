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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@WebServlet({"/transferBtoB/*"})
public class TransactionBtoBServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(TransactionBtoBServlet.class);
    private UserBillsDao dao;
    private XmlHistoryDao xmlDao;

    @Override
    public void init() throws ServletException {
        logger.info("TransactionServlet has started");
        dao = new UserBillsDao();
        dao.setConnectionBuilder(new ConnectionBuilderImpl());
        xmlDao = new XmlHistoryDao();
        xmlDao.setConnectionBuilder(new ConnectionBuilderImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("doGet method has started");
        HttpSession session = req.getSession();
        List<UserBill> bills = new ArrayList<>();
        try {
            bills = (List<UserBill>) session.getAttribute("billList");
            logger.info("bill list: " + bills.toString());
        }catch (NullPointerException e){
            logger.error("NPE!!!", e);
            e.printStackTrace();
        }

        String tail = req.getPathInfo();
        this.logger.info("tail(needed path info) of servletPath is: " + tail);
        tail = tail.substring(1);
        Long billId = Long.parseLong(tail); //!!!

        BigDecimal fromBillBalance = null;
        int counter = 0;
        //ConcurrentModificationException!!! if use forEach
        Iterator iterator = bills.iterator();

        while (iterator.hasNext()){
            UserBill bill = (UserBill) iterator.next();
            logger.info("Current bill: " + bill);
            if(bill.getId().equals(billId)){
                logger.info("bill with id: " + billId + "will be removed");
                fromBillBalance = bill.getBalance();
                logger.info("its balance: " + fromBillBalance);
                iterator.remove();
            }
            if(!bill.getStatus().equals(Status.OPEN)){
                logger.info("remove bill if is't open");
                iterator.remove();
            }
        }

        req.setAttribute("FromBillBalance", fromBillBalance);
        req.setAttribute("billFromId", billId);
        req.setAttribute("otherBills", bills);

        RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/transferBtoB.jsp");
        requestDispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("doPost method has started");
        String amountStr = req.getParameter("amount");
        String billToIdStr = req.getParameter("BillToId");
        String billFromIdStr = req.getParameter("BillFromId");
        String amountBorderStr = req.getParameter("BillFromAmount");
        logger.info("amount/firstId/secondId/amountBorder: " + amountStr + "/" + billToIdStr + "/" + billFromIdStr + "/" + amountBorderStr);

        BigDecimal amount = BigDecimal.valueOf(Double.valueOf(amountStr));
        BigDecimal amountBorder = BigDecimal.valueOf(Double.valueOf(amountBorderStr));
        Long billToId = Long.parseLong(billToIdStr);
        Long billFromId = Long.parseLong(billFromIdStr);

        if(amount.compareTo(amountBorder) > 0){
            RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/invalidAmount.jsp");
            requestDispatcher.forward(req, resp);
        }

        int res = 0;
        //update (first bill)-- and (second bill)++
        res = dao.billToBillTransaction(billFromId, billToId, amount);

        if(res > 0){
            HttpSession session = req.getSession();
            UserAccount loginedUser = AppUtils.getLoginedUser(session);
            String actionTo = ActionName.INCREASE.name();
            String actionFrom = ActionName.WITHDRAW.name();
            xmlDao.auditMoneyIncrease(loginedUser.getId(), billToId, billFromId, amount, actionTo, actionFrom);
            logger.info("history has stored");
        }

        resp.sendRedirect(req.getContextPath() + "/billInfo");
    }
}
