package com.nutritionlabs.service.users;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nutritionlabs.business.users.UserProfileBusinessService;
import com.nutritionlabs.domain.users.dto.UserProfileDTO;
import com.nutritionlabs.domain.users.dto.WorkProfileParamsDTO;
import com.nutritionlabs.service.ResourcePaths;
import com.nutritionlabs.service.utils.SecurityUtils;

/**
 * Profile of the logged account (table nlabs_user_profiles).
 */
@RestController
@RequestMapping(ResourcePaths.USER_PROFILE)
public class UserProfileService {

	@Autowired
	private UserProfileBusinessService userProfileBusinessService;

	@Autowired
	private SecurityUtils securityUtils;

	@GetMapping
	public UserProfileDTO getProfile(Principal principal) {
		return userProfileBusinessService.getProfile(securityUtils.getUsername(principal));
	}

	/**
	 * Data of the "work-profile" interface.
	 */
	@PutMapping("/work")
	public UserProfileDTO updateWorkProfile(@RequestBody WorkProfileParamsDTO params, Principal principal) {
		return userProfileBusinessService.updateWorkProfile(securityUtils.getUsername(principal), params);
	}
}
