package appsec.openblock.security;

import appsec.openblock.model.User;
import appsec.openblock.service.UserService;
import appsec.openblock.utils.Utilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    UserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        System.out.println("***********************FAILED***********************");

        if (exception instanceof DisabledException) {
            String email=request.getParameter("email");
            User user=userService.getUserDetails(email).get();
            userService.setOtp(user);
            String token= Base64.getEncoder().encodeToString((email+"-"+user.getPrivateUserToken()).getBytes());
            response.sendRedirect("/verification?token="+token);
        } else {
            response.sendRedirect("/login?error=true");
        }
    }
}