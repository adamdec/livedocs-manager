package com.indexdata.livedocs.manager.service.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.indexdata.livedocs.manager.service.UserService;
import com.indexdata.livedocs.manager.service.model.User;

/**
 * @author Adam Dec
 * @since 0.0.6
 */
@Service
public class UsernameDetailsService implements UserDetailsService {

    private static Logger LOGGER = LoggerFactory.getLogger(UsernameDetailsService.class);

    @Autowired
    protected UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.debug("Checking username=[{}]...", username);
        final User user = userService.getUserByUsername(username);
        if (user != null) {
            LOGGER.debug("User [{}] found.\n{}", username, user);
            boolean enabled = true;
            boolean accountNonLocked = true;
            boolean accountNonExpired = true;
            boolean credentialsNonExpired = true;

            return new LiveDocsUser(user.getUserName(),
                    user.getPassword(), enabled,
                    accountNonExpired, credentialsNonExpired, accountNonLocked,
                    LiveDocsAuthorityUtils.convertToAuthorities(user
                            .getUserPermissions()), LiveDocsAuthorityUtils.convertToAuthorities(user
                            .getProfilePermissions()));
        } else {
            LOGGER.error("User [{}] not found!", username);
            throw new UsernameNotFoundException(username);
        }
    }
}