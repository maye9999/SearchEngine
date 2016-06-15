#include <algorithm>
#include <cstdio>
#include <map>
#include <string>
#include <vector>
#include <cmath>
#include <assert.h>

using std::max;
using std::make_pair;
using std::pair;
using std::sort;
using std::string;
using std::vector;

const int kMAXN = 300000;
const int kITER = 30;
const double kA = 0.15;
typedef vector<vector<int>> Graph;
typedef pair<int, double> Rank;

Graph graph;

bool cmp(const Rank &a, const Rank &b) {
    return a.second > b.second;
}

int main(int argc, char const *argv[]) {
    FILE *wiki_graph = fopen("graph.txt", "r");
    // Init
    printf(" - Initialize\n");
    vector<int> ind = vector<int>(kMAXN, 0);
    graph = Graph(kMAXN);

    // Add Edges
    int magic = 10000;
    int n = 0;
    printf(" - Loading dataset\n");
    for (int i, cnt = 0; fscanf(wiki_graph, "%d", &i) != EOF; ++cnt) {
        if (cnt % magic == 0)
            printf("    * %d: %d\n", cnt, i);
        n = max(i, n);
        int m;
        fscanf(wiki_graph, "%d", &m);
        for (int j; m--; ) {
            fscanf(wiki_graph, "%d", &j);
            graph[i].push_back(j);
            n = max(i, n);
            ind[j] += 1;
        }
        fscanf(wiki_graph, "\n");
    }
    ++n;
    printf(" > n = %d\n", n);
    fclose(wiki_graph);
    
    

    // Page Rank
    printf(" - Page Rank\n");
    vector<double> PR = vector<double>(n, 1.0 / n);
    vector<double> I = vector<double>(n);
    for (int iter = 0; iter < kITER; ++iter) {
        double S = 0;
        double delta = 0;
        for (int i = 0; i < n; ++i) {
            I[i] = 0;
            if (graph[i].empty())
                S += PR[i] / n;
        }

        for (int i = 0; i < n; ++i)
            for (int j : graph[i])
                I[j] += PR[i] / graph[i].size();

        for (int i = 0; i < n; ++i) {
            double pr = (1 - kA) * (I[i] + S) + kA / n;
            assert(pr < 1);
            delta += (PR[i] - pr) * (PR[i] - pr);
            PR[i] = pr;
        }
        printf("     * iter %d, MSR %f\n", iter, sqrt(delta));
    }

    vector<pair<int, double>> rank;
    for (int i = 0; i < n; ++i)
        rank.push_back(make_pair(i, PR[i]));
    sort(rank.begin(), rank.end(), cmp);
    for (int i = 0; i < 10; ++i) {
        printf("%d : %f, in degree: %d\n", rank[i].first, rank[i].second, ind[rank[i].first]);
    }
    
    printf(" - saving result\n");
    printf("     * page rank\n");
    FILE *fp = fopen("page_rank.txt", "w");
    for(int i = 0; i < n; ++i) {
        fprintf(fp, "%d %f\n", i, PR[i]);
    }
    fclose(fp);
    
    printf("     * in degree\n");
    FILE *findegree = fopen("in_degree.csv", "w");
    fprintf(findegree, "x,y\n");
    vector<int> cnt = vector<int>(n, 0);
    for (int i = 0; i < n; ++i)
        cnt[ind[i]] += 1;
    for (int i = 0; i < n; ++i)
        if (cnt[i] > 0)
            fprintf(findegree, "%d,%d\n", cnt[i], i);
    fclose(findegree);
    printf("     * out degree\n");
    FILE *foutdegree = fopen("out_degree.csv", "w");
    fprintf(foutdegree, "x,y\n");
    cnt = vector<int>(n, 0);
    for (int i = 0; i < n; ++i)
        cnt[graph[i].size()] += 1;
    for (int i = 0; i < n; ++i)
        if (cnt[i] > 0)
            fprintf(foutdegree, "%d,%d\n", cnt[i], i);
    fclose(foutdegree);
    return 0;
}
