import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

//추후 삭제
import javax.swing.JButton;

public class CatchGameView extends JFrame {

    private JPanel contentPane;

    public CatchGameView() {

        setTitle("캐치캐치 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 650);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        //게임 제목
        JLabel lblTitle = new JLabel("캐치캐치");
        lblTitle.setBounds(30, 20, 200, 40);
        contentPane.add(lblTitle);

        //라운드 표시, 라운드는 플레이어 수만큼 진행 추후수정
        JLabel lblRound = new JLabel("Round 1/4");
        lblRound.setBounds(30, 70, 200, 30);
        contentPane.add(lblRound);

        //참가자 목록
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(null);
        playerPanel.setBounds(30, 120, 200, 400);
        playerPanel.setBackground(Color.LIGHT_GRAY);
        contentPane.add(playerPanel);

        //플레이어수 변수로 관리
        for (int i = 0; i < 4; i++) {
            JLabel player = new JLabel("Player " + (i + 1) + " : 0점");
            player.setBounds(20, 30 + i * 80, 170, 40);
            playerPanel.add(player);
        }

        //제시어 ??? 변수로변경예정
        JLabel lblWord = new JLabel("제시어 : ???");
        lblWord.setHorizontalAlignment(SwingConstants.CENTER);
        lblWord.setBounds(260, 50, 490, 40);
        contentPane.add(lblWord);

        //타이머
        JLabel lblTime = new JLabel("시계이미지추가"); //디자인 보고 결정할게용
        lblTime.setBounds(260, 100, 100, 30);
        contentPane.add(lblTime);
        //하단에 타이머 바 세로로 추가

        //그림판 영역
        JPanel drawingPanel = new JPanel();
        drawingPanel.setBounds(260, 140, 490, 350);
        drawingPanel.setBackground(Color.WHITE);
        contentPane.add(drawingPanel);

        //그림 도구
        JPanel toolPanel = new JPanel();
        toolPanel.setBounds(260, 505, 490, 50);
        toolPanel.setBackground(Color.LIGHT_GRAY);
        contentPane.add(toolPanel);

        //채팅 내역
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBounds(780, 120, 275, 370);
        contentPane.add(scrollPane);

        //채팅 입력
        JTextField txtChat = new JTextField();
        txtChat.setBounds(780, 505, 275, 40);
        contentPane.add(txtChat);

        //-----------임시버튼 추후삭제------------
        JButton btnRanking = new JButton("랭킹 보기");
        btnRanking.setBounds(780, 560, 275, 35);
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