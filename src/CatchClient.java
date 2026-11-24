//클라이언트 소켓 통신용 클래스, 싱글턴패턴 사용
import javax.swing.SwingUtilities;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CatchClient {

    private static CatchClient instance = new CatchClient();

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    
    private int playerId = -1; //플레이어ID초기화
    private JoinListener joinListener;

    //참가 승인 결과를 화면에 전달
    public interface JoinListener { //인터페이스
        void onJoinResult(String msg);
    }
    public void setJoinListener(JoinListener listener) {
        joinListener = listener;
    }
    public int getPlayerId() {
        return playerId;
    }
    public void setPlayerId(int id) {
        playerId = id;
    }
    
    //참가자 목록을 대기실에 전달
    private PlayersListener playersListener;
    public interface PlayersListener {
        void onPlayers(String msg); ///PLAYERS수신했을 떄 실행할거
    }
    public void setPlayersListener(PlayersListener listener) {
        playersListener = listener;
    }

    //준비 상태 변경 알림을 대기실에 전달
    private ReadyListener readyListener;
    public interface ReadyListener {
        void onReadyUpdate(String msg);
        void onGameStart();
    }
    public void setReadyListener(ReadyListener listener) {
        readyListener = listener;
    }
    
    //외부에서 직접 객체 생성하지 않도록 설정
    private CatchClient() {
    }

    //모든 화면에서 같은 객체 사용
    public static CatchClient getInstance() {
        return instance;
    }

    //서버 연결
    public void connect(String ip_addr, int port_no) throws IOException {
        if (isConnected()) { //중복연결 방지
            throw new IOException("이미 서버에 연결되어 있습니다.");
        }

        socket = new Socket(ip_addr, port_no);

        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        //서버에서 수신하는 스레드
        ListenNetwork listen = new ListenNetwork();
        listen.start();
    }

    //서버에 메시지 전송
    public void sendMessage(String msg) throws IOException {
        if (!isConnected()) {
            throw new IOException("서버에 연결되어 있지 않습니다.");
        }

        dos.writeUTF(msg);
        dos.flush();
    }
    
//서버에서 메세지 수신 스레드(프로토콜 구분)
class ListenNetwork extends Thread {
    @Override
    public void run() {
        //현재 연결의 입력 스트림을 사용
        DataInputStream input = dis;
        Socket connectedSocket = socket;
        try {
            while (true) {

                String msg = input.readUTF();
                System.out.println("서버 수신: " + msg);

                String[] args = msg.trim().split("\\s+");

                switch (args[0]) {
                    case "/JOIN_OK":
                    case "/JOIN_FAIL":
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                if (joinListener != null
                                        && socket == connectedSocket) {
                                    joinListener.onJoinResult(msg);
                                }
                            }
                        });
                        break;
                    case "/PLAYERS":
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                if (playersListener != null
                                        && socket == connectedSocket) {
                                    playersListener.onPlayers(msg);
                                }
                            }
                        });
                        break;
                    case "/READY_UPDATE":
                    	SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                if (readyListener != null
                                        && socket == connectedSocket) {
                                    readyListener.onReadyUpdate(msg);
                                }
                            }
                        });
                        break;
                    case "/GAME_START":
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                if (readyListener != null
                                        && socket == connectedSocket) {
                                    readyListener.onGameStart();
                                }
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("서버 연결 종료");
            
            //참가 응답을 기다리던 중 연결이 끊긴 경우
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (joinListener != null
                            && socket == connectedSocket) {
                        joinListener.onJoinResult("/JOIN_FAIL DISCONNECTED");
                    }
                }
            });
        }
    }
}


    //소켓 연결 상태 확인
    public boolean isConnected() {
        return socket != null
                && socket.isConnected()
                && !socket.isClosed();
    }

    //서버 연결 종료
    public void disconnect() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket = null;
        dis = null;
        dos = null;
        playersListener = null;
        readyListener = null;
        playerId = -1;
    }
}
