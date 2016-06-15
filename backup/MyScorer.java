import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by MaYe on 2016/6/14.
 */
public class MyScorer extends Scorer{
    private HashMap<Integer, Float> id2pageRank;
    private IndexReader indexReader;
    private final TermDocs termDocs;
    private final byte[] norms;
    private float weightValue;      // IDF value
    private int doc = -1;
    private int freq;

    private final int[] docs = new int[32];         // buffered doc numbers
    private final int[] freqs = new int[32];        // buffered term freqs
    private int pointer;
    private int pointerMax;

    private static final int SCORE_CACHE_SIZE = 32;
    private final float[] scoreCache = new float[SCORE_CACHE_SIZE];

    static private float avgLength = 3099.3403f;
    static private float K1 = 2.0f;
    static private float b = 0.75f;

    MyScorer(Weight weight, TermDocs td, Similarity similarity, byte[] norms, IndexReader ir, HashMap<Integer, Float> map) {
        super(similarity, weight);
        this.indexReader = ir;
        this.id2pageRank = map;
        this.termDocs = td;
        this.norms = norms;
        this.weightValue = weight.getValue();

        for (int i = 0; i < SCORE_CACHE_SIZE; i++)
            scoreCache[i] = getSimilarity().tf(i) * weightValue;
    }

    public static void setBM25Parameters(float K, float B, float avg) {
        avgLength = avg;
        K1 = K;
        b = B;
    }

    @Override
    public void score(Collector c) throws IOException {
        score(c, Integer.MAX_VALUE, nextDoc());
    }

    @Override
    protected boolean score(Collector c, int end, int firstDocID)
            throws IOException {
        c.setScorer(this);
        while (doc < end) { // for docs in window
            c.collect(doc); // collect score

            if (++pointer >= pointerMax) {
                pointerMax = termDocs.read(docs, freqs); // refill buffers
                if (pointerMax != 0) {
                    pointer = 0;
                } else {
                    termDocs.close(); // close stream
                    doc = Integer.MAX_VALUE; // set to sentinel value
                    return false;
                }
            }
            doc = docs[pointer];
            freq = freqs[pointer];
        }
        return true;
    }

    @Override
    public int docID() {
        return doc;
    }

    @Override
    public float freq() {
        return freq;
    }

    @Override
    public int nextDoc() throws IOException {
        pointer++;
        if (pointer >= pointerMax) {
            pointerMax = termDocs.read(docs, freqs); // refill buffer
            if (pointerMax != 0) {
                pointer = 0;
            } else {
                termDocs.close(); // close stream
                return doc = NO_MORE_DOCS;
            }
        }
        doc = docs[pointer];
        freq = freqs[pointer];
        return doc;
    }

    @Override
    public float score() {
        assert doc != -1;
//        float n = Similarity.decodeNorm(norms[doc]);
//        float l = 1.0f / n / n;
        try {
            Document d = indexReader.document(doc);
            float l = d.get("contentField").length();
            int id = Integer.parseInt(d.get("idField"));
            float pr = id2pageRank.get(id);
            float s = pr * weightValue * ((K1+1f) * freq) / (K1 * (1.0f - b + b * (l / avgLength)) + freq);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
//        return weightValue * freq;
    }

    @Override
    public int advance(int target) throws IOException {
        // first scan in cache
        for (pointer++; pointer < pointerMax; pointer++) {
            if (docs[pointer] >= target) {
                freq = freqs[pointer];
                return doc = docs[pointer];
            }
        }

        // not found in cache, seek underlying stream
        boolean result = termDocs.skipTo(target);
        if (result) {
            pointerMax = 1;
            pointer = 0;
            docs[pointer] = doc = termDocs.doc();
            freqs[pointer] = freq = termDocs.freq();
        } else {
            doc = NO_MORE_DOCS;
        }
        return doc;
    }

    @Override
    public String toString() {
        return "scorer(" + weight + ")";
    }
}
