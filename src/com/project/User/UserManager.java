package com.project.User;

import com.project.MyExceptions;
import com.project.controller.FileManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    List<User> users ;
    private static UserManager instance;

    static FileManager fm = new FileManager();



    private UserManager() throws MyExceptions {
        Object loadedUsers = fm.loadUsers();

        if (loadedUsers != null) {
            this.users = (List<User>) loadedUsers;
        } else {

            this.users = new java.util.ArrayList<>();
            this.users.add(new User("admin", "123", UserRole.MANAGER));
            this.users.add(new User("user", "123", UserRole.SUPERVISOR));
            FileManager.logError("Users file missing/corrupt. Created default users.");
        }
    }


    public static synchronized UserManager getInstance() throws MyExceptions {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public boolean checkUser (User user){
        for (User cs : users){
            if (cs.equals(user))
                return true;
        }
        return false;
    }

    public void removeUser(User user) {
        users.remove(user);
        fm.saveUsers(users);
    }

    public void addUser(User user) {
        users.add(user);
        fm.saveUsers(users);
    }

    public List<User> getUsers() {
        return users;
    }

}
