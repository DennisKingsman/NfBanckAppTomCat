package dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import utils.ActionName;
import utils.Status;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class XmlHistoryDao {

    private ConnectionBuilder connectionBuilder;

    //createInitXml(u_Id integer,u_Login text)
    private static final String SQL_INIT_XML_QUERY = "insert into xml_audit(user_id, recording_date, audit) values (?, current_date, createInitXml(?::integer, ?::text))";

    //initBillStatusAudit(userId, billId, actionName, billStatus)
    private static final String SQL_INIT_NEW_BILL_STATUS_AUDIT = "select initBillStatusAudit(?::integer, ?::integer, ?::text, ?::text)";

//    transferMoney(
//            userId integer,
//            billToId integer,
//            amount numeric,
//            actionTo text,
//            billFromId integer default null,
//            actionFrom text default null
//    )
    private static final String SQL_INIT_BIIL_MONEY = "select transferMoney(?::integer, ?::integer, ?::numeric, ?::text)";
    private static final String SQL_TRANSFER_BILL_MONEY = "select transferMoney(?::integer, ?::integer, ?::numeric, ?::text, ?::integer, ?::text)";

    private final Logger logger = LoggerFactory.getLogger(XmlHistoryDao.class);

    public XmlHistoryDao(){

    }

    public void setConnectionBuilder(ConnectionBuilder connectionBuilder){
        this.connectionBuilder = connectionBuilder;
    }

    private Connection getConnection() throws SQLException {
        return this.connectionBuilder.getConnection();
    }

    public void auditMoneyIncrease(Long userInSessionId, Long billId, BigDecimal initMoney, String actionName) {
        logger.info("first method auditMoneyIncrease has started");
        int counter = 1;
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_INIT_BIIL_MONEY)){
            preparedStatement.setLong(counter++, userInSessionId);
            preparedStatement.setLong(counter++, billId);
            preparedStatement.setBigDecimal(counter++, initMoney);
            preparedStatement.setString(counter, actionName);
            logger.info("executing statement...");
            preparedStatement.executeQuery();
            logger.info("executed successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void  auditMoneyIncrease(Long userId, Long billToId, Long billFromId, BigDecimal amount, String actionTo, String actionFrom){
        logger.info("second method auditMoneyIncrease has started");

        int counter;
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_TRANSFER_BILL_MONEY)){

            counter = 1;
            preparedStatement.setLong(counter++, userId);
            preparedStatement.setLong(counter++, billToId);
            preparedStatement.setBigDecimal(counter++, amount);
            preparedStatement.setString(counter++, actionTo);
            preparedStatement.setLong(counter++, billFromId);
            preparedStatement.setString(counter, actionFrom);
            preparedStatement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void auditInitUserBill(Long userId, Long billId, String actionName, String billStatus){
        logger.info("method auditINitUserBill has started");

            int counter = 1;

            try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_INIT_NEW_BILL_STATUS_AUDIT)){

                preparedStatement.setLong(counter++, userId);
                preparedStatement.setLong(counter++, billId);
                preparedStatement.setString(counter++, actionName);
                preparedStatement.setString(counter, billStatus);
                if(preparedStatement.execute()){
                    logger.info("initBillStatusAudit called successfully");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

    }

    public int startHistoryCollect(Long userId, String userLogin){
        logger.info("method startHistoryCollect has started");

        //Document xmlInit = createXmlDoc(userId, userLogin, billId);
        //String strXmlInit = docToStr(xmlInit);

        int c = 1;
        try (Connection connection = connectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INIT_XML_QUERY)){

            preparedStatement.setLong(c++, userId);

            //createXmlInit
            preparedStatement.setLong(c++, userId);
            preparedStatement.setString(c, userLogin);
            logger.info("result query: " + preparedStatement.toString());
            int res = preparedStatement.executeUpdate();
            return res;

        }catch (SQLException e){
            e.printStackTrace();
        }

        return 0;
    }

    private String docToStr(Document xmlInit) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(xmlInit), new StreamResult(sw));
            logger.info("xml to str result: " + sw.toString());
            return sw.toString();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Document createXmlDoc(Long userId, String userLogin, Long billId) {
        String actionName = ActionName.INITIALISATION.name();

        DocumentBuilderFactory dbf = null;
        DocumentBuilder db  = null;
        Document doc = null;

        String rootNodeName = "audit";
        String rootAttributeName = "lang";
        String rootAttributeValue = "en";

        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            doc = db.newDocument();

            //root
            Element rootAudit = doc.createElement(rootNodeName);
            rootAudit.setAttribute(rootAttributeName, rootAttributeValue);

            Element userElement = doc.createElement("user");
            userElement.setAttribute("userId", String.valueOf(userId));
            userElement.setAttribute("login", userLogin);

            //root/user
            rootAudit.appendChild(userElement);

            Element statusElement = doc.createElement("status");
            statusElement.setTextContent(Status.OPEN.name());

            Element actionElement = doc.createElement("action");
            actionElement.setAttribute("billId", String.valueOf(billId));
            actionElement.setAttribute("name", actionName);

            //action/status
            actionElement.appendChild(statusElement);
            //user/action/status
            userElement.appendChild(actionElement);
            Element replaceableAction = doc.createElement("action");
            userElement.appendChild(replaceableAction);


        }catch (ParserConfigurationException e){
            e.printStackTrace();
        }

        return doc;
    }
}
