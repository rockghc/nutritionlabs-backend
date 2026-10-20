package com.nutritionlabs.domain.files.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Uploaded file, linked to the property of an entity ({@code typeEntity}, {@code idEntity}, {@code property}).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_image")
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "status")
	private Boolean status;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "size")
	private Long size;

	@Column(name = "uuid")
	private String uuid;

	@Column(name = "system_name")
	private String systemName;

	@Column(name = "data")
	private byte[] data;

	@Column(name = "type_entity")
	private String typeEntity;

	@Column(name = "id_entity")
	private Long idEntity;

	@Column(name = "property")
	private String property;

	@Column(name = "username")
	private String username;
}
