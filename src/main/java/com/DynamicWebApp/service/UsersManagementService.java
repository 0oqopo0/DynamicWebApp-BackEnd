package com.DynamicWebApp.service;
import java.time.LocalDateTime;

import com.DynamicWebApp.config.JWTAuthFilter;
import com.DynamicWebApp.dto.ReqRes;
import com.DynamicWebApp.entity.OurUsers;
import com.DynamicWebApp.entity.Role;
import com.DynamicWebApp.repository.UsersRepo;
import com.DynamicWebApp.repository.RoleRepository;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsersManagementService {

    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private RoleRepository roleRepository; // اضافه کردن این خط
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
/////////////////////////////////////////////////////////
public ReqRes registerUser(ReqRes registrationRequest) {
    ReqRes resp = new ReqRes();
    try {
        // بخش مربوط به چک کردن نام کاربری را حذف کردم
        Optional<OurUsers> existingUserByEmail = usersRepo.findByEmail(registrationRequest.getEmail());

        if (existingUserByEmail.isPresent()) {
            resp.setStatusCode(400);
            resp.setMessage("این ایمیل قبلاً استفاده شده است");
            return resp;
        }

        OurUsers ourUser = new OurUsers();
        ourUser.setEmail(registrationRequest.getEmail());
        ourUser.setCity(registrationRequest.getCity());
        ourUser.setName(registrationRequest.getName());
        ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        ourUser.setCreateDate(LocalDateTime.now());

        Optional<Role> roleOptional = roleRepository.findByRoleCodeEng("USER");
        if (roleOptional.isEmpty()) {
            resp.setStatusCode(400);
            resp.setMessage("نقش پیش‌فرض USER یافت نشد");
            return resp;
        }
        Role role = roleOptional.get();
        ourUser.setRoles(List.of(role));
        ourUser.setRolesCreateDate(List.of(LocalDateTime.now())); // تنظیم تاریخ جاری به عنوان CREATE_DATE برای نقش‌ها

        OurUsers ourUsersResult = usersRepo.save(ourUser);
        if (ourUsersResult.getId() > 0) {
            resp.setOurUsers(ourUsersResult);
            resp.setMessage("کاربر با موفقیت ذخیره شد");
            resp.setStatusCode(200);
        }
    } catch (Exception e) {
        resp.setStatusCode(500);
        resp.setError(e.getMessage());
    }
    return resp;
}

    public ReqRes registerAdminUser(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();
        try {
            // بخش مربوط به چک کردن نام کاربری را حذف کردم
            Optional<OurUsers> existingUserByEmail = usersRepo.findByEmail(registrationRequest.getEmail());

            if (existingUserByEmail.isPresent()) {
                resp.setStatusCode(400);
                resp.setMessage("این ایمیل قبلاً استفاده شده است");
                return resp;
            }

            OurUsers ourUser = new OurUsers();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setCity(registrationRequest.getCity());
            ourUser.setName(registrationRequest.getName());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            ourUser.setCreateDate(LocalDateTime.now());

            Optional<Role> roleOptional = roleRepository.findByRoleCodePer(registrationRequest.getRole());
            if (roleOptional.isEmpty()) {
                resp.setStatusCode(400);
                resp.setMessage("نقش انتخاب شده معتبر نیست");
                return resp;
            }
            Role role = roleOptional.get();
            ourUser.setRoles(List.of(role));
            ourUser.setRolesCreateDate(List.of(LocalDateTime.now())); // تنظیم تاریخ جاری به عنوان CREATE_DATE برای نقش‌ها

            OurUsers ourUsersResult = usersRepo.save(ourUser);
            if (ourUsersResult.getId() > 0) {
                resp.setOurUsers(ourUsersResult);
                resp.setMessage("کاربر با موفقیت ذخیره شد");
                resp.setStatusCode(200);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

/////////////////////////////////////////////////////////

    public ReqRes login(ReqRes loginRequest) {
        ReqRes response = new ReqRes();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRoles().stream().map(Role::getRoleCodeEng).collect(Collectors.joining(",")));
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public ReqRes refreshToken(ReqRes refreshTokenReqiest) {
        ReqRes response = new ReqRes();
        try {
            String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
            OurUsers users = usersRepo.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenReqiest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();

        try {
            List<OurUsers> result = usersRepo.findAll();
            if (!result.isEmpty()) {
                reqRes.setOurUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }

    public ReqRes getUsersById(Integer id) {
        ReqRes reqRes = new ReqRes();
        try {
            OurUsers usersById = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setOurUsers(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes deleteUser(Integer userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                usersRepo.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes updateUser(Integer userId, OurUsers updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                OurUsers existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());

                // تنظیم نقش‌ها
                Role role = new Role();
                role.setRoleCodeEng(updatedUser.getRoles().get(0).getRoleCodeEng());
                existingUser.setRoles(List.of(role));

                // بررسی و به‌روزرسانی رمز عبور
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                OurUsers savedUser = usersRepo.save(existingUser);
                reqRes.setOurUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes getMyInfo(String email) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setOurUsers(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;

    }

    ////////////////////////////////
    public ReqRes getAllRoles() {
        ReqRes reqRes = new ReqRes();
        try {
            List<Role> roles = roleRepository.findAll();
            reqRes.setRoles(roles);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Roles fetched successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }


////////////////////////////////
}
