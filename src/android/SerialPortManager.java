package android_serialport_api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android.os.SystemClock;
import android.util.Log;

public class SerialPortManager {

	/**
	 * ���ڲ�����
	 */
	private static int BAUDRATE = 460800;

	public static boolean switchRFID = false;

	final byte[] UP = { '1' };
	final byte[] DOWN = { '0' };

	private static final String[] PATHS = { "/dev/ttyHS1", "/dev/ttyHSL0",
			"/dev/ttyHSL0", "/dev/ttyHSL0", "/dev/ttyHSL0", "/dev/ttyHSL0",
			"/dev/ttyHSL0", "/dev/ttyHSL0" };

	private static final String[] GPIO_DEVS = { "/sys/GPIO/GPIO13/value",
			"/sys/class/pwv_gpios/pwv-seccpu/enable",
			"/sys/class/pwv_gpios/as602-en/enable",
			"/sys/class/pwv_gpios/as602-en/enable",
			"/sys/class/pwv_gpios/as602-en/enable",
			"/sys/class/pwv_gpios/as602-en/enable",
			"/sys/class/pwv_gpios/as602-en/enable",
			"/sys/class/pwv_gpios/as602-en/enable" };
	private static final String[] VERSION = { "M802", "M806", "A370",
			"COREWISE_V0", "CFON640", "ZTE Blade A460", "CFON640_50",
			"CFON640_43" };
	/**
	 * �����豸·�� louliang add 2016.02.04 start Ĭ��Ϊ��/dev/ttyHSL0
	 * /sys/class/pwv_gpios/as602-en/enable louliang add 2016.02.04 end
	 */
	private static String PATH = PATHS[4];
	private static String GPIO_DEV = GPIO_DEVS[4];
	static {
		Log.i("MODEL NAME=", "MODEL=" + android.os.Build.MODEL);
		// Toast.makeText(getApplicationContext(), "Ĭ��Toast��ʽ",
		// Toast.LENGTH_SHORT).show();
		for (int i = 0; i < VERSION.length; i++) {
			if (VERSION[i].equals(android.os.Build.MODEL)) {
				PATH = PATHS[i];
				// Log.i("ll AS602 POWER MODEL NAME=", "path="+PATH );

				GPIO_DEV = GPIO_DEVS[i];
				// Log.i("ll AS602 POWER MODEL NAME=", "gpio_dev ="+GPIO_DEV );
				break;
			}
		}
	}

	private static SerialPortManager mSerialPortManager = new SerialPortManager();

	private static final byte[] SWITCH_COMMAND = "D&C00040104".getBytes();

	private SerialPort mSerialPort = null;

	private boolean isOpen;

	private boolean firstOpen = false;

	private OutputStream mOutputStream;

	private InputStream mInputStream;

	private byte[] mBuffer = new byte[50 * 1024];

	private int mCurrentSize = 0;

	private ReadThread mReadThread;

	private SerialPortManager() {
	}

	/**
	 * ��ȡ�����ʵ������Ϊ����
	 * 
	 * @return
	 */
	public static SerialPortManager getInstance() {
		return mSerialPortManager;
	}

	public void setBaudrate(int baudrate) {
		BAUDRATE = baudrate;
	}

	/**
	 * �жϴ����Ƿ��
	 * 
	 * @return true���� false��δ��
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * �л��ɶ�ȡRFID
	 * 
	 * @return
	 */
	public void switchStatus() {
		if (!isOpen) {
			return;
		}
		write(SWITCH_COMMAND);
		Log.i("whw", "SWITCH_COMMAND hex=" + new String(SWITCH_COMMAND));
		SystemClock.sleep(200);
		if (!isOpen) {
			return;
		}
		switchRFID = true;
		Log.i("whw", "SWITCH_COMMAND end");
	}

