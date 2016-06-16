import com.ng.util.analyzer.IKAnalyzer5x;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.lionsoul.jcseg.analyzer.v5x.JcsegAnalyzer5X;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by MaYe on 2016/6/13.
 */

public class MyIndexer {
    private Analyzer analyzer;
    private IndexWriter indexWriter;
    private HashMap<Integer, String> id2url;
    private HashMap<Integer, Double> id2pageRank;
    private HashMap<Integer, String> id2title;
    private HashMap<Integer, String> id2h1;
    private HashMap<Integer, String> id2h2;

    private final String[] blacklist = {"erelaw.tsinghua.edu.cn/NewsPL.asp?",
            "qh.daf.tsinghua.edu.cn/", "93001.tsinghua.edu.cn:8081/", "syskfgl.cic.tsinghua.edu.cn/"};


    public MyIndexer(String indexDir){
        id2url = new HashMap<>();
        id2pageRank = new HashMap<>();
        id2title = new HashMap<>();
        id2h1 = new HashMap<>();
        id2h2 = new HashMap<>();
//        analyzer = new IKAnalyzer5x();
        analyzer = new JcsegAnalyzer5X(JcsegTaskConfig.COMPLEX_MODE);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setSimilarity(new MySimilarity());
        try {
            indexWriter = new IndexWriter(FSDirectory.open(Paths.get(indexDir)), indexWriterConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getTitle(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String title = "";
            int id = -1;
            while ((line = br.readLine()) != null) {
                try {
                    id = Integer.parseInt(line.substring(0, line.indexOf(' ')));
                    title = line.substring(line.indexOf(' ') + 1);
                    id2title.put(id, title);
                } catch (Exception e) {
//                    System.out.println(id);
                    title += line;
                    id2title.put(id, title);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getId2Url(File file) {
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextInt()) {
                int id = scanner.nextInt();
                String url = scanner.next();
                id2url.put(id, url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getId2Headers(File h1File, File h2File) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(h1File));
            String line;
            int id = -1;
            while ((line = br.readLine()) != null) try {
                id = Integer.parseInt(line.substring(0, line.indexOf(' ')));
                String title = line.substring(line.indexOf(' ') + 1);
                id2h1.put(id, title);
            } catch (Exception e) {
                System.out.println(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(h2File));
            String line;
            int id = -1;
            while ((line = br.readLine()) != null) {
                try {
                    id = Integer.parseInt(line.substring(0, line.indexOf(' ')));
                    String title = line.substring(line.indexOf(' ') + 1);
                    id2h2.put(id, title);
                } catch (Exception e) {
                    System.out.println("h2 " + id);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getId2PageRank(File file) {
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextInt()) {
                int id = scanner.nextInt();
                double pr = scanner.nextDouble();
                id2pageRank.put(id, pr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Document createDocument(File file) {
        // For HTML files
        Document document = new Document();
        try {
            int id = Integer.parseInt(file.getName().split("\\.")[0]);
            String text = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            String title = id2title.get(id);
            if(title == null) {
                return null;
            }
            String url = id2url.get(id);
            String h1 = id2h1.get(id), h2 = id2h2.get(id);
            if(h1 == null) {
                h1 = "";
            }
            if(h2 == null) {
                h2 = "";
            }

            double pr = id2pageRank.get(id);
            for(String s : blacklist) {
                if(url.startsWith(s)) {
                    pr = 1E-6;
                }
            }
            Field idField = new StringField("idField", String.valueOf(id), Field.Store.YES);
            Field titleField = new TextField("titleField", title, Field.Store.YES);
            Field h1Field = new TextField("h1Field", h1, Field.Store.YES);
            Field h2Field = new TextField("h2Field", h2, Field.Store.YES);
            Field urlField = new StringField("urlField", url, Field.Store.YES);
            Field contentField = new TextField("contentField", text, Field.Store.YES);
            Field prField = new FloatDocValuesField("prField", (float)pr);
            Field typeField = new StringField("typeField", "HTML", Field.Store.YES);
            document.add(titleField);
            document.add(h1Field);
            document.add(h2Field);
            document.add(idField);
            document.add(urlField);
            document.add(contentField);
            document.add(prField);
            document.add(typeField);
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Document createDocument(File file, String type) {
        // For doc docx pdf Files
        Document document = new Document();
        try {
            int id = Integer.parseInt(file.getName().split("\\.")[0]);
            String text = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            String url = id2url.get(id);
            String title = id2title.get(id);
            if (title == null) {
                try {
                    title = url.substring(url.lastIndexOf('/')+1);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            double pr = id2pageRank.get(id);
            Field idField = new StringField("idField", String.valueOf(id), Field.Store.YES);
            Field titleField = new TextField("titleField", title, Field.Store.YES);
            Field h1Field = new TextField("h1Field", "", Field.Store.YES);
            Field h2Field = new TextField("h1Field", "", Field.Store.YES);
            Field urlField = new StringField("urlField", url, Field.Store.YES);
            Field contentField = new TextField("contentField", text, Field.Store.YES);
            Field prField = new FloatDocValuesField("prField", (float)pr);
            Field typeField = new StringField("typeField", type, Field.Store.YES);
            document.add(titleField);
            document.add(h1Field);
            document.add(h2Field);
            document.add(idField);
            document.add(urlField);
            document.add(contentField);
            document.add(prField);
            document.add(typeField);
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createIndex(String dataDirPath) {
        int n = id2url.size();
//        int n = 5000;
        // Create HTML files
        System.out.println("Creating HTML indices");
        for(int i = 0; i < n; ++i) {
            File file = new File(dataDirPath + "\\" + String.valueOf(i) + ".txt");
            try {
                if(file.exists() && file.canRead()) {
                    Document document = createDocument(file);
                    if (document != null) {
                        indexWriter.addDocument(document);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            if (i % 1000 == 0) {
                System.out.println(i);
            }
        }
        // PDFs
        System.out.println("Creating PDF indices");
        File pdfs = new File(dataDirPath + "\\" + "pdfs");
        for (File file : pdfs.listFiles()) {
            Document document = createDocument(file, "PDF");
            if (document != null) {
                try {
                    indexWriter.addDocument(document);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Creating DOC indices");
        File docs = new File(dataDirPath + "\\" + "docs");
        for (File file : docs.listFiles()) {
            Document document = createDocument(file, "DOC");
            if (document != null) {
                try {
                    indexWriter.addDocument(document);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        File docxs = new File(dataDirPath + "\\" + "docxs");
        for (File file : docxs.listFiles()) {
            Document document = createDocument(file, "DOCX");
            if (document != null) {
                try {
                    indexWriter.addDocument(document);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MyIndexer myIndexer = new MyIndexer("index-new-analyzer");
        myIndexer.getId2Url(new File("E:\\MaYe\\THU\\Study\\Junior_2\\Search_Engine\\preprocess\\id2url.txt"));
        myIndexer.getId2PageRank(new File("E:\\MaYe\\THU\\Study\\Junior_2\\Search_Engine\\preprocess\\page_rank.txt"));
        myIndexer.getId2Headers(new File("E:\\MaYe\\THU\\Study\\Junior_2\\Search_Engine\\preprocess\\h1.txt"),
                new File("E:\\MaYe\\THU\\Study\\Junior_2\\Search_Engine\\preprocess\\h2.txt"));
        myIndexer.getTitle(new File("E:\\MaYe\\THU\\Study\\Junior_2\\Search_Engine\\preprocess\\titles.txt"));
        myIndexer.createIndex("E:\\MaYe\\THU\\Study\\Junior_2\\Search_Engine\\preprocess\\all_text");
        System.out.println();
    }
}
