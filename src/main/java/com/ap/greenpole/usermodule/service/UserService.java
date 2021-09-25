package com.ap.greenpole.usermodule.service;

import com.ap.greenpole.usermodule.config.GreenPoleConfig;
import com.ap.greenpole.usermodule.model.*;
import com.ap.greenpole.usermodule.repository.*;
import com.ap.greenpole.usermodule.util.Helpers;
import com.ap.greenpole.usermodule.util.JwtTokenUtil;
import com.ap.greenpole.usermodule.util.SharedEnvironment;
import io.github.thecarisma.FatalObjCopierException;
import io.github.thecarisma.ObjCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Member;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 12-Aug-20 01:44 AM
 */
@Service
public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    UserPermissionRepository userPermissionRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private Environment env;

    SharedEnvironment sharedEnvironment;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    public UserService() {
        sharedEnvironment = new SharedEnvironment(env);
    }

    public boolean emailExisted(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    public User.Response getAuthorizedUserResponse(User user) {
        final String token = jwtTokenUtil.generateToken(userDetailsFromEmail(user));
        User.Response responseBody = new User.Response();
        responseBody.setAccessToken(token);
        responseBody.setUser(user);
        responseBody.setFirstTimeLogin(user.isFirstTimeLogin());
        if (user.isFirstTimeLogin()) {
            responseBody.setMessage("Your permission is minimal. For the first time logging in, you need to change your password");
        }
        return responseBody;
    }

    public GreenPoleUserDetails userDetailsFromEmail(User user) {
        List<UserPermission> permissions;
        permissions = user.getPermissions();
        if (user.isFirstTimeLogin()) {
            //permissions = new ArrayList<>();
            UserPermission permission = new UserPermission();
            permission.setValue("CHANGE_PASSWORD");
            permissions.add(permission);
        }
//        else {
//            permissions = user.getPermissions();
//        }
        return new GreenPoleUserDetails(user.getEmail(), user.getRoles(), permissions);
    }

    public Optional<User> memberFromAuthorization(String authorization) {
        String loginId = emailFromAuthorization(authorization);
        return getMemberByEmail(loginId);
    }

    public Optional<User> getUserWithPermissionsAndRolesByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            user.get().setPermissions(userPermissionRepository.findAllByUserId(user.get().getId()));
            user.get().setRoles(userRoleRepository.findAllByUserId(user.get().getId()));
        }
        return user;
    }

    public Optional<User> getMemberByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user;
    }

    public String emailFromAuthorization(String authorization) {
        if (authorization.length() < 10) {
            return null;
        }
        String authToken = authorization.substring(7);
        return jwtTokenUtil.getEmailFromToken(authToken);
    }

    public Optional<User> authenticateUser(User.Request requestBody) {
        Optional<User> user = getMemberByEmail(requestBody.getEmail());
        if (user.isPresent() && (new BCryptPasswordEncoder().matches(requestBody.getPassword(), user.get().getPassword()))) {
            return user;
        }
        return Optional.empty();
    }

    public boolean passwordAdhereToPolicy(String password) {
        if (password.length() < 8) { //Password must not be less than 8 characters
            return false;
        }
        //Password must have at least one Upper case character
        //Password must have at least one lower case character
        //Password must have at least one number
        //Password must not have spacing
        if (!Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$").matcher(password).matches()) {
            return false;
        }

        //Password must have at least one special character like
        if (!password.contains("@") && !password.contains("!") && !password.contains("#") && !password.contains("$") &&
                !password.contains("%") && !password.contains("^") && !password.contains("&") && !password.contains("*") &&
                !password.contains("(") && !password.contains(")") && !password.contains("-") && !password.contains("_") &&
                !password.contains("=") && !password.contains("+") && !password.contains("=") && !password.contains(".") &&
                !password.contains("=") && !password.contains(",")) {
            return false;
        }

        return true;
    }

    public User.Response changePassword(User user, ChangePassword changePassword) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setFirstTimeLogin(false);
        user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        userRepository.save(user);
        return getAuthorizedUserResponse(user);
    }

    public boolean createNewAdmin(User user) {
//        if (!sharedEnvironment.adminRegisterKey.equals(user.getAdminRegisterKey())) {
//            return false;
//        }
        return createUser(user, true);
    }

    public boolean createNewUser(User user) {
        user.setFirstTimeLogin(true);
        return createUser(user, false);
    }

    public boolean createUser(User user, boolean isAdmin) {
        try {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (user.getFirstName().isEmpty()) {
                user.setFirstName(user.getEmail().substring(0, user.getEmail().indexOf("@")));
            }
            userRepository.saveAndFlush(user);
            Optional<User> savedUser = userRepository.findByEmail(user.getEmail());
            savedUser.ifPresent(value -> addDefaultRolesAndPermissions(value.getId(), isAdmin));
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    // Add default role and permissions for admin and user
    private void addDefaultRolesAndPermissions(long userId, boolean isAdmin) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        if (isAdmin) {
            Optional<Role> role = roleRepository.findByValue("ADMIN");
            if (role.isPresent()) {
                userRole.setRoleId(role.get().getId());
                userRoleRepository.saveAndFlush(userRole);
            }
            List<Permission> permissions = permissionRepository.findAll();
            for (Permission permission : permissions) {
                UserPermission userPermission = new UserPermission();
                userPermission.setPermissionId(permission.getId());
                userPermission.setUserId(userId);
                userPermission.setValue(permission.getValue());
                userPermissionRepository.save(userPermission);
            }
        } else {
            Optional<Role> role = roleRepository.findByValue("USER");
            if (role.isPresent()) {
                userRole.setRoleId(role.get().getId());
                userRoleRepository.saveAndFlush(userRole);
            }
        }
    }

    public List<User> getUsersByPermission(String permission) {
        return userRepository.findAllByPermission(permission);
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findAllByRole(role);
    }

    public void sendPasswordResetEmail(User user, String prefixUrl) throws NoSuchAlgorithmException, MessagingException, UnsupportedEncodingException {
        String passwordResetUrl = String.format("%s?token=%s",
                prefixUrl,
                generateUniqueVerificationToken(user, 24));
        //System.out.println(passwordResetUrl);
        Context context = new Context();
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );
        context.setVariable("iconUrl", sharedEnvironment.iconUrl);
        context.setVariable("firstName", user.getFirstName());
        context.setVariable("supportEmail", sharedEnvironment.supportEmailAddress);
        context.setVariable("passwordResetUrl", passwordResetUrl);
        context.setVariable("date", new Date().toString());

        String html = templateEngine.process("reset-password-template", context);
        helper.setTo(user.getEmail());
        helper.setText(html, true);
        helper.setSubject("Reset Your Greenpole Account Password");
        helper.setFrom(sharedEnvironment.smptUserName, sharedEnvironment.fineName);
        emailSender.send(message);
    }

    private String generateUniqueVerificationToken(User user, int expiryHour) throws NoSuchAlgorithmException {
        EmailVerification emailVerification = new EmailVerification();
        Date dateIssued = new Date();
        Date expiryDate = new Date(System.currentTimeMillis() + (expiryHour * 60 * 60) * 1000);
        String token = Helpers.MD5(Helpers.GetSaltString(
                String.format("%s%d%s",user.getEmail(),
                        dateIssued.getTime(), secret), 10
        ));
        token = Helpers.GetSaltString(user.getFirstName() + token + user.getEmail(), 40);

        emailVerification.setUserId(user.getId());
        emailVerification.setEmailAddress(user.getEmail());
        emailVerification.setDateIssued(dateIssued.getTime() / 1000);
        emailVerification.setExpiryDate(expiryDate.getTime() / 1000);
        emailVerification.setTokenUsed(false);
        emailVerification.setToken(token);
        emailVerificationRepository.save(emailVerification);
        return token;
    }

    public Page<User> listUsers(Pageable pageable) {
        return userRepository.listUsers(pageable);
    }

    public User updateUser(User existing, User newData) throws FatalObjCopierException {
        ObjCopier.copyFieldsExcept(new String[]{"id"}, existing, newData);
        return userRepository.saveAndFlush(existing);
    }

}
