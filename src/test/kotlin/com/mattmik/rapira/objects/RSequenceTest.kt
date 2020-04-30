package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraIndexOutOfBoundsError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

class RSequenceTest : StringSpec({
    "length returns integer" {
        checkAll(Arb.list(rapiraObjectArb)) { objectList ->
            objectList.toRSequence().length() shouldBe objectList.size.toRInteger()
        }
    }

    "element at with integer returns object" {
        val sequence = listOf(
            1.toRInteger(),
            "Hello, world!".toRText(),
            2.toRInteger(),
            "This is a test.".toRText()
        ).toRSequence()

        sequence.elementAt(1.toRInteger()) shouldBe 1.toRInteger()
        sequence.elementAt(2.toRInteger()) shouldBe "Hello, world!".toRText()
        sequence.elementAt(3.toRInteger()) shouldBe 2.toRInteger()
        sequence.elementAt(4.toRInteger()) shouldBe "This is a test.".toRText()

        shouldThrow<RapiraIndexOutOfBoundsError> {
            sequence.elementAt(0.toRInteger())
        }
        shouldThrow<RapiraIndexOutOfBoundsError> {
            sequence.elementAt(5.toRInteger())
        }
    }

    "slice with integers returns object" {
        val sequence = listOf(
            1.toRInteger(),
            2.toRInteger(),
            3.toRInteger(),
            4.toRInteger()
        ).toRSequence()

        sequence.slice(null, null) shouldBe sequence
        sequence.slice(2.toRInteger(), null) shouldBe listOf(
            2.toRInteger(),
            3.toRInteger(),
            4.toRInteger()
        ).toRSequence()
        sequence.slice(2.toRInteger(), 3.toRInteger()) shouldBe listOf(
            2.toRInteger(),
            3.toRInteger()
        ).toRSequence()
        sequence.slice(null, 3.toRInteger()) shouldBe listOf(
            1.toRInteger(),
            2.toRInteger(),
            3.toRInteger()
        ).toRSequence()
    }

    "toString returns user friendly representation" {
        val emptySequence = emptyList<RObject>().toRSequence()
        emptySequence shouldConvertToString "<* *>"

        val simpleNumberSequence = listOf(1, 2, 3)
            .map { num -> num.toRInteger() }
            .toRSequence()
        simpleNumberSequence shouldConvertToString "<* 1, 2, 3 *>"

        val nestedSequences = listOf(
            1.toRInteger(),
            listOf(
                2.toRInteger(),
                3.toRInteger(),
                4.toRInteger()
            ).toRSequence(),
            5.toRInteger()
        ).toRSequence()
        nestedSequences shouldConvertToString "<* 1, <* 2, 3, 4 *>, 5 *>"

        val sequenceOfMixedTypes = listOf(
            1.toRInteger(),
            RReal(2.5),
            "okay".toRText()
        ).toRSequence()
        sequenceOfMixedTypes shouldConvertToString "<* 1, 2.5, \"okay\" *>"
    }
})
