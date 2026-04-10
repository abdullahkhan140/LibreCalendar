import java.text.SimpleDateFormat;
import java.util.Calendar;



public class format {
	
	
	
	public static void main(String[] args) {// Get an instance of a Calendar, using the current time.
	
			         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
			         Calendar calendar = Calendar.getInstance();
		
			         System.out.println(dateFormat.format(calendar.getTime()));

			         
			         
			                  System.out.println("ERA: " + calendar.get(Calendar.ERA));
			         
			         	        System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
			         
			                     System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
			         
			         	        System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
			         
			         	        System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
}

}