package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    @NotNull
    val categoryId: String,
    val name: String
)
