import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;

public class CatchGameView extends JFrame {
	
	//화면 단독 테스트용 (추후 삭제)
	public static void main(String[] args) {
	    java.awt.EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                new CatchGameView();
            }
        });
	}

    private JPanel contentPane;
    private JTextArea chatArea;
    private JTextField txtChat;
    private final java.util.Map<Integer, String> chatNames = new java.util.HashMap<>();
    // 소켓 연결 전 로컬 확인용
    private java.util.function.Consumer<String> chatSender =
            new java.util.function.Consumer<String>() {
                @Override public void accept(String message) {
                    localChatMessage(message);
                }
            };

    public void setChatMessageSender(java.util.function.Consumer<String> sender) {
        if (sender == null) throw new IllegalArgumentException("송신 함수를 지정해주세요.");
        chatSender = sender;
    }

    // /PLAYERS 수신 시 ID와 닉네임을 등록한다.
    public void setChatPlayerName(int id, String name) {
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            javax.swing.SwingUtilities.invokeLater(() -> setChatPlayerName(id, name));
            return;
        }
        chatNames.put(id, name);
    }

    // ListenNetwork에서 호출할 수 있도록 UI 변경은 EDT에 전달
    public void AppendText(String message) {
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            javax.swing.SwingUtilities.invokeLater(() -> AppendText(message));
            return;
        }
        chatArea.append(message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    // 서버 수신 스레드에서 /CHAT_MSG 보낸ID 내용 한 줄을 넘긴다.
    public boolean receiveChatMessage(String message) {
        if (message == null) return false;
        String[] parts = message.trim().split("\\s+", 3);
        if (parts.length != 3 || !parts[0].equals("/CHAT_MSG")) return false;
        try {
            final int id = Integer.parseInt(parts[1]);
            if (id < 1 || parts[2].isEmpty()) return false;
            final String body = parts[2];
            javax.swing.SwingUtilities.invokeLater(() ->
                    AppendText(chatNames.getOrDefault(id, "Player " + id) + " : " + body));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 서버가 없는 현재 단계에는 채팅을 자신에게 돌려주는 방식으로 테스트
    private void localChatMessage(String command) {
        if (command.startsWith("/CHAT ")) {
            receiveChatMessage("/CHAT_MSG 1 " + command.substring(6));
        } else if (command.startsWith("/ANSWER ")) {
            AppendText("[정답 제출] " + command.substring(8) + " (서버 판정 대기)");
        }
    }

    private class ChatAction implements java.awt.event.ActionListener {
        @Override public void actionPerformed(java.awt.event.ActionEvent e) {
            String input = txtChat.getText().trim();
            if (input.isEmpty()) return;
            if (input.indexOf('\n') >= 0 || input.indexOf('\r') >= 0) {
                AppendText("[안내] 한 줄씩 입력해주세요.");
                return;
            }
            String command;
            if (input.equals("/정답")) {
                AppendText("[안내] /정답 사과 처럼 입력해주세요.");
                return;
            } else if (input.startsWith("/정답 ")) {
                String answer = input.substring(4).trim();
                if (answer.isEmpty()) return;
                if (!roundRunning) {
                    AppendText("[안내] 라운드 진행 중에 정답을 제출해주세요.");
                    return;
                }
                command = "/ANSWER " + answer;
            } else {
                command = "/CHAT " + input;
            }
            try {
                chatSender.accept(command);
                txtChat.setText("");
                txtChat.requestFocusInWindow();
            } catch (RuntimeException error) {
                // 전송 실패 시 입력을 보존하여 다시 보낼 수 있게 한다.
                AppendText("[전송 실패] 연결 상태를 확인해주세요.");
            }
        }
    }

    private static final int ROUND_SECONDS = 60;
    private static final int TOTAL_ROUNDS = 4;
    private int roundNumber;
    private int remainingSeconds = ROUND_SECONDS;
    private long roundDeadline;
    private boolean roundRunning;
    private String roundAnswer;
    private JLabel lblRound;
    private JLabel lblWord;
    private JLabel lblSeconds;
    private javax.swing.Timer roundTimer;
    private javax.swing.Timer nextRoundTimer;

    private void startRound() {
        roundNumber++;
        roundRunning = true;
        roundAnswer = null;
        remainingSeconds = ROUND_SECONDS;
        lblRound.setText("Round " + roundNumber + "/" + TOTAL_ROUNDS);
        lblWord.setText("제시어 : ???");
        drawingPanel.receiveMessage("/CLEAR");
        drawingPanel.setDrawingAllowed(true);
        roundDeadline = System.nanoTime() + ROUND_SECONDS * 1_000_000_000L;
        updateTimeDisplay();
        roundTimer.start();
    }

    // 향후 /WORD 또는 /TIMEOUT의 정답을 전달한다. 서버 연결 전에는 정답 미설정
    public void setRoundAnswer(String answer) {
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            javax.swing.SwingUtilities.invokeLater(() -> setRoundAnswer(answer));
            return;
        }
        roundAnswer = answer;
    }

    // 정답 판정 후 호출하면 시간이 남아 있어도 라운드를 한 번만 종료한다
    public void finishRound(String answer) {
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            javax.swing.SwingUtilities.invokeLater(() -> finishRound(answer));
            return;
        }
        if (!roundRunning) return;
        roundRunning = false;
        roundTimer.stop();
        drawingPanel.setDrawingAllowed(false);
        lblWord.setText(answer == null || answer.trim().isEmpty()
                ? "시간 종료" : "정답 : " + answer);
        nextRoundTimer = new javax.swing.Timer(2000, new NextRoundAction());
        nextRoundTimer.setRepeats(false);
        nextRoundTimer.start();
    }
    
    // 대기실에서 사용한 Timer + ActionListener 방식을 게임 타이머에도 적용
    private class RoundTimerAction implements java.awt.event.ActionListener {
        @Override public void actionPerformed(java.awt.event.ActionEvent e) {
            long remaining = roundDeadline - System.nanoTime();
            remainingSeconds = (int) Math.max(0,
                    (remaining + 999_999_999L) / 1_000_000_000L);
            updateTimeDisplay();
            if (remainingSeconds == 0) finishRound(roundAnswer);
        }
    }

    private class NextRoundAction implements java.awt.event.ActionListener {
        @Override public void actionPerformed(java.awt.event.ActionEvent e) {
            if (roundNumber < TOTAL_ROUNDS) {
                startRound();
            } else {
                new CatchRankingView();
                dispose();
            }
        }
    }

    private void updateTimeDisplay() {
        lblSeconds.setText(remainingSeconds + "초");
        lblSeconds.setForeground(remainingSeconds <= 10
                ? new Color(210, 48, 42) : new Color(76, 47, 39));
        contentPane.repaint();
    }

    @Override
    public void dispose() {
        if (roundTimer != null) roundTimer.stop();
        if (nextRoundTimer != null) nextRoundTimer.stop();
        roundRunning = false;
        super.dispose();
    }

    private CatchDrawingPanel drawingPanel;
    private JButton[] toolButtons = new JButton[8];
    private int selectedColorTool = 2;

    private void updateToolSelection(int activeTool) {
        for (int i = 0; i < toolButtons.length; i++) {
            JButton button = toolButtons[i];
            if (button == null) continue;
            boolean active = i == activeTool || i == selectedColorTool;
            button.setBorder(BorderFactory.createLineBorder(
                    new Color(139, 90, 55), 2));
            button.setBorderPainted(active);
            button.repaint();
        }
    }

    // 기존 서버 송신 함수와 연결한다. 실제 write/flush는 EDT 밖에서 처리
    public void setDrawingMessageSender(java.util.function.Consumer<String> sender) {
        drawingPanel.setMessageSender(sender);
    }

    public boolean receiveDrawingMessage(String message) {
        return drawingPanel.receiveMessage(message);
    }

    // /ROUND_START의 출제자와 본인의 ID를 비교한 결과를 전달한다.
    public void setDrawingAllowed(boolean allowed) {
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            javax.swing.SwingUtilities.invokeLater(() -> setDrawingAllowed(allowed));
            return;
        }
        drawingPanel.setDrawingAllowed(allowed);
    }

    //이미지 크기 조절
    private ImageIcon loadImage(String name, int width, int height) {
        ImageIcon icon = new ImageIcon(
                getClass().getResource("/images/" + name)
        );

        Image image = icon.getImage().getScaledInstance(
                width, height, Image.SCALE_REPLICATE
        );

        return new ImageIcon(image);
    }

    public CatchGameView() {

        setTitle("캐치캐치 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 650);
        setLocationRelativeTo(null);

        //전체 배경
        contentPane = new JPanel() {
            private Image background = new ImageIcon(
                    getClass().getResource("/images/gamepanel.png")
            ).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0,
                        getWidth(), getHeight(), this);
            }
        };

        contentPane.setLayout(null);
        setContentPane(contentPane);

        //게임 제목
        JLabel lblTitle = new JLabel(
                loadImage("캐치캐치로고.png", 160, 54)
        );
        lblTitle.setBounds(32, 42, 160, 54);
        contentPane.add(lblTitle);

        //라운드 표시, 라운드는 플레이어 수만큼 진행 추후수정
        lblRound = new JLabel("Round 1/4");
        lblRound.setFont(new Font("맑은 고딕", Font.BOLD, 19));
        lblRound.setHorizontalAlignment(SwingConstants.CENTER);
        lblRound.setForeground(new Color(80, 55, 48));
        lblRound.setBounds(32, 108, 185, 35);
        contentPane.add(lblRound);

        //참가자 목록
        JPanel playerPanel = new JPanel() {
            private Image background = loadImage(
                    "game-ui-players.png", 205, 395
            ).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0,
                        getWidth(), getHeight(), this);
            }
        };

        playerPanel.setLayout(null);
        playerPanel.setBounds(28, 168, 205, 395);
        contentPane.add(playerPanel);

      //플레이어수 변수로 관리
        for (int i = 0; i < 4; i++) {

            //첫 번째 플레이어는 임시로 활성화 디자인
            String rowImage = (i == 0)
                    ? "game-ui-player-active.png"
                    : "game-ui-player-row.png";

            //참가자 목록 배경
            JLabel player = new JLabel(
                    loadImage(rowImage, 160, 64)
            );

            player.setLayout(null);
            player.setBounds(23, 65 + i * 76, 160, 64);
            playerPanel.add(player);

            //프로필 이미지 (임시로 1~4번 순서대로 표시)
            JLabel lblProfile = new JLabel(
                    loadImage("lobby-chracter" + (i + 1) + ".png", 35, 45)
            );

            lblProfile.setBounds(10, 9, 35, 45);
            player.add(lblProfile);

            //닉네임 및 점수
            JLabel lblPlayerInfo = new JLabel(
                    "<html>Player " + (i + 1) + "<br>점수 0</html>"
            );

            lblPlayerInfo.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            lblPlayerInfo.setForeground(new Color(75, 48, 40));
            lblPlayerInfo.setBounds(53, 10, 100, 45);
            player.add(lblPlayerInfo);
        }

        //그림판 외부 영역
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(null);
        boardPanel.setBounds(250, 86, 580, 488);
        boardPanel.setBackground(new Color(247, 231, 202));
        boardPanel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(174, 126, 87), 3)
        );
        contentPane.add(boardPanel);

        //제시어 ??? 이후에 변수로 변경 예정
        lblWord = new JLabel(
                "제시어 : ???",
                loadImage("game-ui-round.png", 260, 52),
                SwingConstants.CENTER
        );

        lblWord.setHorizontalTextPosition(SwingConstants.CENTER);
        lblWord.setVerticalTextPosition(SwingConstants.CENTER);
        lblWord.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        lblWord.setForeground(new Color(76, 47, 39));
        lblWord.setBounds(155, 13, 270, 52);
        boardPanel.add(lblWord);

        //타이머 바 배경
        JLabel lblTimerTrack = new JLabel(
                loadImage("game-ui-timer-track.png", 18, 300)
        );
        lblTimerTrack.setBounds(26, 94, 18, 300);
        boardPanel.add(lblTimerTrack);

        // 아래쪽을 기준으로 남은 시간만큼 채워지는 세로 막대.
        JPanel timeFill = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int height = (int) Math.round(278.0 * remainingSeconds / ROUND_SECONDS);
                g.setColor(remainingSeconds <= 10
                        ? new Color(210, 48, 42) : new Color(165, 59, 100));
                g.fillRect(0, 278 - height, 8, height);
            }
        };
        timeFill.setOpaque(false);
        timeFill.setBounds(31, 105, 8, 278);
        boardPanel.add(timeFill);
        boardPanel.setComponentZOrder(timeFill, 0);

        lblSeconds = new JLabel("60초", SwingConstants.CENTER);
        lblSeconds.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        lblSeconds.setBounds(9, 66, 52, 24);
        boardPanel.add(lblSeconds);

        //수업의 마우스 이벤트와 paintComponent를 활용하는 그림판
        drawingPanel = new CatchDrawingPanel();
        drawingPanel.setBounds(58, 82, 503, 330);
        drawingPanel.setBackground(new Color(255, 250, 239));
        drawingPanel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(218, 188, 149), 2)
        );
        boardPanel.add(drawingPanel);

        //그림 도구
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(null);
        toolPanel.setBounds(95, 425, 390, 50);
        toolPanel.setBackground(new Color(226, 192, 151));
        toolPanel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(163, 115, 79), 2)
        );
        boardPanel.add(toolPanel);

        //그림 도구 버튼 이미지 목록
        String[] toolImages = {
                "game-ui-pencil.png",
                "game-ui-eraser.png",
                "game-ui-color-black.png",
                "game-ui-color-pink.png",
                "game-ui-color-red.png",
                "game-ui-color-orange.png",
                "game-ui-color-green.png",
                "game-ui-color-blue.png"
        };

        //하단 도구 8개를 각각 JButton으로 생성
        for (int i = 0; i < toolImages.length; i++) {

            JButton btnTool = new JButton(
                    loadImage(toolImages[i], 36, 36)
            );

            btnTool.setBounds(12 + i * 47, 7, 36, 36);
            btnTool.setContentAreaFilled(false);
            btnTool.setBorderPainted(false);
            btnTool.setFocusPainted(false);

            toolButtons[i] = btnTool;
            toolPanel.add(btnTool);

            final int toolNumber = i;
            String[] toolNames = {"연필", "지우개", "검정", "분홍", "빨강", "주황", "초록", "파랑"};
            btnTool.setToolTipText(toolNames[i]);
            btnTool.addActionListener(new java.awt.event.ActionListener() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (toolNumber == 0) drawingPanel.selectPencil();
                    else if (toolNumber == 1) drawingPanel.selectEraser();
                    else {
                        drawingPanel.selectColor(toolNumber - 1);
                        selectedColorTool = toolNumber;
                    }
                    updateToolSelection(toolNumber == 1 ? 1 : 0);
                }
            });
        }

        updateToolSelection(0);

        // /CLEAR 명령을 보내는 전체 지우기 버튼.
        final ImageIcon clearBox = loadImage("game-ui-round.png", 112, 52);
        JButton btnClear = new JButton("전체 지우기") {
            @Override
            protected void paintComponent(Graphics g) {
                // 제시어 박스와 동일한 이미지, 글꼴, 색상을 직접 그린다.
                clearBox.paintIcon(this, g, 0, 0);
                g.setFont(lblWord.getFont());
                g.setColor(lblWord.getForeground());
                java.awt.FontMetrics metrics = g.getFontMetrics();
                g.drawString(getText(),
                        (getWidth() - metrics.stringWidth(getText())) / 2,
                        (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent());
            }
        };
        btnClear.setContentAreaFilled(false);
        btnClear.setBorderPainted(false);
        btnClear.setFocusPainted(false);
        btnClear.setBounds(449, 13, 112, 52);
        btnClear.addActionListener(e -> drawingPanel.requestClear());
        boardPanel.add(btnClear);

        //채팅창 배경
        JPanel chatPanel = new JPanel() {
            private Image background = loadImage(
                    "game-ui-chat.png", 210, 478
            ).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0,
                        getWidth(), getHeight(), this);
            }
        };

        chatPanel.setLayout(null);
        chatPanel.setBounds(850, 87, 210, 478);
        contentPane.add(chatPanel);

        //채팅 내역
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBounds(12, 16, 186, 382);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        chatPanel.add(scrollPane);

        //채팅 입력 이미지
        JLabel lblChatInput = new JLabel(
                loadImage("game-ui-chat-input.png", 194, 43)
        );
        lblChatInput.setLayout(null);
        lblChatInput.setBounds(8, 430, 194, 43);
        chatPanel.add(lblChatInput);

        //채팅 입력
        txtChat = new JTextField();
        txtChat.setBounds(12, 8, 170, 27);
        txtChat.setOpaque(false);
        txtChat.setBorder(null);
        lblChatInput.add(txtChat);
        chatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        chatArea.setForeground(new Color(76, 47, 39));
        txtChat.setFont(chatArea.getFont());
        txtChat.setForeground(chatArea.getForeground());
        txtChat.setToolTipText("Enter로 채팅 / 정답은 /정답 단어");
        txtChat.addActionListener(new ChatAction());
        chatNames.put(1, "나");
        AppendText("[안내] Enter로 채팅을 보낼 수 있어요.");
        AppendText("[안내] 정답은 /정답 사과 처럼 입력해주세요.");
        AppendText("[안내] 현재는 서버 연결 전 로컬 채팅입니다.");

        //임시 버튼 추후 삭제 예정

        JButton btnRanking = new JButton("랭킹 보기");
        btnRanking.setBounds(850, 573, 210, 30);
        contentPane.add(btnRanking);

        btnRanking.addActionListener(e -> {

            CatchRankingView view = new CatchRankingView();
            setVisible(false);
            dispose();

        });

        roundTimer = new javax.swing.Timer(200, new RoundTimerAction());
        startRound();
        setVisible(true);
    }
}
