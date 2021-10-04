package idv.heimlich.IntegrationTesting.common.tester.utils;

import idv.heimlich.IntegrationTesting.common.log.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;

public class ChannelSftpUtils {
	
	public static String readFileContent(ChannelSftp channelSftp, String src) throws IOException {
		int tryTimeMax = 5;
		int nowTime = 0;
		StringBuffer content = new StringBuffer();
		while (true && nowTime++ < tryTimeMax) {// 小於測試測試
			System.out.println("start :" + src);
			try {
				InputStream in = channelSftp.get(src);
				byte[] tempbytes = new byte[100];
				int byteread = 0;
				while ((byteread = in.read(tempbytes)) != -1) {
					content.append(new String(tempbytes, 0, byteread));
				}
				in.close();
				break;
			} catch (SftpException e) {
				e.printStackTrace();
				LogFactory.getInstance().error("找不到檔案try 等30s (%d/%d)\n", nowTime, tryTimeMax);
				try {
					TimeUnit.SECONDS.sleep(30);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		return content.toString();

	}

	@SuppressWarnings("unchecked")
	public static List<String> searchFileName(ChannelSftp channelSftp, String likeName) {
		final List<String> files = new ArrayList<String>();
		try {

			final Vector<ChannelSftp.LsEntry> remoteFiles = channelSftp.ls(likeName);
			for (LsEntry lsEntry : remoteFiles) {
				files.add(lsEntry.getFilename());
			}
			return files;
		} catch (SftpException e) {
			e.printStackTrace();
		}
		return files;
	}

	public static List<String> searchFileName(ChannelSftp channelSftp, String likeName, int time) {
		final List<String> files = new ArrayList<>();
		for (int searchTime = 0; searchTime < 5; searchTime++) {
			final List<String> tmpfiles = searchFileName(channelSftp, likeName);
			if (tmpfiles.size() == 0) {

			} else {
				files.addAll(tmpfiles);
				break;
			}

		}
		return files;
	}

}
