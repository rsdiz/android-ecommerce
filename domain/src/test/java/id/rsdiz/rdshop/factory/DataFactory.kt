package id.rsdiz.rdshop.data.factory

import id.rsdiz.rdshop.data.source.local.entity.Gender
import java.util.*

/**
 * This class is used to generate data objects for usage on tests
 */
object DataFactory {
    /**
     * Returns a random String object
     */
    fun randomString(): String = UUID.randomUUID().toString()

    /**
     * Returns a random Int object
     */
    fun randomInt(): Int = Random().nextInt()

    /**
     * Returns a random Double object
     */
    fun randomDouble(): Double = Random().nextDouble()

    /**
     * Returns a random boolean
     */
    fun randomBoolean(): Boolean = Math.random() < 0.5

    /**
     * Returns a random gender objects
     */
    fun randomGender(): Gender = if (randomBoolean()) Gender.MALE else Gender.FEMALE
}
