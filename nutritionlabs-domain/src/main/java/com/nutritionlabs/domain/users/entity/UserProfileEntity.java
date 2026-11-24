package com.nutritionlabs.domain.users.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Additional information of an account (Keycloak user), created together with the Keycloak user and completed by
 * the "complete-profile" and "work-profile" interfaces.
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "nlabs_user_profiles")
public class UserProfileEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@Column(name = "id_keycloak", nullable = false, unique = true, length = 36)
	private String idKeycloak;

	@Column(name = "id_gender")
	private Long idGender;

	@Column(name = "id_country")
	private Long idCountry;

	@Column(name = "birthdate")
	private LocalDate birthdate;

	@Column(name = "phone_prefix", length = 5)
	private String phonePrefix;

	@Column(name = "mobile_number", length = 20)
	private String mobileNumber;

	@Column(name = "id_profession")
	private Long idProfession;

	/** Feature looked for (id of nlabs_catalog_details). */
	@Column(name = "id_feature")
	private Long idFeature;

	@Column(name = "workplace_name", length = 150)
	private String workplaceName;

	/** Average clients per month (id of nlabs_catalog_details). */
	@Column(name = "id_clients_per_month")
	private Long idClientsPerMonth;

	/** Areas of practice (ids of nlabs_catalog_details), stored in nlabs_user_profile_practice_areas. */
	@Builder.Default
	@ElementCollection
	@CollectionTable(name = "nlabs_user_profile_practice_areas",
			joinColumns = @JoinColumn(name = "id_user_profile"))
	@Column(name = "id_practice_area", nullable = false)
	private Set<Long> practiceAreaIds = new HashSet<>();

	@Column(name = "creation_date", nullable = false, updatable = false)
	private LocalDateTime creationDate;

	@Column(name = "update_date")
	private LocalDateTime updateDate;

	@Version
	@Column(name = "version")
	private long version;

	@PrePersist
	void onCreate() {
		if (creationDate == null) {
			creationDate = LocalDateTime.now();
		}
	}

	@PreUpdate
	void onUpdate() {
		updateDate = LocalDateTime.now();
	}
}
