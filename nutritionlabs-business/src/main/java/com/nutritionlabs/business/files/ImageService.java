package com.nutritionlabs.business.files;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nutritionlabs.dataaccess.files.ImageRepository;
import com.nutritionlabs.domain.files.dto.ImageContent;
import com.nutritionlabs.domain.files.dto.ImageResponse;
import com.nutritionlabs.domain.files.entity.Image;
import com.nutritionlabs.domain.files.event.ImageReplacedEvent;

/**
 * Stores the uploaded images in the database and serves them, scaled when requested.
 */
@Service
@Transactional
public class ImageService {

	private static final String DEFAULT_SYSTEM_NAME = "default";
	private static final String DEFAULT_IMAGE = "files/notfound.jpg";
	private static final String DATA_URL_PREFIX = "data:";
	private static final String DATA_URL_BASE64_SEPARATOR = ";base64,";

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private FileNameHelper fileNameHelper;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	/**
	 * Stores the file as the image of the property of an entity, replacing the previous one.
	 */
	public ImageResponse upload(MultipartFile file, String username, String typeEntity, Long idEntity, String property) {
		Image image = buildImage(file, username, typeEntity, idEntity, property);
		return new ImageResponse(replace(image));
	}

	public Image replace(Image image) {
		if (image.getIdEntity() != null) {
			imageRepository.deleteAll(imageRepository.findByTypeEntityAndIdEntityAndProperty(image.getTypeEntity(), image.getIdEntity(), image.getProperty()));
		}
		Image imageSaved = imageRepository.save(image);
		if (imageSaved.getIdEntity() != null) {
			eventPublisher.publishEvent(new ImageReplacedEvent(imageSaved.getTypeEntity(), imageSaved.getIdEntity(), imageSaved.getProperty(), imageSaved.getFileName()));
		}
		return imageSaved;
	}

	@Transactional(readOnly = true)
	public List<ImageResponse> findAllImageResponse() {
		return imageRepository.findAllImageResponse();
	}

	/**
	 * Content of the image with the given name, or the default image when it does not exist.
	 *
	 * @param width  width of the scaled image, 0 to keep the original size
	 * @param height height of the scaled image, 0 to keep the original size
	 */
	@Transactional(readOnly = true)
	public ImageContent getImageByName(String fileName, int width, int height) {
		return toContent(imageRepository.findFirstByFileName(fileName), width, height);
	}

	@Transactional(readOnly = true)
	public ImageContent getImageByUuid(String uuid, int width, int height) {
		return toContent(imageRepository.findFirstByUuid(uuid), width, height);
	}

	public ImageContent getDefaultImage(int width, int height) {
		return scale(new ImageContent(MediaType.IMAGE_JPEG_VALUE, readDefaultImage()), width, height);
	}

	/**
	 * Binary content of a stored image. The front end stores some images as data URLs
	 * ({@code data:image/png;base64,...}); those are decoded.
	 */
	@Transactional(readOnly = true)
	public Optional<byte[]> getImageBinaryContent(String fileName) {
		return imageRepository.findFirstByFileName(fileName)
				.map(Image::getData)
				.map(ImageService::decodeDataUrl);
	}

	private ImageContent toContent(Optional<Image> image, int width, int height) {
		return image
				.map(storedImage -> scale(new ImageContent(storedImage.getFileType(), storedImage.getData()), width, height))
				.orElseGet(() -> getDefaultImage(width, height));
	}

	private Image buildImage(MultipartFile file, String username, String typeEntity, Long idEntity, String property) {
		LocalDateTime now = LocalDateTime.now();
		try {
			return Image.builder()
					.uuid(UUID.randomUUID().toString())
					.createdDate(now)
					.updatedDate(now)
					.createdBy(username)
					.updatedBy(username)
					.systemName(DEFAULT_SYSTEM_NAME)
					.status(true)
					.fileName(fileNameHelper.generateDisplayName(file.getOriginalFilename()))
					.fileType(file.getContentType())
					.size(file.getSize())
					.data(file.getBytes())
					.username(username)
					.typeEntity(typeEntity)
					.idEntity(idEntity)
					.property(property)
					.build();
		} catch (IOException e) {
			throw new UncheckedIOException("Error reading the uploaded file", e);
		}
	}

	/**
	 * Scales the image to the given size. Content that is not a readable image is returned unchanged.
	 */
	private ImageContent scale(ImageContent content, int width, int height) {
		if (width <= 0 || height <= 0 || content.data() == null) {
			return content;
		}
		try {
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(content.data()));
			if (image == null) {
				return content;
			}
			java.awt.Image scaledImage = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
			BufferedImage scaledBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			scaledBuffer.getGraphics().drawImage(scaledImage, 0, 0, Color.BLACK, null);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			ImageIO.write(scaledBuffer, "jpg", buffer);
			return new ImageContent(MediaType.IMAGE_JPEG_VALUE, buffer.toByteArray());
		} catch (IOException e) {
			return content;
		}
	}

	private byte[] readDefaultImage() {
		try (InputStream inputStream = new ClassPathResource(DEFAULT_IMAGE).getInputStream()) {
			return inputStream.readAllBytes();
		} catch (IOException e) {
			throw new UncheckedIOException("Default image not found", e);
		}
	}

	private static byte[] decodeDataUrl(byte[] data) {
		if (data == null || data.length < DATA_URL_PREFIX.length()) {
			return data;
		}
		String header = new String(data, 0, Math.min(data.length, 100), StandardCharsets.US_ASCII);
		if (!header.startsWith(DATA_URL_PREFIX)) {
			return data;
		}
		String dataUrl = new String(data, StandardCharsets.US_ASCII);
		int base64Start = dataUrl.indexOf(DATA_URL_BASE64_SEPARATOR);
		if (base64Start < 0) {
			return data;
		}
		return Base64.getMimeDecoder().decode(dataUrl.substring(base64Start + DATA_URL_BASE64_SEPARATOR.length()));
	}
}
