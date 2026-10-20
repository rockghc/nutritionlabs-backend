package com.nutritionlabs.domain.users.entity;

import java.io.Serializable;

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
 * Username (Keycloak user) that works at a workplace.
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "workplaceEntity")
@Setter
@Getter
@Table(name = "nlabs_workplace_users")
@Entity
@Builder
public class WorkplaceUserEntity implements Serializable {

	private static final long serialVersionUID = 3436415182804929066L;

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "idUser", column = @Column(name = "id_workplace")),
		@AttributeOverride(name = "username", column = @Column(name = "username"))
	})
	private UserPkEntity workplaceUserPkEntity;

	@ManyToOne
	@JoinColumn(name = "id_workplace")
	@MapsId("idUser")
	private WorkplaceEntity workplaceEntity;

	@Version
	private long version;
}
