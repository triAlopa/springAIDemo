package com.chen.util;

import com.chen.pojo.dto.UserDTO;

/**
 * 获取当前线程的用户
 */
public class CurrentUserHolder {

    private static ThreadLocal<UserDTO> currentUser = new ThreadLocal<>();

    public static UserDTO getCurrentUser() {
        return  currentUser.get();
    }

    public static void setCurrentUser(UserDTO userDTO) {
        currentUser.set(userDTO);
    }

    public static void removeCurrentUser() {
        currentUser.remove();
    }
}
