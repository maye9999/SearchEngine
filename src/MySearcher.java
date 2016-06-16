import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.lionsoul.jcseg.analyzer.v5x.JcsegAnalyzer5X;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by MaYe on 2016/6/14.
 */
public class MySearcher {
    private DirectoryReader reader;
    private IndexSearcher searcher;
    private Analyzer analyzer;
    public HashMap<Integer, Double> id2pageRank;

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

    public Query buildQuery(String queryString) {
//        QueryBuilder builder = new QueryBuilder(analyzer);
//        Query query4 = builder.createMinShouldMatchQuery("contentField", queryString, 1f);
//        Query query1 = builder.createMinShouldMatchQuery("titleField", queryString, 1f);
//        Query query2 = builder.createMinShouldMatchQuery("h1Field", queryString, 1f);
//        Query query3 = builder.createMinShouldMatchQuery("h2Field", queryString, 1f);
//        Query q1 = new BoostQuery(query1, 6f);
//        Query q2 = new BoostQuery(query2, 3f);
//        Query q3 = new BoostQuery(query3, 1.5f);
//        Query q4 = new BoostQuery(query4, 1f);
//        Query q = new BooleanQuery.Builder().add(q1, BooleanClause.Occur.SHOULD).add(q2, BooleanClause.Occur.SHOULD)
//                .add(q3, BooleanClause.Occur.SHOULD).add(q4, BooleanClause.Occur.SHOULD).build();
//        Query q = new WildcardQuery(new Term("urlField", "*c?.tsinghua.edu.cn*"));
        Query q = new FuzzyQuery(new Term("titleField", "机算机系"));
        return q;
    }

    public ScoreDoc[] search(String queryString, int maxNum) {
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
            return searcher.search(query, maxNum).scoreDocs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        MySearcher mySearcher = new MySearcher("index-new-analyzer");
        mySearcher.getId2PageRank();
//        System.out.println("avgLength = " + mySearcher.getAvgLength());

        ScoreDoc[] hits = mySearcher.search("computer science", 20);
        for (ScoreDoc hit : hits) {
            Document doc = mySearcher.getDoc(hit.doc);
            int id = Integer.parseInt(doc.get("idField"));
//            System.out.println(id);
            System.out.println("doc id = " + hit.doc + "\t\ttitle = " + doc.get("titleField") + "\t\tscore = " + hit.score + "\t\tpr = " + mySearcher.id2pageRank.get(id) + "\t\turl = " + doc.get("urlField"));
        }
    }
}
