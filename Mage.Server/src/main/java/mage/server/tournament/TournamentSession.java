/*
* Copyright 2011 BetaSteward_at_googlemail.com. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are
* permitted provided that the following conditions are met:
*
*    1. Redistributions of source code must retain the above copyright notice, this list of
*       conditions and the following disclaimer.
*
*    2. Redistributions in binary form must reproduce the above copyright notice, this list
*       of conditions and the following disclaimer in the documentation and/or other materials
*       provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
* FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
* ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
* The views and conclusions contained in the software and documentation are those of the
* authors and should not be interpreted as representing official policies, either expressed
* or implied, of BetaSteward_at_googlemail.com.
*/

package mage.server.tournament;

import mage.cards.decks.Deck;
import mage.game.tournament.Tournament;
import mage.interfaces.callback.ClientCallback;
import mage.server.User;
import mage.server.UserManager;
import mage.server.util.ThreadExecutor;
import mage.view.TournamentView;
import org.apache.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class TournamentSession {
    protected final static Logger logger = Logger.getLogger(TournamentSession.class);

    protected UUID userId;
    protected UUID playerId;
    protected UUID tableId;
    protected Tournament tournament;
    protected boolean killed = false;

    private ScheduledFuture<?> futureTimeout;
    protected static ScheduledExecutorService timeoutExecutor = ThreadExecutor.getInstance().getTimeoutExecutor();

    public TournamentSession(Tournament tournament, UUID userId, UUID tableId, UUID playerId) {
        this.userId = userId;
        this.tournament = tournament;
        this.playerId = playerId;
        this.tableId = tableId;
    }

    public boolean init() {
        if (!killed) {
            User user = UserManager.getInstance().getUser(userId);
            if (user != null) {
                user.fireCallback(new ClientCallback("tournamentInit", tournament.getId(), getTournamentView()));
                return true;
            }
        }
        return false;
    }

    public void update() {
        if (!killed) {
            User user = UserManager.getInstance().getUser(userId);
            if (user != null) {
                user.fireCallback(new ClientCallback("tournamentUpdate", tournament.getId(), getTournamentView()));
            }
        }
    }

    public void gameOver(final String message) {
        if (!killed) {
            User user = UserManager.getInstance().getUser(userId);
            if (user != null) {
                user.fireCallback(new ClientCallback("tournamentOver", tournament.getId(), message));
            }
        }
    }

    public void construct(int timeout) {
        if (!killed) {
            setupTimeout(timeout);
            User user = UserManager.getInstance().getUser(userId);
            if (user != null) {
                int remaining = (int) futureTimeout.getDelay(TimeUnit.SECONDS);
                user.ccConstruct(tournament.getPlayer(playerId).getDeck(), tableId, remaining);
            }
        }
    }

    public void submitDeck(Deck deck) {
        cancelTimeout();
        tournament.submitDeck(playerId, deck);
    }

    public void updateDeck(Deck deck) {
        tournament.updateDeck(playerId, deck);
    }

    public void setKilled() {
        killed = true;
    }

    public boolean isKilled() {
        return killed;
    }

    private synchronized void setupTimeout(int seconds) {
        if (futureTimeout != null && !futureTimeout.isDone()) {
            return;
        }
        cancelTimeout();
        if (seconds > 0) {
            futureTimeout = timeoutExecutor.schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TournamentManager.getInstance().timeout(tournament.getId(), userId);
                        } catch (Exception e) {
                            logger.fatal("TournamentSession error - userId " + userId + " tId " + tournament.getId(), e);
                        }
                    }
                },
                seconds, TimeUnit.SECONDS
            );
        }
    }

    private synchronized void cancelTimeout() {
        if (futureTimeout != null) {
            futureTimeout.cancel(false);
            logger.debug("Timeout is Done: " + futureTimeout.isDone() + "  userId: " + userId);
        }
    }

    private TournamentView getTournamentView() {
        return new TournamentView(tournament);
    }

    public UUID getTournamentId() {
        return tournament.getId();
    }

    public void tournamentOver() {
        cleanUp();
        removeTournamentForUser();
    }

    public void quit() {
        cleanUp();
        removeTournamentForUser();
    }
    
    private void cleanUp() {
        if (futureTimeout != null && !futureTimeout.isDone()) {
            futureTimeout.cancel(true);
        }
    }
    
    private void removeTournamentForUser() {
        User user = UserManager.getInstance().getUser(userId);
        if (user != null) {
            user.removeTournament(playerId);
        }        
    }
    
}
