package com.littleyes.common.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generic converter, thanks to Java8 features not only provides a way of generic bidirectional
 * conversion between corresponding types, but also a common way of converting a collection of
 * objects of the same type, reducing boilerplate code to the absolute minimum.
 *
 * @param <T> DTO representation's type
 * @param <U> Domain representation's type
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
public class HawkEyeConverter<T, U> {

  private final Function<T, U> fromDto;
  private final Function<U, T> fromEntity;

  /**
   * Constructor.
   *
   * @param fromDto    Function that converts given dto entity into the domain entity.
   * @param fromEntity Function that converts given domain entity into the dto entity.
   */
  public HawkEyeConverter(final Function<T, U> fromDto, final Function<U, T> fromEntity) {
    this.fromDto = fromDto;
    this.fromEntity = fromEntity;
  }

  /**
   * Converts DTO to Entity.
   *
   * @param dto DTO entity
   * @return The domain representation - the result of the converting function application on dto
   *     entity.
   */
  public final U convertFromDto(final T dto) {
    return fromDto.apply(dto);
  }

  /**
   * Converts Entity to DTO.
   *
   * @param entity domain entity
   * @return The DTO representation - the result of the converting function application on domain
   *     entity.
   */
  public final T convertFromEntity(final U entity) {
    return fromEntity.apply(entity);
  }

  /**
   * Converts collection of DTOs to list of Entities.
   *
   * @param dtos collection of DTO entities
   * @return List of domain representation of provided entities retrieved by mapping each of them
   *     with the conversion function
   */
  public final List<U> convertFromDtos(final Collection<T> dtos) {
    return dtos.stream().map(this::convertFromDto).collect(Collectors.toList());
  }

  /**
   * Converts collection of Entities to list of DTOs.
   *
   * @param entities collection of domain entities
   * @return List of domain representation of provided entities retrieved by mapping each of them
   *     with the conversion function
   */
  public final List<T> convertFromEntities(final Collection<U> entities) {
    return entities.stream().map(this::convertFromEntity).collect(Collectors.toList());
  }

}
