import pickle
import traceback

from bs4 import BeautifulSoup, SoupStrainer

rootdir = 'H:/heritrix-3.2.0/bin/jobs/tsinghua2/20160513152337/mirror/'
url2name = pickle.load(open('url2name.dmp', 'rb'))
url2encoding = pickle.load(open('url2encoding.dmp', 'rb'))


name2id = pickle.load(open('name2id.dmp', 'rb'))

with open('h1.txt', 'w', encoding='utf-8') as fout1, open('h2.txt', 'w', encoding='utf-8') as fout2,\
        open('h3.txt', 'w', encoding='utf-8') as fout3, open('h4.txt', 'w', encoding='utf-8') as fout4,\
        open('h5.txt', 'w', encoding='utf-8') as fout5, open('h6.txt', 'w', encoding='utf-8') as fout6:
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
            all_txt = ""
            for h1 in soup.find_all('h1'):
                if h1 is None:
                    continue
                txt = h1.get_text(" ", strip=True).replace("\n", " ")
                all_txt += txt + " "
            if all_txt == "" or all_txt.isspace() or len(all_txt) > 100:
                continue
            fout1.write(str(id) + " " + all_txt + '\n')
            all_txt = ""
            for h2 in soup.find_all('h2'):
                if h2 is None:
                    continue
                txt = h2.get_text(" ", strip=True).replace("\n", " ")
                all_txt += txt + " "
            if all_txt == "" or all_txt.isspace() or len(all_txt) > 300:
                continue
            fout2.write(str(id) + " " + all_txt + '\n')
            all_txt = ""
            for h3 in soup.find_all('h3'):
                if h3 is None:
                    continue
                txt = h3.get_text(" ", strip=True).replace("\n", " ")
                all_txt += txt + " "
            if all_txt == "" or all_txt.isspace() or len(all_txt) > 500:
                continue
            fout3.write(str(id) + " " + all_txt + '\n')

            all_txt = ""
            for h4 in soup.find_all('h4'):
                if h4 is None:
                    continue
                txt = h4.get_text(" ", strip=True).replace("\n", " ")
                all_txt += txt + " "
            if all_txt == "" or all_txt.isspace() or len(all_txt) > 700:
                continue
            fout4.write(str(id) + " " + all_txt + '\n')

            all_txt = ""
            for h5 in soup.find_all('h5'):
                if h5 is None:
                    continue
                txt = h5.get_text(" ", strip=True).replace("\n", " ")
                all_txt += txt + " "
            if all_txt == "" or all_txt.isspace() or len(all_txt) > 700:
                continue
            fout5.write(str(id) + " " + all_txt + '\n')

            all_txt = ""
            for h6 in soup.find_all('h6'):
                if h6 is None:
                    continue
                txt = h6.get_text(" ", strip=True).replace("\n", " ")
                all_txt += txt + " "
            if all_txt == "" or all_txt.isspace() or len(all_txt) > 700:
                continue
            fout6.write(str(id) + " " + all_txt + '\n')
        except Exception as e:
            print(e)
            continue
