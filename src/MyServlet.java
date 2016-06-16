import java.io.IOException;

/**
 * Created by lzhengning on 6/16/16.
 */

public class MyServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        request.setAttribute("name", "Liu");
        request.getRequestDispatcher("myIndex.jsp").forward(request, response);
    }
}
