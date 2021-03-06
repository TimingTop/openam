/*
* The contents of this file are subject to the terms of the Common Development and
* Distribution License (the License). You may not use this file except in compliance with the
* License.
*
* You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
* specific language governing permission and limitations under the License.
*
* When distributing Covered Software, include this CDDL Header Notice in each file and include
* the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
* Header, with the fields enclosed by brackets [] replaced by your own identifying
* information: "Portions copyright [year] [name of copyright owner]".
*
* Copyright 2015-2016 ForgeRock AS.
*/

/*
* @since 13.0.0
*/
package org.forgerock.openam.sso.providers.stateless;

import com.iplanet.dpro.session.Session;
import com.iplanet.dpro.session.SessionException;
import com.iplanet.dpro.session.SessionID;
import com.iplanet.dpro.session.TokenRestriction;
import com.iplanet.dpro.session.share.SessionBundle;
import com.iplanet.dpro.session.share.SessionInfo;
import org.forgerock.util.Reject;

import java.util.Set;

/**
 * The <code>StatelessSession</code> class represents a stateless session. It is a simple overridden version of
 * <code>Session</code> which allows for the addition of extra methods, or overriding of inherited ones.
 */
public class StatelessSession extends Session {
    public static final String RESTRICTED_TOKENS_UNSUPPORTED = SessionBundle.getString("restrictedTokensUnsupported");
    public static final String SSOTOKEN_LISTENERS_UNSUPPORTED = SessionBundle.getString("ssoTokenListenersUnsupported");

    private final String stableId;

    public StatelessSession(SessionID sid, SessionInfo sessionInfo) throws SessionException {
        super(sid);
        this.sessionIsLocal = false;
        update(sessionInfo);
        Reject.ifNull(sessionInfo.getSecret());
        this.stableId = sessionInfo.getSecret();
    }

    @Override
    public void scheduleToTimerPool() {
        // Ignore
    }

    @Override
    public String dereferenceRestrictedTokenID(Session s, String restrictedId) throws SessionException {
        throw new UnsupportedOperationException(RESTRICTED_TOKENS_UNSUPPORTED);
    }

    @Override
    protected void setRestriction(TokenRestriction restriction) {
        throw new UnsupportedOperationException(RESTRICTED_TOKENS_UNSUPPORTED);
    }

    @Override
    public String getStableStorageID() {
        return stableId;
    }

    /*
     * Only a value of true will cause Session#isTimedOut to refresh the session, where timeout calculations should be performed.
     * Because StatelessSession state is immutable and encapsulated in the jwt, Session#update is called from the StatelessSession ctor, which updates
     * Session#latestRefreshTime, which will prevent Session#maxCachingTimeReached from ever returning true. StatelessSessions
     * are not maintained in any state table, and thus are not cached, and are trivially refreshed from jwt state, justifying a return value of true.
     */
    @Override
    public boolean maxCachingTimeReached() {
        return true;
    }

    public Set<String> getPropertyNames() {
        return sessionProperties.keySet();
    }

    @Override
    public String getProperty(String name) throws SessionException {
        return sessionProperties.get(name);
    }

    @Override
    public int getState(boolean reset) throws SessionException {
        return sessionState;
    }

}
