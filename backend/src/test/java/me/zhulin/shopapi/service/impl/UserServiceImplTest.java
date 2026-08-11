package me.zhulin.shopapi.service.impl;

import me.zhulin.shopapi.entity.User;
import me.zhulin.shopapi.exception.MyException;
import me.zhulin.shopapi.repository.CartRepository;
import me.zhulin.shopapi.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CartRepository cartRepository;

    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setPassword("password");
        user.setEmail("email@email.com");
        user.setName("Name");
        user.setPhone("Phone Test");
        user.setAddress("Address Test");
    }

    @Test
    public void createUserTest() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        userService.save(user);

        Mockito.verify(passwordEncoder).encode("password");
        Mockito.verify(userRepository, Mockito.times(2)).save(user);
        Mockito.verify(cartRepository).save(Mockito.any());
    }

    @Test(expected = MyException.class)
    public void createUserExceptionTest() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        when(userRepository.save(user))
                .thenThrow(new RuntimeException("Database error"));

        userService.save(user);
    }
    @Test
    public void updateTest() {
        User oldUser = new User();
        oldUser.setEmail(user.getEmail());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(oldUser);

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        when(userRepository.save(oldUser)).thenReturn(oldUser);

        User userResult = userService.update(user);

        assertThat(userResult.getName(), is("Name"));
        assertThat(userResult.getPhone(), is("Phone Test"));
        assertThat(userResult.getAddress(), is("Address Test"));
        assertThat(userResult.getPassword(), is("encodedPassword"));
    }
    @Test
    public void saveDoesNotRestrictRoleTest() {
        user.setRole("ROLE_MANAGER");

        when(userRepository.save(user)).thenReturn(user);

        User saved = userService.save(user);

        assertThat(saved.getRole(), is("ROLE_MANAGER"));
    }

    // Bo sung: findOne()/findByRole() truoc do chua duoc test lan nao (0% coverage)
    @Test
    public void findOneTest() {
        when(userRepository.findByEmail("email@email.com")).thenReturn(user);

        User result = userService.findOne("email@email.com");

        assertThat(result.getEmail(), is(user.getEmail()));
    }

    @Test
    public void findByRoleTest() {
        java.util.Collection<User> users = java.util.List.of(user);
        when(userRepository.findAllByRole("ROLE_CUSTOMER")).thenReturn(users);

        java.util.Collection<User> result = userService.findByRole("ROLE_CUSTOMER");

        assertThat(result.size(), is(1));
    }
}