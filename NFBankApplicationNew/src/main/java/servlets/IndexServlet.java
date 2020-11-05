package servlets;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet({"/", "/index"})
public class IndexServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(IndexServlet.class);
    private static final long serialVersionUID = 1L;

    public IndexServlet() {
    }

    public void init() throws ServletException {
        super.init();
        this.logger.info("Servlet IndexServlet has started");
        RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/index.jsp");
        requestDispatcher.forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
