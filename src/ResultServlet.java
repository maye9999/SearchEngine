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
    int maxNum = 1000;
    int perPage = 10;

//    final String indexPosition = "/Users/lzhengning/Desktop/index-new-analyzer";
    final String indexPosition = "E:\\MaYe\\THU\\Study\\Junior_2\\Search_Engine\\project\\SearchEngine\\index-new";

    @Override
    public void init() throws ServletException {
        super.init();
        searcher = new MySearcher(indexPosition);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 0;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (Exception e) {
            page = 0;
        }
        int mode = 0;
        if (request.getParameter("mode") != null) {
            mode = Integer.parseInt(request.getParameter("mode"));
        }

        MySearcher.SearchResult result;
        if (mode == 0) {
            result = searcher.search(request.getParameter("query"), maxNum);
        } else if (mode == 1) {
            result = searcher.searchComplex(request.getParameter("query"), "", "", "", "HTML", "", maxNum);
        } else if (mode == 2) {
            result = searcher.searchComplex(request.getParameter("query"), "", "", "", "NOHTML", "", maxNum);
        } else if (mode == 3) {
            String query = new String(request.getParameter("query").getBytes("UTF-8"));
            result = searcher.searchWildCardOrFuzzy(query, false, maxNum);
        } else if (mode == 4) {
            String query = new String(request.getParameter("query").getBytes("UTF-8"));
            result = searcher.searchWildCardOrFuzzy(query, true, maxNum);
        } else {
            String stringMust = request.getParameter("all-keywords");
            String stringShould = request.getParameter("any-keywords");
            String stringNo = request.getParameter("no-keywords");
            String stringSite = request.getParameter("in-site");
            String fileType = request.getParameter("doc-type");
            String searchField = request.getParameter("keyword-pos");
            result = searcher.searchComplex(stringMust, stringShould, stringNo, stringSite, fileType, searchField, maxNum);
        }

        ScoreDoc[] hits = result.scoreDocs;
        int n = hits.length - perPage * page;
        if (n > perPage) {
            n = perPage;
        }
        String[] titles = new String[n];
        String[] contents = new String[n];
        String[] urls = new String[n];
        String[] types = new String[n];

        for (int i = 0; i < n; ++i) {
            contents[i] = searcher.getHightlight(result.query, hits[i + perPage * page], "contentField");
            Document doc = searcher.getDoc(hits[i + perPage * page].doc);
            titles[i] = searcher.getHightlight(result.query, hits[i + perPage * page], "titleField");
            types[i] = doc.get("typeField");
            urls[i] = doc.get("urlField");
        }

        request.setAttribute("page", page);
        request.setAttribute("totalPage", (hits.length-1) / perPage + 1);
        request.setAttribute("mode", mode);
        request.setAttribute("currentQuery", request.getParameter("query"));
        request.setAttribute("titles", titles);
        request.setAttribute("contents", contents);
        request.setAttribute("urls", urls);
        request.setAttribute("types", types);
        request.getRequestDispatcher("myResult.jsp").forward(request, response);
    }
}
