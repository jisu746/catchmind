import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CatchRankingView extends JFrame {
    private JPanel contentPane;
    private JButton btnLobby;
    private JButton btnLeave;

    // 서버 연결 전 화면 확인용 데이터 같은 인덱스 같은 플레이어
    private String[] names = {"지수", "보람", "민수", "수아"};
    private int[] scores = {0, 0, 0, 0};
    private int[] profiles = {1, 2, 3, 4};
    private final Color textColor = new Color(80, 66, 43);

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                new CatchRankingView();
            }
        });
    }

    public CatchRankingView() {
        setTitle("캐치캐치 최종 랭킹");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);


        final ImageIcon background = loadImage("ranking/gamebackground.png");
        final ImageIcon paper = loadImage("ranking/gamepanel.png");
        contentPane = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D imageGraphics = (Graphics2D) g.create();
                imageGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                imageGraphics.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
                imageGraphics.drawImage(paper.getImage(), 16, 10,
                        getWidth() - 32, getHeight() - 20, this);
                imageGraphics.dispose();
            }
        };
        contentPane.setLayout(null);
        setContentPane(contentPane);

        picture(contentPane, "ranking/ranking.png", 462, 34);

        btnLeave = imageButton("ranking/game-button.png", "ranking/서버 나가기.png", 832, 28);
        btnLobby = imageButton("ranking/game-button.png", "ranking/대기실로 돌아가기.png", 832, 98);
        btnLeave.setToolTipText("서버 나가기");
        btnLobby.setToolTipText("대기실로 돌아가기");

        //배열과 반복문으로 점수 내림차순 인덱스를 만든다
        int[] order = new int[names.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        for (int i = 1; i < order.length; i++) {
            int player = order[i];
            int j = i - 1;
            while (j >= 0 && scores[order[j]] < scores[player]) {
                order[j + 1] = order[j];
                j--;
            }
            order[j + 1] = player;
        }
        int winner = order[0];

        // JPanel 안에 자식 컴포넌트를 배치해 이미지가 글씨를 덮지 않게 한다.
        JPanel winnerPanel = imagePanel(contentPane, "ranking/winner-card.png", 42, 176);
        picture(winnerPanel, "ranking/yellow-crown.png", 119, 16);
        // 순위표와 같은 이미지 묶음을 사용해야 캐릭터 번호가 일치한다.
        ImageIcon winnerIcon = loadImage("ranking/ranking-chracter" + profiles[winner] + ".png");
        // 도트를 유지하도록 가로·세로를 동일하게 4배 확대한다.
        ImageIcon winnerLargeIcon = new ImageIcon(winnerIcon.getImage().getScaledInstance(
                winnerIcon.getIconWidth() * 4, winnerIcon.getIconHeight() * 4,
                java.awt.Image.SCALE_REPLICATE));
        JLabel winnerCat = new JLabel(winnerLargeIcon);
        winnerCat.setBounds(77, 130, 200, 172);
        winnerPanel.add(winnerCat);
        text(winnerPanel, names[winner], 25, 304, 304, 54, 32, SwingConstants.CENTER);

        JPanel rankingPanel = imagePanel(contentPane, "ranking/ranking-panel.png", 406, 176);
        picture(rankingPanel, "ranking/최종 순위.png", 34, 28);
        int maxScore = Math.max(1, scores[winner]);

        for (int rank = 0; rank < order.length; rank++) {
            int player = order[rank];
            String rowName = rank == 0 ? "rank-row-first 1.png" : "rank-row-normal 1.png";
            JPanel row = imagePanel(rankingPanel, "ranking/" + rowName, 23, 61 + rank * 82);

            picture(row, "ranking/" + (rank + 1) + "위.png", 17, 24);
            JLabel cat = new JLabel(loadImage("ranking/ranking-chracter" + profiles[player] + ".png"));
            cat.setBounds(70, 13, 36, 42);
            row.add(cat);
            text(row, names[player], 106, 13, 330, 25, 14, SwingConstants.LEFT);
            text(row, scores[player] + "점", 456, 13, 100, 35, 22, SwingConstants.RIGHT);

            final ImageIcon track = loadImage("ranking/score-track.png");
            final ImageIcon fill = loadImage("ranking/" +
                    (rank == 0 ? "score-first 1.png" : "score-normal.png"));
            final int barWidth = (int) (334L * Math.max(0, scores[player]) / maxScore);
            JPanel bar = new JPanel() {
                @Override protected void paintComponent(Graphics graphics) {
                    super.paintComponent(graphics);
                    track.paintIcon(this, graphics, 0, 0);
                    Graphics2D g = (Graphics2D) graphics.create();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    if (barWidth > 0) g.drawImage(fill.getImage(), 0, 0, barWidth, 8, this);
                    g.dispose();
                }
            };
            bar.setOpaque(false);
            bar.setBounds(106, 43, 334, 8);
            row.add(bar);
        }

        Myaction action = new Myaction();
        btnLobby.addActionListener(action);
        btnLeave.addActionListener(action);
        setBounds(100, 100, 1100, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private ImageIcon loadImage(String name) {
        URL url = getClass().getResource("/images/" + name);
        if (url == null) throw new IllegalArgumentException("이미지 파일을 확인해주세요: " + name);
        return new ImageIcon(url);
    }

    private void picture(JPanel parent, String name, int x, int y) {
        ImageIcon image = loadImage(name);
        JLabel label = new JLabel(image);
        label.setBounds(x, y, image.getIconWidth(), image.getIconHeight());
        parent.add(label);
    }

    private JPanel imagePanel(JPanel parent, String name, int x, int y) {
        final ImageIcon image = loadImage(name);
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                image.paintIcon(this, g, 0, 0);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(null);
        panel.setBounds(x, y, image.getIconWidth(), image.getIconHeight());
        parent.add(panel);
        return panel;
    }

    private void text(JPanel parent, String value, int x, int y,
            int width, int height, int size, int alignment) {
        JLabel label = new JLabel(value, alignment);
        label.setFont(new Font("맑은 고딕", Font.BOLD, size));
        label.setForeground(textColor);
        label.setBounds(x, y, width, height);
        parent.add(label);
    }

    private JButton imageButton(String backgroundName, String textName, int x, int y) {
        final ImageIcon image = loadImage(backgroundName);
        final ImageIcon words = loadImage(textName);
        JButton button = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                image.paintIcon(this, g, 0, 0);
                words.paintIcon(this, g, (getWidth() - words.getIconWidth()) / 2,
                        (getHeight() - words.getIconHeight()) / 2);
            }
        };
        button.setBounds(x, y, image.getIconWidth(), image.getIconHeight());
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        contentPane.add(button);
        return button;
    }

    //ActionListener 내부 클래스 방식으로 화면 이동을 처리한다.
    class Myaction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnLobby) {
                new CatchLobbyView("지수", 1, "127.0.0.1", "9999");
                dispose();
            } else if (e.getSource() == btnLeave) {
                CatchClientMain view = new CatchClientMain();
                view.setVisible(true);
                dispose();
            }
        }
    }
}
