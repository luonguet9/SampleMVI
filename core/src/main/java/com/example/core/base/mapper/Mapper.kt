package com.example.core.base.mapper

/**
 * Generic mapper interface for converting between data models across layers.
 *
 * Common patterns:
 * - DTO → Domain model (data layer → domain layer)
 * - Entity → Domain model (room entity → domain model)
 * - Domain model → Entity (domain model → room entity for persistence)
 *
 * HOW TO USE IN NEW PROJECT:
 * ```kotlin
 * class UserDtoMapper @Inject constructor() : Mapper<UserDto, User> {
 *     override fun map(input: UserDto): User = User(
 *         id = input.id,
 *         name = input.name
 *     )
 * }
 * ```
 *
 * @param I Input type
 * @param O Output type
 */
interface Mapper<I, O> {
    fun map(input: I): O
}

/**
 * Extension of [Mapper] that adds a convenience [mapList] function.
 *
 * HOW TO USE IN NEW PROJECT:
 * Simply implement [map] — [mapList] is provided for free:
 * ```kotlin
 * class UserDtoMapper @Inject constructor() : ListMapper<UserDto, User> {
 *     override fun map(input: UserDto): User = User(id = input.id, name = input.name)
 * }
 * // Call site:
 * val users: List<User> = userDtoMapper.mapList(dtoList)
 * ```
 */
interface ListMapper<I, O> : Mapper<I, O> {
    fun mapList(input: List<I>): List<O> = input.map { map(it) }
}
