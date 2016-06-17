import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.lionsoul.jcseg.analyzer.v5x.JcsegAnalyzer5X;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

/**
 * Created by MaYe on 2016/6/14.
 */
public class MySearcher {
    private DirectoryReader reader;
    private IndexSearcher searcher;
    private Analyzer analyzer;
    public HashMap<Integer, Double> id2pageRank;

    public class SearchResult {
        ScoreDoc[] scoreDocs;
        Query query;

        SearchResult(ScoreDoc[] scoreDocs, Query query) {
            this.scoreDocs = scoreDocs;
            this.query = query;
        }
    }

    public MySearcher(String indexDir) {
        id2pageRank = new HashMap<>();
//        analyzer = new IKAnalyzer5x();
        analyzer = new JcsegAnalyzer5X(JcsegTaskConfig.COMPLEX_MODE);
        try {
            reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
            searcher = new IndexSearcher(reader);
            searcher.setSimilarity(new MySimilarity());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        MyScorer.setBM25Parameters(2.0f, 0.75f, getAvgLength());
    }

    public float getAvgLength() {
        double sum = 0;
        try {
            for(int i = 0; i < reader.numDocs(); ++i) {
                Document document = reader.document(i);
                sum += document.get("contentField").length();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
//        byte[] normArray;
//        try {
//            normArray = reader.norms("contentField");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return 0;
//        }
//        double sum = 0;
//        for(int i = 0; i < reader.numDocs(); ++i) {
//            Document document = reader.document(i);
//            sum += document.get("contentField").length();
//        }
//
//        for(int i = 0; i < normArray.length; ++i) {
//            double l = DefaultSimilarity.decodeNorm(normArray[i]);
//            l = 1.0 / (l * l);
//            sum += l;
//        }
        return (float)sum / reader.numDocs();
    }

    private void getId2PageRank() {
        try {
            Scanner scanner = new Scanner(new File("E:\\MaYe\\THU\\Study\\Junior_2\\Search_Engine\\preprocess\\page_rank.txt"));
            while (scanner.hasNextInt()) {
                int id = scanner.nextInt();
                double pr = scanner.nextDouble();
                id2pageRank.put(id, pr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Query buildQuery(String queryString) {
        return buildQuery(queryString, "", false, false);
    }


    private Query buildQuery(String queryString, String searchField, boolean useWildCard, boolean useFuzzy) {
        // Search Field = "" for all or "title" for title only or "h1" for h1 only
        QueryBuilder builder = new QueryBuilder(analyzer);
        if(Objects.equals(searchField, "title")) {
            return builder.createMinShouldMatchQuery("titleField", queryString, 1f);
        } else if(Objects.equals(searchField, "h1")) {
            return builder.createMinShouldMatchQuery("h1Field", queryString, 1f);
        } else {
            Query query1, query2, query3, query4;
            if(useWildCard) {
                query1 = new WildcardQuery(new Term("titleField", queryString));
                query2 = new WildcardQuery(new Term("h1Field", queryString));
                query3 = new WildcardQuery(new Term("h2Field", queryString));
                query4 = new WildcardQuery(new Term("contentField", queryString));
            } else if(useFuzzy) {
                query1 = new FuzzyQuery(new Term("titleField", queryString));
                query2 = new FuzzyQuery(new Term("h1Field", queryString));
                query3 = new FuzzyQuery(new Term("h2Field", queryString));
                query4 = new FuzzyQuery(new Term("contentField", queryString));
            } else {
                query4 = builder.createMinShouldMatchQuery("contentField", queryString, 1f);
                query1 = builder.createMinShouldMatchQuery("titleField", queryString, 1f);
                query2 = builder.createMinShouldMatchQuery("h1Field", queryString, 1f);
                query3 = builder.createMinShouldMatchQuery("h2Field", queryString, 1f);
            }
            Query q1 = new BoostQuery(query1, 6f);
            Query q2 = new BoostQuery(query2, 3f);
            Query q3 = new BoostQuery(query3, 1.5f);
            Query q4 = new BoostQuery(query4, 1f);
            return new BooleanQuery.Builder().add(q1, BooleanClause.Occur.SHOULD).add(q2, BooleanClause.Occur.SHOULD)
                    .add(q3, BooleanClause.Occur.SHOULD).add(q4, BooleanClause.Occur.SHOULD).build();
        }
    }

    public SearchResult searchWildCardOrFuzzy(String queryString , boolean useWildCard, int maxNum) {
        queryString = queryString.toLowerCase(Locale.ENGLISH);
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        String[] strings = queryString.split(" ");
        for (String string : strings) {
            if(Objects.equals(string, ""))
                continue;
            builder.add(buildQuery(string, "", useWildCard, !useWildCard), BooleanClause.Occur.MUST);
        }
        try {
            Query query = builder.build();
            return new SearchResult(searcher.search(query, maxNum).scoreDocs, query);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public SearchResult searchComplex(String stringMust, String stringShould, String stringNo, String stringSite, String fileType, String searchField, int maxNum) {
        String[] stringsMust = stringMust.split(" ");
        String[] stringsShould = stringShould.split(" ");
        String[] stringsNo = stringNo.split(" ");
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        for(String string : stringsMust) {
            if(Objects.equals(string, ""))
                continue;
            builder.add(buildQuery(string, searchField, false, false), BooleanClause.Occur.MUST);
        }
        for(String string : stringsShould) {
            if(Objects.equals(string, ""))
                continue;
            builder.add(buildQuery(string, searchField, false, false), BooleanClause.Occur.SHOULD);
        }
        for(String string : stringsNo) {
            if(Objects.equals(string, ""))
                continue;
            builder.add(buildQuery(string, searchField, false, false), BooleanClause.Occur.MUST_NOT);
        }
        if (!Objects.equals(stringSite, "")) {
            Query query = new WildcardQuery(new Term("urlField", stringSite+"*"));
            builder.add(query, BooleanClause.Occur.MUST);
        }
        if (!Objects.equals(fileType, "")) {
            assert (Objects.equals(fileType, "PDF") || Objects.equals(fileType, "DOC") || Objects.equals(fileType, "DOCX"));
            Query query = new TermQuery(new Term("typeField", fileType));
            builder.add(query, BooleanClause.Occur.MUST);
        }
        try {
            Query query = builder.build();
            return new SearchResult(searcher.search(query, maxNum).scoreDocs, query);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SearchResult search(String queryString, int maxNum) {
//        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"contentField", "titleField", "h1Field", "h2Field"}, analyzer, boosts);

        try {
            Query query = buildQuery(queryString);
//            BooleanQuery booleanQuery = new BooleanQuery();
//            Query query1 = new TermQuery(new Term("typeField", "DOC"));
//            booleanQuery.add(query1, BooleanClause.Occur.MUST);
//            booleanQuery.add(query, BooleanClause.Occur.SHOULD);
//            Query query1 = new TermQuery(new Term("urlField", "www.tsinghua.edu.cn/publish/newthu/index.html"));
//            System.out.println(getDoc(947).get("urlField"));
//            System.out.println(searcher.explain(query, 30418).toHtml());
            return new SearchResult(searcher.search(query, maxNum).scoreDocs, query);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getHightlight(Query query, ScoreDoc scoreDoc, String field) {
        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<mark>", "</mark>");
        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
        int id = scoreDoc.doc;
        Document doc = getDoc(id);
        String txt = doc.get(field);
        TokenStream tokenStream = null;
        try {
            tokenStream = TokenSources.getAnyTokenStream(reader, id, field, analyzer);
            return highlighter.getBestFragments(tokenStream, txt, 2, "...");
        } catch (IOException | InvalidTokenOffsetsException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Document getDoc(int docID) {
        try {
            return searcher.doc(docID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        MySearcher mySearcher = new MySearcher("index-new");
        mySearcher.getId2PageRank();
//        System.out.println("avgLength = " + mySearcher.getAvgLength());


        SearchResult result = mySearcher.search("清华大学计算机系", 20);

        ScoreDoc[] hits = result.scoreDocs;
        String hightlight = mySearcher.getHightlight(result.query, hits[1], "contentField");
        System.out.println(hightlight);
//        ScoreDoc[] hits = mySearcher.searchComplex("足球", "", "", "", "PDF", "title", 20);
//        ScoreDoc[] hits = mySearcher.searchWildCardOrFuzzy("ts?nghua Unive?sity", true, 20);
//        ScoreDoc[] hits = mySearcher.searchWildCardOrFuzzy("tssnghua Univewsity", false, 20);
        for (ScoreDoc hit : hits) {
            Document doc = mySearcher.getDoc(hit.doc);
            int id = Integer.parseInt(doc.get("idField"));
            System.out.println(id);
            System.out.println("doc id = " + hit.doc + "\t\ttitle = " + doc.get("titleField") + "\t\tscore = " + hit.score + "\t\tpr = " + mySearcher.id2pageRank.get(id) + "\t\turl = " + doc.get("urlField"));
        }
    }
}
