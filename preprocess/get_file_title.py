import os
import pickle

from pyPdf import PdfFileReader

rootdir = 'H:/heritrix-3.2.0/bin/jobs/tsinghua2/20160513152337/mirror/'
url2name = pickle.load(open('url2name.dmp', 'rb'))
name2id = pickle.load(open('name2id.dmp', 'rb'))


with open("file_title.txt", "w", encoding="UTF-8") as fout:
    for url, name in url2name.items():
        try:
            id = name2id[name]
        except:
            continue
        filename = rootdir + name
        if filename.endswith(".pdf") or filename.endswith(".PDF"):
            try:
                pdf_toread = PdfFileReader(open(filename, "rb"))
                info = pdf_toread.getDocumentInfo()
                if info is None:
                    continue
            except:
                continue
            title = info.title
            if title is None or title == "" or title.isspace():
                continue
            fout.write(str(id) + " " + title.strip().replace("\n", " ") + "\n")
