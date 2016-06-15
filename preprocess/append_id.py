from bs4 import BeautifulSoup
import pickle
import chardet
import os
from bs4 import SoupStrainer
import urllib.parse

only_a_tags = SoupStrainer("a")
rootdir = 'H:/heritrix-3.2.0/bin/jobs/tsinghua2/20160513152337/mirror/'

url2name = pickle.load(open('url2name.dmp', 'rb'))
url2encoding = pickle.load(open('url2encoding.dmp', 'rb'))


graph = pickle.load(open('graph.dmp', 'rb'))
name2id = pickle.load(open('name2id.dmp', 'rb'))

count = len(name2id)
graph_count = len(graph)


def add_to_dict(name):
    if name in name2id:
        return name2id[name]
    global count
    name2id[name] = count
    count += 1
    if count % 1000 == 0:
        print(count)
    return count - 1

for url, name in url2name.items():
    if name in name2id:
        continue
    encoding = url2encoding[url]
    filename = rootdir + name
    if encoding == 'utf-8':
        f = open(filename, 'r', encoding='utf-8')
        try:
            soup = BeautifulSoup(f.read(), "lxml", parse_only=only_a_tags)
        except :
            continue
    else:
        f = open(filename, 'r')
        try:
            soup = BeautifulSoup(f.read(), "lxml", parse_only=only_a_tags)
        except :
            f.close()
            try:
                f = open(filename, 'r', encoding='utf-8')
                soup = BeautifulSoup(f.read(), "lxml", parse_only=only_a_tags)
            except:
                continue
    edge_i = add_to_dict(name)
    edge_j = set()

    try:
        for h in soup.find_all('a'):
            hh = h.get('href')
            if hh is None:
                continue
            if hh == "" or hh == '#':
                continue
            new_url = urllib.parse.urljoin('http://' + url, hh)
            new_url = new_url.lstrip("http://")
            if new_url == url:
                continue
            if new_url in url2name:
                new_name = url2name[new_url]
                edge_j.add(add_to_dict(new_name))

        if len(edge_j) != 0:
            graph[edge_i] = list(edge_j)
            graph_count += 1
    except:
        continue

pickle.dump(graph, open('graph_new.dmp', 'wb'))
pickle.dump(name2id, open('name2id_new.dmp', 'wb'))
print(count)
print(graph_count)
        #
        # for h in soup.find_all('a'):
        #     hh = h.get('href')
        #     if hh is None:
        #         continue
        #     if hh == "":
        #         continue





# with open("H:/heritrix-3.2.0/bin/jobs/tsinghua2/20160513152337/mirror/auth.cic.tsinghua.edu.cn/hb/index.html") as f:
#     soup = BeautifulSoup(f.read())
#     for h in soup.find_all('a'):
#         print(h.get('href'))
    # print(soup.prettify(encoding='utf-8'))
