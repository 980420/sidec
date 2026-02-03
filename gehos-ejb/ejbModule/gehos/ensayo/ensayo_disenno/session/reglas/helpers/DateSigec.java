package gehos.ensayo.ensayo_disenno.session.reglas.helpers;

import org.joda.time.DateTime;

public class DateSigec 
{	
	private String rawValue;
	private boolean isDayPartComplete = false;
	private boolean isMonthPartComplete = false;
	private boolean isYearPartComplete = false;
	
	private Integer yearPart,
	monthPart,
	dayPart;
	
	int less = -1, equal = 0, greater=1;
	
	public static DateSigec parse(String rawValue) throws Exception  
	{
		String ress = IsValid(rawValue);//this will probably be the int8l code
		if(ress!=null)
		{
			throw new Exception(ress);
		}
		return ExtractDate(rawValue);		
	}
		
	private static DateSigec ExtractDate(String rawValue)
	{
		DateSigec date = new DateSigec();
		date.rawValue = rawValue;
		Object[] parts = GetParts(rawValue);
		date.isDayPartComplete = (Boolean)parts[0];
		date.isMonthPartComplete = (Boolean)parts[1];
		date.isYearPartComplete = (Boolean)parts[2];
		
		if(date.isDayPartComplete)
			date.dayPart = Integer.parseInt(parts[3].toString());
		
		if(date.isMonthPartComplete)
			date.monthPart = Integer.parseInt(parts[4].toString());
				
		date.yearPart = Integer.parseInt(parts[5].toString());
		
		return date;
	}
	
	private static Object[] GetParts(String value)
	{
		boolean isDayPartComplete = false;
		boolean isMonthPartComplete = false;
		boolean isYearPartComplete = false;
		
		String dayPart = null;
		String monthPart = null;
		String yearPart = null;
		
		String[] sp = value.toString().trim().split("/");
		yearPart = sp[0];
		isYearPartComplete = yearPart.matches("\\d+");
		
		if(sp.length==2)
		{			
			monthPart = sp[1];
			isMonthPartComplete = monthPart.matches("\\d+");
		}
		else if(sp.length==3)
		{
			 monthPart = sp[1];
			 isMonthPartComplete = monthPart.matches("\\d+");
			
			dayPart = sp[2];
			isDayPartComplete = dayPart.matches("\\d+");
		}
		
		return new Object[]{isDayPartComplete,isMonthPartComplete,isYearPartComplete, dayPart, monthPart, yearPart};
	}
	
	/**
	 * A DateSigec is valid if this method returns null, otherwise the message why is not.
	 * @param value
	 * @return
	 */
	//TODO An additional check must be added: when the date is complete the joda lib must be used to avoid non-existing dates such as 31/02 (Feb has 28 days, normaly)
	private static String IsValid(String value)
	{		
		Object[] parts = GetParts(value);
		boolean isDayPartComplete = (Boolean)parts[0];
		boolean isMonthPartComplete = (Boolean)parts[1];
		boolean isYearPartComplete = (Boolean)parts[2];
		
		String dayPart = (String)parts[3];
		String monthPart = (String)parts[4];
		String yearPart = (String)parts[5];
		
		if(!isYearPartComplete)
		{ 
			return "Anno es requerido";
		}
		if(!isMonthPartComplete && isDayPartComplete)
		{				
			return  "Mes requerido si se especifica el dia";
			
		}			
		return null;
		
	}
	
	public boolean Equal(DateSigec date)
	{
		//if(!hasSameComponents(date))//if the dates has not the same components they cannot be compared
		//	return false;
		
		return this.rawValue.equals(date.rawValue);
	}
	
	public boolean NotEqual(DateSigec date)
	{
		return !Equal(date);
	}
	
	public boolean Before(DateSigec date)
	{
		//if(!hasSameComponents(date))//if the dates has not the same components they cannot be compared
		//	return false;
		
		return InnerCompare(date)==less;		
	}
	
	public boolean After(DateSigec date)
	{
		//if(!hasSameComponents(date))//if the dates has not the same components they cannot be compared
		//	return false;
		
		return InnerCompare(date)==greater;		
	}
	
	public int InnerCompare(DateSigec date)
	{		
		if(this.yearPart < date.yearPart)
			return less;
		
		if(this.yearPart > date.yearPart)
			return greater;
		
		if(this.isMonthPartComplete && date.isMonthPartComplete) //if month is present
		{
			if(this.monthPart < date.monthPart)
				return less;
			
			if(this.monthPart > date.monthPart)
				return greater;			
		}		
		
		if(this.isDayPartComplete && date.isDayPartComplete) //if day is present
		{
			if(this.dayPart < date.dayPart)
				return less;
			
			if(this.dayPart > date.dayPart)
				return greater;			
		}		
		
		return equal;
	}
	/**
	 * The components are: day, month, year
	 * @return
	 */
	public boolean hasSameComponents(DateSigec date)
	{
		return this.isYearPartComplete == date.isYearPartComplete && this.isMonthPartComplete == date.isMonthPartComplete && this.isDayPartComplete == date.isDayPartComplete;			
	}
	/**
	 * Tells if the date has all the components
	 * @return
	 */
	public boolean IsComplete()
	{
		return this.isYearPartComplete && this.isMonthPartComplete  && this.isDayPartComplete;
	}
	
}
