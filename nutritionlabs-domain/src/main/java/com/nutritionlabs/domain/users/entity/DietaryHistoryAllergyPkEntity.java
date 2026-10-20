package com.nutritionlabs.domain.users.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@Setter(value = AccessLevel.PUBLIC)
@Getter
public class DietaryHistoryAllergyPkEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "id_dietary_history")
	private Long idDietaryHistory;

	@Column(name = "id_allergy")
	private Long idAllergy;
}
