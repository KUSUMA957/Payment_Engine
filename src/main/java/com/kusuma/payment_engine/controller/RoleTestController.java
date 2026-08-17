package com.kusuma.payment_engine.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role-test")
public class RoleTestController {

	@GetMapping("/customer")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String customerApi() {
		return "Customer Endpoint Accessed Successfully";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminApi() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("ADMIN CHECK -> " + auth.getAuthorities());
		return "Admin Endpoint";
	}

	@GetMapping("/both")
	@PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
	public String bothApi() {
		return "Admin or Customer can access";
	}

	@GetMapping("/test")
	public String test() {
		return "Test";
	}

//	@GetMapping("/whoami")
//	public String whoAmI() {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if (auth == null) {
//			return "No Authentication";
//		}
//		return auth.getName() + " -> " + auth.getAuthorities();
//	}
//
//	@GetMapping("/authorities")
//	public String authorities() {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		return auth.getAuthorities().toString();
//	}
}