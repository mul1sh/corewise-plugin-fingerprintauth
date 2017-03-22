package android_serialport_api;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.os.Environment;
import android.util.Log;

public class FileUtil {
	private static String path = Environment.getExternalStorageDirectory()
			+ File.separator + "image_log.txt";
	public static void write(byte[] data) {
         File file = new File(path);
         RandomAccessFile raf = null;
         try {
			raf = new RandomAccessFile(file, "rw");
			if(data == null){
				return;
			}else{
				raf.seek(raf.length());
				raf.writeChars("***********size="+data.length+"\n");
				int start = 12;
				int length = (data.length-12)%139==0?(data.length-12)/139:(data.length-12)/139+1;
				byte[] temp;
				Log.i("whw", "****length="+length);
				for (int i = 0; i < length; i++) {
					if(i<length-1){
						temp = new byte[139];
						System.arraycopy(data, start+i*139, temp, 0, 139);
					}else{
						temp = new byte[(data.length-12)%139];
						System.arraycopy(data, start+i*139, temp, 0, temp.length);
					}
					String hexStr = DataUtils.toHexString(temp)+"\n";
					byte[] hexTemp = hexStr.getBytes();
					Log.i("whw", "hexTemp="+hexTemp.length);
//					raf.writeChars(hexStr+"\n");
					raf.seek(raf.length());
					raf.write(hexTemp, 0, hexTemp.length);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void write(String str) {
        File file = new File(path);
        RandomAccessFile raf = null;
        try {
			raf = new RandomAccessFile(file, "rw");
			if(str == null){
				return;
			}else{
				raf.seek(raf.length());
				raf.writeChars(str+"\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
