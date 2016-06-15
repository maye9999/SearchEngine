import pickle

graph = pickle.load(open('graph_new.dmp', 'rb'))
name2id = pickle.load(open('name2id_new.dmp', 'rb'))

with open('graph.txt', 'w') as fout:
    for edgei, edgej in graph.items():
        fout.write('%d %d' % (edgei, len(edgej)))
        for j in edgej:
            fout.write(' %d' % j)
        fout.write('\n')

id2name = []
with open('name2id.txt', 'w') as fout:
    for name, id in name2id.items():
        id2name.append((id, name))
    id2name.sort(key=lambda x: x[0])
    for t in id2name:
        fout.write('%d %s\n' % (t[0], t[1]))

url2name = pickle.load(open('url2name.dmp', 'rb'))
url2id = []

for url, name in url2name.items():
    try:
        i = name2id[name]
    except:
        continue
    url2id.append((i, url))

url2id.sort(key=lambda x: x[0])

with open('id2url.txt', 'w') as fout:
    for a in url2id:
        fout.write('%d %s\n' % (a[0], a[1]))
