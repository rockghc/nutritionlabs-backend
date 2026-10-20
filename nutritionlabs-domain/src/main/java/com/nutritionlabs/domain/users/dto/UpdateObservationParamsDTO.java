package com.nutritionlabs.domain.users.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateObservationParamsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String observation;
	private LocalDate registrationDate;
}
