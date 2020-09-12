package com.example.demo.config;

import com.example.demo.service.impl.JwtUserDetailsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenService jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
//            String jwt = null;
//            String header = httpServletRequest.getHeader("Authorization");
//            if(StringUtils.hasText(header) && header.startsWith("Bearer ")){
//                jwt = header.substring(7,header.length());
//            }
            String jwt = extractToken(httpServletRequest);
            if (jwt != null && jwtTokenUtil.validateToken(jwt)) {
                String email = jwtTokenUtil.getUsernameFromToken(jwt);
                UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }catch (Exception ex){
            log.info(String.format("User authentication couldn't set: [%s]",ex));
        }
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }

    public String extractToken(HttpServletRequest request){
        String jwt = null;
        String header = request.getHeader("Authorization");
        if(StringUtils.hasText(header) && header.startsWith("Bearer ")){
            jwt = header.substring(7,header.length());
        }
        return jwt;
    }
}
