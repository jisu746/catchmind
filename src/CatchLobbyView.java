
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.SwingConstants;

import java.io.IOException;
import javax.swing.JOptionPane;

public class CatchLobbyView extends JFrame {

	// --화면 단독 테스트용 (추후 삭제)--
	public static void main(String[] args) {
	    new CatchLobbyView("테스트유저", 1, "127.0.0.1", "9999");
	}
	//----------------------------
	
    private JPanel contentPane;
    private JButton btnReady;
    private JButton btnLeave;

    private JLabel[] lblReady = new JLabel[4];
    //플레이어마다 닉네임, 캐릭터 표시
    private JLabel[] lblPlayers = new JLabel[4];
    private JLabel[] lblCharacters = new JLabel[4];
    
    private boolean ready = false;
    //전원 ready후 카운트다운 디자인용
    private Timer countdownTimer;
    private JLabel lblCountdown;
    private int count = 3;

    private String username;
    private int selectedProfile;
    private String ip_addr;
    private String port_no;

    public CatchLobbyView(String username, int selectedProfile,
            String ip_addr, String port_no) {

        this.username = username;
        this.selectedProfile = selectedProfile;
        this.ip_addr = ip_addr;
        this.port_no = port_no;

        setTitle("캐치캐치 게임 대기실");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 550);
        setLocationRelativeTo(null);

        //대기실 배경 이미지
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

        //게임 타이틀
        ImageIcon titleIcon = new ImageIcon(
                getClass().getResource("/images/gamelobby.png")
        );

        Image titleImage = titleIcon.getImage().getScaledInstance(
                260, 103, Image.SCALE_REPLICATE
        );

        JLabel lblTitle = new JLabel(new ImageIcon(titleImage));
        lblTitle.setBounds(295, 20, 260, 103);
        contentPane.add(lblTitle);

        //캐릭터 선택칸 이미지
        ImageIcon boxIcon = new ImageIcon(
                getClass().getResource("/images/lobby-caracter_box.png")
        );

        Image boxImage = boxIcon.getImage().getScaledInstance(
                160, 160, Image.SCALE_REPLICATE
        );

        //플레이어 4명 표시
        for (int i = 0; i < 4; i++) {

            int x = 48 + i * 195;
            int y = 165;

            //캐릭터 선택칸 배경
            JLabel lblBox = new JLabel(new ImageIcon(boxImage));
            lblBox.setBounds(x, y, 160, 160);
            contentPane.add(lblBox);

            //캐릭터 이미지
            ImageIcon characterIcon = new ImageIcon(
                    getClass().getResource(
                            "/images/lobby-chracter" + (i + 1) + ".png")
            );

            Image characterImage = characterIcon.getImage().getScaledInstance(
                    75, 110, Image.SCALE_REPLICATE
            );

            JLabel lblCharacter = new JLabel(new ImageIcon(characterImage));
            lblCharacters[i] = lblCharacter;
            
            lblCharacter.setBounds(x + 42, y + 25, 75, 110);
            contentPane.add(lblCharacter);

            //캐릭터 이미지를 선택칸 배경보다 앞에 표시
            contentPane.setComponentZOrder(lblCharacter, 0);

            //참여한 유저 이름, 처음엔 빈 이름으로 설정
            JLabel player = new JLabel("");
            player.setHorizontalAlignment(JLabel.CENTER);
            player.setFont(new Font("맑은 고딕", Font.BOLD, 17));
            player.setForeground(new Color(84, 48, 43));
            player.setBounds(x, 330, 160, 30);
            contentPane.add(player);
            //서버값 도착하면 갱신
            lblPlayers[i] = player;

            lblReady[i] = new JLabel("Wait..");
            lblReady[i].setHorizontalAlignment(JLabel.CENTER);
            lblReady[i].setFont(new Font("맑은 고딕", Font.BOLD, 17));
            lblReady[i].setForeground(new Color(163, 78, 154));
            lblReady[i].setBounds(x, 365, 160, 30);
            contentPane.add(lblReady[i]);
            
            //참가하지 않은 플레이어 칸은 비워둠
            lblCharacters[i].setVisible(false);
            lblReady[i].setVisible(false);
        }

        //버튼 배경 이미지
        ImageIcon buttonIcon = new ImageIcon(
                getClass().getResource("/images/lobby-button.png")
        );

        //서버 나가기 글자 이미지
        ImageIcon leaveIcon = new ImageIcon(
                getClass().getResource("/images/서버 나가기.png")
        );

        //준비 완료 글자 이미지
        ImageIcon readyIcon = new ImageIcon(
                getClass().getResource("/images/준비 완료.png")
        );

        //서버 나가기 버튼
        btnLeave = new JButton("서버 나가기") {
            @Override
            protected void paintComponent(Graphics g) {

                //버튼 배경
                g.drawImage(buttonIcon.getImage(), 0, 0,
                        getWidth(), getHeight(), this);

                //글자 이미지 중앙 배치
                int x = (getWidth() - leaveIcon.getIconWidth()) / 2;
                int y = (getHeight() - leaveIcon.getIconHeight()) / 2;

                g.drawImage(leaveIcon.getImage(), x, y, this);
            }
        };

        btnLeave.setBounds(45, 410, 200, 65);
        btnLeave.setContentAreaFilled(false);
        btnLeave.setBorderPainted(false);
        btnLeave.setFocusPainted(false);
        contentPane.add(btnLeave);

