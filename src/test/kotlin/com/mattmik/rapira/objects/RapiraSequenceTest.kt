package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraIndexOutOfBoundsError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

class RapiraSequenceTest : StringSpec({
    "length returns integer" {
        checkAll(Arb.list(rapiraObjectArb)) { objectList ->
            objectList.toRapiraSequence().length() shouldBe objectList.size.toRapiraInteger()
        }
    }

    "element at with integer returns object" {
        val sequence = listOf(
            1.toRapiraInteger(),
            "Hello, world!".toRapiraText(),
            2.toRapiraInteger(),
            "This is a test.".toRapiraText()
        ).toRapiraSequence()

        sequence.elementAt(1.toRapiraInteger()) shouldBe 1.toRapiraInteger()
        sequence.elementAt(2.toRapiraInteger()) shouldBe "Hello, world!".toRapiraText()
        sequence.elementAt(3.toRapiraInteger()) shouldBe 2.toRapiraInteger()
        sequence.elementAt(4.toRapiraInteger()) shouldBe "This is a test.".toRapiraText()

        shouldThrow<RapiraIndexOutOfBoundsError> {
            sequence.elementAt(0.toRapiraInteger())
        }
        shouldThrow<RapiraIndexOutOfBoundsError> {
            sequence.elementAt(5.toRapiraInteger())
        }
    }

    "slice with integers returns object" {
        val sequence = listOf(
            1.toRapiraInteger(),
            2.toRapiraInteger(),
            3.toRapiraInteger(),
            4.toRapiraInteger()
        ).toRapiraSequence()

        sequence.slice(null, null) shouldBe sequence
        sequence.slice(2.toRapiraInteger(), null) shouldBe listOf(
            2.toRapiraInteger(),
            3.toRapiraInteger(),
            4.toRapiraInteger()
        ).toRapiraSequence()
        sequence.slice(2.toRapiraInteger(), 3.toRapiraInteger()) shouldBe listOf(
            2.toRapiraInteger(),
            3.toRapiraInteger()
        ).toRapiraSequence()
        sequence.slice(null, 3.toRapiraInteger()) shouldBe listOf(
            1.toRapiraInteger(),
            2.toRapiraInteger(),
            3.toRapiraInteger()
        ).toRapiraSequence()
    }

    "toString returns user friendly representation" {
        val emptySequence = emptyList<RapiraObject>().toRapiraSequence()
        emptySequence shouldConvertToString "<* *>"

        val simpleNumberSequence = listOf(1, 2, 3)
            .map { num -> num.toRapiraInteger() }
            .toRapiraSequence()
        simpleNumberSequence shouldConvertToString "<* 1, 2, 3 *>"

        val nestedSequences = listOf(
            1.toRapiraInteger(),
            listOf(
                2.toRapiraInteger(),
                3.toRapiraInteger(),
                4.toRapiraInteger()
            ).toRapiraSequence(),
            5.toRapiraInteger()
        ).toRapiraSequence()
        nestedSequences shouldConvertToString "<* 1, <* 2, 3, 4 *>, 5 *>"

        val sequenceOfMixedTypes = listOf(
            1.toRapiraInteger(),
            RapiraReal(2.5),
            "okay".toRapiraText()
        ).toRapiraSequence()
        sequenceOfMixedTypes shouldConvertToString "<* 1, 2.5, \"okay\" *>"
    }
})
