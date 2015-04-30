package com.handy.portal.core;

import com.handy.portal.data.SecurePreferences;
import com.squareup.otto.Bus;

import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

public final class UserManager implements Observer {
    private final Bus bus;
    private User user;
    private final SecurePreferences securePrefs;

    @Inject
    UserManager(final Bus bus, final SecurePreferences prefs) {
        this.securePrefs = prefs;
        this.bus = bus;
        this.bus.register(this);
    }

    public final User getCurrentUser() {
        if (user != null) return user;
        else {
            if ((user = User.fromJson(securePrefs.getString("USER_OBJ"))) != null)
                user.addObserver(this);
            return user;
        }
    }

    public final void setCurrentUser(final User newUser) {
        if (user != null) user.deleteObserver(this);

        if (newUser == null || newUser.getAuthToken() == null || newUser.getId() == null) {
            user = null;
            securePrefs.put("USER_OBJ", null);
            //bus.post(new UserLoggedInEvent(false));
            return;
        }

        user = newUser;
        user.addObserver(this);

        securePrefs.put("USER_OBJ", user.toJson());
        //UAirship.shared().getPushManager().setAlias(user.getId());

        //bus.post(new UserLoggedInEvent(true));
    }

    @Override
    public void update(final Observable observable, final Object data) {
        if (observable instanceof User) setCurrentUser((User)observable);
    }

//    @Subscribe
//    public final void environmentUpdated(final EnvironmentUpdatedEvent event) {
//        if (event.getEnvironment() != event.getPrevEnvironment()) setCurrentUser(null);
//    }
}
