import os

def parse_words(reader, stop_words):
    word_list = []
    ch = reader.read(1)

    while ch != '':
        while ch != '' and not ch.isalnum():
            ch = reader.read(1)

        word_chars = []
        while ch != '' and ch.isalnum():
            word_chars.append(ch.lower())
            ch = reader.read(1)

        word = ''.join(word_chars)
        if len(word) >= 2 and word not in stop_words:
            word_list.append(word)

    return word_list


def main():
    base_path = os.path.dirname(os.path.dirname(__file__))
    stop_words_path = os.path.join(base_path, "stop_words.txt")
    pride_path = os.path.join(base_path, "pride-and-prejudice.txt")

    with open(stop_words_path) as f:
        stop_words = set(f.read().split(","))

    with open(pride_path, encoding='utf-8') as f:
        words = parse_words(f, stop_words)

    freq = {}
    for w in words:
        freq[w] = freq.get(w, 0) + 1

    for word, count in sorted(freq.items(), key=lambda x: x[1], reverse=True)[:25]:
        print(f"{word} - {count}")


if __name__ == "__main__":
    main()
