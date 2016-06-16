import org.apache.lucene.search.ScoreDoc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by lzhengning on 6/16/16.
 */
public class ResultServlet extends javax.servlet.http.HttpServlet {
    MySearcher searcher;

    @Override
    public void init() throws ServletException {
        super.init();
        searcher = new MySearcher("index-new-analyzer");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ScoreDoc[] hits = searcher.search(request.getParameter("query"), 10);
        request.setAttribute("currentQuery", request.getParameter("query"));
        request.setAttribute("result", hits);
        request.getRequestDispatcher("myResult.jsp").forward(request, response);
    }
}
