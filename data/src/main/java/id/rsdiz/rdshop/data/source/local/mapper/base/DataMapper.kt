package id.rsdiz.rdshop.data.source.local.mapper.base

/**
 * Defined contract to map from entity (data layer) to model (domain layer) and vice-versa
 */
interface DataMapper<E, M> {
    /**
     * Map from a specified entity
     */
    fun mapFromEntity(entity: E): M

    /**
     * Map from a specified entities
     */
    fun mapFromEntities(entities: List<E>): List<M>

    /**
     * Map to a specified entity
     */
    fun mapToEntity(model: M): E
}
