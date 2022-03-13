package id.rsdiz.rdshop.data

/**
 * Restricted class hierarchies that provide more control over inheritance
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    /**
     * Flag class Resource as Success and return data
     */
    class Success<T>(data: T) : Resource<T>(data)

    /**
     * Flag class Resource as Loading and return data if available
     */
    class Loading<T>(data: T? = null) : Resource<T>(data)

    /**
     * Flag class Resource as Error and return message with data if available
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}
