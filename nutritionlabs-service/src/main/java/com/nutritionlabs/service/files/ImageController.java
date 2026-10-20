package com.nutritionlabs.service.files;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nutritionlabs.business.files.ImageService;
import com.nutritionlabs.domain.files.dto.ImageContent;
import com.nutritionlabs.domain.files.dto.ImageResponse;
import com.nutritionlabs.service.ResourcePaths;
import com.nutritionlabs.service.utils.SecurityUtils;

/**
 * Upload and download of images.
 */
@RestController
@RequestMapping(ResourcePaths.FILE + "/api")
public class ImageController {

	@Autowired
	private ImageService imageService;

	@Autowired
	private SecurityUtils securityUtils;

	@GetMapping("/images")
	public ResponseEntity<List<ImageResponse>> getAllImageInfo() {
		return ResponseEntity.ok(imageService.findAllImageResponse());
	}

	/**
	 * Stores the file as the image of the property of an entity, replacing the previous one.
	 */
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ImageResponse uploadSingleFile(@RequestParam("file") MultipartFile file, @RequestPart("id") String id,
			@RequestPart("type") String type, @RequestPart("propertyName") String propertyName, Principal principal) {
		return imageService.upload(file, securityUtils.getUsername(principal), type, Long.parseLong(id.trim()), propertyName);
	}

	/**
	 * Stores files that do not belong to any entity.
	 */
	@PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public List<ImageResponse> uploadMultiFiles(@RequestParam("files") MultipartFile[] files, Principal principal) {
		String username = securityUtils.getUsername(principal);
		return Arrays.stream(files)
				.map(file -> imageService.upload(file, username, null, null, null))
				.toList();
	}

	@GetMapping("/show/{fileName:.+}")
	public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
		return toResponse(imageService.getImageByName(fileName, 0, 0));
	}

	@GetMapping("/show")
	public ResponseEntity<byte[]> getImageWithRequestParam(@RequestParam(required = false, value = "uuid") String uuid,
			@RequestParam(required = false, value = "name") String name) {
		return getScaledImageWithRequestParam(0, 0, uuid, name);
	}

	@GetMapping("/show/{width}/{height}")
	public ResponseEntity<byte[]> getScaledImageWithRequestParam(@PathVariable int width, @PathVariable int height,
			@RequestParam(required = false, value = "uuid") String uuid, @RequestParam(required = false, value = "name") String name) {
		if (uuid != null) {
			return toResponse(imageService.getImageByUuid(uuid, width, height));
		}
		if (name != null) {
			return toResponse(imageService.getImageByName(name, width, height));
		}
		return toResponse(imageService.getDefaultImage(width, height));
	}

	@GetMapping("/show/{width}/{height}/{fileName:.+}")
	public ResponseEntity<byte[]> getScaledImage(@PathVariable int width, @PathVariable int height, @PathVariable String fileName) {
		return toResponse(imageService.getImageByName(fileName, width, height));
	}

	private ResponseEntity<byte[]> toResponse(ImageContent imageContent) {
		MediaType mediaType = imageContent.fileType() != null
				? MediaType.parseMediaType(imageContent.fileType())
				: MediaType.APPLICATION_OCTET_STREAM;
		return ResponseEntity.ok().contentType(mediaType).body(imageContent.data());
	}
}
