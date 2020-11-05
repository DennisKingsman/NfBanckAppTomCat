package servlets.billServlets;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/editBill/*")
public class EditBillServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(EditBillServlet.class);

    private UserBillsDao dao;

    private XmlHistoryDao xmlDao;

    @Override
    public void init() throws ServletException {
        logger.info("init method in Edit Bill Servlet has started");
        super.init();
        dao = new UserBillsDao();
        xmlDao = new XmlHistoryDao();
        xmlDao.setConnectionBuilder(new ConnectionBuilderImpl());
        dao.setConnectionBuilder(new ConnectionBuilderImpl());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("do POst method in EditBillServlet has started");
        //take param from UI
        String selectedStatus = req.getParameter("statusItems");
        logger.info("Received parameters: selectedStatus: " + selectedStatus);
        HttpSession session = req.getSession();
        //take userId
        Long userId = AppUtils.getLoginedUser(session).getId(); //!!!
        logger.info("userId: " + userId);
        //take bill Id
        String servletPath = req.getServletPath();
        this.logger.info("currentPath: " + servletPath);

        //todo create util functional
        //start working with session
        String savedPathInfo = (String) session.getAttribute("savedPathInfo");
        boolean compare = savedPathInfo == null;
        //== => true != => false
        logger.info("Compare: " + compare);
        String tail = req.getPathInfo();
        try {
            if(compare){
                logger.info("Alert!!!Path info in Edit param servlet is null: no param found. Tail is: " + tail);
                resp.sendRedirect(req.getContextPath() + "/billInfo");
            }else {
                tail = savedPathInfo;
                logger.info("Path info for edited param is: " + tail);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        this.logger.info("tail(needed path info) of servletPath is: " + tail);
        tail = tail.substring(1);
        Long billId = Long.parseLong(tail); //!!!
        this.logger.info(" supposed bill id: " + billId);
        logger.info("check requested data: billId " + billId + " status " + Status.valueOf(selectedStatus));

        String userLogin = AppUtils.getLoginedUser(session).getLogin();

        int res = 0;

        String closed = String.valueOf(Status.CLOSED);
        if (selectedStatus.equals(closed)){
            logger.info("closing bill");
            LocalDate closeDate = LocalDate.now();
            res = dao.closeUserBill(billId, closeDate);
        }else {
            res = dao.updateBillStatus(billId, Status.valueOf(selectedStatus));
            logger.info("update completed");
        }

        if(res > 0){
            xmlDao.auditInitUserBill(userId, billId, ActionName.REPLACEMENT.name(), selectedStatus);
        }

        resp.sendRedirect(req.getContextPath() + "/billInfo");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("doGet method in EditBillServlet has started");

        //todo ArrayList
        List<String> billStatus = new ArrayList<>();
        for (Status s: Status.values()) {
            logger.info("Preparing status " + s.name());
            billStatus.add(s.name());
        }

        logger.info("StringList: " + billStatus);
        req.setAttribute("status", billStatus);

        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/WEB-INF/editBill.jsp");
        requestDispatcher.forward(req,resp);
    }
}
