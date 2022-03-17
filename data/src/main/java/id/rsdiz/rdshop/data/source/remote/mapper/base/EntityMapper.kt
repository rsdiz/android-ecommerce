package id.rsdiz.rdshop.data.source.remote.mapper.base

/**
 * Contract between API response and our entities
 */
interface EntityMapper<in R, out E> {
    /**
     * Map from remote model to our entity
     */
    fun mapRemoteToEntity(remote: R): E

    /**
     * Map from remote model to our entities
     */
    fun mapRemoteToEntities(remotes: List<R>): List<E>
}
