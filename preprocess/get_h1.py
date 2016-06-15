import pickle
import traceback

from bs4 import BeautifulSoup, SoupStrainer

rootdir = 'H:/heritrix-3.2.0/bin/jobs/tsinghua2/20160513152337/mirror/'
url2name = pickle.load(open('url2name.dmp', 'rb'))
url2encoding = pickle.load(open('url2encoding.dmp', 'rb'))

only_h1_tags = SoupStrainer("h1")
name2id = pickle.load(open('name2id.dmp', 'rb'))

with open('h1.txt', 'w', encoding='utf-8') as fout:
    for url, name in url2name.items():
        try:
            id = name2id[name]
        except:
            continue
        encoding = url2encoding[url]
        filename = rootdir + name
        if filename.endswith('.pdf') or filename.endswith('.doc') or filename.endswith('.docx') or filename.endswith('.DOC') or filename.endswith('.DOCX'):
            continue
        if encoding == 'utf-8':
            f = open(filename, 'r', encoding='utf-8')
            try:
                soup = BeautifulSoup(f.read(), "lxml", parse_only=only_h1_tags)
            except :
                continue
        else:
            f = open(filename, 'r')
            try:
                soup = BeautifulSoup(f.read(), "lxml", parse_only=only_h1_tags)
            except:
                f.close()
                try:
                    f = open(filename, 'r', encoding='utf-8')
                    soup = BeautifulSoup(f.read(), "lxml", parse_only=only_h1_tags)
                except:
                    continue
        try:
            h = soup.h1
            if h is None:
                continue
            txt = h.string
            if txt is not None:
                txt = txt.strip().replace("\n", " ")
                # print(txt)
                fout.write(str(id) + " " + txt + '\n')
                # fout.flush()
        except Exception as e:
            print(e)
            continue
