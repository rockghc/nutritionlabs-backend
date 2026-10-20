package com.nutritionlabs.domain.users.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private Long idGender;
	private Long idWorkplace;
	private WorkplaceDTO workplace;
	private LocalDate birthdate;
	private String occupation;
	private Long idCountryResidence;
	private Long idStateResidence;
	private String zipCode;
	private String mobileNumber;
	private String email;
	private Boolean sendNutritionForm;
	private String address;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm a")
	private LocalDateTime creationDate;
	private String processNumber;
	private String nationalNumber;
	private String vatNumber;
}
