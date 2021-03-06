/*
 *  Copyright 2011 BetaSteward_at_googlemail.com. All rights reserved.
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

/*
 * DraftGrid.java
 *
 * Created on 7-Jan-2011, 6:23:39 PM
 */

package mage.client.cards;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mage.cards.CardDimensions;
import mage.cards.MageCard;
import mage.client.plugins.impl.Plugins;
import mage.client.util.CardViewRarityComparator;
import mage.client.util.Event;
import mage.client.util.Listener;
import mage.client.util.audio.AudioManager;
import mage.constants.Constants;
import mage.view.CardView;
import mage.view.CardsView;
import org.apache.log4j.Logger;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class DraftGrid extends javax.swing.JPanel implements MouseListener {

    private static final Logger logger = Logger.getLogger(DraftGrid.class);

    protected CardEventSource cardEventSource = new CardEventSource();
    protected BigCard bigCard;
    protected MageCard markedCard;
    protected boolean emptyGrid;

    /** Creates new form DraftGrid */
    public DraftGrid() {
        initComponents();
        markedCard = null;
        emptyGrid= true;
    }

    public void clear() {
        markedCard = null;
        this.clearCardEventListeners();
        for (Component comp: getComponents()) {
            if (comp instanceof Card || comp instanceof MageCard) {
                this.remove(comp);
            }
        }
    }

    public void loadBooster(CardsView booster, BigCard bigCard) {
        if (booster instanceof CardsView && booster.size() == 0) {
            emptyGrid = true;
        } else {
            if (!emptyGrid) {
                AudioManager.playOnDraftSelect();
            }
            emptyGrid = false;
        }
        this.bigCard = bigCard;
        this.removeAll();

        int maxRows = 4;

        int numColumns = 5;
        int curColumn = 0;
        int curRow = 0;
        int offsetX = 5;
        int offsetY = 3;

        CardDimensions cardDimension = null;
        int maxCards;
        double scale ;

        for (int i = 1; i < maxRows; i++) {
            scale = (double) (this.getHeight()/i) / Constants.FRAME_MAX_HEIGHT;
            cardDimension = new CardDimensions(scale);
            maxCards = this.getWidth() / (cardDimension.frameWidth + offsetX);
            if ((maxCards * i) >= booster.size()) {
                numColumns = booster.size() / i;
                if (booster.size() % i > 0) {
                    numColumns++;
                }
                break;
            }
        }

        if (cardDimension != null) {
            Rectangle rectangle = new Rectangle(cardDimension.frameWidth, cardDimension.frameHeight);
            Dimension dimension = new Dimension(cardDimension.frameWidth, cardDimension.frameHeight);

            List<CardView> sortedCards = new ArrayList<>(booster.values());
            Collections.sort(sortedCards, new CardViewRarityComparator());
            for (CardView card: sortedCards) {
                MageCard cardImg = Plugins.getInstance().getMageCard(card, bigCard, dimension, null, true);
                cardImg.addMouseListener(this);
                add(cardImg);
                cardImg.update(card);
                rectangle.setLocation(curColumn * (cardDimension.frameWidth + offsetX) + offsetX, curRow * (rectangle.height + offsetY) + offsetY);

                cardImg.setBounds(rectangle);
                cardImg.setCardBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                curColumn++;
                if (curColumn == numColumns) {
                    curColumn = 0;
                    curRow++;
                }
            }
            repaint();
        } else {
            logger.warn("Draft Grid - no possible fit of cards");
        }
    }

    public void addCardEventListener(Listener<Event> listener) {
        cardEventSource.addListener(listener);
    }

    public void clearCardEventListeners() {
        cardEventSource.clearListeners();
    }

    private void hidePopup() {
        Plugins.getInstance().getActionCallback().mouseExited(null, null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void mouseClicked(MouseEvent e) {
        if ((e.getClickCount() & 1) == 0 && (e.getClickCount() > 0)) { // double clicks and repeated double clicks
            if (e.getButton() == MouseEvent.BUTTON1) {
                Object obj = e.getSource();
                if (obj instanceof MageCard) {
                    this.cardEventSource.doubleClick(((MageCard)obj).getOriginal(), "pick-a-card");
                    this.hidePopup();
                    AudioManager.playOnDraftSelect();
                }
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) { // left or right click
            Object obj = e.getSource();
            if (obj instanceof MageCard) {
                if (this.markedCard != null) {
                    markedCard.setSelected(false);
                }
                this.cardEventSource.doubleClick(((MageCard)obj).getOriginal(), "mark-a-card");
                markedCard = ((MageCard)obj);
                markedCard.setSelected(true);
                repaint();
            }
        }

    }

    public boolean isEmptyGrid() {
        return emptyGrid;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
