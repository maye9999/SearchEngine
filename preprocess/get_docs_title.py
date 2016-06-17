import os

with open("file_doc_title.txt", "w", encoding="UTF-8") as fout:
    for l in os.listdir("all_text/docs/"):
        f = open("all_text/docs/" + l, "r", encoding="UTF-8")
        id = l.split('.')[0]
        try:
            for line in f:
                line = line.strip()
                if len(line) > 6:
                    # print(line)
                    fout.write(id + " " + line + "\n")
                    break
        except Exception as e:
            print(id)
            continue
    for l in os.listdir("all_text/docxs/"):
        f = open("all_text/docxs/" + l, "r", encoding="UTF-8")
        id = l.split('.')[0]
        try:
            for line in f:
                line = line.strip()
                if len(line) > 6:
                    # print(line)
                    fout.write(id + " " + line + "\n")
                    break
        except Exception as e:
            print(id)
            continue