package com.nutritionlabs.domain.recipes.entity;

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
@Table(name = "nlabs_tags_recipe")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "recipeEntities", "version"})
public class RecipeTagEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToMany(mappedBy = "recipeTagsEntities")
	private List<RecipeEntity> recipeEntities;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Version
	private long version;
}
