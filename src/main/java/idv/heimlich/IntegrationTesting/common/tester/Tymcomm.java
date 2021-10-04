package idv.heimlich.IntegrationTesting.common.tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.tradevan.common.exception.ApBusinessException;
import com.tradevan.commons.io.FileUtil;
import com.tradevan.commons.logging.LogLevel;
import com.tradevan.commons.logging.Logger;
import com.tradevan.event.callback.TransportEventCallback;
import com.tradevan.event.listener.EventListener;
import com.tradevan.event.type.BaseGatewayEvent.Type;
import com.tradevan.event.type.TransportEvent;
import com.tradevan.exception.GatewayException;
import com.tradevan.logger.GatewayLogger;
import com.tradevan.logger.GatewayLoggerHandler;
import com.tradevan.transport.DolphinTransporter;
import com.tradevan.transport.TransportException;
import com.tradevan.transport.bo.DolphinTransportConfig;
import com.tradevan.transport.bo.DolphinTransportInfo;
import com.tradevan.transport.bo.TransportInfo;
import com.tradevan.transport.proc.SFTPPacketHandler;
import com.tradevan.tymcommJ.TymcommEventHandler;
import com.tradevan.tymcommJ.TymcommException;
import com.tradevan.util.GatewayUtil;
import com.tradevan.util.ProxyUtil;
import com.tradevan.util.crypt.EncryptCommonService;
import com.tradevan.util.enums.ErrorCodeEnum;
import com.tradevan.util.enums.ServiceTypeEnum;
import com.tradevan.util.enums.TransportEnum;

public class Tymcomm {
	public static final String VERSION = "T2.2.6";
	public static final String DEF_LOG_PATH;
	public static final int MAX_SEND_FILE_SIZE = 10485760;
	public static final String ENABLE = "Y";
	private final GatewayLoggerHandler logger = new GatewayLoggerHandler();
	public static final String TRANS_TYPE = "TransType";
	public static final String FTP_SERVER1 = "FtpServer1";
	public static final String FTP_SERVER2 = "FtpServer2";
	public static final String FTP_SERVER3 = "FtpServer3";
	public static final String FTP_PORT = "FtpPort";
	public static final String SFTP_SERVER1 = "sFtpServer1";
	public static final String SFTP_SERVER2 = "sFtpServer2";
	public static final String SFTP_SERVER3 = "sFtpServer3";
	public static final String SFTP_PORT = "sFtpPort";
	public static final String PARTNER_RECEIVER_ID = "Partner_ReceiverId";
	public static final String LOG_FILE = "LogFile";
	public static final String UPLD_PATH = "UpldPath";
	public static final String UPLD_BACK_UP = "UpldBackUp";
	public static final String SEND_INFO_FILE = "SendInfoFile";
	public static final String RECV_INFO_FILE = "RecvInfoFile";
	public static final String RECV_ACK = "RecvAck";
	public static final String FIX_SEND_FILE_NAME = "FixSendFileName";
	public static final String ENABL_LOG_FILE = "EnableLogFile";
	public static final String Log_Level = "LogLevel";
	private Properties prop = null;
	private List<String> serverIpList = new ArrayList<String>();
	private int serverPort = -1;
	private int maxSendFileSize = 0;
	private int maxSendFileIndex = -1;
	private boolean needEDIFlag = true;
	private DolphinTransportConfig transConfig = null;
	public TransportEventCallback transportEventCallback = new TransportEventCallback();
	private EventListener listener = null;

	static {
		DEF_LOG_PATH = "log" + File.separator + "tymcomm.log";
	}

	private Tymcomm() {
		this.prop = new Properties();
		this.prop.setProperty("VERSION", "T2.2.6");
	}

	public Tymcomm(String arg0) {
		final String arg1 = Thread.currentThread().getStackTrace()[1].getMethodName();
		final String arg2 = GatewayUtil.chkFile(arg0, "configFile", false, true);
		if (!GatewayUtil.isBlankOrNull(arg2)) {
			final TymcommException arg3 = new TymcommException(this.getClass(), arg1, ErrorCodeEnum.CONFIG_IS_NOT_EXIST,
					ErrorCodeEnum.CONFIG_IS_NOT_EXIST.getErrorMessage() + ", " + arg2);
																						
			throw new IllegalArgumentException(ErrorCodeEnum.CONFIG_IS_NOT_EXIST.getErrorMessage(), arg3);
		} else {
			this.prop = new Properties();
			this.prop.setProperty("VERSION", "T2.2.6");
			this.loadConfig(arg0, (String) null);
		}
	}

	public Tymcomm(Properties arg0) {
		final String arg1 = Thread.currentThread().getStackTrace()[1].getMethodName();
		if (arg0 != null && arg0.size() != 0) {
			this.prop = new Properties();
			if (GatewayUtil.isBlankOrNull(arg0.getProperty("VERSION"))) {
				this.prop.setProperty("VERSION", "T2.2.6");
			} else {
				this.prop.setProperty("VERSION", arg0.getProperty("VERSION"));
			}

			this.loadConfig(arg0, (Properties) null);
			if (GatewayUtil.isBlankOrNull(this.prop.getProperty("DialogueId"))) {
				this.prop.setProperty("DialogueId", ServiceTypeEnum.SFTP.getValue());
			}

		} else {
			final TymcommException arg2 = new TymcommException(this.getClass(), arg1, ErrorCodeEnum.CONFIG_IS_NOT_EXIST);
			throw new IllegalArgumentException(ErrorCodeEnum.CONFIG_IS_NOT_EXIST.getErrorMessage(), arg2);
		}
	}

