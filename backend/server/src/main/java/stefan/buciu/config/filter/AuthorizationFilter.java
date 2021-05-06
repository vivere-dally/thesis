package stefan.buciu.config.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import stefan.buciu.environment.AppSettings;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final AppSettings appSettings;

    public AuthorizationFilter(AuthenticationManager authenticationManager, AppSettings appSettings) {
        super(authenticationManager);
        this.appSettings = appSettings;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String authorizationHeader = request.getHeader(appSettings.getSecurityRequiredAuthorizationHeader());
        if (authorizationHeader != null) {
            String[] authorizationHeaderTokenParts = authorizationHeader.split(" ");
            if (authorizationHeaderTokenParts.length == 2 &&
                    authorizationHeaderTokenParts[0].equals(appSettings.getSecurityAccessTokenTypeHeaderValue())
            ) {
                String authorizationToken = authorizationHeaderTokenParts[1];
                try {
                    Claims claims = Jwts
                            .parser()
                            .setSigningKey(Keys.hmacShaKeyFor(appSettings.getSecurityKey().getBytes()))
                            .parseClaimsJws(authorizationToken)
                            .getBody();

                    if (claims != null) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                claims,
                                null,
                                AuthorityUtils.createAuthorityList(((ArrayList<String>) claims.get("authorities")).toArray(String[]::new))
                        );

                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                } catch (JwtException jwtException) {
                    response.setStatus(403);
                }
            }
        }

        chain.doFilter(request, response);
    }
}
