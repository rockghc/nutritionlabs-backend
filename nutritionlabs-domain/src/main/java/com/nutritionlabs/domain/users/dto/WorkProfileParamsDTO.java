package com.nutritionlabs.domain.users.dto;

import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data of the "work-profile" interface. Every value is optional.
 * The ids are ids of nlabs_catalog_details.
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkProfileParamsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idFeature;
	private String workplaceName;
	private Set<Long> practiceAreaIds;
	private Long idClientsPerMonth;
}
