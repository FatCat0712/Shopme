package com.shopme.admin.user;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    public static final int USER_PER_PAGE = 5;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final  PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> listAll() {
        return (List<User>)userRepository.findAll(Sort.by("firstName").ascending());
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, USER_PER_PAGE, userRepository);
    }

    public List<Role> listRoles() {
        return (List<Role>)roleRepository.findAll();
    }

    public User save(User userInForm) {
        boolean isUpdatingUser = (userInForm.getId() != null);
        if(isUpdatingUser) {
            Optional<User> user = userRepository.findById(userInForm.getId());
            if(user.isPresent()) {
                User existingUser = user.get();
                if(userInForm.getPassword().isEmpty()) {
                    userInForm.setPassword(existingUser.getPassword());
                }
                else {
                    encodePassword(existingUser);
                }
            }

        }else {
            encodePassword(userInForm);
        }

       return userRepository.save(userInForm);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
       User userByEmail =  userRepository.getUserByEmail(email);

       if(userByEmail == null) return true;

       boolean isCreatingNew = (id == null);

       if(isCreatingNew) {
           return false;
       }
       else {
           return !userByEmail.getId().equals(id);
       }
    }

    public User get(Integer id) throws UserNotFoundException {
        Optional<User> savedUser = userRepository.findById(id);
        if(savedUser.isEmpty()) {
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }
        else {
            return savedUser.get();
        }
    }

    public void delete(Integer id) throws UserNotFoundException{
        Long countById = userRepository.countById(id);
        if(countById == null || countById == 0) {
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }

        userRepository.deleteById(id);
    }


    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepository.updateEnabledStatus(id, enabled);
    }

    public User getByEmail(String email){
        return userRepository.getUserByEmail(email);
    }

    public User updateAccount(User userInform) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userInform.getId());

        if(user.isEmpty()) {
            throw new UserNotFoundException("Could not find any user with ID " + userInform.getId());
        }
        else {
            User userInDB = user.get();
            if(!userInform.getPassword().isEmpty()) {
                userInDB.setPassword(userInform.getPassword());
                encodePassword(userInDB);
            }

            if(userInform.getPhotos() != null) {
                userInDB.setPhotos(userInform.getPhotos());
            }

            userInDB.setFirstName(userInform.getFirstName());
            userInDB.setLastName(userInform.getLastName());
            return userRepository.save(userInDB);
        }


    }


}
