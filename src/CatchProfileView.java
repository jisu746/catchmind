//프로필 이미지 선택화면
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class CatchProfileView extends JFrame {

    private JPanel contentPane;
    private JButton[] profileButtons = new JButton[4];
    private JButton btnJoin;

    //프로필 기본값
    private int selectedProfile = -1;

    private String username;
    private String ip_addr;
    private String port_no;

    public CatchProfileView(String username, String ip_addr, String port_no) {

        this.username = username;
        this.ip_addr = ip_addr;
        this.port_no = port_no;

        setTitle("캐치캐치 - 프로필 선택");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 550);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel lblTitle = new JLabel("프로필 선택");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        lblTitle.setBounds(40, 30, 300, 50);
        contentPane.add(lblTitle);

        //프로필 선택 버튼 4개(추후 프로필 이미지로 바꿔주세용)
        for (int i = 0; i < 4; i++) {

            JButton button = new JButton("프로필 " + (i + 1));

            int x = 40 + (i % 2) * 160;
            int y = 120 + (i / 2) * 150;

            button.setBounds(x, y, 140, 120);
            button.setBackground(new Color(206, 148, 198));
            button.setFocusPainted(false);

            final int profileNumber = i + 1;

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectProfile(profileNumber);
                }
            });

            profileButtons[i] = button;
            contentPane.add(button);
            
            setVisible(true);
        }

        //게임 참여 버튼
        btnJoin = new JButton("게임 참여");
        btnJoin.setBounds(40, 440, 300, 45);
        contentPane.add(btnJoin);

        btnJoin.addActionListener(new Myaction());
    }

    //선택한 프로필 플레이어에게 표시해주는 디자인
    private void selectProfile(int number) {

        selectedProfile = number;

        for (int i = 0; i < 4; i++) {
            if (i + 1 == number) {
                profileButtons[i].setBackground(new Color(163, 78, 154));
            } else {
                profileButtons[i].setBackground(new Color(206, 148, 198));
            }
        }
    }

    //게임 참여 클릭시 이벤트처리용
    class Myaction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (selectedProfile == -1) {
                JOptionPane.showMessageDialog(
                    CatchProfileView.this,
                    "프로필을 선택해주세요."
                );
                return;
            }
            
            CatchLobbyView view = new CatchLobbyView(username, selectedProfile, ip_addr, port_no);
            setVisible(false);
        }
    }
}