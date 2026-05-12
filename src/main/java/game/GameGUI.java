package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GameGUI extends JFrame {
    private GameEngine gameEngine;
    private JPanel mainPanel;
    private JPanel aiAreaPanel;
    private JPanel playerAreaPanel;
    private JPanel aiPointPanel;
    private JPanel aiTrumpPanel;
    private JPanel playerPointPanel;
    private JPanel playerTrumpPanel;
    private JButton hitButton;
    private JButton standButton;
    private JButton endTurnButton;
    private JButton restartButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JButton resetZoomButton;
    private JLabel statusLabel;
    private JLabel aiInfoLabel;
    private JLabel playerInfoLabel;
    private JLabel deckInfoLabel;
    private JLayeredPane layeredPane;
    private double scale = 1.0;
    private static final int BASE_CARD_WIDTH = 60;
    private static final int BASE_CARD_HEIGHT = 85;
    private static final int CARD_GAP = 5;
    private Timer slideTimer;
    private int slideX;
    private int slideTargetX;
    private JPanel slidingCard;

    public GameGUI(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        setTitle("21点 - 纸牌对决");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        initUI();
        getRootPane().setGlassPane(new JLayeredPane());
        gameEngine.startGame();
        updateUI();
    }

    private int cardWidth() {
        return (int) (BASE_CARD_WIDTH * scale);
    }

    private int cardHeight() {
        return (int) (BASE_CARD_HEIGHT * scale);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(34, 85, 51));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        deckInfoLabel = new JLabel("牌库: ");
        deckInfoLabel.setForeground(Color.WHITE);
        deckInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        topBar.add(deckInfoLabel, BorderLayout.WEST);

        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        zoomPanel.setOpaque(false);
        zoomInButton = createSmallButton("放大+");
        zoomOutButton = createSmallButton("缩小-");
        resetZoomButton = createSmallButton("还原");
        zoomInButton.addActionListener(e -> { scale = Math.min(2.0, scale + 0.15); updateUI(); });
        zoomOutButton.addActionListener(e -> { scale = Math.max(0.5, scale - 0.15); updateUI(); });
        resetZoomButton.addActionListener(e -> { scale = 1.0; updateUI(); });
        zoomPanel.add(zoomInButton);
        zoomPanel.add(zoomOutButton);
        zoomPanel.add(resetZoomButton);
        topBar.add(zoomPanel, BorderLayout.EAST);
        mainPanel.add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        aiAreaPanel = new JPanel(new BorderLayout(5, 5));
        aiAreaPanel.setOpaque(false);
        aiInfoLabel = new JLabel("AI");
        aiInfoLabel.setForeground(Color.WHITE);
        aiInfoLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        aiInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        aiAreaPanel.add(aiInfoLabel, BorderLayout.NORTH);

        JPanel aiCardsPanel = new JPanel(new BorderLayout(5, 5));
        aiCardsPanel.setOpaque(false);
        JPanel aiPointLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        aiPointLabelPanel.setOpaque(false);
        JLabel aiPointTitle = new JLabel("点牌区:");
        aiPointTitle.setForeground(new Color(200, 200, 255));
        aiPointTitle.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        aiPointLabelPanel.add(aiPointTitle);
        aiCardsPanel.add(aiPointLabelPanel, BorderLayout.NORTH);

        aiPointPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, CARD_GAP, 3));
        aiPointPanel.setOpaque(false);
        aiCardsPanel.add(aiPointPanel, BorderLayout.CENTER);

        JPanel aiTrumpLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        aiTrumpLabelPanel.setOpaque(false);
        JLabel aiTrumpTitle = new JLabel("王牌组:");
        aiTrumpTitle.setForeground(new Color(255, 200, 200));
        aiTrumpTitle.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        aiTrumpLabelPanel.add(aiTrumpTitle);
        aiCardsPanel.add(aiTrumpLabelPanel, BorderLayout.SOUTH);

        aiTrumpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, CARD_GAP, 3));
        aiTrumpPanel.setOpaque(false);
        aiCardsPanel.add(aiTrumpPanel, BorderLayout.AFTER_LAST_LINE);

        aiAreaPanel.add(aiCardsPanel, BorderLayout.CENTER);
        centerPanel.add(aiAreaPanel, BorderLayout.NORTH);

        statusLabel = new JLabel("游戏开始");
        statusLabel.setForeground(Color.YELLOW);
        statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        centerPanel.add(statusLabel, BorderLayout.CENTER);

        playerAreaPanel = new JPanel(new BorderLayout(5, 5));
        playerAreaPanel.setOpaque(false);
        playerInfoLabel = new JLabel("玩家");
        playerInfoLabel.setForeground(Color.WHITE);
        playerInfoLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        playerInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerAreaPanel.add(playerInfoLabel, BorderLayout.NORTH);

        JPanel playerCardsPanel = new JPanel(new BorderLayout(5, 5));
        playerCardsPanel.setOpaque(false);
        JPanel playerPointLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerPointLabelPanel.setOpaque(false);
        JLabel playerPointTitle = new JLabel("点牌区:");
        playerPointTitle.setForeground(new Color(200, 200, 255));
        playerPointTitle.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        playerPointLabelPanel.add(playerPointTitle);
        playerCardsPanel.add(playerPointLabelPanel, BorderLayout.NORTH);

        playerPointPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, CARD_GAP, 3));
        playerPointPanel.setOpaque(false);
        playerCardsPanel.add(playerPointPanel, BorderLayout.CENTER);

        JPanel playerTrumpLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerTrumpLabelPanel.setOpaque(false);
        JLabel playerTrumpTitle = new JLabel("王牌组:");
        playerTrumpTitle.setForeground(new Color(255, 200, 200));
        playerTrumpTitle.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        playerTrumpLabelPanel.add(playerTrumpTitle);
        playerCardsPanel.add(playerTrumpLabelPanel, BorderLayout.SOUTH);

        playerTrumpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, CARD_GAP, 3));
        playerTrumpPanel.setOpaque(false);
        playerCardsPanel.add(playerTrumpPanel, BorderLayout.AFTER_LAST_LINE);

        playerAreaPanel.add(playerCardsPanel, BorderLayout.CENTER);
        centerPanel.add(playerAreaPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setOpaque(false);

        hitButton = createGameButton("要牌", new Color(46, 139, 87));
        standButton = createGameButton("停牌", new Color(178, 34, 34));
        endTurnButton = createGameButton("结束回合", new Color(70, 130, 180));
        restartButton = createGameButton("重新开始", new Color(128, 128, 128));

        hitButton.addActionListener(e -> {
            if (!gameEngine.isGameActive()) return;
            if (!gameEngine.getCurrentPlayer().isHuman()) return;
            Player player = gameEngine.getCurrentPlayer();
            int prevCount = player.getPointCards().size();
            gameEngine.playerHit();
            if (player.getPointCards().size() > prevCount) {
                NumberCard newCard = player.getPointCards().get(player.getPointCards().size() - 1);
                animateCardSlide(newCard, true);
            } else {
                updateUI();
            }
        });

        standButton.addActionListener(e -> {
            if (!gameEngine.isGameActive()) return;
            if (!gameEngine.getCurrentPlayer().isHuman()) return;
            gameEngine.playerStand();
            updateUI();
        });

        endTurnButton.addActionListener(e -> {
            if (!gameEngine.isGameActive()) return;
            if (!gameEngine.getCurrentPlayer().isHuman()) return;
            gameEngine.endTurn();
            updateUI();
        });

        restartButton.addActionListener(e -> {
            gameEngine.startGame();
            updateUI();
        });

        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(endTurnButton);
        buttonPanel.add(restartButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        layeredPane.setPreferredSize(mainPanel.getPreferredSize());

        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createGameButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(100, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        btn.setMargin(new Insets(2, 6, 2, 6));
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel createNumberCardPanel(NumberCard card, boolean showFace) {
        int w = cardWidth();
        int h = cardHeight();
        JPanel panel = new JPanel(new BorderLayout(2, 2));
        panel.setPreferredSize(new Dimension(w, h));
        panel.setMaximumSize(new Dimension(w, h));

        if (showFace) {
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 200), 2, true));

            JLabel valueLabel = new JLabel(String.valueOf(card.getPointValue()), SwingConstants.CENTER);
            valueLabel.setFont(new Font("Arial", Font.BOLD, (int) (22 * scale)));
            valueLabel.setForeground(new Color(30, 30, 150));
            panel.add(valueLabel, BorderLayout.CENTER);

            JLabel typeLabel = new JLabel("点", SwingConstants.CENTER);
            typeLabel.setFont(new Font("微软雅黑", Font.PLAIN, (int) (9 * scale)));
            typeLabel.setForeground(Color.GRAY);
            panel.add(typeLabel, BorderLayout.SOUTH);
        } else {
            panel.setBackground(new Color(60, 60, 120));
            panel.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 80), 2, true));
            JLabel backLabel = new JLabel("?", SwingConstants.CENTER);
            backLabel.setFont(new Font("Arial", Font.BOLD, (int) (20 * scale)));
            backLabel.setForeground(new Color(150, 150, 200));
            panel.add(backLabel, BorderLayout.CENTER);
        }
        return panel;
    }

    private JPanel createSpecialCardPanel(SpecialCard card, boolean clickable, int cardIndex) {
        int w = cardWidth();
        int h = cardHeight();
        JPanel panel = new JPanel(new BorderLayout(2, 1));
        panel.setPreferredSize(new Dimension(w, h));
        panel.setMaximumSize(new Dimension(w, h));

        panel.setBackground(new Color(255, 248, 230));
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 150, 50), 2, true));

        JLabel nameLabel = new JLabel(card.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, (int) (9 * scale)));
        nameLabel.setForeground(new Color(150, 50, 0));
        panel.add(nameLabel, BorderLayout.NORTH);

        String desc = card.getDescription();
        if (desc.length() > 12) {
            desc = desc.substring(0, 12) + "..";
        }
        JLabel descLabel = new JLabel("<html><center>" + desc + "</center></html>", SwingConstants.CENTER);
        descLabel.setFont(new Font("微软雅黑", Font.PLAIN, (int) (7 * scale)));
        descLabel.setForeground(Color.DARK_GRAY);
        panel.add(descLabel, BorderLayout.CENTER);

        JLabel suitLabel = new JLabel(card.getDisplayText(), SwingConstants.CENTER);
        suitLabel.setFont(new Font("微软雅黑", Font.PLAIN, (int) (8 * scale)));
        suitLabel.setForeground(new Color(100, 100, 100));
        panel.add(suitLabel, BorderLayout.SOUTH);

        if (clickable) {
            panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!gameEngine.isGameActive()) return;
                    if (!gameEngine.getCurrentPlayer().isHuman()) return;
                    int result = JOptionPane.showConfirmDialog(
                        GameGUI.this,
                        "使用【" + card.getName() + "】？\n效果: " + card.getDescription(),
                        "确认使用王牌",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (result == JOptionPane.YES_OPTION) {
                        gameEngine.playSpecialCard(cardIndex);
                        updateUI();
                    }
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    panel.setBorder(BorderFactory.createLineBorder(Color.RED, 3, true));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    panel.setBorder(BorderFactory.createLineBorder(new Color(200, 150, 50), 2, true));
                }
            });
        }
        return panel;
    }

    private JPanel createActiveEffectPanel(SpecialCard card) {
        int w = (int) (cardWidth() * 0.7);
        int h = (int) (cardHeight() * 0.5);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(w, h));
        panel.setBackground(new Color(220, 255, 220));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 0), 1, true));

        JLabel label = new JLabel(card.getName(), SwingConstants.CENTER);
        label.setFont(new Font("微软雅黑", Font.PLAIN, (int) (8 * scale)));
        label.setForeground(new Color(0, 100, 0));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    public void updateUI() {
        Player player = gameEngine.getPlayers().get(0);
        Player ai = gameEngine.getPlayers().get(1);
        boolean isPlayerTurn = gameEngine.isGameActive() && gameEngine.getCurrentPlayer().isHuman();

        aiInfoLabel.setText(String.format("<html><b>AI</b> | 点数: %d | 目标: %d | 赌注: %d | 王牌: %d%s%s</html>",
            ai.getTotalPoints(), ai.getTargetScore(), ai.getEffectiveBet(), ai.getTrumpCardCount(),
            ai.isBusted() ? " | 💥爆牌" : "",
            ai.hasStood() ? " | ✋停牌" : ""));

        playerInfoLabel.setText(String.format("<html><b>玩家</b> | 点数: %d | 目标: %d | 赌注: %d | 王牌: %d%s%s%s</html>",
            player.getTotalPoints(), player.getTargetScore(), player.getEffectiveBet(), player.getTrumpCardCount(),
            player.isBusted() ? " | 💥爆牌" : "",
            player.hasStood() ? " | ✋停牌" : "",
            isPlayerTurn ? " | 🎯你的回合" : ""));

        deckInfoLabel.setText(String.format("点数牌库: %d | 功能牌库: %d | 回合: %d",
            gameEngine.getDeck().pointDeckSize(),
            gameEngine.getDeck().specialDeckSize(),
            gameEngine.getRoundCount()));

        aiPointPanel.removeAll();
        for (int i = 0; i < ai.getPointCards().size(); i++) {
            NumberCard card = ai.getPointCards().get(i);
            boolean showFace;
            if (!gameEngine.isGameActive()) {
                showFace = true;
            } else if (ai.isHoleCard(i)) {
                showFace = false;
            } else {
                showFace = true;
            }
            aiPointPanel.add(createNumberCardPanel(card, showFace));
        }

        aiTrumpPanel.removeAll();
        for (SpecialCard card : ai.getTrumpCards()) {
            int w = (int) (cardWidth() * 0.6);
            int h = (int) (cardHeight() * 0.6);
            JPanel panel = new JPanel(new BorderLayout());
            panel.setPreferredSize(new Dimension(w, h));
            panel.setBackground(new Color(60, 60, 100));
            panel.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 70), 1, true));
            JLabel l = new JLabel("?", SwingConstants.CENTER);
            l.setFont(new Font("Arial", Font.BOLD, (int) (12 * scale)));
            l.setForeground(new Color(150, 150, 200));
            panel.add(l, BorderLayout.CENTER);
            aiTrumpPanel.add(panel);
        }

        if (!ai.getActiveEffects().isEmpty()) {
            JPanel effectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
            effectPanel.setOpaque(false);
            for (SpecialCard effect : ai.getActiveEffects()) {
                effectPanel.add(createActiveEffectPanel(effect));
            }
            aiTrumpPanel.add(effectPanel);
        }

        playerPointPanel.removeAll();
        for (NumberCard card : player.getPointCards()) {
            playerPointPanel.add(createNumberCardPanel(card, true));
        }

        playerTrumpPanel.removeAll();
        for (int i = 0; i < player.getTrumpCards().size(); i++) {
            SpecialCard card = player.getTrumpCards().get(i);
            playerTrumpPanel.add(createSpecialCardPanel(card, isPlayerTurn, i));
        }

        if (!player.getActiveEffects().isEmpty()) {
            JPanel effectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
            effectPanel.setOpaque(false);
            for (SpecialCard effect : player.getActiveEffects()) {
                effectPanel.add(createActiveEffectPanel(effect));
            }
            playerTrumpPanel.add(effectPanel);
        }

        statusLabel.setText(gameEngine.getLastMessage());

        hitButton.setEnabled(isPlayerTurn && !player.isDrawBlocked());
        standButton.setEnabled(isPlayerTurn);
        endTurnButton.setEnabled(isPlayerTurn);

        if (!gameEngine.isGameActive()) {
            Player winner = gameEngine.getWinner();
            if (winner != null) {
                statusLabel.setText("🏆 " + gameEngine.getLastMessage());
            }
            hitButton.setEnabled(false);
            standButton.setEnabled(false);
            endTurnButton.setEnabled(false);
            showGameResult();
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showGameResult() {
        Player player = gameEngine.getPlayers().get(0);
        Player ai = gameEngine.getPlayers().get(1);
        Player winner = gameEngine.getWinner();

        StringBuilder sb = new StringBuilder();
        sb.append("<html><div style='text-align: center; font-family: 微软雅黑;'>");
        if (winner != null) {
            sb.append("<h2>🏆 ").append(winner.getName()).append(" 获胜！</h2>");
        } else {
            sb.append("<h2>平局！</h2>");
        }
        sb.append("<hr>");
        sb.append("<p><b>玩家</b>: 点数 ").append(player.getTotalPoints())
          .append(" / 目标 ").append(player.getTargetScore())
          .append(" | 赌注 ").append(player.getEffectiveBet());
        if (player.isBusted()) sb.append(" | 💥爆牌");
        sb.append("</p>");
        sb.append("<p><b>AI</b>: 点数 ").append(ai.getTotalPoints())
          .append(" / 目标 ").append(ai.getTargetScore())
          .append(" | 赌注 ").append(ai.getEffectiveBet());
        if (ai.isBusted()) sb.append(" | 💥爆牌");
        sb.append("</p>");
        sb.append("</div></html>");

        JOptionPane.showMessageDialog(this, sb.toString(), "游戏结束", JOptionPane.INFORMATION_MESSAGE);
    }

    private void animateCardSlide(NumberCard card, boolean isPlayer) {
        if (slideTimer != null && slideTimer.isRunning()) {
            slideTimer.stop();
        }

        JPanel cardPanel = createNumberCardPanel(card, true);
        int w = cardWidth();
        int h = cardHeight();
        cardPanel.setSize(w, h);
        cardPanel.setOpaque(true);

        JLayeredPane glassPane = (JLayeredPane) getRootPane().getGlassPane();
        glassPane.setLayout(null);
        glassPane.setVisible(true);
        glassPane.removeAll();
        glassPane.add(cardPanel);
        glassPane.setLayer(cardPanel, JLayeredPane.PALETTE_LAYER);

        int frameWidth = getWidth();
        int startX = frameWidth;
        int endX = frameWidth / 2 - w / 2;
        int startY;
        if (isPlayer) {
            startY = getHeight() - h - 120;
        } else {
            startY = 80;
        }

        cardPanel.setLocation(startX, startY);
        slideX = startX;

        slideTimer = new Timer(12, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slideX -= 25;
                if (slideX <= endX) {
                    slideX = endX;
                    slideTimer.stop();
                    glassPane.removeAll();
                    glassPane.setVisible(false);
                    updateUI();
                }
                cardPanel.setLocation(slideX, startY);
                glassPane.repaint();
            }
        });
        slideTimer.start();
    }
}
