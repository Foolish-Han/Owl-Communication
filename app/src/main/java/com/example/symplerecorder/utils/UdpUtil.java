package com.example.symplerecorder.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Optional;

public class UdpUtil {
    private final DatagramSocket socket;
    private static InetAddress address;
    private final int port;
    private final int TIMEOUT = 5000;

    public UdpUtil(String ipAddress, int port) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket(port);
        this.socket.setSoTimeout(TIMEOUT);
        address = InetAddress.getByName(ipAddress);
        this.port = port;
    }

    public void send(byte[] buffer) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
    }

    public Optional<byte[]> receive() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            return Optional.of(packet.getData());
        } catch (SocketTimeoutException e) {
            return Optional.empty();
        }
    }

    public static void updateIP(String ipAddress) throws UnknownHostException {
        address = InetAddress.getByName(ipAddress);
    }
    
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}

