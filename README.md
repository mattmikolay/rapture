# Rapture

Rapture is an interpreter for Rapira (Рапира), a programming language created in
the Soviet Union during the 1980s.

```
fun fibonacci(=>n)
    if n = 0 then
        return 0
    fi
    if n = 1 then
        return 1
    fi
    return fibonacci(n - 1) + fibonacci(n - 2)
end

values := <* *>
for i from 0 to 10 do
    values := values + <* fibonacci(i) *>
od

output: values      \ Prints <* 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55 *>
```

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
