package info.androidhive.gmail.discovery.dial;

import android.os.Parcel;
import android.os.Parcelable;
import java.net.InetAddress;

public class DialServer implements Parcelable {
	private String location;
	private InetAddress ipAddress;
	private int port;
	private String appsUrl;
	private String friendlyName;
	private String uuid;
	private String manufacturer;
	private String modelName;

	public DialServer() {

	}

	public DialServer(String location, InetAddress ipAddress, int port, String appsUrl, String friendlyName, String uuid, String manufacturer, String modelName) {
		this.location = location;
		this.ipAddress = ipAddress;
		this.port = port;
		this.appsUrl = appsUrl;
		this.friendlyName = friendlyName;
		this.uuid = uuid;
		this.manufacturer = manufacturer;
		this.modelName = modelName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public InetAddress getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAppsUrl() {
		return appsUrl;
	}

	public void setAppsUrl(String appsUrl) {
		this.appsUrl = appsUrl;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public DialServer clone() {
		return new DialServer(location, ipAddress, port, appsUrl, friendlyName, uuid, manufacturer, modelName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DialServer)) {
			return false;
		}
		DialServer that = (DialServer) obj;
		return equal(this.ipAddress, that.ipAddress) && (this.port == that.port);
	}

	private static <T> boolean equal(T obj1, T obj2) {
		if (obj1 == null) {
			return obj2 == null;
		}
		return obj1.equals(obj2);
	}

	@Override
	public String toString() {
		return String.format("%s [%s:%d]", friendlyName, ipAddress.getHostAddress(), port);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(location);
		parcel.writeSerializable(ipAddress);
		parcel.writeInt(port);
		parcel.writeString(appsUrl);
		parcel.writeString(friendlyName);
		parcel.writeString(uuid);
	}

	public static final Creator<DialServer> CREATOR = new Creator<DialServer>() {

		public DialServer createFromParcel(Parcel parcel) {
			return new DialServer(parcel);
		}

		public DialServer[] newArray(int size) {
			return new DialServer[size];
		}
	};

	private DialServer(Parcel parcel) {
		this(parcel.readString(), (InetAddress) parcel.readSerializable(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString());
	}
}
