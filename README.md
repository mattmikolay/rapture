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

[Kotlin]: https://kotlinlang.org/
[ANTLR]: https://github.com/antlr/antlr4/
