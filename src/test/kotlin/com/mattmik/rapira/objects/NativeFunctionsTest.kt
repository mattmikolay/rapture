package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.errors.RapiraIncorrectArgumentCountError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row

class NativeFunctionsTest : WordSpec() {
    init {
        lateinit var environment: Environment

        beforeTest {
            environment = Environment()
        }

        "all native functions" should {
            "throw exception when invoked with invalid number of arguments" {
                forAll(
                    row("abs", 1),
                    row("sign", 1),
                    row("sqrt", 1),
                    row("entier", 1),
                    row("round", 1),
                    row("rand", 1),
                    row("int_rand", 1),
                    row("index", 2),
                    row("is_empty", 1),
                    row("is_log", 1),
                    row("is_int", 1),
                    row("is_real", 1),
                    row("is_text", 1),
                    row("is_seq", 1),
                    row("is_proc", 1),
                    row("is_fun", 1),
                    row("sin", 1),
                    row("cos", 1),
                    row("tg", 1),
                    row("arcsin", 1),
                    row("arctg", 1),
                    row("exp", 1),
                    row("ln", 1),
                    row("lg", 1)
                ) { functionName, expectedArgumentCount ->
                    shouldThrow<RapiraIncorrectArgumentCountError> {
                        (nativeFunctions[functionName] as RapiraCallable).call(environment, emptyList())
                    }
                }
            }
        }
    }
}
