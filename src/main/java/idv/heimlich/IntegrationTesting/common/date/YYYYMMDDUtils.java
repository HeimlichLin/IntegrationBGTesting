package idv.heimlich.IntegrationTesting.common.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.java.common.exception.BaseBusinessException;

public class YYYYMMDDUtils {
	
	private static final SimpleDateFormat SDF = new SimpleDateFormat(
			"yyyyMMdd");

	public static String getText(Date date) {
		return SDF.format(date);
	}

	public static String getText() {
		return getText(new Date());
	}

	public static Date parse(String yyyymmdd) {
		try {
			return SDF.parse(yyyymmdd);
		} catch (ParseException arg1) {
			throw new BaseBusinessException("日期格式錯誤:" + yyyymmdd);
		}
	}

}
