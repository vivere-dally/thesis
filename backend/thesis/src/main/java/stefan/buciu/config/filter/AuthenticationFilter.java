package stefan.buciu.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import stefan.buciu.domain.model.SecurityUser;
import stefan.buciu.domain.model.dto.UserLoginDTO;
import stefan.buciu.environment.AppSettings;
import stefan.buciu.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final AppSettings appSettings;

    public AuthenticationFilter(@Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager,
                                UserService userService,
                                AppSettings appSettings) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.appSettings = appSettings;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            String refreshToken = request.getHeader(appSettings.getSecurityRefreshTokenHeaderName());
            if (refreshToken != null) {
                Claims claims = Jwts.parser()
                        .setSigningKey(Keys.hmacShaKeyFor(appSettings.getSecurityKey().getBytes()))
                        .parseClaimsJws(refreshToken)
                        .getBody();
                if (claims != null) {
                    // TODO get authorities if needed by using the ServiceUser
                    return new UsernamePasswordAuthenticationToken(
                            new SecurityUser(claims.getSubject(), "", new ArrayList<>()),
                            null,
                            null
                    );
                }
            }

            UserLoginDTO userLogin = new ObjectMapper().readValue(request.getInputStream(), UserLoginDTO.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userLogin.getUsername(),
                    userLogin.getPassword(),
                    new ArrayList<>()
            ));
        } catch (BadCredentialsException | UsernameNotFoundException exception) {
            this.setErrorResponse(HttpStatus.FORBIDDEN, response, exception);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        Key key = Keys.hmacShaKeyFor(appSettings.getSecurityKey().getBytes());

        SecurityUser securityUser = (SecurityUser) authResult.getPrincipal();

        Claims accessTokenClaims = Jwts.claims().setSubject(securityUser.getUsername());
        accessTokenClaims.put("authorities", securityUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray());
        Date accessTokenExpirationDate = new Date(System.currentTimeMillis() + appSettings.getSecurityAccessTokenExpirationTimeInMilliseconds());
        String accessToken = getToken(accessTokenClaims, key, accessTokenExpirationDate);

        Claims refreshTokenClaims = Jwts.claims().setSubject(securityUser.getUsername());
        Date refreshTokenExpirationDate = new Date(System.currentTimeMillis() + appSettings.getSecurityRefreshTokenExpirationTimeInMilliseconds());
        String refreshToken = getToken(refreshTokenClaims, key, refreshTokenExpirationDate);

        response.setHeader(appSettings.getSecurityAccessTokenTypeHeaderName(), appSettings.getSecurityAccessTokenTypeHeaderValue());
        response.setHeader(appSettings.getSecurityAccessTokenHeaderName(), accessToken);
        response.setHeader(appSettings.getSecurityRefreshTokenHeaderName(), refreshToken);

        response.setHeader("Access-Control-Expose-Headers",
                String.join(",",
                        appSettings.getSecurityAccessTokenTypeHeaderName(),
                        appSettings.getSecurityAccessTokenHeaderName(),
                        appSettings.getSecurityRefreshTokenHeaderName()));

        request.setAttribute("username", securityUser.getUsername()); // Used in UserController @ login method to find the user by username.

        chain.doFilter(request, response);
    }

    private String getToken(Claims claims, Key key, Date expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, key)
                .setExpiration(expiration)
                .compact();
    }

    private void setErrorResponse(HttpStatus httpStatus, HttpServletResponse response, Throwable throwable) {
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        try {
            response.getWriter().write(throwable.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