	public Tymcomm(DolphinTransportConfig arg0) {
		final String arg1 = Thread.currentThread().getStackTrace()[1].getMethodName();
		if (arg0 == null) {
			final TymcommException arg2 = new TymcommException(this.getClass(), arg1, ErrorCodeEnum.CONFIG_IS_NOT_EXIST);
			throw new IllegalArgumentException("DolphinTransportConfig can not be null", arg2);
		} else {
			this.transConfig = arg0;
		}
	}

	public int transmit(String arg0) {
		this.loadConfig((String) null, arg0);
		return this.transmit();
	}

	public int transmit(Properties arg0) {
		this.loadConfig((Properties) null, arg0);
		return this.transmit();
	}

	public void setServerIpList(List<String> arg0) {
		this.serverIpList = arg0;
	}

	public void addServerIpList(String arg0) {
		if (this.serverIpList == null) {
			this.serverIpList = new ArrayList<String>();
		}

		this.serverIpList.add(arg0);
	}

	public void setServerPort(int arg0) {
		this.serverPort = arg0;
	}

	public void setNeedEDIFlag(boolean arg0) {
		this.needEDIFlag = arg0;
	}

	private void loadConfig(Properties arg0, Properties arg1) {
		if (arg0 != null) {
			this.prop.putAll(arg0);
		}

		if (arg1 != null) {
			this.prop.putAll(arg1);
			this.prop.setProperty("DialogueId", ServiceTypeEnum.SFTP.getValue());
		}

	}

	private void loadConfig(String arg0, String arg1) {
		final String arg2 = Thread.currentThread().getStackTrace()[1].getMethodName();

		try {
			File arg3;
			if (!GatewayUtil.isBlankOrNull(arg0)) {
				arg3 = new File(arg0);
				this.prop.setProperty("config", arg0);
				if (!arg3.exists()) {
					throw new TymcommException(this.getClass(), arg2, ErrorCodeEnum.CONFIG_LOADING_FAILS,
							ErrorCodeEnum.CONFIG_LOADING_FAILS.getErrorMessage() + ", config file ["
									+ arg3.getAbsolutePath() + "] not found...");
																					
				}

				try {
					EncryptCommonService.readConfig(arg3, this.prop);
					System.out.println("Read config file success!");
				} catch (final Exception arg6) {
					throw new TymcommException(this.getClass(), arg2, ErrorCodeEnum.CONFIG_LOADING_FAILS,
							ErrorCodeEnum.CONFIG_LOADING_FAILS.getErrorMessage() + ", " + arg3.getAbsolutePath());
																													
				}
			}

			if (!GatewayUtil.isBlankOrNull(arg1)) {
				arg3 = new File(arg1);
				this.prop.setProperty("sftpConfig", arg1);
				if (!arg3.exists()) {
					throw new TymcommException(this.getClass(), arg2, ErrorCodeEnum.CONFIG_LOADING_FAILS,
							ErrorCodeEnum.CONFIG_LOADING_FAILS.getErrorMessage() + ", sftpconfig file ["
									+ arg3.getAbsolutePath() + "] not found...");
																					
				}

				try {
					EncryptCommonService.readConfig(arg3, this.prop);
					this.prop.setProperty("DialogueId", ServiceTypeEnum.SFTP.getValue());
					System.out.println("Read sftp config file success!");
				} catch (final Exception arg5) {
					throw new TymcommException(this.getClass(), arg2, ErrorCodeEnum.CONFIG_LOADING_FAILS,
							ErrorCodeEnum.CONFIG_LOADING_FAILS.getErrorMessage() + ", " + arg3.getAbsolutePath());
																													
				}
			}

		} catch (final TymcommException arg7) {
			throw new IllegalArgumentException(ErrorCodeEnum.CONFIG_LOADING_FAILS.getErrorMessage(), arg7);
		}
	}

