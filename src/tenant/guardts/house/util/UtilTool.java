package tenant.guardts.house.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class UtilTool {

	private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7",  
            "8", "9", "a", "b", "c", "d", "e", "f"};  
	
	protected static char hexDigits2[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	private UtilTool() {}
	
	public final static String getMessageDigest(byte[] buffer) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(buffer);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String formatDuring(long mss) {  
	    long days = mss / (1000 * 60 * 60 * 24);  
	    long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);  
	    long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);  
	    long seconds = (mss % (1000 * 60)) / 1000;  
	    return days + " days " + hours + " hours " + minutes + " minutes "  
	            + seconds + " seconds ";  
	}  
	
	public static long formatDuringToHour(long mss){
		long hours = mss / (1000 * 60 * 60);
		return hours;
	}
	
	
	/** 
     * ת���ֽ�����Ϊ16�����ִ� 
     * @param b �ֽ����� 
     * @return 16�����ִ� 
     */  
    public static String byteArrayToHexString(byte[] b) {  
        StringBuilder resultSb = new StringBuilder();  
        for (byte aB : b) {  
            System.out.println(aB);  
            resultSb.append(byteToHexString(aB));  
        }  
        return resultSb.toString();  
    }  
  
    /** 
     * ת��byte��16���� 
     * @param b Ҫת����byte 
     * @return 16���Ƹ�ʽ 
     */  
    private static String byteToHexString(byte b) {  
        int n = b;  
        if (n < 0) {  
            n = 256 + n;  
        }  
        int d1 = n / 16;  
        int d2 = n % 16;  
        return hexDigits[d1] + hexDigits[d2];  
    }  
  
    /** 
     * MD5���� 
     * @param origin ԭʼ�ַ� 
     * @return ����MD5����֮��Ľ�� 
     */  
    public static String MD5Encode(String origin) {  
        System.out.println(origin);  
        String resultString = null;  
        try {  
            resultString = origin;  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            resultString = byteArrayToHexString(md.digest(resultString.getBytes("utf-8")));  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return resultString;  
  
    }  
    
    public static Map<String,String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName=parser.getName();
                switch (event) {
                case XmlPullParser.START_DOCUMENT:

                    break;
                case XmlPullParser.START_TAG:

                    if("xml".equals(nodeName)==false){
                        //实例化student对象
                        xml.put(nodeName,parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            LogUtil.e("Simon","----"+e.toString());
        }
        return null;

    }
    
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    
    public static String stampToNormalDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    
    public static String stampToDateTime(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    
    public static long DateTimeToStamp(String time){
    	SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        Date date;
		try {
			date = format.parse(time);
			return date.getTime();  
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        return 0;
       
    }
    
    public static String generateOrderNo(){
    	String orderNo = null;
    	String timeStamp = stampToDate(System.currentTimeMillis()+"");
        String randomValue = String.valueOf(Math.random()).replace("0.", "").substring(0, 8);
        orderNo = timeStamp+randomValue;
        return orderNo;
    }
    
    public static String getPrintSize(long size) {  
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义  
        if (size < 1024) {  
            return String.valueOf(size) + "B";  
        } else {  
            size = size / 1024;  
        }  
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位  
        //因为还没有到达要使用另一个单位的时候  
        //接下去以此类推  
        if (size < 1024) {  
            return String.valueOf(size) + "KB";  
        } else {  
            size = size / 1024;  
        }  
        if (size < 1024) {  
            //因为如果以MB为单位的话，要保留最后1位小数，  
            //因此，把此数乘以100之后再取余  
            size = size * 100;  
            return String.valueOf((size / 100)) + "."  
                    + String.valueOf((size % 100)) + "MB";  
        } else {  
            //否则如果要以GB为单位的，先除于1024再作同样的处理  
            size = size * 100 / 1024;  
            return String.valueOf((size / 100)) + "."  
                    + String.valueOf((size % 100)) + "GB";  
        }  
    }  
    
    public static String getFileMD5String(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		FileChannel ch = in.getChannel();
		MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(byteBuffer);
			return bufferToHex(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			if (ch != null) {
				ch.close();
			}
			
			if (in != null) {
				in.close();
			}
		}

		return null;
	}
    
    private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}
    
    private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}
    
    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits2[(bt & 0xf0) >> 4];
		char c1 = hexDigits2[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}
    
    public static String getStringToUtf8(String xml) {  
        // A StringBuffer Object  
        StringBuffer sb = new StringBuffer();  
        sb.append(xml);  
        String xmlUTF8="";  
        try {  
//        xmString = new String(sb.toString().getBytes("UTF-8"));  
//        xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");  
//        System.out.println("utf-8 编码：" + xmlUTF8) ;  
        	xmlUTF8 = new String(sb.toString().getBytes(),"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {  
        // TODO Auto-generated catch block  
        e.printStackTrace();  
        }  
        // return to String Formed  
        return xmlUTF8;  
        }  
    
}
