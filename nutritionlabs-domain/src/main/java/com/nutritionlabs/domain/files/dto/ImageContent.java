package com.nutritionlabs.domain.files.dto;

/**
 * Bytes of an image ready to be sent to the client.
 *
 * @param fileType media type of the content
 * @param data     content
 */
public record ImageContent(String fileType, byte[] data) {
}
