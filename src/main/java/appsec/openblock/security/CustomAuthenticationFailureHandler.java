package appsec.openblock.security;

import appsec.openblock.model.User;
import appsec.openblock.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

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

        // DOM XSS possible
        if (exception instanceof BadCredentialsException) {
            response.sendRedirect("/login?error=VXNlcm5hbWUgb3IgcGFzc3dvcmQgaXMgaW5jb3JyZWN0ICE=");
        }
        // verification token generation is vulnerable also XSS possible
        else if (exception instanceof DisabledException) {
            String email = request.getParameter("email");
            User user = userService.getUserDetails(email).get();
            userService.setOtp(user);
            String token = Base64.getEncoder().encodeToString((email + "-" + user.getPrivateUserToken() + "-" + user.getLastOtp()).getBytes());
            response.sendRedirect("/verification?token=" + token);
        }
    }
}