        //준비 완료 버튼
        btnReady = new JButton("준비완료!") {
            @Override
            protected void paintComponent(Graphics g) {

                //버튼 배경
                g.drawImage(buttonIcon.getImage(), 0, 0,
                        getWidth(), getHeight(), this);

                //기존 준비 상태에 따라 글자 표시
                if (getText().equals("준비완료!")) {

                    int x = (getWidth() - readyIcon.getIconWidth()) / 2;
                    int y = (getHeight() - readyIcon.getIconHeight()) / 2;

                    g.drawImage(readyIcon.getImage(), x, y, this);

                } else {

                    //준비 취소 상태에서는 기존 텍스트 표시
                    g.setColor(new Color(84, 48, 43));
                    g.setFont(new Font("맑은 고딕", Font.BOLD, 18));

                    FontMetrics fm = g.getFontMetrics();
                    String text = getText();

                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = (getHeight() - fm.getHeight()) / 2
                            + fm.getAscent();

                    g.drawString(text, x, y);
                }
            }
        };

        btnReady.setBounds(605, 410, 200, 65);
        btnReady.setContentAreaFilled(false);
        btnReady.setBorderPainted(false);
        btnReady.setFocusPainted(false);
        contentPane.add(btnReady);

        Myaction action = new Myaction();
        btnReady.addActionListener(action);
        btnLeave.addActionListener(action);
        
        //시작 카운트다운
        lblCountdown = new JLabel("");
        lblCountdown.setHorizontalAlignment(SwingConstants.CENTER);
        lblCountdown.setFont(new Font("맑은 고딕", Font.BOLD, 36));
        lblCountdown.setForeground(new Color(163, 78, 154));
        lblCountdown.setBounds(350, 120, 150, 60);
        contentPane.add(lblCountdown);

        CatchClient client = CatchClient.getInstance();
        //이부분 추가 이해 필요, 대기실 생성 시 리스너를 한 번 등록
        //이후 /PLAYERS를 수신할 때마다 updatePlayers()가 실행됨
        //화면 클래스에 대한 의존성 없애기 위함
        client.setPlayersListener(new CatchClient.PlayersListener() {

            @Override
            public void onPlayers(String msg) {
                updatePlayers(msg);
            }
        });
        
        setVisible(true);
    }


    //서버에서 전달받은 참가자 목록 화면에 표시
    //플레이어목록에 변화가 있을때마다 전부 지우고 다시 그리는 방식
    private void updatePlayers(String msg) {
        //기존 참가자 표시 초기화
        for (int i = 0; i < 4; i++) {
            lblPlayers[i].setText("");
            lblCharacters[i].setVisible(false);
            lblReady[i].setVisible(false);
        }
        String data = msg.substring("/PLAYERS".length()).trim();

        if (data.isEmpty()) {
            return;
        }
        //플레이어별 정보 분리
        String[] players = data.split(";");

        for (String player : players) {
            String[] info = player.split(",");
            if (info.length != 4) {
                continue;
            }

            int id;
            int profile;

            try {
                id = Integer.parseInt(info[0]);
                profile = Integer.parseInt(info[2]);
            } catch (NumberFormatException e) {
                continue;
            }

            if (id < 1 || id > 4 || profile < 1 || profile > 4) {
                continue;
            }
            int index = id - 1;
            String nickname = info[1];

            //닉네임 표시
            lblPlayers[index].setText(nickname);

            //플레이어가 선택한 프로필 표시
            ImageIcon icon = new ImageIcon(
                    getClass().getResource(
                            "/images/lobby-chracter" + profile + ".png"
                    )
            );

            Image image = icon.getImage().getScaledInstance(
                    75, 110, Image.SCALE_REPLICATE
            );

            lblCharacters[index].setIcon(new ImageIcon(image));
            lblCharacters[index].setVisible(true);

            //준비 상태는 아직 미구현
            lblReady[index].setText("Wait..");
            lblReady[index].setVisible(true);
        }
        contentPane.repaint();
    }

    
    //게임시작 카운트다운
    private void startCountdown() {

        count = 3;
        lblCountdown.setText("" + count);

        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                count--;

                if (count > 0) {
                    lblCountdown.setText("" + count);

                } else {
                    countdownTimer.stop();

                    CatchGameView view = new CatchGameView();
                    setVisible(false);
                }
            }
        });
        countdownTimer.start();
    }

    //카운트다운 취소
    private void cancelCountdown() {

        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        count = 3;
        lblCountdown.setText("");
    }

    //버튼 클릭했을 때 이벤트처리
    class Myaction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == btnReady) {

                ready = !ready;
                //서버 연결 후 lblReady[i]를 해당 플레이어 ID로변경
                //게임시작 카운트다운 1인용으로 만들어둠, 추후 다인용으로 교체하곘습니다
                if (ready) {
                    btnReady.setText("준비 취소");
                    lblReady[0].setText("Ready!");

                    startCountdown();

                } else {
                    btnReady.setText("준비완료!");
                    lblReady[0].setText("Wait..");

                    cancelCountdown();
                }

            } else if (e.getSource() == btnLeave) {

                cancelCountdown();
                
                CatchClient client = CatchClient.getInstance();
                
                try {
                    //서버에 나가기 요청
                    if (client.isConnected()) {
                        client.sendMessage("/LEAVE");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            CatchLobbyView.this,
                            "서버 나가기 요청 전송 실패"
                    );  
                } finally {
                    //서버 연결 종료
                    client.disconnect();
                }

                //메인 화면으로 이동
                CatchClientMain view = new CatchClientMain();
                view.setVisible(true);
                dispose();
            }
            
        }
    }
}
