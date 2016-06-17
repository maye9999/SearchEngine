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
        int n = 10;
        if (request.getParameter("mode") != null) {
            mode = Integer.parseInt(request.getParameter("mode"));
        }
        MySearcher.SearchResult result;
        if (mode == 0) {
            result = searcher.search(request.getParameter("query"), n);
        } else if (mode == 1) {
            result = searcher.searchComplex(request.getParameter("query"), "", "", "", "HTML", "", n);
        } else if (mode == 2) {
            result = searcher.searchComplex(request.getParameter("query"), "", "", "", "NOHTML", "", n);
        } else if (mode == 3) {
            String query = new String(request.getParameter("query").getBytes("UTF-8"));
            result = searcher.searchWildCardOrFuzzy(query, false, n);
        } else if (mode == 4) {
            String query = new String(request.getParameter("query").getBytes("UTF-8"));
            result = searcher.searchWildCardOrFuzzy(query, true, n);
        } else {
            String stringMust = request.getParameter("all-keywords");
            String stringShould = request.getParameter("any-keywords");
            String stringNo = request.getParameter("no-keywords");
            String stringSite = request.getParameter("in-site");
            String fileType = request.getParameter("doc-type");
            String searchField = request.getParameter("keyword-pos");
            result = searcher.searchComplex(stringMust, stringShould, stringNo, stringSite, fileType, searchField, n);
        }

        ScoreDoc[] hits = result.scoreDocs;
        String[] titles = new String[hits.length];
        String[] contents = new String[hits.length];
        String[] urls = new String[hits.length];
        String[] types = new String[hits.length];

        for (int i = 0; i < hits.length; ++i) {
            contents[i] = searcher.getHightlight(result.query, hits[i], "contentField");
            Document doc = searcher.getDoc(hits[i].doc);
            titles[i] = doc.get("titleField");
            types[i] = doc.get("typeField");
            urls[i] = doc.get("urlField");
        }

        request.setAttribute("mode", mode);
        request.setAttribute("currentQuery", request.getParameter("query"));
        request.setAttribute("titles", titles);
        request.setAttribute("contents", contents);
        request.setAttribute("urls", urls);
        request.setAttribute("types", types);
        request.getRequestDispatcher("myResult.jsp").forward(request, response);
    }
}
