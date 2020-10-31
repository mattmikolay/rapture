![Rapture](logo.png)
====================

[![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/mattmikolay/rapture?sort=semver)](https://github.com/mattmikolay/rapture/releases)

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

Rapture was built using [Kotlin] and [ANTLR]. The Rapture logo is rendered using
the [Righteous font](https://fonts.google.com/specimen/Righteous).

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
Zvenigorodsky [\[1\]](#references).

The original implementation of Rapira was part of Schoolgirl (Shkolnitsa)
[\[1\]](#references), an educational software system created at the Computing
Center of the Siberian Branch of the USSR Academy of Science in Novosibirsk.
[\[3, 4\]](#references) The Schoolgirl system was developed “in late 1984 by a
group of young graduates of the Young Programmers School, mentored by G.
Zvenigorodski under the guidance of [Andrei Ershov]” and accompanied a course
meant to teach “the basics of informatics and computing machines.”
[\[3\]](#references) It supported two languages, Robic and Rapira, used to
teach programming to “children from 2nd to 10th grades.” [\[4\]](#references)

**What computers was Rapira originally used on?**

Rapira interpreters existed for the [Agat], [Korvet], and an unspecified Yamaha
computer. [\[1\]](#references)

**What languages does Rapira support?**

Russian, English, and Moldovan variants of Rapira were developed.
[\[1\]](#references) Rapture only supports the English and Russian variants.

## Development

1. Clone this repository:

        git clone https://github.com/mattmikolay/rapture.git

2. Build an executable `rapture` application in the `build/install/rapture/bin`
directory:

        ./gradlew installDist

3. If making source code changes, run unit tests:

        ./gradlew test

## References
[1]: L.S. Baraz, E.V. Borovikov, N.G. Glagoleva, P.A. Zemtsov, E.V. Nalimov, and
     V.A. Tsikoza. *Rapira Programming Language (Язык программирования Рапира)*.
     English URL: http://ershov.iis.nsk.su/ru/node/772586 Russian URL:
     http://ershov.iis.nsk.su/ru/node/772584 (visited on 09/13/2020).

[2]: *Звенигородский*. URL: http://alley.iis.nsk.su/person/zvenigorodskiy
     (visited on 09/13/2020).

[3]: *“Shkolnitsa” – an Application Package for Automation of School Educational
     Process*. URL: https://web.archive.org/web/20160401080544/http://sorucom.karelia.ru/view_thesis.html?id=17&user_id=17
     (visited on 09/19/2020).

[4]: Boenig-Liptsin, Margarita. 2015. *Making Citizens of the Information Age: A
     Comparative Study of the First Computer Literacy Programs for Children in
     the United States, France, and the Soviet Union, 1970-1990*. Doctoral
     dissertation, Harvard University, Graduate School of Arts & Sciences.

[Agat]: https://en.wikipedia.org/wiki/Agat_%28computer%29
[Korvet]: https://en.wikipedia.org/wiki/Corvette_%28computer%29
[Kotlin]: https://kotlinlang.org/
[ANTLR]: https://github.com/antlr/antlr4/
[Andrei Ershov]: https://en.wikipedia.org/wiki/Andrey_Ershov
