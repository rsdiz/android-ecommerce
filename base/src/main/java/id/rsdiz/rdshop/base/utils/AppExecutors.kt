package id.rsdiz.rdshop.base.utils

import androidx.annotation.VisibleForTesting
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * Class for Executor for running the worker threads
 */
class AppExecutors @VisibleForTesting constructor(
    private val diskIO: Executor
) {
    @Inject
    constructor() : this(
        Executors.newSingleThreadExecutor()
    )

    fun diskIO(): Executor = diskIO
}
