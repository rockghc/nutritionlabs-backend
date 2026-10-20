package com.nutritionlabs.domain.users.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "nlabs_tags_user")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "userEntities", "version"})
public class UserTagEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToMany(mappedBy = "userTagsEntities")
	private List<UserEntity> userEntities;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Version
	private long version;
}
