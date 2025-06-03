import sys, string
import numpy as np
import os

file_path = os.path.join("..", sys.argv[1])
with open(file_path, "r", encoding="utf-8") as f:
    text = f.read()

characters = np.array([' '] + list(text) + [' '], dtype='<U1')

#normalize to uppercase
characters[~np.char.isalpha(characters)] = ' '
characters = np.char.upper(characters)

spaces = np.where(characters == ' ')[0]
doubled = np.repeat(spaces, 2)
pairs = np.reshape(doubled[1:-1], (-1, 2))

# filter words of length >= 2 
pairs = pairs[np.where(pairs[:, 1] - pairs[:, 0] > 2)]

words = np.array([''.join(characters[r[0]:r[1]]).strip() for r in pairs])

# remove words with len < 2
words = words[np.char.str_len(words) >= 2]

# replace vowels
leet_map = str.maketrans({'A': '4', 'E': '3', 'I': '1', 'O': '0'})
leet_words = np.char.translate(words, leet_map)

# 2-gram
first = leet_words[:-1]
second = leet_words[1:]
two_grams = np.char.add(np.char.add(first, ' '), second)

# count top 5
uniq, counts = np.unique(two_grams, return_counts=True)
top5 = sorted(zip(uniq, counts), key=lambda x: x[1], reverse=True)[:5]

for pair, count in top5:
    print(pair, '-', count)
