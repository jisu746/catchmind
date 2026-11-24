//14주차 JavaChatServer.java와 유사하니 작업에 참고
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class CatchServer extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private JTextArea textArea;

    private JTextField txtPortNumber;
    private JTextField txtMaxPlayers;
    private JButton btnServerStart;

    //서버 관련 변수
    private ServerSocket socket;
    private Vector<Socket> UserVec = new Vector<>(); //TCP연결된 소켓
    private Vector<UserService> joinedUsers = new Vector<>(); //JOIN검사 통과한 소켓
    private int maxPlayers = 4; //최대인원 기본값 4로 임의로 정했습니당
    private CatchProtocol protocol = new CatchProtocol(this,joinedUsers);
    //프로그램 시작
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CatchServer frame = new CatchServer();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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

    //서버 시작 화면
    public CatchServer() {

        setTitle("캐치캐치 - 서버 실행");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 350, 530);
        setLocationRelativeTo(null);
        setResizable(false);

        //배경 이미지
        contentPane = new JPanel() {

            private Image background = new ImageIcon(
                    getClass().getResource("/images/serverpanel.png")
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

        //서버 실행 제목
        JLabel lblTitle = new JLabel(
                loadImage("startserver.png", 190, 64)
        );
        lblTitle.setBounds(72, 12, 190, 64);
        contentPane.add(lblTitle);

        //서버 로그 출력 영역, IP주소 대신에 넣엇습니다 이게 더 필요할것같아서
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(new Color(255, 249, 237));
        textArea.setForeground(new Color(83, 55, 44));
        textArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(40, 88, 255, 175);
        contentPane.add(scrollPane);

        AppendText("서버 실행 대기 중...");

        //포트 번호 입력창
        txtPortNumber = new JTextField("9999");
        txtPortNumber.setBounds(55, 300, 225, 30);
        txtPortNumber.setOpaque(false);
        txtPortNumber.setBorder(null);
        contentPane.add(txtPortNumber);

        JLabel lblPortNumber = new JLabel(
                loadImage("포트 번호.png", 70, 18)
        );
        lblPortNumber.setHorizontalAlignment(SwingConstants.LEFT);
        lblPortNumber.setBounds(
                txtPortNumber.getX() - 10,
                txtPortNumber.getY() - 33,
                120, 20
        );
        contentPane.add(lblPortNumber);

        // 최대 참여 인원 입력창
        txtMaxPlayers = new JTextField("4");
        txtMaxPlayers.setBounds(55, 375, 225, 30);
        txtMaxPlayers.setOpaque(false);
        txtMaxPlayers.setBorder(null);
        contentPane.add(txtMaxPlayers);

        JLabel lblMaxPlayers = new JLabel(
                loadImage("최대 참여 인원.png", 105, 18)
        );
        lblMaxPlayers.setHorizontalAlignment(SwingConstants.LEFT);
        lblMaxPlayers.setBounds(
                txtMaxPlayers.getX() - 10,
                txtMaxPlayers.getY() - 33,
                130, 20
        );
        contentPane.add(lblMaxPlayers);

        //입력창 장식 이미지
        JTextField[] fields = {
                txtPortNumber, txtMaxPlayers
        };

        for (int i = 0; i < fields.length; i++) {

            JTextField field = fields[i];

            JLabel lblCard = new JLabel(
                    loadImage("card1.png", 250, 52)
            );

            lblCard.setBounds(
                    field.getX() - 12,
                    field.getY() - 11,
                    250, 52
            );

            contentPane.add(lblCard);
            contentPane.setComponentZOrder(
                    lblCard, contentPane.getComponentCount() - 1
            );
        }

        //서버 시작 버튼
        ImageIcon buttonIcon =
                loadImage("card2.png", 238, 55);

        ImageIcon startIcon = new ImageIcon(
                getClass().getResource("/images/서버 시작.png")
        );

        btnServerStart = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {

                // 버튼 배경 이미지
                g.drawImage(buttonIcon.getImage(), 0, 0,
                        getWidth(), getHeight(), this);

                // 글자 이미지 중앙 배치
                int x = (getWidth() - startIcon.getIconWidth()) / 2;
                int y = (getHeight() - startIcon.getIconHeight()) / 2;

                g.drawImage(startIcon.getImage(), x, y, this);
            }
        };

        btnServerStart.setBounds(49, 415, 238, 50);
        btnServerStart.setContentAreaFilled(false);
        btnServerStart.setBorderPainted(false);
        btnServerStart.setFocusPainted(false);
        contentPane.add(btnServerStart);

        //이벤트 등록
        btnServerStart.addActionListener(new Myaction());
    }

    //로그 출력용 함수, 안정성을 위해 EDT사용
    public void AppendText(String str) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.append(str + "\n");
                textArea.setCaretPosition(
                        textArea.getDocument().getLength()
                );
            }
        });
    }

    //서버 시작 버튼 클릭 리스너
    class Myaction implements ActionListener {
        @Override
        //메모:이부분 교수님 코드에서는 소켓 생성에만 try썼는데 그럼 실패했을떄도 성공메세지 떠서 전체를 try문으로 감쌈
        public void actionPerformed(ActionEvent e) {
            try {
            	//입력값 앞뒤 공백 제거
                int port = Integer.parseInt(txtPortNumber.getText().trim());
                int players = Integer.parseInt(txtMaxPlayers.getText().trim());

                //포트번호 범위 검사
                if (port < 1 || port > 65535) {
                    JOptionPane.showMessageDialog(
                            CatchServer.this,
                            "포트 번호는 1~65535 사이여야 합니다."
                    );return;}
                //플레이어는 2~4명만 허용
                if (players < 2 || players > 4) {
                    JOptionPane.showMessageDialog(CatchServer.this,
                            "참여 인원은 2~4명으로 설정해주세요." );
                    return; }
                maxPlayers = players;

                //서버 소켓 생성
                socket = new ServerSocket(port);
               
                AppendText("서버 실행 완료");
                AppendText("포트 번호: " + port);
                AppendText("최대 참여 인원: " + maxPlayers);

                //서버 중복 실행 방지
                btnServerStart.setEnabled(false);
                txtPortNumber.setEditable(false);
                txtMaxPlayers.setEditable(false);

                //클라이언트 접속 대기 스레드
                AcceptServer acceptServer = new AcceptServer();
                acceptServer.start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        CatchServer.this,"포트 번호와 참여 인원을 숫자로 입력해주세요."
                );
            } catch (IOException ex) {
                AppendText("서버 실행 실패: " + ex.getMessage());
            }
        }
    }

    //클라이언트 접속 대기, user thread생성
    class AcceptServer extends Thread {
        @Override
        public void run() {
            while (!socket.isClosed()) {
                try {
                    AppendText("클라이언트 접속 대기 중...");
                    Socket clientSocket = socket.accept();
                    

                    //스레드생성, 클라이언트 배열에 저장
                    UserService newUser = new UserService(clientSocket);
                    UserVec.add(clientSocket);

                    AppendText("새로운 접속: "
                            + clientSocket.getInetAddress());
                    AppendText("현재 접속 인원: "
                            + UserVec.size());

                    newUser.start();
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        AppendText("accept 에러 발생...");
                    }
                    break;
                }
            }
        }
    }

    //클라이언트별 수신 스레드
    class UserService extends Thread {

        private Socket clientSocket;
        private DataOutputStream dos;
        
        private int playerId = -1; //서버배정 번호
        private String nickname;
        private int profileNumber;
        
        //매개변수로 넘어온 소켓 객체 저장
        public UserService(Socket clientSocket)throws IOException {
            this.clientSocket = clientSocket;
            dos = new DataOutputStream(clientSocket.getOutputStream());
        }
        
        //프로토콜 송신용
        public synchronized void sendMessage(String msg) throws IOException {
            dos.writeUTF(msg);
            dos.flush();
        }
        
        //플레이어 ID 반환
        public int getPlayerId() {
            return playerId;
        }

        //닉네임 반환
        public String getNickname() {
            return nickname;
        }
        
        //프로필 번호 반환
        public int getProfileNumber() {
            return profileNumber;
        }

        //플레이어 정보 저장
        public void setPlayerInfo(int id, String name, int profile) {
            playerId = id;
            nickname = name;
            profileNumber = profile;
        }

        //수신 스레드
        @Override
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(
                        clientSocket.getInputStream()
                );

                while (true) {

                    String msg = dis.readUTF();
                    msg = msg.trim();
                    AppendText("수신: " + msg);

                    //명령어와 매개변수 분리
                    String[] args = msg.split("\\s+");

                    //프로토콜 구분
                    switch (args[0]) {
                        case "/JOIN":
                            if (!protocol.handleJoin(this,msg,maxPlayers)) {
                                return;
                            }
                            break;
                        
                        case "/LEAVE":
                        	protocol.handleLeave(this);
                        	return;
                        	
                        case "/READY":
                        	break;
                        	
                        default:
                            AppendText("알 수 없는 프로토콜: " + msg);
                            break;
                    }
                }

            } catch (IOException e) {
                AppendText("클라이언트 연결 종료");

            } finally {
                UserVec.remove(clientSocket);
                boolean removed = joinedUsers.remove(this);
                //참가자 나가면 목록 갱신
                if (removed) {
                    protocol.sendPlayers();
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                AppendText("현재 접속 인원: "
                        + UserVec.size());
            }
        }
    }
   
}
