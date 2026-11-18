
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
	
	// --화면 단독 테스트용 (추후 삭제)--
	public static void main(String[] args) {
	    new CatchGameView();
	}
	//---------------------------

    private JPanel contentPane;

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
        JLabel lblRound = new JLabel("Round 1/4");
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

        //제시어 ??? 변수로 변경 예정
        JLabel lblWord = new JLabel(
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

        //타이머 바 채움 (현재는 고정 상태)
        JLabel lblTime = new JLabel(
                loadImage("game-ui-timer-fill.png", 8, 278)
        );
        lblTime.setBounds(31, 105, 8, 278);
        boardPanel.add(lblTime);
        boardPanel.setComponentZOrder(lblTime, 0);

        //그림판 영역 (실제 그림 기능은 추후 구현)
        JPanel drawingPanel = new JPanel();
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

            toolPanel.add(btnTool);

            //도구 선택 및 색상 변경 기능은 추후 구현
        }

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
        JTextArea chatArea = new JTextArea();
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
        JTextField txtChat = new JTextField();
        txtChat.setBounds(12, 8, 170, 27);
        txtChat.setOpaque(false);
        txtChat.setBorder(null);
        lblChatInput.add(txtChat);

        //-----------임시버튼 추후삭제------------

        JButton btnRanking = new JButton("랭킹 보기");
        btnRanking.setBounds(850, 573, 210, 30);
        contentPane.add(btnRanking);

        btnRanking.addActionListener(e -> {

            CatchRankingView view = new CatchRankingView();
            setVisible(false);
            dispose();

        });

        //-----------------------------------

        setVisible(true);
    }
}
