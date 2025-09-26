package cz.malickov.backend.service;


import cz.malickov.backend.entity.User;
import cz.malickov.backend.model.CustomUserDetails;
import cz.malickov.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


// Used in spring security to override loadUsewrByUsername
@Service
public class UserDetailsLoginService  implements UserDetailsService {
    private final UserRepository userRepository;


    public UserDetailsLoginService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository
                .findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

        return new CustomUserDetails(user);
    }
}
