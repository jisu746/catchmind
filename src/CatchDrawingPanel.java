import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// /DRAW 시작x 시작y 끝x 끝y 색상 펜타입
// 좌표는 그림판 안쪽의 499 x 326 기준, 색상은 1~6, 펜은 1=연필 / 2=지우개.
// 서버도 같은 번호표와 범위를 사용하고 출제자의 요청인지 검사해야 함
public class CatchDrawingPanel extends JPanel {
    public static final int CANVAS_WIDTH = 499;
    public static final int CANVAS_HEIGHT = 326;
    public static final Color PAPER = new Color(255, 250, 239);
    private static final Color[] COLORS = {
        new Color(40, 37, 54), new Color(238, 82, 142),
        new Color(236, 80, 73), new Color(244, 162, 35),
        new Color(54, 185, 117), new Color(72, 136, 233)
    };
    private final BufferedImage canvas = new BufferedImage(
            CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int colorNumber = 1;
    private int penType = 1;
    private boolean drawingAllowed = true;
    private Point previous;

    // 서버 연결 전에는 전송한 명령을 즉시 수신하는 로컬 실습 방식.
    // 연결 후에는 서버 송신 함수로 교체하고 서버가 돌려준 메시지만 그림에 반영한다.
    private Consumer<String> messageSender = new Consumer<String>() {
        @Override
        public void accept(String message) {
            receiveMessage(message);
        }
    };

    public CatchDrawingPanel() {
        setBackground(PAPER);
        clearCanvas();
        DrawingMouseListener mouse = new DrawingMouseListener();
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    // 수업의 내부 클래스 방식. 눌렀을 때 이전 좌표를 저장하고 드래그하며 선을 연결한다.
    private class DrawingMouseListener extends MouseAdapter {
            @Override public void mousePressed(MouseEvent e) {
                if (!drawingAllowed || !SwingUtilities.isLeftMouseButton(e)) return;
                previous = logicalPoint(e);
                sendSegment(previous, previous); // 클릭만 해도 점이 찍힌다.
            }
            @Override public void mouseDragged(MouseEvent e) {
                if (!drawingAllowed || previous == null) return;
                Point current = logicalPoint(e);
                sendSegment(previous, current);
                previous = current;
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (previous == null) return;
                if (drawingAllowed && SwingUtilities.isLeftMouseButton(e)) {
                    Point current = logicalPoint(e);
                    if (!previous.equals(current)) sendSegment(previous, current);
                }
                previous = null;
            }
    }

    public void setMessageSender(Consumer<String> sender) {
        if (sender == null) throw new IllegalArgumentException("송신 함수를 지정해주세요.");
        messageSender = sender;
        previous = null;
    }

    public void setDrawingAllowed(boolean allowed) {
        drawingAllowed = allowed;
        previous = null;
    }

    public void selectPencil() { penType = 1; }
    public void selectEraser() { penType = 2; }

    public void selectColor(int number) {
        if (number < 1 || number > COLORS.length)
            throw new IllegalArgumentException("색상 번호는 1~6입니다.");
        colorNumber = number;
        penType = 1;
    }

    public void requestClear() {
        if (drawingAllowed) messageSender.accept("/CLEAR");
        previous = null;
    }

    private void sendSegment(Point from, Point to) {
        messageSender.accept("/DRAW " + from.x + " " + from.y + " "
                + to.x + " " + to.y + " " + colorNumber + " " + penType);
    }

    private Point logicalPoint(MouseEvent e) {
        java.awt.Insets insets = getInsets();
        int width = Math.max(1, getWidth() - insets.left - insets.right);
        int height = Math.max(1, getHeight() - insets.top - insets.bottom);
        int x = (int) ((long) (e.getX() - insets.left) * CANVAS_WIDTH / width);
        int y = (int) ((long) (e.getY() - insets.top) * CANVAS_HEIGHT / height);
        return new Point(Math.max(0, Math.min(CANVAS_WIDTH - 1, x)),
                Math.max(0, Math.min(CANVAS_HEIGHT - 1, y)));
    }

    // 수신 스레드에서 호출해도 실제 그림 변경은 Swing의 EDT에서 실행한다.
    // 잘못된 입력은 false를 반환하며 기존 그림을 변경하지 않는다.
    public boolean receiveMessage(String message) {
        if (message == null) return false;
        String[] values = message.trim().split("\\s+");
        if (values.length == 1 && values[0].equals("/CLEAR")) {
            onEdt(this::clearCanvas);
            return true;
        }
        if (values.length != 7 || !values[0].equals("/DRAW")) return false;
        try {
            int x1 = Integer.parseInt(values[1]), y1 = Integer.parseInt(values[2]);
            int x2 = Integer.parseInt(values[3]), y2 = Integer.parseInt(values[4]);
            int color = Integer.parseInt(values[5]), pen = Integer.parseInt(values[6]);
            if (!validPoint(x1, y1) || !validPoint(x2, y2)
                    || color < 1 || color > COLORS.length || (pen != 1 && pen != 2))
                return false;
            onEdt(() -> drawSegment(x1, y1, x2, y2, color, pen));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validPoint(int x, int y) {
        return x >= 0 && x < CANVAS_WIDTH && y >= 0 && y < CANVAS_HEIGHT;
    }

    private void onEdt(Runnable action) {
        if (SwingUtilities.isEventDispatchThread()) action.run();
        else SwingUtilities.invokeLater(action);
    }

    private void clearCanvas() {
        Graphics2D g = canvas.createGraphics();
        g.setColor(PAPER);
        g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        g.dispose();
        previous = null;
        repaint();
    }

    private void drawSegment(int x1, int y1, int x2, int y2, int color, int pen) {
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(pen == 2 ? PAPER : COLORS[color - 1]);
        g.setStroke(new BasicStroke(pen == 2 ? 18 : 3,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(x1, y1, x2, y2);
        g.dispose();
        repaint();
    }

    // Graphics에만 그리면 가려졌다 나타날 때 지워지므로 BufferedImage에 저장한다
    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        java.awt.Insets insets = getInsets();
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(canvas, insets.left, insets.top,
                getWidth() - insets.left - insets.right,
                getHeight() - insets.top - insets.bottom, this);
        g.dispose();
    }
}