	private int transmit() {
		final String arg0 = Thread.currentThread().getStackTrace()[1].getMethodName();
//		final boolean arg1 = false;
		this.setSystemProp("TransType");
		this.setSystemProp("DnldPath");
		this.setSystemProp("DnldFile");
		this.setSystemProp("SSL");
		this.setSystemProp("EnableLogFile");
		final String arg2 = this.prop.getProperty("EnableLogFile");
		boolean arg3 = true;
		if (GatewayUtil.isBlankOrNull(arg2)) {
			arg3 = true;
		} else if ("Y".equalsIgnoreCase(arg2)) {
			arg3 = true;
		} else {
			arg3 = false;
		}

		String arg4;
		if (arg3) {
			if (this.logger.getLogger() == null) {
				this.setSystemProp("LogFile");
				arg4 = this.prop.getProperty("LogFile");
				if (GatewayUtil.isBlankOrNull(arg4)) {
					this.setLogger(DEF_LOG_PATH);
				} else {
					this.setLogger(arg4);
				}
			}

			if (this.prop.getProperty("LogLevel") != null && !this.prop.getProperty("LogLevel").equals("")) {
				this.logger.getLogger().setLevel(LogLevel.parse(this.prop.getProperty("LogLevel")));
			}
		}

		this.subscribeTransportEvent(new TymcommEventHandler(this.logger));

		int arg10;
		try {
			this.logger.info("Prepare transmitting ( T2.2.6 ) ...", this.getClass(), arg0);
			this.chkProp();
			this.logger.info("Check properties success! (config=" + this.prop.getProperty("config") + " ,sftpConfig="
					+ this.prop.getProperty("sftpConfig") + ")", this.getClass(), arg0);
			this.transConfig = this.makeDolphinTransportConfig(this.prop);
			arg4 = this.prop.getProperty("TransType");
			List<DolphinTransportInfo> arg5;
			if (TransportEnum.SEND.getValue().equalsIgnoreCase(arg4)) {
				this.transConfig.setTransportId(this.prop.getProperty("SenderId"));
				arg5 = null;
				if (!GatewayUtil.isBlankOrNull(this.prop.getProperty("SendInfoFile"))) {
					arg5 = this.makeSendDolphinTransportInfo(this.transConfig, this.prop.getProperty("SendInfoFile"));
				} else {
					arg5 = this.makeSendDolphinTransportInfo(this.prop);
				}

				this.sendProcess(arg5);
				this.backSendFile(arg5);
			} else {
				DolphinTransportInfo arg11;
				if (TransportEnum.RECEIVE.getValue().equalsIgnoreCase(arg4)) {
					this.transConfig.setTransportId(this.prop.getProperty("ReceiverId"));
					arg11 = this.makeDolphinTransportInfo(false, this.prop, (File) null);
					this.recvProcess(arg11);
				} else {
					DolphinTransportInfo arg6;
					if (TransportEnum.SEND_RECEIVE.getValue().equalsIgnoreCase(arg4)) {
						this.transConfig.setTransportId(this.prop.getProperty("SenderId"));
						arg5 = null;
						if (!GatewayUtil.isBlankOrNull(this.prop.getProperty("SendInfoFile"))) {
							arg5 = this.makeSendDolphinTransportInfo(this.transConfig,
									this.prop.getProperty("SendInfoFile"));
						} else {
							arg5 = this.makeSendDolphinTransportInfo(this.prop);
						}

						this.sendProcess(arg5);
						this.backSendFile(arg5);
						this.transConfig.setTransportId(this.prop.getProperty("ReceiverId"));
						arg6 = this.makeDolphinTransportInfo(false, this.prop, (File) null);
						this.recvProcess(arg6);
					} else if (TransportEnum.RECEIVE_SEND.getValue().equalsIgnoreCase(arg4)) {
						this.transConfig.setTransportId(this.prop.getProperty("ReceiverId"));
						arg11 = this.makeDolphinTransportInfo(false, this.prop, (File) null);
						this.recvProcess(arg11);
						this.transConfig.setTransportId(this.prop.getProperty("SenderId"));
						arg6 = null;
						List<DolphinTransportInfo> arg12;
						if (!GatewayUtil.isBlankOrNull(this.prop.getProperty("SendInfoFile"))) {
							arg12 = this.makeSendDolphinTransportInfo(this.transConfig,
									this.prop.getProperty("SendInfoFile"));
						} else {
							arg12 = this.makeSendDolphinTransportInfo(this.prop);
						}

						this.sendProcess(arg12);
						this.backSendFile(arg12);
					}
				}
			}

			this.logger.info("Transmission complete!", this.getClass(), arg0);
			return ErrorCodeEnum.NO_ERR.getErrValue();
		} catch (final TransportException arg7) {
			this.logger.error("Transmission Fail occurs TransportException!", arg7, this.getClass(), arg0);
			arg10 = arg7.getErrCode().getErrValue();
		} catch (final TymcommException arg8) {
			this.logger.error("Transmission Fail occurs TymcommException!", arg8, this.getClass(), arg0);
			arg10 = arg8.getErrCode().getErrValue();
		} catch (final GatewayException arg9) {
			this.logger.error("Transmission Fail occurs GatewayException!", arg9, this.getClass(), arg0);
			arg10 = arg9.getErrCode().getErrValue();
		}

		return arg10 != ErrorCodeEnum.DOLPHIN_HEADER_ERR.getErrValue() && arg10 != ErrorCodeEnum.PACK_ERR.getErrValue()
				&& arg10 != ErrorCodeEnum.UNPACK_ERR.getErrValue() && arg10 != ErrorCodeEnum.SEND_FAIL.getErrValue()
				&& arg10 != ErrorCodeEnum.RECEIVE_ERR.getErrValue()
				&& arg10 != ErrorCodeEnum.CLOSE_SOCKET_ERR.getErrValue()
				&& arg10 != ErrorCodeEnum.CONNECT_ERR.getErrValue() && arg10 != ErrorCodeEnum.READ_ERR.getErrValue()
				&& arg10 != ErrorCodeEnum.WRITE_ERR.getErrValue() ? arg10 : ErrorCodeEnum.RECEIVE_FAIL.getErrValue();
																														
																														
																														
																														
																														
																														
																														
																														
																														
	}

