package bg.libapp.libraryapp.service;

import bg.libapp.libraryapp.model.entity.User;
import bg.libapp.libraryapp.model.enums.Role;
import bg.libapp.libraryapp.model.security.AppUserDetails;
import bg.libapp.libraryapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import static bg.libapp.libraryapp.model.constants.ApplicationConstants.LOGGING_USER_WITH_USERNAME;
import static bg.libapp.libraryapp.model.constants.ApplicationConstants.USER_WITH_USERNAME_NOT_FOUND_STRING_FORMAT;


public class AppUserDetailsService implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(AppUserDetailsService.class);
    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info(LOGGING_USER_WITH_USERNAME, username);
        return this.userRepository
                .findByUsername(username)
                .map(this::mapUserToUserDetails)
                .orElseThrow(() -> {
                    logger.info(String.format(USER_WITH_USERNAME_NOT_FOUND_STRING_FORMAT, username));
                    return new UsernameNotFoundException(String.format(USER_WITH_USERNAME_NOT_FOUND_STRING_FORMAT, username));
                });
    }

    private UserDetails mapUserToUserDetails(User user) {
        return new AppUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + Role.values()[user.getRole()])));
    }
}
