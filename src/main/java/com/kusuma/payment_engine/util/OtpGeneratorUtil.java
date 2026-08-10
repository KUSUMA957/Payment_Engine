package com.kusuma.payment_engine.util;

import java.util.Random;

/*"This class is only a utility class. 
 * It should not be instantiated - To prevent object creation since the class contains only utility methods.
 * It should not be inherited - To prevent inheritance because utility classes are not designed to be extended.
 * Just use its static methods - So it can be called directly using the class name without creating an object."
*/
public final class OtpGeneratorUtil {

	private OtpGeneratorUtil() {}

	public static String generateOtp() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}
}