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
package mage.sets.bornofthegods;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.keyword.InspiredAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.counters.CounterType;

/**
 *
 * @author LevelX2
 */
public class PheresBandTromper extends CardImpl {

    public PheresBandTromper(UUID ownerId) {
        super(ownerId, 134, "Pheres-Band Tromper", Rarity.COMMON, new CardType[]{CardType.CREATURE}, "{3}{G}");
        this.expansionSetCode = "BNG";
        this.subtype.add("Centaur");
        this.subtype.add("Warrior");

        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // <i>Inspired</i> - Whenever Pheres-Band Tromper becomes untapped, put a +1/+1 counter on it.
        this.addAbility(new InspiredAbility(new AddCountersSourceEffect(CounterType.P1P1.createInstance())));
    }

    public PheresBandTromper(final PheresBandTromper card) {
        super(card);
    }

    @Override
    public PheresBandTromper copy() {
        return new PheresBandTromper(this);
    }
}
