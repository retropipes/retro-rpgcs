/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.puttysoftware.retrorpgcs.resourcemanagers.LogoManager;

public class AboutDialog {
    private class EventHandler implements ActionListener {
        EventHandler() {
            // Do nothing
        }

        // Handle buttons
        @Override
        public void actionPerformed(final ActionEvent e) {
            try {
                final var ad = AboutDialog.this;
                final var cmd = e.getActionCommand();
                if (cmd.equals("OK")) {
                    ad.hideAboutDialog();
                }
            } catch (final Exception ex) {
                RetroRPGCS.getInstance().handleError(ex);
            }
        }
    }

    // Fields
    private JFrame aboutFrame;

    // Constructors
    public AboutDialog(final String ver) {
        this.setUpGUI(ver);
    }

    void hideAboutDialog() {
        this.aboutFrame.setVisible(false);
    }

    private void setUpGUI(final String ver) {
        final var handler = new EventHandler();
        this.aboutFrame = new JFrame("About RetroRPGCS");
        final Image iconlogo = LogoManager.getIconLogo();
        this.aboutFrame.setIconImage(iconlogo);
        final var aboutPane = new Container();
        final var textPane = new Container();
        final var buttonPane = new Container();
        final var logoPane = new Container();
        final var aboutOK = new JButton("OK");
        final var miniLabel = new JLabel("", LogoManager.getMiniatureLogo(),
                SwingConstants.LEFT);
        miniLabel.setLabelFor(null);
        aboutOK.setDefaultCapable(true);
        this.aboutFrame.getRootPane().setDefaultButton(aboutOK);
        this.aboutFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        aboutPane.setLayout(new BorderLayout());
        logoPane.setLayout(new FlowLayout());
        logoPane.add(miniLabel);
        textPane.setLayout(new GridLayout(4, 1));
        textPane.add(new JLabel("RetroRPGCS Version: " + ver));
        textPane.add(new JLabel("Author: Eric Ahnell"));
        textPane.add(new JLabel(
                "Web Site: http://www.puttysoftware.com/retrorpgcs/"));
        textPane.add(new JLabel(
                "E-mail bug reports to: products@puttysoftware.com  "));
        buttonPane.setLayout(new FlowLayout());
        buttonPane.add(aboutOK);
        aboutPane.add(logoPane, BorderLayout.WEST);
        aboutPane.add(textPane, BorderLayout.CENTER);
        aboutPane.add(buttonPane, BorderLayout.SOUTH);
        this.aboutFrame.setResizable(false);
        aboutOK.addActionListener(handler);
        this.aboutFrame.setContentPane(aboutPane);
        this.aboutFrame.pack();
    }

    // Methods
    public void showAboutDialog() {
        this.aboutFrame.setVisible(true);
    }
}