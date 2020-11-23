/**
 * 
 */
package com.pon.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author Sanjeev Kumar
 * @Date Dec 10, 2018
 * @Time 11:19:33 PM
 */
@Configuration
//Addition of below annotation will turn off all the auto configuration of security by Springboot
@EnableWebSecurity(debug = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	
	public void configure(WebSecurity web) {}
	// The most important methods that can be overridden here are following. All
	// these will override the default implementation of Springboot

	/**
	 * [1] It allows configuring web-based security for specific http requests.
	 * 
	 * @throws Exception
	 ***/
	protected void configure(HttpSecurity http) throws Exception {
		// At first, we’ll also close all accesses to our endpoints. We’ll correctly
		// configure the accesses later on.

		http.formLogin().disable() // disable form authentication
				.anonymous().disable() // disable anonymous user
				.httpBasic().and()
				// restricting access to authenticated users
				.authorizeRequests().anyRequest().authenticated();

	}

	/**
	 * [2] The place to configure the default authenticationManager @Bean
	 ***/
	@Override
	protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {

		authenticationManagerBuilder.inMemoryAuthentication() // creating user in memory
				.withUser("user").password(passwordEncoder().encode("password")).roles("USER").and().withUser("admin")
				.password(passwordEncoder().encode("password")).authorities("ADMIN");
	}

	/**
	 * [3] Override this method to expose the default AuthenticationManager as a
	 * Bean which has just been configured above. It is just because it could be
	 * injected somewhere else. We will use it while defining AuthenticationServer
	 * for OAuth2 implementaion
	 ***/	

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		// provides the default AuthenticationManager as a Bean
		return super.authenticationManagerBean();
	}

	/**
	 * [4] It allows configuring .
	 ***/

	/**
	 * A password encoder is required to encode the password of the user and client
	 * while authentication. The similar password encoder will be used while storing
	 * the password of a user and that of client in the database if we are using
	 * user and client details store in the database.
	 **/
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
