package com.uol.mml.cmds;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import com.jcraft.jsch.*;

public class MMLCommandExecutor {

    public static void main(String[] args) {
        String hostname = "172.24.16.180"; // Replace with the actual hostname or IP address
        String username = "BSS_user"; // Replace with your SSH username
        String password = "VoLTE12\\#\\$"; // Replace with your SSH password VoLTE12\#\$
        int port = 8080; // SSH port (default is 22)

        String mmlCommand = "show version"; // Replace with your actual MML command

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, hostname, port);
            session.setPassword(password);

            // Avoid strict host key checking (for demonstration purposes)
            session.setConfig("StrictHostKeyChecking", "no");

            session.connect();

            // Execute the MML command
            Channel channel = session.openChannel("exec");
            
            ((ChannelExec) channel).setCommand("mmlsh " + mmlCommand);

            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            // Read the output from the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("=======>"+line);
            }

            channel.disconnect();
            session.disconnect();
        } catch (JSchException | IOException e) {
            System.out.println("ssh connection issue:----->"+e.getMessage());
        }
    }
}

