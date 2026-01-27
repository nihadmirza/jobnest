package com.example.jobnest.services.impl;

import com.example.jobnest.dto.request.UserRegistrationRequest;
import com.example.jobnest.dto.response.RegisterPageDTO;
import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.RegistrationPageService;
import com.example.jobnest.services.UsersService;
import com.example.jobnest.services.UsersTypeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistrationPageServiceImpl implements RegistrationPageService {

    private static final String REGISTER_VIEW = "register";

    private final UsersTypeService usersTypeService;
    private final UsersService usersService;
    private final AuthenticationService authenticationService;

    @Override
    public PageResult showRegisterPage(String role) {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setRole(role != null ? role : "CANDIDATE");
        return showRegisterPage(RegisterPageDTO.builder().request(request).build());
    }

    @Override
    public PageResult showRegisterPage(RegisterPageDTO page) {
        List<UsersType> types = usersTypeService.getAll();
        UserRegistrationRequest request = page.getRequest() != null ? page.getRequest() : new UserRegistrationRequest();
        if (request.getRole() == null) {
            request.setRole("CANDIDATE");
        }
        RegisterPageDTO hydrated = RegisterPageDTO.builder()
                .request(request)
                .types(types)
                .error(page.getError())
                .successMessage(page.getSuccessMessage())
                .build();

        return new PageResult(REGISTER_VIEW, Map.of("page", hydrated));
    }

    @Override
    public PageResult registerNewUser(RegisterPageDTO page, HttpServletRequest httpRequest) {
        try {
            Users savedUser = usersService.registerUser(page.getRequest());
            authenticationService.authenticateAndCreateSession(savedUser, httpRequest);
            return new PageResult("redirect:/dashboard", Map.of());
        } catch (Exception e) {
            RegisterPageDTO errorPage = RegisterPageDTO.builder()
                    .request(page.getRequest())
                    .error(e.getMessage())
                    .build();
            return showRegisterPage(errorPage);
        }
    }
}

