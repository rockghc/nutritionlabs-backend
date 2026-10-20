package com.nutritionlabs.domain.users.entity;

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

/**
 * Identifier of the relation between an entity and the username that owns it.
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@Setter
@Getter
@Embeddable
public class UserPkEntity implements Serializable {

	private static final long serialVersionUID = 8439652083957850795L;

	@JsonIgnore
	private Long idUser;

	private String username;
}
