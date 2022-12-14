package com.da_ta.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtUtil {

    private static final String REGEX = "\\.";

    public static Map<String, String> getPayloadByToken(String token) {
        try {
            String[] splitJwt = token.split(REGEX);
            Base64.Decoder decoder = Base64.getDecoder();
            String payload = new String(decoder.decode(splitJwt[1].getBytes()));
            return new ObjectMapper().readValue(payload, HashMap.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
