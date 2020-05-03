package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangLexer
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.toSequence
import com.mattmik.rapira.objects.toText
import com.mattmik.rapira.variables.IndexedVariable
import com.mattmik.rapira.variables.SimpleVariable
import com.mattmik.rapira.variables.Variable
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.beOfType
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class VariableVisitorTest : WordSpec({
    lateinit var environment: Environment

    fun evaluateVariable(input: String): Variable {
        val lexer = RapiraLangLexer(CharStreams.fromString(input))
        val parser = RapiraLangParser(CommonTokenStream(lexer))
        val tree = parser.variable()
        return VariableVisitor(environment).visit(tree)
    }

    beforeTest {
        environment = Environment()
        environment["string_value"] = SimpleVariable(Text("Hello, world!"))
        environment["seq_value"] = SimpleVariable(listOf(
            "Alpha".toText(),
            "Bravo".toText(),
            "Charlie".toText()
        ).toSequence())
        environment["nested_sequence"] = SimpleVariable(listOf(
            "Oink".toText(),
            listOf(
                "Bark".toText(),
                "Meow".toText(),
                "Quack".toText()
            ).toSequence(),
            "Moo".toText()
        ).toSequence())
        environment["one"] = SimpleVariable(RInteger(1))
    }

    "visit" should {
        "return simple variable present in environment" {
            val expectedVariable = environment["string_value"]
            val actualVariable = evaluateVariable("string_value")
            actualVariable shouldBeSameInstanceAs expectedVariable
        }

        "return simple variable not present in environment" {
            val variable = evaluateVariable("not_present_value")
            variable should beOfType<SimpleVariable>()
            variable.value shouldBe Empty
        }

        "return simple variable with index expression" {
            val variable = evaluateVariable("seq_value[one + 1]")
            variable should beOfType<IndexedVariable>()
            variable.value shouldBe Text("Bravo")
        }

        "return simple variable with multiple index expressions" {
            val variable = evaluateVariable("nested_sequence[one + 1][3]")
            variable should beOfType<IndexedVariable>()
            variable.value shouldBe Text("Quack")
        }

        "return simple variable with comma expression" {
            val variable = evaluateVariable("nested_sequence[one + 1, 3]")
            variable should beOfType<IndexedVariable>()
            variable.value shouldBe Text("Quack")
        }
    }
})
