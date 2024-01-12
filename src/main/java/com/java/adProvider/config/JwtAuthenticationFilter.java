package com.java.adProvider.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.java.adProvider.service.impl.UserDetailsServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceimpl;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
         final String requestTokenHeader=   request.getHeader("Authorization");
         System.out.println(requestTokenHeader);
         String username=null;
         String jwtToken=null;
         if(requestTokenHeader!=null&& requestTokenHeader.startsWith("Bearer"))
         {
        	 jwtToken=requestTokenHeader.substring(7);
        	 try {
        	username= this.jwtUtil.extractUsername(jwtToken);
        	 }catch(ExpiredJwtException e)
        	 {
        		 e.printStackTrace();
        		 System.out.println("JWT Token has Expired");
        	 }catch(Exception e)
        	 {
        		 e.printStackTrace();
        		 System.out.println("Error");
        	 }

         }else {
        	 System.out.println("Invalid Token not start with Bearer");
         }
     	if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			final UserDetails userDetails = this.userDetailsServiceimpl.loadUserByUsername(username);
			if (this.jwtUtil.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthentication);
			}
		} else {
			System.out.print("Token is not Valid");
		}
		filterChain.doFilter(request, response);
	}

}