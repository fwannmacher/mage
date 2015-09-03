/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.sets.planechase;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.common.EntersBattlefieldAbility;
import mage.abilities.effects.EntersBattlefieldEffect;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.permanent.Permanent;

/**
 *
 * @author cbt33
 */
public class IvyElemental extends CardImpl {

    public IvyElemental(UUID ownerId) {
        super(ownerId, 74, "Ivy Elemental", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{X}{G}");
        this.expansionSetCode = "HOP";
        this.subtype.add("Elemental");

        this.power = new MageInt(0);
        this.toughness = new MageInt(0);

        // Ivy Elemental enters the battlefield with X +1/+1 counters on it.
        this.addAbility(new EntersBattlefieldAbility(new IvyElementalEntersBattlefieldEffect(), "with X +1/+1 counters on it"));
    }

    public IvyElemental(final IvyElemental card) {
        super(card);
    }

    @Override
    public IvyElemental copy() {
        return new IvyElemental(this);
    }
}

class IvyElementalEntersBattlefieldEffect extends OneShotEffect {

    public IvyElementalEntersBattlefieldEffect() {
        super(Outcome.BoostCreature);
        staticText = "{this} enters the battlefield with X +1/+1 counters on it";
    }

    public IvyElementalEntersBattlefieldEffect(final IvyElementalEntersBattlefieldEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanent(source.getSourceId());
        if (permanent != null) {
            SpellAbility spellAbility = (SpellAbility) getValue(EntersBattlefieldEffect.SOURCE_CAST_SPELL_ABILITY);
            if (spellAbility != null
                    && spellAbility.getSourceId().equals(source.getSourceId())
                    && permanent.getZoneChangeCounter(game) - 1 == spellAbility.getSourceObjectZoneChangeCounter()) {
                if (spellAbility.getSourceId().equals(source.getSourceId())) { // put into play by normal cast
                    int amount = spellAbility.getManaCostsToPay().getX();
                    if (amount > 0) {
                        permanent.addCounters(CounterType.P1P1.createInstance(amount), game);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public IvyElementalEntersBattlefieldEffect copy() {
        return new IvyElementalEntersBattlefieldEffect(this);
    }

}
