package com.indexdata.livedocs.manager.core.service.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.indexdata.livedocs.manager.service.security.encoder.UserPasswordEncoder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/common-context.xml", "/spring/jpa-context.xml", "/spring/security-context.xml"
})
public class UserPasswordEncoderTest {

    @Autowired
    private UserPasswordEncoder userPasswordEncoder;

    @Test
    public void shouldGeneratePasswordFOrAdmin() {
        // given
        final String userName = "admin";

        // when
        final String encodedUserName = userPasswordEncoder.encode(userName);

        // then
        System.out.println(encodedUserName);
    }
}