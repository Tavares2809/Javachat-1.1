/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.GUI;
import common.Utils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author juaninha
 */
public class Chat extends GUI {

    private JLabel jl_title;
    private JEditorPane messages;
    private JTextField jt_message;
    private JButton jb_message, jb_emoji;
    private JPanel panel;
    private JScrollPane scroll;
    private JFrame frame_Emoji;
    private JList emoji_list;
    private  Map<String, ImageIcon> imageMap;
    private ArrayList<String> message_list;
    private Home home;
    private Socket connection;
    private String connection_info;

    public Chat(Home home, Socket connection, String connection_info, String title) {
        super("Chat " + title);
        this.home = home;
        this.connection_info = connection_info;
        message_list = new ArrayList<String>();
        this.connection = connection;
        this.jl_title.setText(connection_info.split(":")[0]);
        this.jl_title.setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void initComponents() {
        jl_title = new JLabel();
        messages = new JEditorPane();
        scroll = new JScrollPane(messages);
        jt_message = new JTextField();
        jb_message = new JButton("Enviar");
        jb_emoji = new JButton("Emojis");
        panel = new JPanel(new BorderLayout());
    }

    @Override
    protected void configComponents() {
        this.setMinimumSize(new Dimension(480, 720));
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        messages.setContentType("text/html");
        messages.setEditable(false);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jb_message.setSize(80, 40);
        jt_message.setSize(70, 40);
    }

    @Override
    protected void insertComponents() {
        this.add(jl_title, BorderLayout.NORTH);
        this.add(scroll, BorderLayout.CENTER);
        this.add(panel, BorderLayout.SOUTH);
        panel.add(jt_message, BorderLayout.CENTER);
        panel.add(jb_message, BorderLayout.EAST);
        panel.add(jb_emoji, BorderLayout.BEFORE_FIRST_LINE);
    }

    @Override
    protected void insertActions() {
        jt_message.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    send();
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

        });
        jb_message.addActionListener(event -> send());
        jb_emoji.addActionListener(event -> emojis());
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                Utils.sendMessage(connection, "CHAT_CLOSE");
                home.getOpened_chats().remove(connection_info);
                home.getConnected_listeners().get(connection_info).setChatOpen(false);
                home.getConnected_listeners().get(connection_info).setRunning(false);
                home.getConnected_listeners().remove(connection_info);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }

        });
    }

    public void append_message(String received) {
        message_list.add(received);
        String message = "";
        for (String str : message_list) {
            message += str;
        }
        messages.setText(message);
    }

    @Override
    protected void start() {
        this.pack();
        this.setVisible(true);
    }

    private void send() {
        DateFormat df = new SimpleDateFormat("hh:mm:ss");
        message_list.add("<b>[" + df.format(new Date()) + "] Eu: </b><i>" + jt_message.getText() + "</i><br>");
        Utils.sendMessage(connection, "MESSAGE;<b>[" + df.format(new Date()) + "] " + home.getConnection_info().split(":")[0] + ": </b><i>" + jt_message.getText() + "</i><br>");
        String message = "";
        for (String str : message_list) {
            message += str;
        }
        messages.setText(message);
        jt_message.setText("");

    }

    private void emojis() {
        String[] nameList = {"Emoji 1", "Emoji 2", "Emoji 3", "Emoji 4", "Emoji 5"};
        imageMap = createImageMap(nameList);
        JList list = new JList(nameList);
        list.setCellRenderer(new EmojiListRenderer()); 
        
        
        
        
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(300, 400));

        JFrame frame = new JFrame();
        frame.add(scroll);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
       
        
        
        
    }
    public class EmojiListRenderer extends DefaultListCellRenderer {

        Font font = new Font("helvitica", Font.BOLD, 24);

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            label.setIcon(imageMap.get((String) value));
            label.setHorizontalTextPosition(JLabel.RIGHT);
            label.setFont(font);
            return label;
        }
    }
     private Map<String, ImageIcon> createImageMap(String[] list) {
        Map<String, ImageIcon> map = new HashMap<>();
       try {
            map.put("Emoji 1", new ImageIcon(new URL("http://icons.iconarchive.com/icons/designbolts/emoji/32/Emoji-Blushing-icon.png")));
            map.put("Emoji 2", new ImageIcon(new URL("http://icons.iconarchive.com/icons/designbolts/emoji/32/Emoji-Blushing-icon.png")));
            map.put("Emoji 3", new ImageIcon(new URL("http://icons.iconarchive.com/icons/designbolts/emoji/32/Emoji-Blushing-icon.png")));
            map.put("Emoji 4", new ImageIcon(new URL("http://icons.iconarchive.com/icons/designbolts/emoji/32/Emoji-Blushing-icon.png")));
            map.put("Emoji 5", new ImageIcon(new URL("http://icons.iconarchive.com/icons/designbolts/emoji/32/Emoji-Blushing-icon.png")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
    private void sendEmoji() {
        DateFormat df = new SimpleDateFormat("hh:mm:ss");
        message_list.add("<b>[" + df.format(new Date()) + "] Eu: </b><i>" + jt_message.getText() + "</i><br>");
        Utils.sendMessage(connection, "MESSAGE;<b>[" + df.format(new Date()) + "] " + home.getConnection_info().split(":")[0] + ": </b><i>" + jt_message.getText() + "</i><br>");
        String message = "";
        for (String str : message_list) {
            message += str;
        }
        messages.setText(message);
        jt_message.setText("");

    }
    
}
