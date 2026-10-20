package com.nutritionlabs.domain.files.event;

/**
 * Published when the image of an entity property is replaced. It replaces the former
 * FILE_NAME_NOTIFICATION_TOPIC Kafka topic.
 *
 * @param typeEntity entity type, see {@link com.nutritionlabs.domain.common.EntityTypes}
 * @param idEntity   id of the entity
 * @param property   property of the entity that stores the file name
 * @param fileName   name of the new file
 */
public record ImageReplacedEvent(String typeEntity, Long idEntity, String property, String fileName) {
}
