package id.rsdiz.rdshop.base.utils

import id.rsdiz.rdshop.base.BuildConfig

/**
 * Object containing all fixed value in whole projects
 */
object Consts {
    const val DB_NAME = "RdshopDatabase"
    const val PASSPHRASE = "rsdiz-rdshop"
    const val TIMEOUT = 120L
    const val RDSHOP_BASE_URL = "https://rdshop.herokuapp.com/api/"
    const val RDSHOP_HOSTNAME = "rdshop.herokuapp.com"
    const val RDSHOP_API_KEY = BuildConfig.API_KEY
    const val API_HEADER_KEY = "X-Api-Key"
}
