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
        int mode = 0;
        if (request.getParameter("mode") != null) {
            mode = Integer.parseInt(request.getParameter("mode"));
        }
        ScoreDoc[] hits = searcher.search(request.getParameter("query"), 10);
        Document[] docs = new Document[hits.length];
        System.out.println("hits number = " + hits.length);
        for (int i = 0; i < hits.length; ++i) {
            docs[i] = searcher.getDoc(hits[i].doc);
            System.out.println("title : " + docs[i].get("titleField"));
        }
        request.setAttribute("mode", mode);
        request.setAttribute("currentQuery", request.getParameter("query"));
        request.setAttribute("docs", docs);
        request.getRequestDispatcher("myResult.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int mode = 0;
        if (request.getParameter("mode") != null) {
            mode = Integer.parseInt(request.getParameter("mode"));
        }
        String stringMust = new String(request.getParameter("all-keywords").getBytes("UTF-8"));
        String stringShould = new String(request.getParameter("any-keywords").getBytes("UTF-8"));
        String stringNo = new String(request.getParameter("no-keywords").getBytes("UTF-8"));
        String stringSite = request.getParameter("in-site");
        String fileType = request.getParameter("doc-type");
        String searchField = request.getParameter("keyword-pos");

        System.out.println("stringMust : " + stringMust);
        System.out.println("fileType : " + fileType);
        ScoreDoc[] hits = searcher.searchComplex(stringMust, stringShould, stringNo, stringSite, fileType, searchField, 10);

        Document[] docs = new Document[hits.length];
        System.out.println("hits number = " + hits.length);
        for (int i = 0; i < hits.length; ++i) {
            docs[i] = searcher.getDoc(hits[i].doc);
        }
        request.setAttribute("mode", mode);
        request.setAttribute("currentQuery", "");
        request.setAttribute("docs", docs);
        request.getRequestDispatcher("myResult.jsp").forward(request, response);
    }
}
