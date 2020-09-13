# Rapture

Rapture is an interpreter for Rapira (Рапира), a programming language created in
the Soviet Union during the 1980s.

```
функ фибоначи(=>n)
    если n = 0 то
        возврат 0
    все
    если n = 1 то
        возврат 1
    все
    возврат фибоначи(n - 1) + фибоначи(n - 2)
конец

values := <* *>
для i от 0 до 10 цикл
    values := values + <* фибоначи(i) *>
кц

вывод: values      \ Prints <* 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55 *>
```

Rapture supports both the English and Russian variants of Rapira:

```
output: "Good morning!"
вывод: "Доброе утро"
```

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

## FAQ

**What is Rapira?**

Rapira (Рапира) is a programming language that was created in the Soviet Union
during the 1980s. It was originally designed by Gennady Anatolievich
Zvenigorodsky as a "bridge from the education language Robic to the standard
programming languages." [\[1\]](#references)

The original implementation of Rapira was part of Schoolgirl (Shkolnitsa)
[\[1\]](#references), a programming system created by Zvenigorodsky and a group
of his students. [\[2\]](#references)

**What computers was Rapira originally used on?**

Rapira interpreters existed for the [Agat], [Korvet], and an unspecified Yamaha
computer. [\[1\]](#references)

**What languages does Rapira support?**

Russian, English, and Moldovan variants of Rapira were developed.
[\[1\]](#references) Rapture only supports the English and Russian variants.

## Development

1. Clone this repository:

        git clone https://github.com/mattmikolay/rapture.git

2. Generate the parser using ANTLR:

        ./gradlew generateGrammarSource

3. Build an executable `rapture` application in the `build/install/rapture/bin`
directory:

        ./gradlew installDist

4. If making source code changes, run unit tests:

        ./gradlew test

## References
[1]: *Rapira Programming Language (Язык программирования Рапира)*
  L.S. Baraz, E.V. Borovikov, N.G. Glagoleva, P.A. Zemtsov, E.V. Nalimov,
  and V.A. Tsikoza
    - [English](http://ershov.iis.nsk.su/ru/node/772586)
    - [Russian](http://ershov.iis.nsk.su/ru/node/772584)
[2]: [Звенигородский](http://alley.iis.nsk.su/person/zvenigorodskiy)

[Agat]: https://en.wikipedia.org/wiki/Agat_%28computer%29
[Korvet]: https://en.wikipedia.org/wiki/Corvette_%28computer%29
[Kotlin]: https://kotlinlang.org/
[ANTLR]: https://github.com/antlr/antlr4/
