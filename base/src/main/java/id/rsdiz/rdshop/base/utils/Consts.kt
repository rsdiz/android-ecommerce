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
    const val RDSHOP_MERCHANT_URL = "https://payment-rdshop.herokuapp.com/pay.php/"
    const val RAJAONGKIR_BASE_URL = "https://api.rajaongkir.com/starter/"
    const val RDSHOP_HOSTNAME = "rdshop.herokuapp.com"
    const val RDSHOP_API_KEY = BuildConfig.API_KEY
    const val RAJAONGKIR_API_KEY = BuildConfig.API_RAJAONGKIR
    const val MIDTRANS_API_KEY = BuildConfig.API_MIDTRANS_SANDBOX
    const val API_HEADER_KEY = "X-Api-Key"
    const val RAJAONGKIR_API_HEADER_KEY = "key"
    const val PREFERENCE_NAME = "RDSHOP"
    const val PREF_ID = "user_id"
    const val PREF_USERNAME = "username"
    const val PREF_EMAIL = "email"
    const val PREF_NAME = "name"
    const val PREF_GENDER = "gender"
    const val PREF_ADDRESS = "address"
    const val PREF_PHOTO = "photo"
    const val PREF_ROLE = "role"
    const val PREF_TOKEN = "token"
    const val PREF_CART = "cart"
    const val STATUS_WAITING: Short = 0
    const val STATUS_PROCESSED: Short = 1
    const val STATUS_DISPATCH: Short = 2
    const val STATUS_ARRIVED: Short = 3
    const val ORIGIN_CITY = "Temanggung"
}
