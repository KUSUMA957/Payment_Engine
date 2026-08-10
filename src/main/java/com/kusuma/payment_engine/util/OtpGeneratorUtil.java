package com.kusuma.payment_engine.util;

import java.util.Random;

public final class OtpGeneratorUtil {

	private OtpGeneratorUtil() {}

	public static String generateOtp() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}
}