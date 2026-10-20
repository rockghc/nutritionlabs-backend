package com.nutritionlabs.domain.food.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "foodEntity")
@Setter
@Getter
@Table(name = "nlabs_foods_users")
@Entity
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodUser {

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "idFood", column = @Column(name = "id_food")),
		@AttributeOverride(name = "username", column = @Column(name = "username")),
	})
	private FoodUserPkEntity foodUserPkEntity;

	@ManyToOne
	@JoinColumn(name = "id_food")
	@MapsId("idFood")
	private FoodEntity foodEntity;

	@Version
	private Long version;
}
