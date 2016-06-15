import pickle
import traceback

from bs4 import BeautifulSoup

rootdir = 'H:/heritrix-3.2.0/bin/jobs/tsinghua2/20160513152337/mirror/'
url2name = pickle.load(open('url2name.dmp', 'rb'))
url2encoding = pickle.load(open('url2encoding.dmp', 'rb'))
name2id = pickle.load(open('name2id.dmp', 'rb'))


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
            soup = BeautifulSoup(f.read(), "lxml")
        except :
            continue
    else:
        f = open(filename, 'r')
        try:
            soup = BeautifulSoup(f.read(), "lxml")
        except:
            f.close()
            try:
                f = open(filename, 'r', encoding='utf-8')
                soup = BeautifulSoup(f.read(), "lxml")
            except:
                continue
    try:
        # kill all script and style elements
        for script in soup(["script", "style"]):
            script.extract()    # rip it out
        txt = soup.get_text(strip=True, separator=' ')
        with open('all_text/' + str(id) + '.txt', 'w', encoding='utf-8') as fout:
            fout.write(txt)
    except Exception as e:
        traceback.print_exc()
        print(filename)

