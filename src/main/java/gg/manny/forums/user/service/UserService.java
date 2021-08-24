package gg.manny.forums.user.service;

import gg.manny.forums.rank.Rank;
import gg.manny.forums.rank.RankRepository;
import gg.manny.forums.user.User;
import gg.manny.forums.user.UserRepository;
import gg.manny.forums.user.grant.Grant;
import gg.manny.forums.util.MojangUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired private UserRepository userRepository;
    @Autowired private RankRepository roleRepository;

    @Autowired private BCryptPasswordEncoder encoder;

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByName(String name) {
        return userRepository.findByUsername(name);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    /**
     * Creating registered users for the first time
     *
     * @param user User to register
     */
    public void createUser(User user) throws Exception {
        UUID uniqueId = MojangUtils.fetchUUID(user.getUsername());
        if (uniqueId == null) throw new RuntimeException("Error finding uniqueId");

        user.setId(uniqueId);
        user.setDateJoined(new Date(System.currentTimeMillis()));
        user.setDateLastSeen(new Date(System.currentTimeMillis()));

        user.setPassword(encoder.encode(user.getPassword()));

        user.setRegistered(true); // todo add confirmations by email

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = username.contains("@") ? userRepository.findByEmail(username) : userRepository.findByUsername(username);
        if(user != null) {
            System.out.println("USER FROUND" + user.getUsername());
            List<GrantedAuthority> authorities = getUserAuthority(user.getGrants());
            return buildUserForAuthentication(user, authorities);
        } else {
            throw new UsernameNotFoundException("Username not found");
        }
    }

    private List<GrantedAuthority> getUserAuthority(List<Grant> grants) {
        List<GrantedAuthority> permissions = new ArrayList<>();
        for (Grant grant : grants) {
            if (grant.isActive()) {
                Rank rank = grant.getRank();
                rank.getCompoundedPermissions().forEach(node -> {
                    permissions.add(new SimpleGrantedAuthority(node));
                });
            }
        }

        return permissions;
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

}
