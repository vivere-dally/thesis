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
import stefan.buciu.environment.SecurityConstants;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String authorizationToken = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if (authorizationToken != null) {
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(Keys.hmacShaKeyFor(SecurityConstants.KEY.getBytes()))
                        .parseClaimsJws(authorizationToken)
                        .getBody();

                if (claims != null) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            claims,
                            null,
                            AuthorityUtils.createAuthorityList(claims.get("authorities", String[].class))
                    );

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            } catch (JwtException jwtException) {
                response.setStatus(403);
            }
        }

        chain.doFilter(request, response);
    }
}
