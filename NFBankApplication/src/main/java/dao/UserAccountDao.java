
package dao;

import bean.UserAccount;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAccountDao {
    private static final Logger logger = LoggerFactory.getLogger(UserAccountDao.class);
    private static final String SQL_REQUEST_INSERT_INIT = "insert into user_account(user_login, user_password, user_role) values (?, ?, ?)";
    private static final String SQL_REQUEST_SELECT_PROFILE = "select * from user_account where user_login = ?";
    private static final String SQL_REQUEST_LOGIN = "select user_login, user_password from user_account where user_login = ? and user_password = ?";
    private static final String SQL_REQ_SEARCH_REGISTERED_USER = "select user_login from user_account where user_login = ?";
    private static final String SQL_REQUEST_DELETE = "delete from user_account where user_login = ?";
    private static final String SQL_REQUEST_UPDATE_DOB = "update user_account set date_of_birth = ? where user_login = ?";
    private static final String SQL_REQUEST_UPDATE_NAME = "update user_account set user_name = ? where user_login = ?";
    private static final String SQL_REQUEST_UPDATE_SNAME = "update user_account set second_name = ? where user_login = ?";
    private static final String SQL_REQUEST_UPDATE_PROFILE = "update user_account set date_of_birth = ?, user_name = ?, second_name = ? where user_login = ?";
    private ConnectionBuilder connectionBuilder;

    public UserAccountDao() {
    }

    public void setConnectionBuilder(ConnectionBuilder connectionBuilder) {
        this.connectionBuilder = connectionBuilder;
    }

    private Connection getConnection() throws SQLException {
        return this.connectionBuilder.getConnection();
    }

    public void updateUserProfileParam(String login, String param, String paramLabel){
        logger.info("updateUserProfileParam has started");
        paramLabel = paramLabel.substring(1);
        logger.info("new param label: " + paramLabel);
        String requiredStatement;
        int mark = 0;
        switch (paramLabel){
            case "name":
                logger.info("Name case in doPost method");
                requiredStatement = SQL_REQUEST_UPDATE_NAME;
                break;
            case "secondName":
                logger.info("second name case in doPost method");
                requiredStatement = SQL_REQUEST_UPDATE_SNAME;
                break;
                default:
                    logger.info("ALERT!!! NO SQL REQUEST WAS SETTLED");
                    requiredStatement = null;
        }

        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(requiredStatement)){
            int counter = 1;
            preparedStatement.setString(counter++, param);
            preparedStatement.setString(counter, login);
            logger.info("result statement is: " + preparedStatement);
            int row = preparedStatement.executeUpdate();
            logger.info("Was updated " + row + " rows, updating complete! ");
        } catch (SQLException e) {
            logger.info("updateUserProfileParam fails");
            e.printStackTrace();
        }

    }

    public UserAccount loginPerson(String login, String password){
        logger.info("Loggining has started in loginPerson method");
        UserAccount userAccount = new UserAccount();


        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_REQUEST_LOGIN)){

            logger.info("transferred login and password are: " + login + " " + password);
            int counter = 1;
            preparedStatement.setString(counter++, login);
            preparedStatement.setString(counter++, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                logger.info("ResultSet.next has called. Contains: "
                        + resultSet.getString("user_login")
                        + " "
                        + resultSet.getString("user_password"));
                userAccount.setLogin(login);
                userAccount.setPassword(password);
                userAccount.setRole("USER");

                logger.info("userAccount object preparation were complete");
            }
            logger.info("after if");
        } catch (SQLException e) {
            logger.info("login fails");
            e.printStackTrace();
        }

        logger.info("before return userAccount: " + userAccount.getLogin());
        return userAccount;
    }

    public UserAccount getProfile(String login){
        logger.info("getting profile has started");
        UserAccount userAccount = new UserAccount();

        logger.info("before setting connection");
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_REQUEST_SELECT_PROFILE)){
            logger.info("Connected successfully");
            int counter = 1;
            preparedStatement.setString(counter, login);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                logger.info("Preparing user account object...");
                userAccount.setLogin(login);
                userAccount.setPassword(resultSet.getString("user_password"));
                userAccount.setRole("USER");
                userAccount.setDate(resultSet.getDate("date_of_birth"));
                userAccount.setName(resultSet.getString("user_name"));
                userAccount.setSecondName(resultSet.getString("second_name"));
            }else logger.info("empty result set!!!");

        } catch (SQLException e) {
            logger.info("getProfile method fails");
            e.printStackTrace();
        }

        return userAccount;
    }

    public void registerUserAccount(String login, String password){
        logger.info("register in DB has started");

        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_REQUEST_INSERT_INIT)){

            int counter = 1;
            preparedStatement.setString(counter++, login);
            preparedStatement.setString(counter++, password);
            preparedStatement.setString(counter++, "USER");
            preparedStatement.executeUpdate();
            logger.info("register complete");

        } catch (SQLException e) {
            logger.info("register fails");
            e.printStackTrace();
        }
    }

    public boolean hasSameLoginInDataBase(String userLogin) {
        logger.info("CheckForRegistration method has called");

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_REQ_SEARCH_REGISTERED_USER)) {

            String loginFromDB;
            int counter = 1;
            preparedStatement.setString(counter++, userLogin);
            logger.info("Statement in hasSameLogin: " + preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                loginFromDB = resultSet.getString("user_login");
                logger.info("Query was execute: " + loginFromDB + ". Compare logins: " + userLogin.equals(loginFromDB) + ". Checking resultSet: " + !resultSet.next());
                if (!userLogin.equals(loginFromDB)) {
                    return false;
                }else {
                    logger.info("User founded");
                    return true;
                }
            } else {
                logger.info("No such result set");
                return false;
            }
        } catch(Exception e){
            logger.info("did not searched same login in DB");
            e.printStackTrace();
        }

        return false;
    }


    public void setUserProfile(String login, Date date, String name, String secondName){
        logger.info("setUserProfile has started");

        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_REQUEST_UPDATE_PROFILE)){

            int counter = 1;
            preparedStatement.setDate(counter++, date);
            preparedStatement.setString(counter++, name);
            preparedStatement.setString(counter++, secondName);
            preparedStatement.setString(counter++, login);
            preparedStatement.executeUpdate();
            logger.info("setUserProfile complete");
        } catch (SQLException e) {
            logger.info("setUserProfile fails");
            e.printStackTrace();
        }

    }

    // savedPathInfo always be /dateOfBirth because user has not other dates in userDAO
    public void updateDateParam(String userLogin, Date date, String savedPathInfo) {
        logger.info("updateUserProfileParam has started");
        savedPathInfo = savedPathInfo.substring(1);
        logger.info("new param label: " + savedPathInfo);

        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_REQUEST_UPDATE_DOB)){

            logger.info("preparing statement...");
            int c = 1;
            preparedStatement.setDate(c++, date);
            preparedStatement.setString(c, userLogin);
            logger.info("result statement is: " + preparedStatement);
            int row = preparedStatement.executeUpdate();
            logger.info("Was updated " + row + " rows, updating complete! ");

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
