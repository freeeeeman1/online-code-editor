package com.netcracker.edu.auth.repository;

import com.netcracker.edu.project.user.User;

public interface UserRepository {

    void add(User user);

    void update(User user);

    User getByLogin(String login);

    User getByEmail(String email);

    boolean contains(String email);

    void updatePassword(User user);

    void remove(User user);

    void clear();
}