	public List<DolphinTransportInfo> sendProcess(List<DolphinTransportInfo> arg0) throws TransportException,
			TymcommException {
		final String arg1 = Thread.currentThread().getStackTrace()[1].getMethodName();
		this.logger.info("Begin send process!", this.getClass(), arg1);
		ArrayList<DolphinTransportInfo> arg2 = null;
		final ArrayList<DolphinTransportInfo> arg3 = new ArrayList<DolphinTransportInfo>();
		final DolphinTransporter arg4 = new DolphinTransporter(this.transConfig);
		arg4.subscribeTransportEvent(this.listener);
		arg4.setLogger(this.logger.getLogger());
		int arg5 = 0;
		int arg6 = 0;
		if (arg0.size() == 0) {
			this.transportEventCallback.triggerSendNoData(new TransportEvent(this.getClass(), arg1, Type.TYPE_INFO,
					"No file to send", this.transConfig, (TransportInfo) null));
			if (ServiceTypeEnum.SFTP != this.transConfig.getServiceType()) {
				this.writeSendFlagFiles();
			}
		} else {
			arg2 = new ArrayList<DolphinTransportInfo>();
			this.maxSendFileSize = this.getMaxSendFileSize();
			this.maxSendFileIndex = this.getMaxSendFileIndex();
			final Iterator<DolphinTransportInfo> arg8 = arg0.iterator();

			while (arg8.hasNext()) {
				final DolphinTransportInfo arg7 = (DolphinTransportInfo) arg8.next();
				if (arg5 >= this.maxSendFileSize) {
					arg3.addAll(arg4.send(arg2));
					arg2 = new ArrayList<DolphinTransportInfo>();
					arg5 = 0;
				} else if (this.maxSendFileIndex > 0 && arg6 >= this.maxSendFileIndex) {
					arg3.addAll(arg4.send(arg2));
					arg2 = new ArrayList<DolphinTransportInfo>();
					arg6 = 0;
				}

				TymcommException arg9;
				if (arg7.getFileName().length() > 0L) {
					if (!this.transConfig.getNotSendHiddenFile()) {
						arg2.add(arg7);
						arg5 = (int) (arg5 + arg7.getFileName().length());
						++arg6;
					} else if (!arg7.getFileName().isHidden()) {
						arg2.add(arg7);
						arg5 = (int) (arg5 + arg7.getFileName().length());
						++arg6;
					} else {
						arg9 = new TymcommException(this.getClass(), arg1, ErrorCodeEnum.SEND_FAIL,
								"File is a hidden file. path = " + arg7.getFileName().getAbsolutePath());
						arg7.setErrorCode(arg9.getErrCode());
						arg7.setEndDts(GatewayUtil.getDateTime());
						arg7.setException(arg9);
						this.transportEventCallback.triggerSendFail(new TransportEvent(this.getClass(), arg1,
								Type.TYPE_ERROR, arg9.getMessage(), this.transConfig, arg7));
					}
				} else {
					arg9 = new TymcommException(this.getClass(), arg1, ErrorCodeEnum.SEND_FAIL, "File size 0, file = "
							+ arg7.getFileName().getAbsolutePath());
					arg7.setErrorCode(arg9.getErrCode());
					arg7.setEndDts(GatewayUtil.getDateTime());
					arg7.setException(arg9);
					this.transportEventCallback.triggerSendingSize0(new TransportEvent(this.getClass(), arg1,
							Type.TYPE_ERROR, arg9.getMessage(), this.transConfig, arg7));
				}
			}
		}

		if (arg2 != null && arg2.size() > 0) {
			arg3.addAll(arg4.send(arg2));
		}

		this.logger.info("End send process!", this.getClass(), arg1);
		return arg3;
	}

	private void writeSendFlagFiles() throws TymcommException {
		final String arg0 = Thread.currentThread().getStackTrace()[1].getMethodName();

		try {
			FileUtil.write(new File("MDCLOG.flg"), "<< FROM TYMCOMM: NO FILE TO SEND; NO CONNECTION MADE >>".getBytes());
			FileUtil.write(new File("MDCXMIT.flg"), "<< FROM TYMCOMM: NO FILE TO SEND >>".getBytes());
		} catch (final IOException arg2) {
			throw new TymcommException(this.getClass(), arg0, ErrorCodeEnum.IO_EXCEPTION_HAPPENED, arg2);
		}
	}

	public List<DolphinTransportInfo> recvProcess(DolphinTransportInfo arg0) throws TransportException {
		final String arg1 = Thread.currentThread().getStackTrace()[1].getMethodName();
		this.logger.info("Begin receive process!", this.getClass(), arg1);
		final DolphinTransporter arg2 = new DolphinTransporter(this.transConfig);
		arg2.subscribeTransportEvent(this.listener);
		arg2.setLogger(this.logger.getLogger());
		final List<DolphinTransportInfo> arg3 = arg2.receive(arg0);
		this.logger.info("End receive process!", this.getClass(), arg1);
		return arg3;
	}

	private void setSystemProp(String arg0) {
		if (!GatewayUtil.isBlankOrNull(System.getProperty(arg0))) {
			this.prop.setProperty(arg0, System.getProperty(arg0));
		}

	}

