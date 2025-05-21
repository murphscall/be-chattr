package com.kimje.chat.global.util;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class EmailVerifyPassGenerator {

	public String generateCode() {
		Random rand = new Random();
		int number = 100000 + rand.nextInt(900000);
		return String.valueOf(number);
	}
}
