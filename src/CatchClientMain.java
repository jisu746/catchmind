//해당코드 14주차 JavaChatClientView.java와 거의 동일하니 작업에 참고
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

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
        setBounds(100, 100, 400, 550);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        //제목
        JLabel lblTitle = new JLabel("캐치캐치");
        lblTitle.setBounds(40, 30, 300, 40);
        contentPane.add(lblTitle);

        //서버 IP 주소 자동입력
        JLabel lblIpAddress = new JLabel("서버 IP 주소");
        lblIpAddress.setBounds(40, 110, 300, 25);
        contentPane.add(lblIpAddress);

        txtIpAddress = new JTextField();
        txtIpAddress.setText("127.0.0.1");
        txtIpAddress.setBounds(40, 140, 300, 40);
        contentPane.add(txtIpAddress);

        //포트 번호 자동입력
        JLabel lblPortNumber = new JLabel("포트 번호");
        lblPortNumber.setBounds(40, 205, 300, 25);
        contentPane.add(lblPortNumber);

        txtPortNumber = new JTextField();
        txtPortNumber.setText("9999");
        txtPortNumber.setBounds(40, 235, 300, 40);
        contentPane.add(txtPortNumber);

        //닉네임
        JLabel lblUserName = new JLabel("닉네임");
        lblUserName.setBounds(40, 300, 300, 25);
        contentPane.add(lblUserName);

        txtUserName = new JTextField();
        txtUserName.setBounds(40, 330, 300, 40);
        contentPane.add(txtUserName);

        //다음 버튼
        JButton btnNext = new JButton("다음");
        btnNext.setBounds(40, 420, 300, 45);
        contentPane.add(btnNext);

        //이벤트 등록
        Myaction action = new Myaction();
        btnNext.addActionListener(action);
        txtUserName.addActionListener(action);
        txtIpAddress.addActionListener(action);
        txtPortNumber.addActionListener(action);
    }

    //다음버튼 클릭시 이벤트처리용
    class Myaction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = txtUserName.getText().trim();
            String ip_addr = txtIpAddress.getText().trim();
            String port_no = txtPortNumber.getText().trim();
            
            //다음 화면에 닉네임,IP주소, 포트번호 전달
            CatchProfileView frame = new CatchProfileView(username, ip_addr, port_no);
            setVisible(false);
            
            dispose();
        }
    }
}