	private void chkProp() throws TymcommException {
		final String arg0 = Thread.currentThread().getStackTrace()[1].getMethodName();
		final StringBuffer arg1 = new StringBuffer();
		String arg2 = "";
		String arg3 = "";
		String arg4 = "";
		final String[] arg5 = new String[] { TransportEnum.SEND.getValue(), TransportEnum.RECEIVE.getValue(),
				TransportEnum.SEND_RECEIVE.getValue(), TransportEnum.RECEIVE_SEND.getValue() };
		final String arg6 = this.prop.getProperty("TransType");
		if (GatewayUtil.isBlankOrNull(arg6)) {
			arg2 = GatewayUtil.chkTypes(arg6, "TransType", arg5, false);
			arg1.append(arg2);
		} else {
			arg2 = GatewayUtil.chkTypes(arg6, "TransType", arg5, false);
			arg1.append(arg2);
		}

		arg1.append(GatewayUtil.chkString(this.prop.getProperty("UserID"), "UserID"));
		arg1.append(GatewayUtil.chkString(this.prop.getProperty("UserPWD"), "UserPWD"));
		final String[] arg7 = new String[] { ServiceTypeEnum.SFTP.getValue(), ServiceTypeEnum.EDI.getValue(),
				ServiceTypeEnum.PEDI.getValue() };
		final String arg8 = this.prop.getProperty("DialogueId");
		arg3 = GatewayUtil.chkTypes(arg8, "DialogueId", arg7, true);
		arg1.append(arg3);
		String arg9 = "";
		if (ServiceTypeEnum.SFTP.getValue().equalsIgnoreCase(arg8)) {
			if (this.serverIpList == null || this.serverIpList.size() == 0) {
				arg1.append(GatewayUtil.chkString(this.prop.getProperty("sFtpServer1"), "sFtpServer1"));
			}

			if (this.serverPort == -1) {
				arg9 = this.prop.getProperty("sFtpPort");
				if (GatewayUtil.isBlankOrNull(arg9)) {
					arg1.append(GatewayUtil.chkInteger(arg9, "sFtpPort", false));
				} else {
					arg1.append(GatewayUtil.chkInteger(arg9, "sFtpPort", false));
				}
			}
		} else {
			if (this.serverIpList == null || this.serverIpList.size() == 0) {
				arg1.append(GatewayUtil.chkString(this.prop.getProperty("FtpServer1"), "FtpServer1"));
			}

			if (this.serverPort == -1) {
				arg9 = this.prop.getProperty("FtpPort");
				if (GatewayUtil.isBlankOrNull(arg9)) {
					arg1.append(GatewayUtil.chkInteger(arg9, "FtpPort", false));
				} else {
					arg1.append(GatewayUtil.chkInteger(arg9, "FtpPort", false));
				}
			}
		}

		if (GatewayUtil.isBlankOrNull(arg2)) {
			String arg10;
			if (arg6.toUpperCase().indexOf("S") >= 0) {
				arg10 = this.prop.getProperty("SendInfoFile");
				if (GatewayUtil.isBlankOrNull(arg10)) {
					final String arg11 = this.prop.getProperty("UpldPath");
					if (GatewayUtil.isBlankOrNull(arg11)) {
						arg4 = GatewayUtil.chkDir(arg11, "UpldPath", false);
					} else {
						arg4 = GatewayUtil.chkDir(arg11, "UpldPath", false);
					}

					if (GatewayUtil.isBlankOrNull(arg4)) {
						if (!GatewayUtil.isBlankOrNull(this.prop.getProperty("FixSendFileName"))) {
							arg1.append(GatewayUtil.chkFile(this.prop.getProperty("UpldPath") + File.separator
									+ this.prop.getProperty("FixSendFileName"), "FixSendFileName", true, true));
						}
					} else {
						arg1.append(arg4);
					}

					arg1.append(GatewayUtil.chkDir(this.prop.getProperty("UpldBackUp"), "UpldBackUp", true));
					if (ServiceTypeEnum.SFTP.getValue().equalsIgnoreCase(arg8)) {
						arg1.append(GatewayUtil.chkString(this.prop.getProperty("SenderId"), "SenderId"));
						arg1.append(GatewayUtil.chkString(this.prop.getProperty("Partner_ReceiverId"),
								"Partner_ReceiverId"));
					}
				} else {
					arg1.append(GatewayUtil.chkFile(arg10, "SendInfoFile", true, true));
				}
			}

			if (arg6.toUpperCase().indexOf("R") >= 0) {
				if (ServiceTypeEnum.SFTP.getValue().equalsIgnoreCase(arg8)) {
					if (GatewayUtil.isBlankOrNull(this.prop.getProperty("DnldPath"))) {
						this.prop.setProperty("DnldPath", "./");
					}
				} else {
					arg10 = this.prop.getProperty("DnldFile");
					if (GatewayUtil.isBlankOrNull(arg10)) {
						arg1.append(GatewayUtil.chkFile(arg10, "DnldFile", false, false));
					} else {
						final File arg12 = new File(arg10);
						if (arg12.isDirectory()) {
							arg1.append(ErrorCodeEnum.ERR_RECV_FILE_IS_DIR.getErrorMessage() + " : "
									+ arg12.getAbsolutePath());
						} else {
							arg1.append(GatewayUtil.chkFile(arg10, "DnldFile", false, false));
						}
					}
				}

				arg1.append(GatewayUtil.chkFile(this.prop.getProperty("RecvInfoFile"), "RecvInfoFile", true, false));
			}
		}

		arg1.append(GatewayUtil.chkInteger(this.prop.getProperty("MaxFileNameLength"), "MaxFileNameLength", true));
		if (arg1.length() > 0) {
			System.out.println(ErrorCodeEnum.ERR_READ_CONFIG_FAIL.getErrorMessage() + "\r\n" + arg1.toString());
			throw new TymcommException(this.getClass(), arg0, ErrorCodeEnum.ERR_READ_CONFIG_FAIL,
					ErrorCodeEnum.ERR_READ_CONFIG_FAIL.getErrorMessage() + "\r\n" + arg1.toString());
																										
		}
	}

