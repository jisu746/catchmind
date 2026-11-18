import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

public class CatchProfileView extends JFrame {

	// ---화면 단독 테스트용 (추후 삭제)---
	public static void main(String[] args) {
	    new CatchProfileView("테스트유저", "127.0.0.1", "9999");
	}
	//-------------------------------
	
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

        //배경 이미지
        contentPane = new JPanel() {
            private Image background = new ImageIcon(
                    getClass().getResource(
                            "/images/selectcharacter-panel.png")
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

        //프로필 선택 제목 이미지
        ImageIcon titleIcon = new ImageIcon(
                getClass().getResource("/images/selectcharacter.png")
        );

        Image titleImage = titleIcon.getImage().getScaledInstance(
                240, 84, Image.SCALE_REPLICATE
        );

        JLabel lblTitle = new JLabel(new ImageIcon(titleImage));
        lblTitle.setBounds(80, 20, 240, 84);
        contentPane.add(lblTitle);

        //캐릭터 선택칸 이미지
        ImageIcon boxIcon = new ImageIcon(
                getClass().getResource("/images/chcracter-box.png")
        );

        Image boxImage = boxIcon.getImage().getScaledInstance(
                140, 120, Image.SCALE_REPLICATE
        );

        //프로필 선택 버튼 4개
        for (int i = 0; i < 4; i++) {

            ImageIcon characterIcon = new ImageIcon(
                    getClass().getResource(
                            "/images/chracter" + (i + 1) + ".png")
            );

            Image characterImage = characterIcon.getImage().getScaledInstance(
                    65, 95, Image.SCALE_REPLICATE
            );

            JButton button = new JButton(new ImageIcon(characterImage));

            int x = 40 + (i % 2) * 160;
            int y = 120 + (i / 2) * 150;

            button.setBounds(x, y, 140, 120);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
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

            //캐릭터 선택칸 장식 이미지
            JLabel lblBox = new JLabel(new ImageIcon(boxImage));
            lblBox.setBounds(x, y, 140, 120);
            contentPane.add(lblBox);

            //버튼보다 뒤에 배치
            contentPane.setComponentZOrder(
                    lblBox, contentPane.getComponentCount() - 1
            );

            setVisible(true);
        }

        //게임 참여 버튼(아직 이미지로 못바꿨습니다 뒤로미룸)
      //게임 참여 버튼 배경 이미지
        ImageIcon joinIcon = new ImageIcon(
                getClass().getResource("/images/join-button.png")
        );

        //게임 참여 글자 이미지
        ImageIcon joinTextIcon = new ImageIcon(
                getClass().getResource("/images/게임 참여.png")
        );

        //게임 참여 버튼
        btnJoin = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {

                //배경 이미지
                g.drawImage(joinIcon.getImage(), 0, 0,
                        getWidth(), getHeight(), this);

                //글자 이미지 중앙 배치
                int x = (getWidth() - joinTextIcon.getIconWidth()) / 2;
                int y = (getHeight() - joinTextIcon.getIconHeight()) / 2;

                g.drawImage(joinTextIcon.getImage(), x, y, this);
            }
        };

        btnJoin.setBounds(40, 410, 300, 45);
        btnJoin.setContentAreaFilled(false);
        btnJoin.setBorderPainted(false);
        btnJoin.setFocusPainted(false);
        contentPane.add(btnJoin);

        btnJoin.addActionListener(new Myaction());
    }

    //선택한 프로필 플레이어에게 표시해주는 디자인
    private void selectProfile(int number) {

        selectedProfile = number;

        for (int i = 0; i < 4; i++) {
            if (i + 1 == number) {
                profileButtons[i].setBorder(
                        BorderFactory.createLineBorder(
                                new Color(163, 78, 154), 4)
                );
                profileButtons[i].setBorderPainted(true);
            } else {
                profileButtons[i].setBorderPainted(false);
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

            CatchLobbyView view = new CatchLobbyView(
                    username, selectedProfile, ip_addr, port_no
            );

            setVisible(false);
        }
    }
}
