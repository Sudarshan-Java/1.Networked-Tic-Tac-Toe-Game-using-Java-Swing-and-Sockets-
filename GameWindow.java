import java.awt.*;
import javax.swing.*;

public class GameWindow extends JFrame {
    private JButton[] buttons = new JButton[9];
    private boolean isMyTurn;
    private char mySymbol, opponentSymbol;
    private MoveListener moveListener;
    private JLabel statusLabel;
    private boolean gameOver = false;

    public GameWindow(String title, char symbol, boolean isMyTurn) {
        super(title);
        this.mySymbol = symbol;
        this.opponentSymbol = (symbol == 'X') ? 'O' : 'X';
        this.isMyTurn = isMyTurn;

        setLayout(new BorderLayout());
        
        // Status panel at the top
        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel(isMyTurn ? "Your turn (" + mySymbol + ")" : "Waiting for opponent...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.NORTH);

        // Game board in the center
        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        initButtons();
        for (JButton button : buttons) {
            boardPanel.add(button);
        }
        add(boardPanel, BorderLayout.CENTER);

        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initButtons() {
        for (int i = 0; i < 9; i++) {
            final int index = i;
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.BOLD, 40));
            buttons[i].addActionListener(e -> {
                if (!gameOver && isMyTurn && buttons[index].getText().equals("")) {
                    buttons[index].setText(String.valueOf(mySymbol));
                    isMyTurn = false;
                    statusLabel.setText("Waiting for opponent...");
                    if (moveListener != null) {
                        moveListener.onMove(index);
                    }
                    checkGameState();
                }
            });
        }
    }

    public void receiveMove(int index) {
        if (!gameOver) {
            buttons[index].setText(String.valueOf(opponentSymbol));
            isMyTurn = true;
            statusLabel.setText("Your turn (" + mySymbol + ")");
            checkGameState();
        }
    }

    private void checkGameState() {
        // Check rows
        for (int i = 0; i < 9; i += 3) {
            if (checkLine(i, i + 1, i + 2)) {
                return;
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (checkLine(i, i + 3, i + 6)) {
                return;
            }
        }

        // Check diagonals
        if (checkLine(0, 4, 8) || checkLine(2, 4, 6)) {
            return;
        }

        // Check for draw
        boolean isDraw = true;
        for (JButton button : buttons) {
            if (button.getText().equals("")) {
                isDraw = false;
                break;
            }
        }
        if (isDraw) {
            gameOver = true;
            statusLabel.setText("Game Over - It's a draw!");
            disableAllButtons();
        }
    }

    private boolean checkLine(int a, int b, int c) {
        String textA = buttons[a].getText();
        String textB = buttons[b].getText();
        String textC = buttons[c].getText();

        if (!textA.equals("") && textA.equals(textB) && textA.equals(textC)) {
            gameOver = true;
            char winner = textA.charAt(0);
            String result = winner == mySymbol ? "You win!" : "You lose!";
            statusLabel.setText("Game Over - " + result);
            disableAllButtons();
            return true;
        }
        return false;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText("Connection lost!");
        disableAllButtons();
    }

    private void disableAllButtons() {
        for (JButton button : buttons) {
            button.setEnabled(false);
        }
    }

    public void setMoveListener(MoveListener listener) {
        this.moveListener = listener;
    }

    public interface MoveListener {
        void onMove(int index);
    }
}
