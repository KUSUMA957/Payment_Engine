package com.kusuma.payment_engine.util;

import java.util.UUID;

public final class TransactionReferenceUtil {

	private TransactionReferenceUtil() {
	}

	public static String generate() {
		return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
	}
}