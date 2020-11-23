/**
 * 
 */
package com.pon.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.util.matcher.RequestMatcher;



/**
 * @ author Sanjeev Kumar
 * @Date Dec 11, 2018
 * @Time 11:15:03 AM
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	@Value("${security.oauth2.resource.id}")
	private String resourceId;

	// The DefaultTokenServices bean provided at the AuthorizationConfig
	@Autowired
	private DefaultTokenServices tokenServices;

	// The TokenStore bean has already been provided at the AuthorizationConfig. It
	// can be injected here with an objective of giving an access of this bean to
	// the resource server so that it could use it for authenticating the token
	// received while serving the request.
	@Autowired
	private TokenStore tokenStore;

	// To allow the ResourceServerConfigurerAdapter to understand the token,
	// it must share the same characteristics with
	// AuthorizationServerConfigurerAdapter.
	// So, we must wire it up the beans in the ResourceServerSecurityConfigurer.
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(resourceId).tokenServices(tokenServices).tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		http
		    
		 .requestMatcher(new CustomOAuthRequestedMatcher())
         .csrf().disable()
         .anonymous().disable()
         .authorizeRequests()
       //we’re using .requestMatcher(new CustomOAuthRequestedMatcher()), which restricts all options defined on this HttpSecurity 
	       //only to requests directed at URIs with the path /api/, guaranteeing that the HttpSecurity defined at ResourceConfig will 
	       //prevail over any other endpoint remaining.
         .antMatchers(HttpMethod.OPTIONS).permitAll()
         // when restricting access to 'Roles' you must remove the "ROLE_" part role
         // for "ROLE_USER" use only "USER"
         .antMatchers("/api/hello").access("hasAnyRole('USER')")////api/hello can only be accessed by users with "USER" role
         .antMatchers("/api/me").hasAnyRole("USER", "ADMIN")
         .antMatchers("/api/admin").hasRole("ADMIN")//api/admin can only be accessed by users with "ADMIN" role
         // use the full name when specifying authority access
         .antMatchers("/api/registerUser").hasAuthority("ROLE_REGISTER")
         // restricting all access to /api/** to authenticated users
         .antMatchers("/api/**").authenticated();//We’re also restricting the access to all resources derived from /api/** only to authenticated users
		
		
		
		
	}

	private static class CustomOAuthRequestedMatcher implements RequestMatcher {
		public boolean matches(HttpServletRequest request) {
			// Determine if the resource called is "/api/**"
			System.out.println("Sanjeev Singh "+request.getServletPath());
			String path = request.getServletPath();
			if (path.length() >= 5) {
				path = path.substring(0, 5);
				boolean isApi = path.equals("/api/");
				System.out.println("Sanjeev  "+isApi);
				return isApi;
			} else
				return false;
		}
	}
}
