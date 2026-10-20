package com.nutritionlabs.domain.food.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@Setter
@Getter
@Embeddable
public class FoodUserPkEntity implements Serializable {

	private static final long serialVersionUID = 2509691962784089340L;

	@JsonIgnore
	private Long idFood;

	private String username;
}