	/**
	 * �򿪴��ڣ������Ҫ��ȡ���֤��ָ����Ϣ�������ȴ򿪴��ڣ����ô˷���
	 * 
	 * @throws SecurityException
	 * @throws IOException
	 * @throws InvalidParameterException
	 */
	public boolean openSerialPort() {
		if (mSerialPort == null) {
			// �ϵ�
			try {
				setUpGpio();
				Log.i("whw", "setUpGpio status=" + getGpioStatus());
				mSerialPort = new SerialPort(new File(PATH), BAUDRATE, 0);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			mReadThread = new ReadThread();
			mReadThread.start();
			isOpen = true;
			firstOpen = true;
			return true;
		}
		return false;
	}

	private boolean openSerialPort2() {
		if (mSerialPort == null) {
			try {
				mSerialPort = new SerialPort(new File(PATH), BAUDRATE, 0);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.i("whw", "mSerialPort=" + mSerialPort);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			mReadThread = new ReadThread();
			mReadThread.start();
			isOpen = true;
			firstOpen = true;
			return true;
		}
		return false;
	}

	/**
	 * �رմ��ڣ��������Ҫ��ȡָ�ƻ����֤��Ϣʱ���͹رմ���(���Խ�Լ��ص���)����������˳�ʱ�ر�
	 */
	public void closeSerialPort() {
		if (mReadThread != null)
			mReadThread.interrupt();
		mReadThread = null;
		try {
			// �ϵ�
			setDownGpio();
			Log.i("whw", "setDownGpio status=" + getGpioStatus());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (mSerialPort != null) {
			try {
				mOutputStream.close();
				mInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSerialPort.close();
			mSerialPort = null;
		}
		isOpen = false;
		firstOpen = false;
		mCurrentSize = 0;
		switchRFID = false;
		if (looperBuffer != null) {
			looperBuffer = null;
		}
	}

	private void closeSerialPort2() {
		if (mReadThread != null)
			mReadThread.interrupt();
		mReadThread = null;
		if (mSerialPort != null) {
			try {
				mOutputStream.close();
				mInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSerialPort.close();
			mSerialPort = null;
		}
		isOpen = false;
		firstOpen = false;
		mCurrentSize = 0;
		switchRFID = false;
		if (looperBuffer != null) {
			looperBuffer = null;
		}
	}

	protected synchronized int read(byte buffer[], int waittime, int interval) {
		if (!isOpen) {
			return 0;
		}
		int sleepTime = 5;
		int length = waittime / sleepTime;
		boolean shutDown = false;
		for (int i = 0; i < length; i++) {
			if (mCurrentSize == 0) {
				SystemClock.sleep(sleepTime);
				continue;
			} else {
				break;
			}
		}

		if (mCurrentSize > 0) {
			long lastTime = System.currentTimeMillis();
			long currentTime = 0;
			int lastRecSize = 0;
			int currentRecSize = 0;
			while (!shutDown && isOpen) {
				currentTime = System.currentTimeMillis();
				currentRecSize = mCurrentSize;
				if (currentRecSize > lastRecSize) {
					lastTime = currentTime;
					lastRecSize = currentRecSize;
				} else if (currentRecSize == lastRecSize
						&& currentTime - lastTime >= interval) {
					shutDown = true;
				}
			}
			if (mCurrentSize <= buffer.length) {
				System.arraycopy(mBuffer, 0, buffer, 0, mCurrentSize);
			}
		} else {
			// closeSerialPort2();
			SystemClock.sleep(100);
			// openSerialPort2();
		}
		return mCurrentSize;
	}

	protected synchronized int readFixedLength(byte buffer[], int waittime,
			int requestLength) {
		return readFixedLength(buffer, waittime, requestLength, 15);
	}

	protected synchronized int readFixedLength(byte buffer[], int waittime,
			int requestLength, int interval) {
		if (!isOpen) {
			return 0;
		}
		int sleepTime = 5;
		int length = waittime / sleepTime;
		boolean shutDown = false;
		for (int i = 0; i < length; i++) {
			if (mCurrentSize == 0) {
				SystemClock.sleep(sleepTime);
				continue;
			} else {
				break;
			}
		}

		if (mCurrentSize > 0) {
			long lastTime = System.currentTimeMillis();
			long currentTime = 0;
			int lastRecSize = 0;
			int currentRecSize = 0;
			while (!shutDown && isOpen) {
				if (mCurrentSize == requestLength) {
					shutDown = true;
				} else {
					currentTime = System.currentTimeMillis();
					currentRecSize = mCurrentSize;
					if (currentRecSize > lastRecSize) {
						lastTime = currentTime;
						lastRecSize = currentRecSize;
					} else if (currentRecSize == lastRecSize
							&& currentTime - lastTime >= interval) {
						shutDown = true;
					}
				}
			}

			if (mCurrentSize <= buffer.length) {
				System.arraycopy(mBuffer, 0, buffer, 0, mCurrentSize);
			}
		} else {
			closeSerialPort2();
			SystemClock.sleep(100);
			openSerialPort2();
		}
		return mCurrentSize;
	}

	private LooperBuffer looperBuffer;

	public void setLoopBuffer(LooperBuffer looperBuffer) {
		this.looperBuffer = looperBuffer;
	}

	private void writeCommand(byte[] data) {
		if (!isOpen) {
			return;
		}
		if (firstOpen) {
			SystemClock.sleep(2000);
			firstOpen = false;
		}
		mCurrentSize = 0;
		try {
			mOutputStream.write(data);
		} catch (IOException e) {
		}
	}

	protected synchronized void clearReceiveData() {
		mCurrentSize = 0;
	}

	public synchronized void write(byte[] data) {
		Log.i("whw", "send commnad=" + DataUtils.toHexString(data));
		writeCommand(data);
	}

	private void setUpGpio() throws IOException {
		FileOutputStream fw = new FileOutputStream(GPIO_DEV);
		fw.write(UP);
		fw.close();
	}

	private void setDownGpio() throws IOException {
		FileOutputStream fw = new FileOutputStream(GPIO_DEV);
		fw.write(DOWN);
		fw.close();
	}

	public String getGpioStatus() throws IOException {
		String value;
		BufferedReader br = null;
		FileInputStream inStream = new FileInputStream(GPIO_DEV);
		br = new BufferedReader(new InputStreamReader(inStream));
		value = br.readLine();
		inStream.close();
		return value;

	}

	private class ReadThread extends Thread {

		@Override
		public void run() {
			byte[] buffer = new byte[512];
			while (!isInterrupted()) {
				int length = 0;
				try {
					if (mInputStream == null)
						return;
					length = mInputStream.read(buffer);
					if (length > 0) {
						if (looperBuffer != null) {
							byte[] buf = new byte[length];
							System.arraycopy(buffer, 0, buf, 0, length);
							Log.i("xuws",
									"recv buf=" + DataUtils.toHexString(buf));
							looperBuffer.add(buf);
						} // else {
						System.arraycopy(buffer, 0, mBuffer, mCurrentSize,
								length);
						mCurrentSize += length;
						// }
						Log.i("whw", "mCurrentSize=" + mCurrentSize
								+ "  length=" + length);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

}
