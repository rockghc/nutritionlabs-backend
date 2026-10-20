package com.nutritionlabs.dataaccess.files;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nutritionlabs.domain.files.dto.ImageResponse;
import com.nutritionlabs.domain.files.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Integer> {

	List<Image> findByTypeEntityAndIdEntityAndProperty(String typeEntity, Long idEntity, String property);

	Optional<Image> findFirstByFileName(String fileName);

	Optional<Image> findFirstByUuid(String uuid);

	@Query("select new com.nutritionlabs.domain.files.dto.ImageResponse(im.uuid, im.fileName, im.fileType, im.size) "
			+ "from Image im where im.status = true")
	List<ImageResponse> findAllImageResponse();
}
