import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.Timer;
import javax.swing.SwingConstants;

public class CatchLobbyView extends JFrame {

    private JPanel contentPane;
    private JButton btnReady;
    private JButton btnLeave;

    private JLabel[] lblReady = new JLabel[4];
    
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

        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        //게임 타이틀
        //-----------------------
        //이거 좀 못생긴거같은데 이미지 넣어보고 뺄지말지 정해주세용
        //-----------------------
        JLabel lblTitle = new JLabel("캐치캐치");
        lblTitle.setBounds(40, 25, 200, 40);
        contentPane.add(lblTitle);

        //대기실 제목
        JLabel lblLobby = new JLabel("게임 대기실");
        lblLobby.setHorizontalAlignment(JLabel.CENTER);
        lblLobby.setBounds(350, 60, 200, 40);
        contentPane.add(lblLobby);

        //플레이어 4명 표시
        for (int i = 0; i < 4; i++) {
        	//표시 이름 임시로 전부 username으로 작성해둠, 서버 연결 후 수정
            JLabel player = new JLabel(username);
            player.setHorizontalAlignment(JLabel.CENTER);
            player.setBounds(80 + i * 190, 190, 150, 100);
            contentPane.add(player);
            
            lblReady[i] = new JLabel("Wait..");
            lblReady[i].setHorizontalAlignment(JLabel.CENTER);
            lblReady[i].setBounds(80+i*190 , 300, 150, 30);
            contentPane.add(lblReady[i]);
            
        }
        

        //서버 나가기 버튼
        btnLeave = new JButton("서버 나가기");
        btnLeave.setBounds(40, 440, 180, 45);
        contentPane.add(btnLeave);

        //준비 완료 버튼
        btnReady = new JButton("준비완료!");
        btnReady.setBounds(630, 440, 180, 45);
        contentPane.add(btnReady);

        Myaction action = new Myaction();
        btnReady.addActionListener(action);
        btnLeave.addActionListener(action);

        //시작 카운트다운
        lblCountdown = new JLabel("");
        lblCountdown.setHorizontalAlignment(SwingConstants.CENTER);
        lblCountdown.setBounds(350, 120, 150, 60);
        contentPane.add(lblCountdown);
        setVisible(true);
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

                CatchClientMain view = new CatchClientMain();
                view.setVisible(true);
                dispose();
            }
        }
    }
}