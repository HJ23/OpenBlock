package appsec.openblock.security;

import appsec.openblock.checkers.PostAuthChecker;
import appsec.openblock.service.UserServiceImpl;
import appsec.openblock.utils.Md5PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private CustomLoginSucessHandler sucessHandler;


    @Bean
    public UserDetailsService userDetailsService() {
        return new UserServiceImpl();
    }

    @Bean
    public Md5PasswordEncoder passwordEncoder(){
        return new Md5PasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPreAuthenticationChecks(toCheck -> {});
        authProvider.setPostAuthenticationChecks(new PostAuthChecker());

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());
        http.authorizeRequests()
                .mvcMatchers("/login","/register").anonymous()
                .antMatchers("/verification","/api/v1/otp","/profile-pictures/*").permitAll()
                .antMatchers("/api/v1/*").authenticated()
                .antMatchers("/admin","/complains","/addCollection").hasAnyAuthority("ADMIN")
                .antMatchers("/profile/**","/balance/**","/invoice/*","/contact","/increaseBalance","/auction").hasAnyAuthority("USER")
                .and()
                // form login
                .csrf().disable().formLogin()
                .loginPage("/login")
                .successHandler(sucessHandler)
                .usernameParameter("email")
                .passwordParameter("password")
                .failureHandler(customAuthenticationFailureHandler)
                .and()
                // logout
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403");


        //http.headers().frameOptions().sameOrigin();

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**","/assets/**","/profile-pictures/*","/collections/*");
    }

}