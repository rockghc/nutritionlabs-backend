package com.nutritionlabs.domain.users.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Username (Keycloak user) that manages a patient.
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "userEntity")
@Setter
@Getter
@Table(name = "nlabs_user_users")
@Entity
@Builder
public class UserUserEntity {

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "idUser", column = @Column(name = "id_user")),
		@AttributeOverride(name = "username", column = @Column(name = "username"))
	})
	private UserPkEntity userUserPkEntity;

	@ManyToOne
	@JoinColumn(name = "id_user")
	@MapsId("idUser")
	private UserEntity userEntity;

	@Version
	private long version;
}
