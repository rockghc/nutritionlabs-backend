package com.nutritionlabs.domain.files.dto;

import com.nutritionlabs.domain.files.entity.Image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {

	private String uuid;
	private String fileName;
	private String fileType;
	private Long size;

	public ImageResponse(Image image) {
		this(image.getUuid(), image.getFileName(), image.getFileType(), image.getSize());
	}
}
