package com.nutritionlabs.domain.common.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Modification of a single property of an entity.
 *
 * @param <T> Type of the value.
 */
@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SingleValueDTO<T> implements Serializable {

  private static final long serialVersionUID = 2465512724534205830L;

  /**
   * Name of the entity, for example "com.nutritionlabs.domain.users.entity.UserEntity".
   */
  private String type;

  private Long id;

  private String propertyName;

  private T value;
}
