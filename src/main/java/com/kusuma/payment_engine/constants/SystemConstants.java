package com.kusuma.payment_engine.constants;

public final class SystemConstants {
	private SystemConstants() {
	}
	public static final String SYSTEM_ACCOUNT = "SYSTEM_ACCOUNT";
	public static final int OTP_EXPIRY_MINUTES = 5;
	public static final int OTP_RESEND_COOLDOWN_MINUTES = 1;
	public static final int MAX_OTP_ATTEMPTS = 5;
	public static final int MAX_LOGIN_ATTEMPTS = 5;
	public static final int ACCOUNT_LOCK_MINUTES = 30;
}