	private DolphinTransportConfig makeDolphinTransportConfig(Properties arg0) {
		final DolphinTransportConfig arg1 = new DolphinTransportConfig();
		arg1.setUserId(arg0.getProperty("UserID"));
		arg1.setUserPwd(arg0.getProperty("UserPWD"));
		if (!GatewayUtil.isBlankOrNull(arg0.getProperty("DialogueId"))) {
			arg1.setServiceType(ServiceTypeEnum.getServiceTypeEnum(arg0.getProperty("DialogueId")));
		}

		if (this.serverIpList == null || this.serverIpList.size() == 0) {
			if (ServiceTypeEnum.SFTP == arg1.getServiceType()) {
				this.serverIpList.add(arg0.getProperty("sFtpServer1"));
				if (!GatewayUtil.isBlankOrNull(arg0.getProperty("sFtpServer2"))) {
					this.serverIpList.add(arg0.getProperty("sFtpServer2"));
				}

				if (!GatewayUtil.isBlankOrNull(arg0.getProperty("sFtpServer3"))) {
					this.serverIpList.add(arg0.getProperty("sFtpServer3"));
				}
			} else {
				this.serverIpList.add(arg0.getProperty("FtpServer1"));
				if (!GatewayUtil.isBlankOrNull(arg0.getProperty("FtpServer2"))) {
					this.serverIpList.add(arg0.getProperty("FtpServer2"));
				}

				if (!GatewayUtil.isBlankOrNull(arg0.getProperty("FtpServer3"))) {
					this.serverIpList.add(arg0.getProperty("FtpServer3"));
				}
			}
		}

		arg1.setFtpServerList(this.serverIpList);
		if (this.serverPort == -1) {
			if (ServiceTypeEnum.SFTP == arg1.getServiceType()) {
				this.serverPort = Integer.parseInt(arg0.getProperty("sFtpPort"));
			} else {
				this.serverPort = Integer.parseInt(arg0.getProperty("FtpPort"));
			}
		}

		arg1.setFtpPort(this.serverPort);
		arg1.setSSL("Y".equalsIgnoreCase(arg0.getProperty("SSL")));
		arg1.setLongFileName(Boolean.parseBoolean(arg0.getProperty("LongFileName")));
		arg1.setNotSendHiddenFile(Boolean.parseBoolean(arg0.getProperty("NotSendHiddenFile")));

		try {
			arg1.setMaxFileNameLength(Integer.parseInt(arg0.getProperty("MaxFileNameLength")));
		} catch (final NumberFormatException arg3) {
			arg1.setMaxFileNameLength(20);
		}

		arg1.setVersion(arg0.getProperty("VERSION"));
		arg1.setNeedEdiFlag(this.needEDIFlag);
		final ProxyUtil arg2 = new ProxyUtil();
		arg2.setProxyType(arg0.getProperty("ProxyType"));
		arg2.setProxyHost(arg0.getProperty("ProxyHost"));
		arg2.setProxyPort(arg0.getProperty("ProxyPort"));
		arg2.setProxyUser(arg0.getProperty("ProxyUser"));
		arg2.setProxyPassword(arg0.getProperty("ProxyPassword"));
		arg1.setProxyUtil(arg2);
		return arg1;
	}

	private List<DolphinTransportInfo> makeSendDolphinTransportInfo(Properties arg0) {
		final String arg1 = Thread.currentThread().getStackTrace()[1].getMethodName();
		File[] arg2 = null;
		final File arg3 = new File(arg0.getProperty("UpldPath"));
		if (GatewayUtil.isBlankOrNull(arg0.getProperty("FixSendFileName"))) {
			arg2 = arg3.listFiles();
		} else {
			arg2 = new File[] { new File(arg0.getProperty("UpldPath") + File.separator
					+ arg0.getProperty("FixSendFileName")) };
		}

		Arrays.sort(arg2, new Comparator<Object>() {
					@Override
					public int compare(Object o1, Object o2) {
						// TODO Auto-generated method stub
						return 0;
					}
				});
		final ArrayList<DolphinTransportInfo> arg4 = new ArrayList<DolphinTransportInfo>();
		final File[] arg8 = arg2;
		final int arg7 = arg2.length;

		for (int arg6 = 0; arg6 < arg7; ++arg6) {
			final File arg5 = arg8[arg6];
			if (arg5.isDirectory()) {
				this.logger.info(arg5.getAbsolutePath() + " is a directory", this.getClass(), arg1);
			} else if (arg5.isFile()) {
				arg4.add(this.makeDolphinTransportInfo(true, arg0, arg5));
			}
		}

		return arg4;
	}

	private DolphinTransportInfo makeDolphinTransportInfo(boolean arg0, Properties arg1, File arg2) {
		final DolphinTransportInfo arg3 = new DolphinTransportInfo();
		arg3.setSenderId(arg1.getProperty("SenderId"));
		if (arg0) {
			arg3.setReceiverId(arg1.getProperty("Partner_ReceiverId"));
			arg3.setInstruction(this.getInstruction(arg1.getProperty("HubType"), arg1.getProperty("RecvAck")));
			arg3.setApid(arg1.getProperty("HubType"));
			arg3.setFileName(arg2);
			this.setFile(arg1, "UpldBackUp", arg3, "PATH3");
		} else {
			String arg4 = arg1.getProperty("DnldPath");
			String arg5;
			if (!GatewayUtil.isBlankOrNull(arg4)) {
				arg3.setDnldPath(new File(arg4));
				arg5 = arg1.getProperty("RecvInfoFile");
				if (GatewayUtil.isBlankOrNull(arg5)) {
					arg4 = System.getProperty("user.dir", ".");
					arg5 = arg4 + File.separator + "infofile";
				}

				arg3.setRecvInfoFile(new File(arg5));
			}

			arg3.setReceiverId(arg1.getProperty("ReceiverId"));
			arg5 = arg1.getProperty("DialogueId");
			if (!GatewayUtil.isBlankOrNull(arg5)) {
				if (!ServiceTypeEnum.SFTP.getValue().equals(arg5)) {
					arg3.setRecvFileOverWrite(arg1.getProperty("RecvFileOverWrite"));
				}
			} else {
				arg3.setRecvFileOverWrite(arg1.getProperty("RecvFileOverWrite"));
			}

			this.setFile(arg1, "DnldFile", arg3, "DnldFile");
			this.setFile(arg1, "PATH2", arg3, "PATH2");
			this.setFile(arg1, "PATH3", arg3, "PATH3");
		}

		return arg3;
	}

