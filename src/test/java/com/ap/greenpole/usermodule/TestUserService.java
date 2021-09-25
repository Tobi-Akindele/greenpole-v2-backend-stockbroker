package com.ap.greenpole.usermodule;

import com.ap.greenpole.usermodule.model.User;
import com.ap.greenpole.usermodule.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 20-Aug-20 08:56 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestUserService {

    @Autowired
    UserService userService;

    @Test
    public void testGetUsersByPermission() {
        List<User> userList = userService.getUsersByPermission("VIEW_PROFILE");
        Assert.assertNotNull(userList);
    }

    @Test
    public void testGetUsersByRoles() {
        List<User> userList = userService.getUsersByRole("USER");
        Assert.assertNotNull(userList);
    }

    @Test
    public void testGetUsersByRoles_Admin() {
        List<User> userList = userService.getUsersByRole("ADMIN");
        Assert.assertNotNull(userList);
    }

}
