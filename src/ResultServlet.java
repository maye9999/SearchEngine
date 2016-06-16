import org.apache.lucene.document.Document;
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
    final String indexPosition = "/Users/lzhengning/Desktop/index-new-analyzer";

    @Override
    public void init() throws ServletException {
        super.init();
        searcher = new MySearcher(indexPosition);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ScoreDoc[] hits = searcher.search(request.getParameter("query"), 10);
        Document[] docs = new Document[hits.length];
        for (int i = 0; i < hits.length; ++i) {
            docs[i] = searcher.getDoc(hits[i].doc);
        }
        request.setAttribute("currentQuery", request.getParameter("query"));
        request.setAttribute("docs", docs);
        request.getRequestDispatcher("myResult.jsp").forward(request, response);
    }
}
