package dao;

import bean.UserBill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Status;

import javax.transaction.TransactionalException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserBillsDao {

    private ConnectionBuilder connectionBuilder;

    private final Logger logger = LoggerFactory.getLogger(UserBillsDao.class);
    private final int UPDATE_TWO_TUPLES = 2;

    private static final String SQL_REQUEST_CLOSE_BILL = "update user_bills set close_date = ?, status = ? where bill_id = ?";

    private static final String SQL_REQUEST_OPEN_BILL = "insert into user_bills(responsible_user_id, balance, open_date, status) values (?, ?, ?, ?) returning bill_id";

    private static final String SQL_REQUEST_TRANSFER_MONEY = "update user_bills set\n" +
            "\t balance = transfer.balance\n" +
            "from(values \n" +
            "\t(?, (select balance from user_bills where bill_id = ?) + ?),\n" +
            "\t(?, (select balance from user_bills where bill_id = ?) - ?)\n" +
            ") as transfer(bill_id, balance)\n" +
            "where user_bills.bill_id = transfer.bill_id;\n";

    private static final String SQL_REQUEST_GET_USER_BILL_BY_ID = "";
    private static final String SQL_REQUEST_GET_BILLS_BY_USER_ID = "select * from user_bills where responsible_user_id = ?";
    private static final String SQL_REQUEST_UPDATE_STATUS_BY_ID = "update user_bills set status = ? where bill_id = ?;";


    public void setConnectionBuilder(ConnectionBuilder connectionBuilder) {
        this.connectionBuilder = connectionBuilder;
    }

    private Connection getConnection() throws SQLException {
        return this.connectionBuilder.getConnection();
    }


    public List<UserBill> getUserBillsByUserId(Long userId){
        logger.info("Getting list of user bill by userId");
        List<UserBill> list = new ArrayList<>();

        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_REQUEST_GET_BILLS_BY_USER_ID)){

            preparedStatement.setLong(1,userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            logger.info("preparing Bill list");

            if (resultSet.next()) {
                do {
                    logger.info("preparing Bill");
                    UserBill userBill = new UserBill();
                    userBill.setId(resultSet.getLong("bill_id"));

                    Date date = resultSet.getDate("open_date");
                    LocalDate localDate = date.toLocalDate();
                    logger.info("SQL Date be like: " + date.toString() + " and then cast to localDate be like: " + localDate.toString());
                    userBill.setOpenDate(localDate);

                    //todo think about adding close date
                    Date closeDate = resultSet.getDate("close_date");
                    if(closeDate != null) {
                        LocalDate closeLocalDate = closeDate.toLocalDate();
                        userBill.setCloseDate(closeLocalDate);
                    }
                    //userBill.setOpenDate((LocalDate) resultSet.getObject("open_date")); classCastEx!!!
                    userBill.setResponsible_user_id(userId);

                    userBill.setBalance(resultSet.getBigDecimal("balance"));

                    String status = resultSet.getString("status");
                    Status statusEnum = Status.valueOf(status);
                    userBill.setStatus(statusEnum);

                    logger.info("final result is: " + userBill.toString());
                    list.add(userBill);

                } while (resultSet.next());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        logger.info("sending bill list to servlet");
        return list;
    }

    public Long openUserBill(UserBill userBill){
        logger.info("Opening new user bill");

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_REQUEST_OPEN_BILL)){

            int c = 1;
            preparedStatement.setLong(c++, userBill.getResponsible_user_id());
            preparedStatement.setBigDecimal(c++, userBill.getBalance());

            Date date = Date.valueOf(userBill.getOpenDate());

            preparedStatement.setDate(c++, date);
            //logger.info("ALERT!!!Setting Object instead of Date in prepared statement ");
            //preparedStatement.setObject(c++, userBill.getOpenDate());

            //int res = 0;
            preparedStatement.setString(c++, userBill.getStatus().name());
            //todo added returning and executeUpdate to executeQuery
            //res = preparedStatement.executeUpdate();
            logger.info("preparing to execute query...");
            ResultSet resultSet = preparedStatement.executeQuery();
            Long billId = 0L;

            if(resultSet.next()){
                billId = resultSet.getLong("bill_id");
            }

            logger.info("bill has opened with id " + billId);
            return billId;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0L;
    }

    public int closeUserBill(Long billId, LocalDate closeDate){
        logger.info("closeUserBill method has started");

        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_REQUEST_CLOSE_BILL)) {

            int c = 1;
            Date date = Date.valueOf(closeDate);
            preparedStatement.setDate(c++, date);
            preparedStatement.setString(c++, String.valueOf(Status.CLOSED));
            preparedStatement.setLong(c, billId);
            int res = preparedStatement.executeUpdate();
            return res;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int billToBillTransaction(Long billFromId, Long billToId, BigDecimal amount){
        logger.info("billToBillTransfer method has started");

        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_REQUEST_TRANSFER_MONEY)){

            int c = 1;
            preparedStatement.setLong(c++, billToId);
            preparedStatement.setLong(c++, billToId);
            preparedStatement.setBigDecimal(c++, amount);
            preparedStatement.setLong(c++, billFromId);
            preparedStatement.setLong(c++, billFromId);
            preparedStatement.setBigDecimal(c, amount);
            logger.info("preparedStatement: " + preparedStatement.toString());
            int res = preparedStatement.executeUpdate();
            if (res != UPDATE_TWO_TUPLES){
                logger.error("Transaction Error");
            }

            return res;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    //todo or not todo?!
    public UserBill getUserBillById(Long billId){
        UserBill userBill = new UserBill();

        return userBill;
    }

    public int updateBillStatus(Long billId, Status status){
        logger.info("updateBillStatus method has started ");

        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_REQUEST_UPDATE_STATUS_BY_ID)){
            int c = 1;
            preparedStatement.setString(c++, status.name());
            preparedStatement.setLong(c, billId);
            int res = preparedStatement.executeUpdate();
            logger.info("updated " + res + " string");
            return res;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
