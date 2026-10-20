package com.nutritionlabs.domain.users.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PUBLIC)
@Getter
@Entity
@Table(name = "nlabs_users")
public class UserEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "id_gender")
	private Long idGender;

	@ManyToOne
	@JoinColumn(name = "id_workplace")
	private WorkplaceEntity workplace;

	@Column(name = "birthdate")
	private LocalDate birthdate;

	@Column(name = "occupation")
	private String occupation;

	@Column(name = "id_country_residence")
	private Long idCountryResidence;

	@Column(name = "id_state_residence")
	private Long idStateResidence;

	@Column(name = "zip_code")
	private String zipCode;

	@Column(name = "mobile_number")
	private String mobileNumber;

	@Column(name = "email")
	private String email;

	@Column(name = "send_nutrition_form")
	private Boolean sendNutritionForm;

	@Column(name = "address")
	private String address;

	@Column(name = "creation_date")
	private LocalDateTime creationDate;

	@Column(name = "process_number")
	private String processNumber;

	@Column(name = "national_number")
	private String nationalNumber;

	@Column(name = "vat_number")
	private String vatNumber;

	@JsonIgnore
	@OneToMany(orphanRemoval = true, mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
	private List<UserUserEntity> userUserEntity;

	@JsonIgnore
	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinTable(name = "nlabs_user_tags", joinColumns = @JoinColumn(name = "id_user"), inverseJoinColumns = @JoinColumn(name = "id_tag"))
	private List<UserTagEntity> userTagsEntities;

	@JsonIgnore
	@OneToOne(mappedBy = "userEntity")
	private AppointmentInformationEntity appointmentInformation;
}
