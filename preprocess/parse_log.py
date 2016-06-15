import pickle
import os.path

url2name = {}
url2encoding = {}
cnt = 0
rootdir = 'H:/heritrix-3.2.0/bin/jobs/tsinghua2/20160513152337/mirror/'


def function(f):
    global cnt
    for line in f:
        l = line.strip().split()
        status = l[1]
        if l[1].strip() == '200':
            url = l[3].strip()
            if l[-1].strip().startswith('usingCharsetInHTML:UTF-8'):
                encoding = 'utf-8'
            else:
                encoding = 'gbk'
            url = url.split('//')
            filename = url[1]
            if '.cn:' in filename:
                # port
                a = filename.split('.cn:')
                pos = a[1].find('/')
                filename = a[0] + '.cn' + a[1][pos:]
            if '?' in filename:
                a = filename.split('?')
                pos1 = a[0].rfind('.')
                pos2 = a[0].rfind('/')
                if pos1 > pos2:
                    filename = a[0][:pos1] + a[1] + a[0][pos1:]
                else:
                    filename = a[0] + a[1]
            if filename.endswith('/'):
                filename += 'index'

            if os.path.isfile(rootdir + filename):
                url2name[url[1]] = filename
                url2encoding[url[1]] = encoding
                cnt += 1
                if cnt % 10000 == 0:
                    print(cnt)

with open('H:\\heritrix-3.2.0\\bin\\jobs\\tsinghua2\\crawl.log.cp00001-20160513182126', 'r') as f:
    function(f)

with open('H:\\heritrix-3.2.0\\bin\\jobs\\tsinghua2\\crawl.log.cp00002-20160514051953', 'r') as f:
    function(f)

print(cnt)
pickle.dump(url2name, open('url2name.dmp', 'wb'))
pickle.dump(url2encoding, open('url2encoding.dmp', 'wb'))
