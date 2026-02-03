package gehos.comun.updater;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
public class KeysGenerator {	
	
	private static final char[] HEXADECIMAL = { '0', '1', '2', '3',
	        '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	    
    private static String toMD5(String stringToHash)  {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(stringToHash.getBytes());
            StringBuilder sb = new StringBuilder(2 * bytes.length);
            for (int i = 0; i < bytes.length; i++) {
                int low = (int)(bytes[i] & 0x0f);
                int high = (int)((bytes[i] & 0xf0) >> 4);
                sb.append(HEXADECIMAL[high]);
                sb.append(HEXADECIMAL[low]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            //exception handling goes here
            return null;
        }
    }

    public static String[] keys(Date date){
        SimpleDateFormat formatInitial = new SimpleDateFormat("dd/MM/yyyy");
        String initial = toMD5("alasHIS" + formatInitial.format(date));
        SimpleDateFormat formatMiddle = new SimpleDateFormat("MM/dd/yyyy");
        String middle = toMD5("alasHIS" + formatMiddle.format(date));
        SimpleDateFormat formatFinal = new SimpleDateFormat("yyyy/MM/dd");
        String finalC = toMD5("alasHIS" + formatFinal.format(date));
        
        String[] keyResult = new String[32];
        for (int i = 0; i < 32; i++) {
            keyResult[i] = initial.substring(i, i + 1) + middle.substring(i, i + 1) 
                    + finalC.substring(i, i + 1);
        }
	        
        return keyResult;        
    } 
    
    public static final String[] KEYSNAME = { "A1", "B1", "C1", "D1", "E1", "F1", "G1", "H1",
        "A2", "B2", "C2", "D2", "E2", "F2", "G2", "H2",
        "A3", "B3", "C3", "D3", "E3", "F3", "G3", "H3",
        "A4", "B4", "C4", "D4", "E4", "F4", "G4", "H4"
        };
    
    public static int[] positionsKeys(int hour, Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.get(Calendar.DAY_OF_MONTH);
        long seed = hour * 100000000 + 
                calendar.get(Calendar.DAY_OF_MONTH) * 1000000 +
                calendar.get(Calendar.MONTH) * 10000 +
                calendar.get(Calendar.YEAR);
        Random rand = new Random(seed);
        int[] result = new int[] {rand.nextInt(32), rand.nextInt(32)};
        while(result[0] == result[1]){
        	result[1] = rand.nextInt(32);
        }
        return result;
    }
    
    public static String keyString(int hour, Date date){
        String[] keys = keys(date);
        int[] keypos = positionsKeys(hour, date);
        String result = KEYSNAME[keypos[0]] + ":" + keys[keypos[0]] + "-"
                + KEYSNAME[keypos[1]] + ":" + keys[keypos[1]];
        return result;
    }
    
    public static String[] nowKeysString(){
    	int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    	int[] keys = positionsKeys(hour, Calendar.getInstance().getTime());
    	return new String[] {KEYSNAME[keys[0]], KEYSNAME[keys[1]]};
    }
    
    public static boolean validateKeys(String key1, String key2){
    	int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    	int[] keyPos = positionsKeys(hour, Calendar.getInstance().getTime());
    	String[] keysVal = keys(Calendar.getInstance().getTime());
    	if(keysVal[keyPos[0]].equals(key1) && keysVal[keyPos[1]].equals(key2)){
    		return true;
    	}
    	return false;
    }
    
    public static String keyString(){
    	int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String[] keys = keys(Calendar.getInstance().getTime());
        int[] keypos = positionsKeys(hour, Calendar.getInstance().getTime());
        String result = KEYSNAME[keypos[0]] + ":" + keys[keypos[0]] + "-"
                + KEYSNAME[keypos[1]] + ":" + keys[keypos[1]];
        return result;
    }
	
}
