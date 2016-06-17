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
        MySearcher.SearchResult result = searcher.search(request.getParameter("query"), 10);
        ScoreDoc[] hits = result.scoreDocs;
        String[] titles = new String[hits.length];
        String[] contents = new String[hits.length];
        String[] urls = new String[hits.length];
        System.out.println("hits number = " + hits.length);
        for (int i = 0; i < hits.length; ++i) {
            titles[i] = searcher.getHightlight(result.query, hits[i], "titleField");
            contents[i] = searcher.getHightlight(result.query, hits[i], "contentField");
            urls[i] = searcher.getHightlight(result.query, hits[i], "urlField");
        }
        request.setAttribute("mode", mode);
        request.setAttribute("currentQuery", request.getParameter("query"));
        request.setAttribute("titles", titles);
        request.setAttribute("contents", contents);
        request.setAttribute("urls", urls);
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
        MySearcher.SearchResult result = searcher.searchComplex(stringMust, stringShould, stringNo, stringSite, fileType, searchField, 10);
        ScoreDoc[] hits = result.scoreDocs;

        Document[] docs = new Document[hits.length];
        System.out.println("hits number = " + hits.length);
        for (int i = 0; i < hits.length; ++i) {
            docs[i] = searcher.getDoc(hits[i].doc);
        }
        request.setAttribute("mode", mode);
        request.setAttribute("currentQuery", "");
        request.setAttribute("docs", docs);
        request.setAttribute("searcher", searcher);
        request.setAttribute("query", result.query);
        request.getRequestDispatcher("myResult.jsp").forward(request, response);
    }
}
