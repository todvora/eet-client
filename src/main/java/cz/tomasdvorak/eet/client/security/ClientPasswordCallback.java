package cz.tomasdvorak.eet.client.security;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

class ClientPasswordCallback implements CallbackHandler {

    private final String alias;
    private final String password;

    ClientPasswordCallback(final String alias, final String password) {
        this.alias = alias;
        this.password = password;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

        for (Callback thisCallback : callbacks) {
            WSPasswordCallback pwcb = (WSPasswordCallback) thisCallback;
            String user = pwcb.getIdentifier();
            int usage = pwcb.getUsage();
            if (usage == WSPasswordCallback.SIGNATURE && alias.equals(user)) {
                pwcb.setPassword(password);
            }
        }

    }

}