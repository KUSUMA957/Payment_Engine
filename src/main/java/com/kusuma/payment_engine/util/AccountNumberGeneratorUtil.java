package com.kusuma.payment_engine.util;

import java.util.UUID;

public class AccountNumberGeneratorUtil {

	private AccountNumberGeneratorUtil() {
	}

	public static String generate() {
		return "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
	}
}