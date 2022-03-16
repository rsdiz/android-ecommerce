package id.rsdiz.rdshop.data.source.local

import id.rsdiz.rdshop.data.source.local.entity.ProductEntity
import id.rsdiz.rdshop.data.source.local.mapper.ProductMapper
import id.rsdiz.rdshop.data.source.local.room.IProductDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductLocalDataSource @Inject constructor(
    private val productDao: IProductDao,
    val mapper: ProductMapper
) {
    fun getAllProducts() = productDao.getAllProducts()

    fun getProductById(productId: String) = productDao.getProductById(productId)

    fun getProductByCategoryId(categoryId: String) = productDao.getProductByCategoryId(categoryId)

    fun searchProducts(word: String) = productDao.searchProducts(word)

    fun getProductByFilter(
        word: String?,
        minPrice: Int?,
        maxPrice: Int?
    ) = productDao.getProductByFilter(word, minPrice, maxPrice)

    fun update(data: ProductEntity) = productDao.update(data)

    suspend fun insertAll(list: List<ProductEntity>) = productDao.insertAll(list)

    suspend fun insert(data: ProductEntity) = productDao.insert(data)

    suspend fun delete(data: ProductEntity) = productDao.delete(data)
}
