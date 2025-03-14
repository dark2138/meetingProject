package org.example.swaggerexam.jwt.service;

import lombok.RequiredArgsConstructor;
import org.example.swaggerexam.repository.UserRepository;
import org.example.swaggerexam.jwt.dto.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.example.swaggerexam.domain.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return new CustomUserDetails(user.get().getId(), user.get().getEmail(),"", getAuthorities("USER"));
        }
        throw new UsernameNotFoundException("User not found");
    }

    private List<GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }
}
/*


//기본적으로 상요자가 UserDetailsService 를 제공하지 않으면 시큐리티는 자동으로 생성..  그게 비밀번호 생성함.
//우리는 CustomUserDetailsService 를 만들었음.
// 첫번째 해결방법
@Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(); // 빈 등록만 하고 사용자 추가 X
    }

//두번째 해결방법
   @Bean
    public UserDetailsService userDetailsService() {
        return username -> null; // 빈만 등록하고 로직은 사용하지 않음
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

//세번째..

spring.security.user.name=
spring.security.user.password=







 */