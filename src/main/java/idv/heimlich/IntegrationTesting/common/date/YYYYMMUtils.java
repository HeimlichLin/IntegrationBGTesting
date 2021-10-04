package idv.heimlich.IntegrationTesting.common.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.java.common.exception.BaseBusinessException;

public class YYYYMMUtils {
	
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMM");

	public static String getText(Date date) {
		return SDF.format(date);
	}

	public static String getText() {
		return getText(new Date());
	}

	public static Date parse(String yyyyMM) {
		try {
			return SDF.parse(yyyyMM);
		} catch (ParseException arg1) {
			throw new BaseBusinessException("日期格式錯誤:" + yyyyMM);
		}
	}

}
