//랭킹 디자인 2개중에 고민중 일단 킵할게용
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;

public class CatchRankingView extends JFrame {

	// ---화면 단독 테스트용 (추후 삭제)---
	public static void main(String[] args) {
	    new CatchRankingView();
	}
	//---------------------------------
	
    private JPanel contentPane;
    private JButton btnLobby;
    private JButton btnLeave;

    //임시 플레이어 정보 (서버 연결 후 수정)
    private String[] names = {"지수", "보람", "민수", "수아"};
    private int[] scores = {300, 50, 450, 200};

    private Color purple = new Color(126, 33, 115);
    private Color pink = new Color(206, 148, 198);

    public CatchRankingView() {

        setTitle("캐치캐치 최종 랭킹");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 550);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        //게임 타이틀
        JLabel lblTitle = new JLabel("캐치캐치");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        lblTitle.setForeground(purple);
        lblTitle.setBounds(30, 20, 200, 40);
        contentPane.add(lblTitle);

        //랭킹 제목
        JLabel lblRanking = new JLabel("Ranking");
        lblRanking.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        lblRanking.setHorizontalAlignment(SwingConstants.CENTER);
        lblRanking.setBounds(330, 35, 190, 45);
        contentPane.add(lblRanking);

        //대기실 복귀 버튼
        btnLobby = new JButton("대기실로 돌아가기");
        btnLobby.setBounds(630, 25, 180, 45);
        contentPane.add(btnLobby);

        //서버 나가기 버튼
        btnLeave = new JButton("서버 나가기");
        btnLeave.setBounds(630, 80, 180, 45);
        contentPane.add(btnLeave);

        //1등 플레이어 찾기
        int winner = 0;

        for (int i = 1; i < scores.length; i++) {
            if (scores[i] > scores[winner]) {
                winner = i;
            }
        }

        //왕관 (추후 이미지로 수정)
        JLabel lblCrown = new JLabel("♛");
        lblCrown.setFont(new Font("Serif", Font.BOLD, 65));
        lblCrown.setHorizontalAlignment(SwingConstants.CENTER);
        lblCrown.setBounds(125, 90, 200, 70);
        contentPane.add(lblCrown);

        //1등 프로필 이미지 자리
        JLabel lblWinnerProfile = new JLabel("프로필 이미지");
        lblWinnerProfile.setHorizontalAlignment(SwingConstants.CENTER);
        lblWinnerProfile.setOpaque(true);
        lblWinnerProfile.setBackground(pink);
        lblWinnerProfile.setBorder(
                BorderFactory.createLineBorder(purple));
        lblWinnerProfile.setBounds(110, 160, 230, 230);
        contentPane.add(lblWinnerProfile);

        //1등 닉네임과 점수
        JLabel lblWinnerName = new JLabel(
                names[winner] + " (" + scores[winner] + "점)");

        lblWinnerName.setHorizontalAlignment(SwingConstants.CENTER);
        lblWinnerName.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        lblWinnerName.setBounds(110, 405, 230, 35);
        contentPane.add(lblWinnerName);

        //점수 막대그래프
        int maxScore = 1;

        for (int score : scores) {
            if (score > maxScore) {
                maxScore = score;
            }
        }

        for (int i = 0; i < scores.length; i++) {

            int barHeight = (int)
                    ((double) Math.max(scores[i], 0) / maxScore * 230);

            barHeight = Math.max(20, barHeight);

            int x = 465 + i * 85;
            int y = 470 - barHeight;

            //닉네임
            JLabel lblName = new JLabel(names[i]);
            lblName.setHorizontalAlignment(SwingConstants.CENTER);
            lblName.setBounds(x, y - 30, 70, 25);
            contentPane.add(lblName);

            //점수 막대 (이미지 사용 가능한지 확인)
            JLabel lblBar = new JLabel(scores[i] + "점");
            lblBar.setHorizontalAlignment(SwingConstants.CENTER);
            lblBar.setOpaque(true);
            lblBar.setBackground(pink);
            lblBar.setBorder(
                    BorderFactory.createLineBorder(purple));
            lblBar.setBounds(x, y, 70, barHeight);
            contentPane.add(lblBar);
        }

        //버튼 이벤트
        Myaction action = new Myaction();
        btnLobby.addActionListener(action);
        btnLeave.addActionListener(action);

        setVisible(true);
    }

    class Myaction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == btnLobby) {

                //임시 데이터로 대기실 복귀
                CatchLobbyView view =
                        new CatchLobbyView("지수", 1,
                                "127.0.0.1", "9999");

                setVisible(false);
                dispose();

            } else if (e.getSource() == btnLeave) {

                CatchClientMain view = new CatchClientMain();
                view.setVisible(true);

                setVisible(false);
                dispose();
            }
        }
    }
}