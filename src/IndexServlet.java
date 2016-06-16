import java.io.IOException;

/**
 * Created by lzhengning on 6/16/16.
 */

public class IndexServlet extends javax.servlet.http.HttpServlet {

    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        request.getRequestDispatcher("myIndex.jsp").forward(request, response);
    }
}
