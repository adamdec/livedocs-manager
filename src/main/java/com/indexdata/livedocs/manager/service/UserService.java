package com.indexdata.livedocs.manager.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.core.configuration.ManagerConfiguration;
import com.indexdata.livedocs.manager.repository.UserRepository;
import com.indexdata.livedocs.manager.repository.custom.CustomUserRepository;
import com.indexdata.livedocs.manager.repository.domain.UserEntity;
import com.indexdata.livedocs.manager.repository.domain.UserPermissionEntity;
import com.indexdata.livedocs.manager.repository.utils.DaoHibernateUtils;
import com.indexdata.livedocs.manager.service.model.User;

/**
 * This class will provide service layer for user domain.
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
@Service
@Transactional
public class UserService extends BaseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserRepository customUserRepository;

    @Autowired
    private ConversionService conversionService;

    public List<User> getAllUsers() {
        try {
            final Iterable<UserEntity> userEntityList = userRepository.findAll();
            final List<User> userList = new ArrayList<User>();
            for (UserEntity userEntity : userEntityList) {
                userList.add(conversionService.convert(userEntity, User.class));
            }
            return userList;
        } finally {
            logCacheStats();
        }
    }

    public User getUserById(final Long userId) {
        try {
            return conversionService.convert(userRepository.findOne(userId), User.class);
        } finally {
            logCacheStats();
        }
    }

    public void deleteUserById(final Long userId) {
        try {
            userRepository.delete(userId);
        } finally {
            logCacheStats();
        }
    }

    public long updateUser(final User user) {
        try {
            return customUserRepository.merge(conversionService.convert(user, UserEntity.class)).getId();
        } finally {
            logCacheStats();
        }
    }

    public long saveUser(final User user) {
        try {
            return userRepository.save(conversionService.convert(user, UserEntity.class)).getId();
        } finally {
            logCacheStats();
        }
    }

    public User getUserByUsername(final String userName) {
        try {
            return conversionService.convert(userRepository.getUserByUserName(userName), User.class);
        } finally {
            logCacheStats();
        }
    }

    private void logCacheStats() {
        if (ManagerConfiguration.traceCacheStatistics()) {
            int count = counter.incrementAndGet();
            DaoHibernateUtils.printStats(em, UserEntity.class, count);
            DaoHibernateUtils.printStats(em, UserPermissionEntity.class, count);
        }
    }
}
