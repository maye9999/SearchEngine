import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import com.ng.util.analyzer.IKAnalyzer5x;
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

    public ScoreDoc[] search(String queryString, int maxNum) {
        HashMap<String,Float> boosts = new HashMap<String,Float>();
        boosts.put("contentField", 1f);
        boosts.put("titleField", 2f);
        boosts.put("h1Field", 4f);
        boosts.put("h2Field", 2f);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"contentField", "titleField", "h1Field", "h2Field"}, analyzer, boosts);
        try {
            Query query = parser.parse(queryString);
//            BooleanQuery booleanQuery = new BooleanQuery();
//            Query query1 = new TermQuery(new Term("typeField", "DOC"));
//            booleanQuery.add(query1, BooleanClause.Occur.MUST);
//            booleanQuery.add(query, BooleanClause.Occur.SHOULD);
//            Query query1 = new TermQuery(new Term("urlField", "www.tsinghua.edu.cn/publish/newthu/index.html"));
//            System.out.println(getDoc(947).get("urlField"));
//            System.out.println(searcher.explain(query, 2967).toHtml());
            return searcher.search(query, maxNum).scoreDocs;
        } catch (IOException | ParseException e) {
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

        ScoreDoc[] hits = mySearcher.search("清华大学", 100);
        for (ScoreDoc hit : hits) {
            Document doc = mySearcher.getDoc(hit.doc);
            int id = Integer.parseInt(doc.get("idField"));
//            System.out.println(id);
            System.out.println("doc id = " + hit.doc + "\t\ttitle = " + doc.get("titleField") + "\t\tscore = " + hit.score + "\t\tpr = " + mySearcher.id2pageRank.get(id) + "\t\turl = " + doc.get("urlField"));
        }
    }
}
