package com.nutritionlabs.service.users;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nutritionlabs.business.users.UserTagBusinessService;
import com.nutritionlabs.domain.users.dto.UserTagCreationParamsDTO;
import com.nutritionlabs.domain.users.entity.UserTagEntity;
import com.nutritionlabs.service.ResourcePaths;
import com.nutritionlabs.service.utils.SecurityUtils;

/**
 * Tags of the patients.
 */
@RestController
@RequestMapping(ResourcePaths.USER + "/tag")
public class UserTagService {

	@Autowired
	private UserTagBusinessService userTagBusinessService;

	@Autowired
	private SecurityUtils securityUtils;

	@GetMapping
	public List<UserTagEntity> getAllTags(Principal principal, @RequestParam(name = "iduser", required = false) Optional<Long> idUser) {
		return userTagBusinessService.getAllTags(securityUtils.getUsername(principal), idUser);
	}

	@PostMapping
	public void createUserTag(Principal principal, @RequestBody UserTagCreationParamsDTO tagCreationParamsDTO) {
		userTagBusinessService.createTag(securityUtils.getUsername(principal), tagCreationParamsDTO);
	}

	@DeleteMapping("/{idTag}")
	public void deleteUserTag(Principal principal, @PathVariable Long idTag, @RequestParam(name = "iduser") Long idUser) {
		userTagBusinessService.deleteTag(securityUtils.getUsername(principal), idTag, idUser);
	}
}