	private void setFile(Properties arg0, String arg1, DolphinTransportInfo arg2, String arg3) {
		final String arg4 = arg0.getProperty(arg1);
		if (!GatewayUtil.isBlankOrNull(arg4)) {
			arg2.setValue(arg3, new File(arg4));
		}

	}

	private List<DolphinTransportInfo> makeSendDolphinTransportInfo(DolphinTransportConfig arg0, String arg1)
			throws TymcommException {
		final String arg2 = Thread.currentThread().getStackTrace()[1].getMethodName();
		final int arg3 = arg0.getMaxFileNameLength();
		final ArrayList<DolphinTransportInfo> arg4 = new ArrayList<DolphinTransportInfo>();
		BufferedReader arg5 = null;

		ArrayList<DolphinTransportInfo> arg17;
		try {
			arg5 = new BufferedReader(new FileReader(arg1));
			String arg6 = "";
			String arg7 = "";
			String arg8 = "";
			String arg9 = "";
//			final boolean arg11 = false;
			int arg12 = 0;

			while (!(arg6 = arg5.readLine()).startsWith("<<EOF>>")) {
				++arg12;
				if (arg6.length() < SFTPPacketHandler.ID_LEN + SFTPPacketHandler.ID_LEN + SFTPPacketHandler.CTRL_NO_LEN
						+ SFTPPacketHandler.INSTRUCT_LEN + SFTPPacketHandler.APID_LEN + arg3 + 2
						+ SFTPPacketHandler.CC_SIZE + 1) {
					throw new TymcommException(this.getClass(), arg2, ErrorCodeEnum.PACK_ERR,
							"InfoFile Error : Data Length not enough : len=" + arg6.length() + " ReadData=" + arg6);
																													
				}

				final DolphinTransportInfo arg13 = new DolphinTransportInfo();
				final byte[] arg10 = arg6.getBytes("ISO8859_1");
				int arg28 = this.setInfoFromFile(arg13, "SenderId", arg10, 0, SFTPPacketHandler.ID_LEN);
				arg28 = this.setInfoFromFile(arg13, "ReceiverId", arg10, arg28, SFTPPacketHandler.ID_LEN);
				arg28 = this.setInfoFromFile(arg13, "CONTROL_NO", arg10, arg28, SFTPPacketHandler.CTRL_NO_LEN);
				arg28 = this.setInfoFromFile(arg13, "INSTRUCTION", arg10, arg28, SFTPPacketHandler.INSTRUCT_LEN);
				arg28 = this.setInfoFromFile(arg13, "HubType", arg10, arg28, SFTPPacketHandler.APID_LEN);
				arg28 = this.setInfoFromFile(arg13, "CLI_FILE_NAME", arg10, arg28, arg3);
				arg8 = new String(arg10, arg28, 2 + SFTPPacketHandler.CC_SIZE);
				arg28 += 2 + SFTPPacketHandler.CC_SIZE;
				arg9 = new String(arg10, arg28, arg10.length - arg28);
				final String arg14 = GatewayUtil.chkFile(arg9, "sendFile", false, true);
				if (!GatewayUtil.isBlankOrNull(arg14)) {
					throw new TymcommException(this.getClass(), arg2, ErrorCodeEnum.PACK_ERR, "InfoFile Error : "
							+ arg14);
				}

				arg13.setFileName(new File(arg9));
				arg28 = Integer.parseInt(arg8.substring(2));

				for (int arg15 = 0; arg15 < arg28; ++arg15) {
					++arg12;
					if ((arg7 = arg5.readLine()).charAt(0) != 36) {
						throw new TymcommException(this.getClass(), arg2, ErrorCodeEnum.PACK_ERR, "Line " + arg12
								+ " CC error , CC\'s ReceiverId error, read data = " + arg7);
																								
					}

					arg13.addCcList(arg7.substring(1));
				}

				if (!arg5.readLine().equals("*")) {
					throw new TymcommException(this.getClass(), arg2, ErrorCodeEnum.PACK_ERR,
							"PackFile Error : InfoFile File format error, not found \'*\'");
																							
				}

				this.setFile(this.prop, "UpldBackUp", arg13, "PATH3");
				arg4.add(arg13);
			}

			arg17 = arg4;
		} catch (final UnsupportedEncodingException arg25) {
			throw new TymcommException(this.getClass(), arg2, ErrorCodeEnum.PACK_ERR,
					"Occur UnsupportedEncodingException", arg25);
		} catch (final IOException arg26) {
			throw new TymcommException(this.getClass(), arg2, ErrorCodeEnum.PACK_ERR, "Occur IOException", arg26);
																													
		} finally {
			if (arg5 != null) {
				try {
					arg5.close();
				} catch (final IOException arg24) {
					;
				}
			}

		}

		return arg17;
	}

	private int setInfoFromFile(DolphinTransportInfo arg0, String arg1, byte[] arg2, int arg3, int arg4) {
		arg0.setValue(arg1, new String(arg2, arg3, arg4));
		return arg3 + arg4;
	}

	private String getInstruction(String arg0, String arg1) {
		return GatewayUtil.isBlankOrNull(arg0) && "Y".equalsIgnoreCase(arg1) ? "FNY" : "FNN";
																								
																								
	}

