package com.ap.greenpole.usermodule.util;

import com.ap.greenpole.usermodule.model.GenericResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 20-Aug-20 02:41 AM
 */
public class Helpers {

    public static boolean CleanPageable = true;

    public static String RolePrefix = "ROLE_";

    public static String PermissionPrefix = "PERMISSION_";

    public static void WriteError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpStatus httpStatus, String message) throws IOException {
        httpServletResponse.setStatus(httpStatus.value());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        GenericResponse<String> resp = new GenericResponse<>(httpStatus.name(), httpServletRequest.getServletPath(), message);
        new ObjectMapper().writeValue(httpServletResponse.getWriter(), resp);
    }

    public static ObjectNode IncreasePageNumberByOne(Object repoResp) throws IOException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = (ObjectNode) mapper.readTree(ow.writeValueAsString(repoResp));
        int pageNumber = json.get("number").intValue()+1;
        json.put("number", pageNumber);
        ((ObjectNode) json.get("pageable")).put("pageNumber", pageNumber);
        return json;
    }

    public static ObjectNode ChangeJsonContentList(Object repoResp, ArrayList<?> newContent) throws IOException {
        if (CleanPageable) {
            repoResp = IncreasePageNumberByOne(repoResp);
        }
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = (ObjectNode) mapper.readTree(ow.writeValueAsString(repoResp));
        ArrayNode newContentArrayNode = mapper.valueToTree(newContent);
        json.putArray("content").addAll(newContentArrayNode);
        return json;
    }

    public static String GetSaltString(String saltChars, int length) {
        saltChars += "1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) {
            int index = (int) (rnd.nextFloat() * saltChars.length());
            salt.append(saltChars.charAt(index));
        }
        return salt.toString();
    }

    public static String MD5(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(data.getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        return number.toString(16);
    }

}
