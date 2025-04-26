package crossBoard.util

/**
 * Sealed class Either used to compact the information when one of the values is the class of error and the other is when is the correct one.
 */
sealed class Either <out L, out R> {
    /**
     * Data Class Left representing the information on the Left, in this case will represent the information with the error and nothing else.
     * @property value representing the value on the left side in this case will represent the error information.
     * @return Either with only the information of the error value.
     */
    data class Left<out L>(val value: L): Either<L, Nothing>()

    /**
     * Data class Right representing the information on the Right, in this case will represent the information with success.
     * @property value representing the value on the right side in this case will represent the success information.
     * @return Either with only the success information and nothing else.
     */
    data class Right<out R>(val value: R): Either<Nothing, R>()
}

/**
 * Function success used to represent the success information in the data class.
 * @param value the success information to pass to the data class.
 * @return Either.Right with the success information only.
 */
fun <R> success(value: R) = Either.Right(value)

/**
 * Function failure used to represent the failure information in the data class.
 * @param value the failure information to pass to the data class.
 * @return Either.Left with the failure information only.
 */
fun <L> failure(value: L) = Either.Left(value)

/**
 * The two types representing the Success and the Failure.
 */
typealias Success<S> = Either.Right<S>
typealias Failure<F> = Either.Left<F>