	private void backSendFile(List<DolphinTransportInfo> arg0) throws GatewayException {
		final String arg1 = Thread.currentThread().getStackTrace()[1].getMethodName();
		final Iterator<DolphinTransportInfo> arg3 = arg0.iterator();

		while (arg3.hasNext()) {
			final DolphinTransportInfo arg2 = (DolphinTransportInfo) arg3.next();
			if (arg2.getPath3() != null) {
				final File arg4 = new File(arg2.getPath3() + File.separator + arg2.getFileName().getName());

				try {
					GatewayUtil.renameFile(arg2.getFileName(), arg4);
				} catch (final IOException arg6) {
					throw new GatewayException(GatewayUtil.class, arg1, ErrorCodeEnum.IO_EXCEPTION_HAPPENED,
							arg6.getMessage(), arg6);
				}

				this.logger.info(
						"move source file " + arg2.getFileName().getAbsolutePath() + " to " + arg4.getAbsolutePath()
								+ " success!!", this.getClass(), arg1);
			}
		}

	}

	// public void execute() {
	// String arg1 = System.getProperty("Config");
	// String arg2 = System.getProperty("sftpConfig");
	// if (GatewayUtil.isBlankOrNull(arg1)) {
	// throw new ApBusinessException(
	// "Usage : java -DConfig=config_file [-DTransType=S|R -DDnldFile=recvfile -DLogFile=logfile -DSSL=Y|N] com.tradevan.tymcommJ.tymcomm");
	// }
	//
	// try {
	// this.loadConfig(arg1, arg2);
	// this.transmit();
	// } catch (Throwable arg4) {
	// arg4.printStackTrace();
	// ErrorCodeEnum.CONFIG_IS_NOT_EXIST.getErrValue();
	// throw arg4;
	// }
	// }

	public static void main(String[] arg) {
		final String arg1 = System.getProperty("Config");
		final String arg2 = System.getProperty("sftpConfig");
		if (GatewayUtil.isBlankOrNull(arg1)) {
			throw new ApBusinessException(
					"Usage : java -DConfig=config_file [-DTransType=S|R -DDnldFile=recvfile -DLogFile=logfile -DSSL=Y|N] com.tradevan.tymcommJ.tymcomm");
		}
		final Tymcomm tymcomm = new Tymcomm();
		tymcomm.loadConfig(arg1, arg2);
		final int retunrCode = tymcomm.transmit();
		if (retunrCode != 0) {
			throw new ApBusinessException("取得錯誤訊息:" + retunrCode);
		}
		// try {
		//
		// } catch (Throwable arg4) {
		// arg4.printStackTrace();
		// ErrorCodeEnum.CONFIG_IS_NOT_EXIST.getErrValue();
		// throw arg4;
		// }

	}

	public void setLogger(String arg0) {
		final GatewayLogger arg1 = new GatewayLogger(arg0);
		this.setLogger(arg1);
	}

	public void setLogger(Logger arg0) {
		this.logger.setLogger(new GatewayLogger(arg0));
	}

	public void setLogger(GatewayLogger arg0) {
		this.logger.setLogger(arg0);
	}

	public EventListener getListener() {
		return this.listener;
	}

	public void setListener(EventListener arg0) {
		this.listener = arg0;
	}

	public void subscribeTransportEvent(EventListener arg0) {
		this.transportEventCallback.addEventListener(arg0);
		this.setListener(arg0);
	}

	public void removeTransportEventListener(EventListener arg0) {
		this.transportEventCallback.removeEventListener(arg0);
		this.setListener((EventListener) null);
	}

	public void setMaxSendFileSize(String arg0) {
		if (GatewayUtil.isBlankOrNull(arg0)) {
			this.maxSendFileSize = 10485760;
		} else {
			try {
				this.maxSendFileSize = Integer.parseInt(arg0) * 1024 * 1024;
				if (this.maxSendFileSize < 0) {
					this.maxSendFileSize = 10485760;
				}
			} catch (final NumberFormatException arg2) {
				this.maxSendFileSize = 10485760;
			}
		}

	}

	public void setMaxSendFileIndex(String arg0) {
		if (GatewayUtil.isBlankOrNull(arg0)) {
			this.maxSendFileIndex = -1;
		} else {
			try {
				this.maxSendFileIndex = Integer.parseInt(arg0);
				if (this.maxSendFileIndex < 0) {
					this.maxSendFileIndex = -1;
				}
			} catch (final NumberFormatException arg2) {
				this.maxSendFileIndex = -1;
			}
		}

	}

	private int getMaxSendFileSize() {
		final String arg0 = System.getProperty("MaxSendFileSize");
//		final boolean arg1 = false;
		int arg4;
		if (this.maxSendFileSize == 0) {
			if (GatewayUtil.isBlankOrNull(arg0)) {
				arg4 = 10485760;
			} else {
				try {
					arg4 = Integer.parseInt(arg0) * 1024 * 1024;
					if (arg4 < 0) {
						arg4 = 10485760;
					}
				} catch (final NumberFormatException arg3) {
					arg4 = 10485760;
				}
			}
		} else {
			arg4 = this.maxSendFileSize;
		}

		return arg4;
	}

	private int getMaxSendFileIndex() {
		final String arg0 = System.getProperty("MaxSendFileIndex");
//		final boolean arg1 = false;
		int arg4;
		if (this.maxSendFileIndex <= 0) {
			if (GatewayUtil.isBlankOrNull(arg0)) {
				arg4 = -1;
			} else {
				try {
					arg4 = Integer.parseInt(arg0);
					if (arg4 <= 0) {
						arg4 = -1;
					}
				} catch (final NumberFormatException arg3) {
					arg4 = -1;
				}
			}
		} else {
			arg4 = this.maxSendFileIndex;
		}

		return arg4;
	}
}
