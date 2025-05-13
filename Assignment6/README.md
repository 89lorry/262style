**To run this exercise, first compile all:**

```
javac Assignment6/*.java
```
**Then change directory to:**

```
cd Assignment6
```

**Then use the following command:**

```
java Twenty
```

**You can select which plugin combination to use by editing the `config.properties` file. Uncomment the two lines (one for words, one for frequencies).**

| Combination           | Description                                                  |
| --------------------- | ------------------------------------------------------------ |
| Words1 + Frequencies1 | Top 25 most frequent non-stop words                          |
| Words1 + Frequencies2 | Top 25 most frequent first lettersof non-stop words          |
| Words2 + Frequencies1 | Top 25 most frequent non-stop words that contain the letter 'z' |
| Words2 + Frequencies2 | Top 25 most frequent first letters of non-stop words that contain the letter 'z' |