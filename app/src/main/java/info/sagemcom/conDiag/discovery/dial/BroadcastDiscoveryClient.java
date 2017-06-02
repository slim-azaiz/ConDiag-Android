package info.sagemcom.conDiag.discovery.dial;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class BroadcastDiscoveryClient implements Runnable {

	private static final String LOG_TAG = "BroadcastDiscoveryClient";


	private static final int BROADCAST_SERVER_PORT = 1900;

	/**
	 * Frequency of probe messages.
	 */
	private static final int PROBE_INTERVAL_MS = 6000;
	private static final int PROBE_INTERVAL_MS_MAX = 60000;

	private static final String SEARCH_TARGET = "urn:dial-multiscreen-org:service:dial:1";

	private static final String M_SEARCH = "M-SEARCH * HTTP/1.1\r\n" + "HOST: 239.255.255.250:1900\r\n" + "MAN: \"ssdp:discover\"\r\n" + "MX: 10\r\n" + "ST: "
			+ SEARCH_TARGET + "\r\n\r\n";

	private static final String HEADER_LOCATION = "LOCATION";
	private static final String HEADER_ST = "ST";

	private final InetAddress mBroadcastAddress;

	private final Thread mBroadcastThread;
	private int mBroadcastInterval;
	private boolean mBroadcasting = true;
	private boolean mReceiving = true;

	/**
	 * Handle to main thread.
	 */
	private final Handler mHandler;

	/**
	 * Send/receive socket.
	 */
	private final DatagramSocket mSocket;

	/**
	 * Constructor
	 * 
	 * @param broadcastAddress
	 *            destination address for probes
	 * @param handler
	 *            update Handler in main thread
	 */
	public BroadcastDiscoveryClient(InetAddress broadcastAddress, Handler handler) {
		mReceiving = true;
		mBroadcastAddress = broadcastAddress;
		mHandler = handler;

		try {
			mSocket = new DatagramSocket(); // binds to random port
			mSocket.setBroadcast(true);
		} catch (SocketException e) {
			Log.e(LOG_TAG, "Could not create broadcast client socket.", e);
			throw new RuntimeException();
		}
		mBroadcastInterval = PROBE_INTERVAL_MS;
		mBroadcastThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (mBroadcasting) {
					try {
						BroadcastDiscoveryClient.this.sendProbe();
						try {
							Thread.sleep(mBroadcastInterval);
						} catch (InterruptedException e) {
						}
						mBroadcastInterval = mBroadcastInterval * 2;
						if (mBroadcastInterval > PROBE_INTERVAL_MS_MAX) {
							mBroadcastInterval = PROBE_INTERVAL_MS_MAX;
							mBroadcasting = false;
							mReceiving = false;
						}
					} catch (Throwable e) {
						Log.e(LOG_TAG, "run", e);
					}
				}
			}
		});
		Log.i(LOG_TAG, "Starting client on address " + mBroadcastAddress);
	}

	/** {@inheritDoc} */
	public void run() {
		Log.i(LOG_TAG, "Broadcast client thread starting.");
		byte[] buffer = new byte[4096];

		mBroadcastThread.start();

		while (mReceiving) {
			try {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				mSocket.receive(packet);
				handleResponsePacket(packet);
			} catch (InterruptedIOException e) {
				// timeout
			} catch (IOException e) {
				// SocketException - stop() was called
				break;
			} catch (IllegalArgumentException e) {
				break;
			}
		}
		Log.i(LOG_TAG, "Exiting client loop.");
		mBroadcasting = false;
		mBroadcastThread.interrupt();
	}

	/**
	 * Sends a single broadcast discovery request.
	 */
	private void sendProbe() {
		try {
			DatagramPacket packet = makeRequestPacket(mSocket.getLocalPort());
			mSocket.send(packet);
		} catch (Throwable e) {
			Log.e(LOG_TAG, "Exception sending broadcast probe", e);
			return;
		}
	}

	/**
	 * Immediately stops the receiver thread, and cancels the probe timer.
	 */
	public void stop() {
		if (mSocket != null) {
			mSocket.close();
		}
	}

	/**
	 * Constructs a new probe packet.
	 * 
	 * @param serviceName
	 *            the service name to discover
	 * @param responsePort
	 *            the udp port number for replies
	 * @return a new DatagramPacket
	 */
	private DatagramPacket makeRequestPacket(int responsePort) {
		String message = M_SEARCH;
		byte[] buf = message.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, mBroadcastAddress, BROADCAST_SERVER_PORT);
		return packet;
	}

	/**
	 * Parse a received packet, and notify the main thread if valid.
	 * 
	 * @param packet
	 *            The locally-received DatagramPacket
	 */
	private void handleResponsePacket(DatagramPacket packet) {
		try {
			String strPacket = new String(packet.getData(), 0, packet.getLength());
			Log.d(LOG_TAG, "response=" + strPacket);
			String tokens[] = strPacket.trim().split("\\n");

			String location = null;
			boolean foundSt = false;
			for (int i = 0; i < tokens.length; i++) {
				String token = tokens[i].trim();
				if (token.startsWith(HEADER_LOCATION)) {
					// LOCATION: http://192.168.0.51:47944/dd.xml
					location = token.substring(10).trim();
				} else if (token.startsWith(HEADER_ST)) {
					// ST: urn:dial-multiscreen-org:service:dial:1
					String st = token.substring(4).trim();
					if (st.equals(SEARCH_TARGET)) {
						foundSt = true;
					}
				}
			}

			if (!foundSt || location == null) {
				Log.w(LOG_TAG, "Malformed response: " + strPacket);
				return;
			}

			BroadcastAdvertisement advert;
			try {
				Uri uri = Uri.parse(location);
				InetAddress address = InetAddress.getByName(uri.getHost());
				advert = new BroadcastAdvertisement(location, address, uri.getPort());
			} catch (Exception e) {
				return;
			}

			Message message = mHandler.obtainMessage(ServerFinder.BROADCAST_RESPONSE, advert);
			mHandler.sendMessage(message);
		} catch (Exception e) {
			Log.e(LOG_TAG, "handleResponsePacket", e);
		}
	}

}
