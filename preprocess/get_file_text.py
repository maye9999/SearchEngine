import pickle
import docx
import traceback

import subprocess
from bs4 import BeautifulSoup

rootdir = 'H:/heritrix-3.2.0/bin/jobs/tsinghua2/20160513152337/mirror/'
# outdir = 'E:\\MaYe\\THU\\Study\\Junior_2\\Search_Engine\\preprocess\\all_text\\pdfs\\'
# outdir = 'E:\\MaYe\\THU\\Study\\Junior_2\\Search_Engine\\preprocess\\all_text\\docxs\\'
outdir = 'E:\\MaYe\\THU\\Study\\Junior_2\\Search_Engine\\preprocess\\all_text\\docs\\'
url2name = pickle.load(open('url2name.dmp', 'rb'))
url2encoding = pickle.load(open('url2encoding.dmp', 'rb'))
name2id = pickle.load(open('name2id.dmp', 'rb'))

count = 0
for url, name in url2name.items():
    try:
        id = name2id[name]
    except:
        continue
    filename = rootdir + name

    if filename.endswith(".pdf") or filename.endswith(".PDF"):
        # pout.write(str(id) + ": " + filename + "\n")
        pass
        # subprocess.call("D:\\xpdfbin-win-3.04\\bin32\\pdftotext.exe -cfg D:\\xpdfbin-win-3.04\\.xpdfrc -nopgbrk " + filename + " " + outdir + str(id) + ".txt", shell=True)
    elif filename.endswith('.docx') or filename.endswith('.DOCX'):
        # dout.write(str(id) + ": " + filename + "\n")
        # try:
        #     document = docx.opendocx(filename)
        #     ll = docx.getdocumenttext(document)
        # except Exception as e:
        #     continue
        # f = open(outdir + str(id) + ".txt", "w", encoding="utf-8")
        # for l in ll:
        #     f.write(l + '\n')
        # f.close()
        pass
    elif filename.endswith(".doc") or filename.endswith('.DOC'):
        s = "C:\\antiword\\antiword.exe -m UTF-8 \"" + filename + "\" > " + outdir + str(id) + ".txt"
        subprocess.call(s, shell=True)
        pass
