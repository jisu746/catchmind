//프로토콜 case문에 들어간 함수 따로 정리해둠
import java.io.IOException;
import java.util.Vector;

public class CatchProtocol {
	private CatchServer server;
    private Vector<CatchServer.UserService> joinedUsers;
    private boolean countdownStarted = false;

    //서버의 참가자 목록을 전달받음
    public CatchProtocol(CatchServer server,
            Vector<CatchServer.UserService> joinedUsers) {
    			this.server = server;
    			this.joinedUsers = joinedUsers; 
    			}

    //게임 참가 요청 처리
    public boolean handleJoin(CatchServer.UserService user,
                              String msg, int maxPlayers) throws IOException {

        String[] args = msg.trim().split("\\s+");

        //프로토콜 형식 검사
        if (args.length != 3 || user.getPlayerId() != -1) {
            user.sendMessage("/JOIN_FAIL INVALID");
            return false;
        }

        String newNickname = args[1];
        int newProfile;

        try {
            newProfile = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            user.sendMessage("/JOIN_FAIL INVALID");
            return false;
        }

        //프로필 번호 검사
        if (newProfile < 1 || newProfile > 4) {
            user.sendMessage("/JOIN_FAIL INVALID");
            return false;
        }

        String failReason = null;
        int newId = -1;

        synchronized (joinedUsers) {

            //닉네임 중복 확인
            for (CatchServer.UserService player : joinedUsers) {
                if (player.getNickname().equals(newNickname)) {
                    failReason = "DUPLICATE";
                    break;
                }
            }

            //최대 참여 인원 확인
            if (failReason == null && joinedUsers.size() >= maxPlayers) {
                failReason = "FULL";
            }

            //참가 가능하면 플레이어 ID 배정
            if (failReason == null) {

                for (int i = 1; i <= maxPlayers; i++) {

                    boolean used = false;

                    for (CatchServer.UserService player : joinedUsers) {
                        if (player.getPlayerId() == i) {
                            used = true;
                            break;
                        }
                    }

                    if (!used) {
                        newId = i;
                        break;
                    }
                }

                if (newId != -1) {
                    //플레이어 정보 저장
                    user.setPlayerInfo(newId, newNickname, newProfile);
                    joinedUsers.add(user);
                } else {
                    failReason = "FULL";
                }
            }
        }

        //참가 거절
        if (failReason != null) {
            user.sendMessage("/JOIN_FAIL " + failReason);
            server.AppendText("참가 거절: " + newNickname
                    + " (" + failReason + ")");
            return false;
        }

        //참가 승인
        user.sendMessage("/JOIN_OK " + user.getPlayerId());

        server.AppendText("참가 승인: " + newNickname
                + " / ID: " + user.getPlayerId()
                + " / 프로필: " + newProfile);
        //참가자 목록 갱신
        sendPlayers();

        return true;
    }
    
    //서버 나가기 요청 처리
    public void handleLeave(CatchServer.UserService user) {
        server.AppendText("참가자 퇴장: "
                + user.getNickname()
                + " / ID: " + user.getPlayerId());
    }
    
    //참가자 목록 전송(/PLAYERS)
    //새 참가자가 들어왔을때, 기존 참가자가 나갔을 때
    public void sendPlayers() {
    	StringBuilder sb = new StringBuilder("/PLAYERS ");
    	
    	synchronized (joinedUsers) {
    		countdownStarted = false;
    		//참가자 목록이 변경되면 전원 준비 취소
    		for (CatchServer.UserService player : joinedUsers) {
    		    player.setReady(false);
    		}
    		for (CatchServer.UserService player : joinedUsers) {
    			if (sb.length() > "/PLAYERS ".length()) {
                    sb.append(";");
                }
    			sb.append(player.getPlayerId()).append(",");
                sb.append(player.getNickname()).append(",");
                sb.append(player.getProfileNumber()).append(",");
                sb.append(0); //점수는 추후 구현
    		
    		}
    		String msg = sb.toString();
    		
    		//모든 참가자에게 목록 전송
            for (CatchServer.UserService player : joinedUsers) {
                try {
                    player.sendMessage(msg);
                } catch (IOException e) {
                    server.AppendText("참가자 목록 전송 실패: "
                            + player.getNickname());
                }
            }
    	}
    }
    

    //플레이어 준비 상태 변경
    public void handleReady(CatchServer.UserService user, String msg) {
        String[] args = msg.split(" ");

        //준비 상태는 0 또는 1만 허용
        if (args.length != 2) {
            return;
        }
        if (!args[1].equals("0") && !args[1].equals("1")) {
            return;
        }

        boolean ready = args[1].equals("1");

        synchronized (joinedUsers) {
            //참가 승인된 사용자만 준비 가능
            if (!joinedUsers.contains(user)) {
                return;
            }

            //서버에 준비 상태 저장
            user.setReady(ready);

            String updateMsg = "/READY_UPDATE "
                    + user.getPlayerId() + " " + args[1];

            //전체 참가자에게 준비 상태 전송
            for (CatchServer.UserService player : joinedUsers) {
                try {
                    player.sendMessage(updateMsg);
                } catch (IOException e) {
                    server.AppendText("준비 상태 전송 실패: "
                            + player.getNickname());
                }
            }
            
          //준비 취소시 시작 상태 초기화
            if (!ready) {
                countdownStarted = false;
            }

            //들어온 참가자가 모두 준비했는지 확인
            boolean allReady = !joinedUsers.isEmpty();

            for (CatchServer.UserService player : joinedUsers) {
                if (!player.isReady()) {
                    allReady = false;
                    break;
                }
            }

            //모두 준비 완료시 게임 시작
            if (allReady && !countdownStarted) {
                countdownStarted = true;
                for (CatchServer.UserService player : joinedUsers) {
                    try {
                        player.sendMessage("/GAME_START");
                    } catch (IOException e) {
                        server.AppendText("GAME_START 전송 실패");
                    }
                }
            }
        }
        
   
        
        server.AppendText("준비 상태 변경: "
                + user.getNickname()
                + " / " + (ready ? "Ready" : "Wait"));
    }


}
