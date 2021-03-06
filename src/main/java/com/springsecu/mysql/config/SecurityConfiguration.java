package com.springsecu.mysql.config;

import com.springsecu.mysql.repository.UserRepository;
import com.springsecu.mysql.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableGlobalMethodSecurity(prePostEnabled = true) //for the @PreAuthorize endpoint in the controller
@EnableWebSecurity //enables spring security
@EnableJpaRepositories(basePackageClasses = UserRepository.class) //injects all the classes that we need from the JPArepo
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  //load the data from the database instead of
  // spring maintaining the username and password

  @Autowired
  private CustomUserDetailsService userDetailsService; //it is an interface, implemented here

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService)
    .passwordEncoder(getPasswordEncoder()); //connects to the database and gets the data
  }

  //security for http rest endpoints
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable(); //disable cross-site reference
    http.authorizeRequests()
        .antMatchers("**/secured/**").authenticated() //authorize all the requests for these endpoints and use the configure function above
        .anyRequest().permitAll() //any other requests(endpoints) can be called w/o authentication
        .and().formLogin().permitAll(); //move to default spring security login page if no authentication is found
                //for custom login page the url can be given here .formlogin.loginPage("/myLoginPage....")
  }

  private PasswordEncoder getPasswordEncoder() { //23:00 - explanation
    return new PasswordEncoder() {
      @Override
      public String encode(CharSequence charSequence) {
        return charSequence.toString();
      }

      @Override
      public boolean matches(CharSequence charSequence, String s) {
        return true;
      }
    };
  }
}
