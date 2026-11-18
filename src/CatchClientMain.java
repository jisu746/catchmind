
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.JOptionPane;

public class CatchClientMain extends JFrame {

    private JPanel contentPane;
    private JTextField txtUserName;
    private JTextField txtIpAddress;
    private JTextField txtPortNumber;

    //프로그램 시작
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CatchClientMain frame = new CatchClientMain();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //클라이언트 접속 화면 생성
    public CatchClientMain() {

        setTitle("캐치캐치");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 350, 530);
        setLocationRelativeTo(null);
        setResizable(false);

        //배경 이미지
        contentPane = new JPanel() {
            private Image background = new ImageIcon(
                    getClass().getResource(
                            "/images/serverpanel.png")
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

        //게임 로고
        ImageIcon logoIcon = new ImageIcon(
                getClass().getResource(
                        "/images/캐치캐치로고.png")
        );

        Image logoImage = logoIcon.getImage().getScaledInstance(
                190, 64, Image.SCALE_SMOOTH
        );

        JLabel lblTitle = new JLabel(new ImageIcon(logoImage));
        lblTitle.setBounds(72, 20, 190, 64);
        contentPane.add(lblTitle);

        //서버 IP 입력창
        txtIpAddress = new JTextField("127.0.0.1");
        txtIpAddress.setBounds(55, 142, 225, 30);
        txtIpAddress.setOpaque(false);
        txtIpAddress.setBorder(null);
        contentPane.add(txtIpAddress);

        //서버 IP 제목 이미지
        JLabel lblIpAddress = new JLabel(
                new ImageIcon(getClass().getResource(
                        "/images/서버 IP 주소.png"))
        );

        lblIpAddress.setHorizontalAlignment(SwingConstants.LEFT);
        lblIpAddress.setBounds(
                txtIpAddress.getX() - 10,
                txtIpAddress.getY() - 33,
                120, 20
        );
        contentPane.add(lblIpAddress);

        //포트 번호 입력창
        txtPortNumber = new JTextField("9999");
        txtPortNumber.setBounds(55, 237, 225, 30);
        txtPortNumber.setOpaque(false);
        txtPortNumber.setBorder(null);
        contentPane.add(txtPortNumber);

        //포트 번호 제목 이미지
        JLabel lblPortNumber = new JLabel(
                new ImageIcon(getClass().getResource(
                        "/images/포트 번호.png"))
        );

        lblPortNumber.setHorizontalAlignment(SwingConstants.LEFT);
        lblPortNumber.setBounds(
                txtPortNumber.getX() - 10,
                txtPortNumber.getY() - 33,
                120, 20
        );
        contentPane.add(lblPortNumber);

        //닉네임 입력창
        txtUserName = new JTextField();
        txtUserName.setBounds(55, 332, 225, 30);
        txtUserName.setOpaque(false);
        txtUserName.setBorder(null);
        contentPane.add(txtUserName);

        //닉네임 제목 이미지
        JLabel lblUserName = new JLabel(
                new ImageIcon(getClass().getResource(
                        "/images/닉네임.png"))
        );

        lblUserName.setHorizontalAlignment(SwingConstants.LEFT);
        lblUserName.setBounds(
                txtUserName.getX() - 10,
                txtUserName.getY() - 33,
                120, 20
        );
        contentPane.add(lblUserName);

        //입력창 장식 이미지
        ImageIcon cardIcon = new ImageIcon(
                getClass().getResource(
                        "/images/card1.png")
        );

        JTextField[] textFields = {
                txtIpAddress, txtPortNumber, txtUserName
        };

        for (int i = 0; i < textFields.length; i++) {

            JTextField field = textFields[i];

            int cardWidth = 250;
            int cardHeight = 52;

            Image cardImage = cardIcon.getImage().getScaledInstance(
                    cardWidth, cardHeight, Image.SCALE_SMOOTH
            );

            JLabel lblCard = new JLabel(new ImageIcon(cardImage));

            lblCard.setBounds(
                    field.getX() - 12,
                    field.getY() - 11,
                    cardWidth,
                    cardHeight
            );

            contentPane.add(lblCard);

            //입력창보다 뒤에 배치
            contentPane.setComponentZOrder(
                    lblCard, contentPane.getComponentCount() - 1
            );
        }

      //다음 버튼 배경 이미지
        ImageIcon cardIcon2 = new ImageIcon(
                getClass().getResource("/images/card2.png")
        );

        //다음 글자 이미지
        ImageIcon nextIcon = new ImageIcon(
                getClass().getResource("/images/다음.png")
        );

        //다음 버튼
        JButton btnNext = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {

                //배경 이미지
                g.drawImage(cardIcon2.getImage(), 0, 0,
                        getWidth(), getHeight(), this);

                //글자 이미지 중앙 배치
                int x = (getWidth() - nextIcon.getIconWidth()) / 2;
                int y = (getHeight() - nextIcon.getIconHeight()) / 2;

                g.drawImage(nextIcon.getImage(), x, y, this);
            }
        };

        btnNext.setBounds(49, 390, 238, 55);
        btnNext.setContentAreaFilled(false);
        btnNext.setBorderPainted(false);
        btnNext.setFocusPainted(false);
        contentPane.add(btnNext);

        //이벤트 등록
        Myaction action = new Myaction();

        btnNext.addActionListener(action);
        txtUserName.addActionListener(action);
        txtIpAddress.addActionListener(action);
        txtPortNumber.addActionListener(action);
    }

    //다음 버튼 클릭 시 이벤트 처리
    class Myaction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        	//프로필 선택 화면으로 정보 전달 중요!!!!!!!!!!!
            String username = txtUserName.getText().trim();
            String ip_addr = txtIpAddress.getText().trim();
            String port_no = txtPortNumber.getText().trim();

            //닉네임 입력
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(
                        CatchClientMain.this,
                        "닉네임을 입력해주세요."
                );
                return;
            }
            
            CatchProfileView frame =
                    new CatchProfileView(username, ip_addr, port_no);

            setVisible(false);
            dispose();
        }
    }
}
