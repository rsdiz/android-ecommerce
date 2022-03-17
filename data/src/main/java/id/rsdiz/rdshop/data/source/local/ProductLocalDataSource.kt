package id.rsdiz.rdshop.data.source.local

import id.rsdiz.rdshop.data.source.local.entity.ProductEntity
import id.rsdiz.rdshop.data.source.local.entity.ProductWithImages
import id.rsdiz.rdshop.data.source.local.mapper.ProductMapper
import id.rsdiz.rdshop.data.source.local.room.IProductDao
import id.rsdiz.rdshop.data.source.local.room.IProductImageDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductLocalDataSource @Inject constructor(
    private val productDao: IProductDao,
    private val productImageDao: IProductImageDao,
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

    suspend fun insertAll(
        list: List<ProductWithImages>,
    ) {
        val listProducts = mutableListOf<ProductEntity>()
        list.forEach {
            listProducts.add(it.product)
            productImageDao.insertAll(it.images)
        }
        productDao.insertAll(listProducts)
    }

    suspend fun insert(data: ProductWithImages) {
        productDao.insert(data.product)
        data.images.forEach {
            productImageDao.insert(it)
        }
    }

    suspend fun delete(data: ProductWithImages) {
        data.images.forEach {
            productImageDao.delete(it)
        }
        productDao.delete(data.product)
    }
}
