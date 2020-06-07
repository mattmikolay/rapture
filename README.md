# Rapture

Rapture is an interpreter for Rapira (Рапира), a programming language created in
the Soviet Union during the 1980s.

At the moment, Rapture only supports the English variant of Rapira. In the
future, support for Russian lexemes will be added.

Rapture was built using [Kotlin] and [ANTLR].

## Usage
To run the Rapture REPL:

```
./rapture
```

To interpret a file using Rapture:

```
./rapture filename.rap
```

## Development

To generate the parser using ANTLR:

```
./gradlew generateGrammarSource
```

To build a distribution:

```
./gradlew assembleDist
```

[Kotlin]: https://kotlinlang.org/
[ANTLR]: https://github.com/antlr/antlr4/
