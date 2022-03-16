package id.rsdiz.rdshop.data.source.local.room

import androidx.room.Dao
import id.rsdiz.rdshop.data.source.local.entity.DetailOrderEntity
import id.rsdiz.rdshop.data.source.local.room.base.IBaseDao

/**
 * Contracts how application interacts with stored data in [DetailOrderEntity]
 */
@Dao
interface IDetailOrderDao : IBaseDao<DetailOrderEntity>
