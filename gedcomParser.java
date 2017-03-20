package gedcomParser;
import java.beans.FeatureDescriptor;
/*
 * Team submission:
 * 
 * Priya Parmar
 * Ruchika Sutariya
 * Charmi Bhikadiya
 */
import java.io.*;
import java.util.*;
import java.text.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

import javax.swing.text.ChangedCharSetException;

public class gedcomParser {
	
	public static class indi {
		String id; //0
		String name; //1 
		String sex; //1 
		Date birth; //1&2 
		Date death; //1&2 
		String family_child; //1 
		ArrayList<String> family_spouse = new ArrayList<String>(); //1
		int age = 0;
		
		public indi(String id){
			this.id = id; 
		}
		public String getId(){
			return id; 
		}
		public void setName(String name){
			this.name = name;
		}
		public String getName(){
			return name; 
		}
		public void setSex(String sex){
			this.sex = sex;
		}
		public String getSex(){
			return sex; 
		}
		public void setBirth(Date birth){
			this.birth = birth;
		}
		public Date getBirth(){
			return birth; 
		}
		public void setDeath(Date death){
			this.death = death;
		}
		public Date getDeath(){
			return death; 
		}
		public void setFamc(String famc){
			this.family_child = famc;
		}
		public String getFamc(){
			return family_child; 
		}
		public void setFams(String fams){
			this.family_spouse.add(fams);
		}
		public ArrayList<String> getFams(){
			return family_spouse; 
		}
		public void setAge(int age) {
			this.age = age;
		}
		public int getAge() {
	        	return age;
		}
	}
	class AgeComparator implements Comparator<indi> {
		@Override
		public int compare(indi o1, indi o2) {
		    if (o1.getAge() > o2.getAge()) {
		        return -1;
		    } else if (o1.getAge() < o2.getAge()) {
		        return 1;
		    }
		    return 0;
		}
	}
	
	public static class fam {
		String id; //0 
		Date marriage; //1&2
		String husband; //1
		String wife; //1
		ArrayList<String> children = new ArrayList<String>(); //1
		Date divorce; //1&2 
		
		public fam(String id){
			this.id = id; 
		}
		public String getId(){
			return id; 
		}
		public void setMarriage(Date marriage){
			this.marriage = marriage;
		}
		public Date getMarriage(){
			return marriage; 
		}
		public void setHusband(String husband){
			this.husband = husband; 
		}
		public String getHusband(){
			return husband; 
		}
		public void setWife(String wife){
			this.wife = wife; 
		}
		public String getWife(){
			return wife; 
		}
		public void setChildren(String child){
			this.children.add(child);
		}
		public ArrayList<String> getChildren(){
			return children; 
		}
		public void setDivorce(Date divorce){
			this.divorce = divorce;
		}
		public Date getDivorce(){
			return divorce; 
		}
		
	}
	
	static Date stringToDate(String sDate){
		SimpleDateFormat dt = new SimpleDateFormat("dd MMM yyyy"); 
		Date date; 
		try{
			date = dt.parse(sDate);
			return date; 
		}
		catch (ParseException e){
			return null;
		}
	}
	
	//array of valid tags 
	static String[] valid0tags = {"INDI","FAM","HEAD","TRLR", "NOTE"};
	static String[] valid1tags = {"INDI","NAME","SEX","BIRT","DEAT","FAMC","FAMS","FAM","MARR",
			"HUSB","WIFE","CHIL","DIV","HEAD","TRLR", "NOTE"};
	
	public static boolean isValidTag(String tag, int level){
		boolean valid = false;
		if (level == 0){
			for(String s: valid0tags){
				if (s.equals(tag)){
					valid= true; 
				} 
			}
		}
		if (level == 1){
			for(String s: valid1tags){
				if (s.equals(tag)){
					valid= true; 
				} 
			}
		}
		if (level == 2 && tag.equals("DATE")){
				valid= true; 
			} 
		return valid; 
	}
	
	public static void parseFile(String filename, ArrayList<indi> indArray, ArrayList<fam> famArray) throws IOException{
		//open file 
				FileReader fr = new FileReader(filename);
				try(BufferedReader br= new BufferedReader(fr)){
					 
					String line; 
					int level; 
					String tag; 
					String thirdArg; 
					
					//read each line of the file until end of file
					while ((line =br.readLine()) != null) {
						
						//split line on spaces
						String split[] = line.split(" ");
						
						//set level to the first string, parse to integer 
						level = Integer.parseInt(split[0]);
						
						//for all non-0 level lines, the tag always follows the level number, so set tag to the second string
						if (level != 0){
							tag=split[1];
							if(split.length >3){
								String[] split2 = split.clone();
								/*split2[0] = "";
								split2[1]="";*/
								split2 = Arrays.copyOfRange(split2, 2, split2.length);
								StringBuilder builder = new StringBuilder();
								for(String s : split2) {
								    builder.append(s+" ");
								}
								thirdArg = builder.toString();
							}
							else if (split.length == 3){
								thirdArg = split[2];
							}
							else {thirdArg = "";}
						} 
						//when level is 0, either second or last part can be tag, check if second part is a valid tag, if not, set third part as tag
						else{ 
							if(isValidTag(split[1], 0)){
								tag = split[1]; 
								thirdArg = "";
							} else { 
								tag= split[2];
								thirdArg= split[1];
							}
						}
						if (isValidTag(tag, level)){
							storeData(level,tag,thirdArg, indArray, famArray);
						}
						
						//print data 
						//printLineLevelTag(line,level,tag);
					}//end while 
				}//end try 
	}
	
	static boolean birthcheck = false; 
	static boolean deathcheck = false; 
	static boolean marriagecheck = false; 
	static boolean divorcecheck = false; 
	
	public static void storeData(int level, String tag, String arg, ArrayList<indi> individualArray, ArrayList<fam> familyArray){
		if(level ==0){
			if (tag.equals("INDI")){
				indi newindividual = new indi(arg); 
				individualArray.add(newindividual);
			}
			else if (tag.equals("FAM")){
				fam newfamily = new fam(arg); 
				familyArray.add(newfamily);
			}
			else {} //do nothing, don't care about it
		}
		if(level == 1){
				int lastIndiElem1;
				int lastFamElem1;
				indi currentIndi1;
				fam currentFam1;
				//Indi stuff 
				if(tag.equals("NAME")){ 
					lastIndiElem1 = individualArray.size()-1;
					currentIndi1 = individualArray.get(lastIndiElem1);
					currentIndi1.setName(arg);
				}
				if(tag.equals("SEX")){ 
					lastIndiElem1 = individualArray.size()-1;
					currentIndi1 = individualArray.get(lastIndiElem1);
					currentIndi1.setSex(arg);
				}
				if(tag.equals("BIRT")){ birthcheck = true; }
				if(tag.equals("DEAT")){ deathcheck = true; }
				if(tag.equals("FAMC")){ 
					lastIndiElem1 = individualArray.size()-1;
					currentIndi1 = individualArray.get(lastIndiElem1);
					currentIndi1.setFamc(arg);
				}
				if(tag.equals("FAMS")){ 
					lastIndiElem1 = individualArray.size()-1;
					currentIndi1 = individualArray.get(lastIndiElem1);
					currentIndi1.setFams(arg);
				}
				//Fam stuff 
				if(tag.equals("MARR")){ marriagecheck = true; } 
				if(tag.equals("HUSB")){ 
					lastFamElem1 = familyArray.size()-1;
					currentFam1 = familyArray.get(lastFamElem1);
					currentFam1.setHusband(arg);
				}
				if(tag.equals("WIFE")){ 
					lastFamElem1 = familyArray.size()-1;
					currentFam1 = familyArray.get(lastFamElem1);
					currentFam1.setWife(arg);
				}
				if(tag.equals("CHIL")){ 
					lastFamElem1 = familyArray.size()-1;
					currentFam1 = familyArray.get(lastFamElem1);
					currentFam1.setChildren(arg);
				}
				if(tag.equals("DIV")){  divorcecheck = true; }
		}
		if(level == 2){//tag will always be date 
				Date thisDate = stringToDate(arg);
				int Age = getIndiAge(arg);
				int lastIndiElem;
				int lastFamElem;
				indi currentIndi;
				fam currentFam;
					if(birthcheck == true){
						lastIndiElem = individualArray.size()-1;
						currentIndi = individualArray.get(lastIndiElem);
						currentIndi.setBirth(thisDate);
						currentIndi.setAge(Age);
						birthcheck = false;
					}
					if(deathcheck == true){
						lastIndiElem = individualArray.size()-1;
						currentIndi = individualArray.get(lastIndiElem);
						currentIndi.setDeath(thisDate);
						deathcheck = false; 
					}
					if(marriagecheck == true){
						if(arg != null){
							lastFamElem = familyArray.size()-1;
							currentFam = familyArray.get(lastFamElem);
							currentFam.setMarriage(thisDate);
							marriagecheck = false; 
						}
						
					}
					if(divorcecheck == true){
						lastFamElem = familyArray.size()-1;
						currentFam = familyArray.get(lastFamElem);
						currentFam.setDivorce(thisDate);
						divorcecheck = false; 
					}

		}
	}
	
	public static void printIndiAndFamData(ArrayList<indi> indArray, ArrayList<fam> famArray){
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		System.out.println("People");
		System.out.printf("%-11s  %-30s   %-11s  %-12s   %-12s  %-11s  %-15s  %-11s %n", 
				"ID", "Name", "Sex", "Birthday", "Death", "Child", "Spouse","Age");
		for(int i=0; i<indArray.size(); i++){
			indi currentspot = indArray.get(i);
			Date birth = currentspot.getBirth(); 
			Date death = currentspot.getDeath();
			String indiAge=Integer.toString(currentspot.getAge());
			if (birth != null && death != null && birth!= death){
				long difference = death.getTime() - birth.getTime();
                long hours = difference / (60 * 60 * 1000);
                long days = hours / 24; 
                double year = days / 365;
                int yr = (int) year;
				System.out.printf("%-11s  %-30s   %-11s  %-12s   %-12s  %-11s  %-15s  %-11s  %n", 
					currentspot.getId(), currentspot.getName(), currentspot.getSex(), dateFormat.format(birth), 
					dateFormat.format(death), currentspot.getFamc(), currentspot.getFams(), yr);
			}
			if (birth != null && death == null){
				System.out.printf("%-11s  %-30s   %-11s  %-12s   %-12s  %-11s  %-15s  %-11s %n", 
					currentspot.getId(), currentspot.getName(), currentspot.getSex(), dateFormat.format(birth), 
					death, currentspot.getFamc(), currentspot.getFams(), indiAge);
			}
			if (birth == null && death != null){
				System.out.printf("%-11s  %-30s   %-11s  %-12s   %-12s  %-11s  %-11s  %n", 
					currentspot.getId(), currentspot.getName(), currentspot.getSex(), birth, 
					dateFormat.format(death), currentspot.getFamc(), currentspot.getFams());
			}
			if (birth == null && death == null){
				System.out.printf("%-11s  %-30s   %-11s  %-12s   %-12s  %-11s  %-15s  %-11s %n", 
					currentspot.getId(), currentspot.getName(), currentspot.getSex(), birth, 
					death, currentspot.getFamc(), currentspot.getFams(), indiAge="0");
			}
		}
			
		System.out.println("\nFamilies");
		System.out.printf("%-11s  %-12s   %-12s  %-30s   %-30s  %-30s  %n", 
				"ID", "Marriage", "Divorce", "Husband", "Wife", "Children");
		for(int i=0; i<famArray.size(); i++){
			fam currentspot = famArray.get(i);
			Date mar = currentspot.getMarriage();
			Date div = currentspot.getDivorce();
			if (mar != null && div != null){
				System.out.printf("%-11s  %-12s   %-12s  %-30s   %-30s  %-30s  %n", 
					currentspot.getId(), dateFormat.format(mar), dateFormat.format(div), getName(currentspot.getHusband(),indArray), 
					getName(currentspot.getWife(),indArray), currentspot.getChildren());
			}
			if (mar != null && div == null){
				System.out.printf("%-11s  %-12s   %-12s  %-30s   %-30s  %-30s  %n", 
					currentspot.getId(), dateFormat.format(mar), div, getName(currentspot.getHusband(),indArray), 
					getName(currentspot.getWife(),indArray), currentspot.getChildren());
			}
			if (mar == null && div != null){
				System.out.printf("%-11s  %-12s   %-12s  %-30s   %-30s  %-30s  %n", 
					currentspot.getId(), mar, dateFormat.format(div), getName(currentspot.getHusband(),indArray), 
					getName(currentspot.getWife(),indArray), currentspot.getChildren());
			}
			if (mar == null && div == null){
				System.out.printf("%-11s  %-12s   %-12s  %-30s   %-30s  %-30s  %n", 
					currentspot.getId(), mar, div, getName(currentspot.getHusband(),indArray), 
					getName(currentspot.getWife(),indArray), currentspot.getChildren());
			}
		}
		//Priya Parmar - User Story 01 - Sprint 1 for individual
		ArrayList<String> errorsIndividual = checkIndiDateBeforeCurrentDate(indArray);
		for(int j=0; j<errorsIndividual.size();j++){
			System.out.println(errorsIndividual.get(j));
		}
		
		//Priya Parmar - User Story 01 - Sprint 1 for family
		ArrayList<String> errorsFamily = checkFamMarriageBeforeDate(famArray);
		for(int j=0;j<errorsFamily.size();j++){
			System.out.println(errorsFamily.get(j));
		}
		//Priya Parmar - User Story 03 - Sprint 1
		ArrayList<String> errorsIndiBirth = checkBirthBeforeDeath(indArray);
		for(int j=0;j<errorsIndiBirth.size();j++){
			System.out.println(errorsIndiBirth.get(j));
		}
		//Ruchika Sutariya - User Story 02 - Sprint 1
		ArrayList<String> errorsBirthBeforeMrg = checkBirthBeforeMarriage(indArray, famArray);
		for(int j=0;j<errorsBirthBeforeMrg.size();j++){
			System.out.println(errorsBirthBeforeMrg.get(j));
		}
		//Ruchika Sutariya  - User Story 09 - Sprint 1
		ArrayList<String> errorsBirthBeforeDeathOfParents = checkBirthBeforeDeathofParents(indArray, famArray);
		for(int j=0;j<errorsBirthBeforeDeathOfParents.size();j++){
			System.out.println(errorsBirthBeforeDeathOfParents.get(j));
		}
		//Charmi Bhikadiya -User Story 13 - Sprint 1
		ArrayList<String> errorsSiblingSpacing = compareSiblingSpacing(indArray,famArray);
		for(int j=0;j<errorsSiblingSpacing.size();j++){
			System.out.println(errorsSiblingSpacing.get(j));
		}
		//Charmi Bhikadiya - User Story 16 - Sprint 1
		ArrayList<String> errorsMaleNames = checkMaleLastNames(indArray, famArray);
		for(int j=0;j<errorsMaleNames.size();j++){
			System.out.println(errorsMaleNames.get(j));
		}
		//Priya Parmar - User Story 07 - Sprint 2
		ArrayList<String> errorsOver150 = checkIfAgeOver150(indArray);
		for(int j=0;j<errorsOver150.size();j++){
			System.out.println(errorsOver150.get(j));
		}
		//Priya Parmar - User Story 10 - Sprint 2
		ArrayList<String> errorsMrgless14 = checkMarriageAfter14(famArray, indArray);
		for(int j=0;j<errorsMrgless14.size();j++){
			System.out.println(errorsMrgless14.get(j));
		}
		orderSiblingsByAge(famArray, indArray);
	}
	
	//Sprint 1 - Priya Parmar - User Story 01
	public static ArrayList<String> checkIndiDateBeforeCurrentDate(ArrayList<indi> indArray){
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		ArrayList<String> errors = new ArrayList<String>(); 
		for (int j=0; j < indArray.size(); j++) {
            indi currentspot = indArray.get(j);
            Date birth = currentspot.getBirth();
            Date death = currentspot.getDeath();
            Date currentDate = new Date();
			  if( birth != null && currentDate.before(birth)){
            	
            	errors.add("Error US01: INDIVIDUAL: " + currentspot.getId()+ " has a birth date " + dateFormat.format(birth) + " before " + dateFormat.format(currentDate)) ;
            }
			  else if(death != null && currentDate.before(death)){
				  
	            	errors.add("Error US01: INDIVIDUAL: " + currentspot.getId()+ " has a death date " + dateFormat.format(death) + " before " + dateFormat.format(currentDate)) ;
	            }
	            
        }
		return errors;
	}
	//Sprint 1 - Priya Parmar - User Story 01 
	public static ArrayList<String> checkFamMarriageBeforeDate( ArrayList<fam> famArray){
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		ArrayList< String> errors = new ArrayList<String>();
		int errorCount = 0;
		for(int j=0;j<famArray.size();j++){
			fam currentFam = famArray.get(j);		
			Date marriage = currentFam.getMarriage();
			Date divorce = currentFam.getDivorce();         
	         Date currentDate = new Date();
				  if(marriage != null && currentDate.before(marriage)){
	         	
					  errors.add("Error US01: FAMILY: " + currentFam.getId()+ " has a marriage date "+ dateFormat.format(marriage) + " before " + dateFormat.format(currentDate)) ;
					 
				  	}
				  else if( divorce != null && currentDate.before(divorce)){
			         	
			         	errors.add("Error US01: FAMILY: " + currentFam.getId()+ " has a divorce date "+ dateFormat.format(divorce) + " before " + dateFormat.format(currentDate));
			         	    	
				  }			  
		}

		return errors;
		
	}
	//Sprint 1 - Priya Parmar - UserStory 03 : Birth Before Death 
	public static ArrayList<String> checkBirthBeforeDeath(ArrayList<indi> indArray){
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		ArrayList<String> errors = new ArrayList<String>();
		for (int j=0; j < indArray.size(); j++) {
            indi currentspot = indArray.get(j);
            Date birth = currentspot.getBirth();
            Date death = currentspot.getDeath(); 
            if(death != null && birth != null && death.before(birth)){
            	//System.out.println("Error US03: INDIVIDUAL: " + currentspot.getId()
            	//+ " died on " + dateFormat.format(death) + " before birth on " + dateFormat.format(birth));
            	errors.add("Error US03: INDIVIDUAL: " + currentspot.getId()+ " died on " + dateFormat.format(death) + " before birth on " + dateFormat.format(birth)) ;
            }
        }
		return errors;
	}
	//Sprint 1 - Ruchika Sutariya - User Story 02: Birth before marriage
	public static ArrayList<String> checkBirthBeforeMarriage(ArrayList<indi> indArray,ArrayList<fam> famArray){
		ArrayList<String> errors = new ArrayList<String>();
		for(int fam=0;fam<famArray.size();fam++){
			fam currentfam = famArray.get(fam);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date marriage = currentfam.getMarriage();
			String husband = currentfam.getHusband(); 
			String wife = currentfam.getWife(); 
			Date hbirth = getBirthDeath(husband, indArray).get(0);
			Date wbirth = getBirthDeath(wife, indArray).get(0);
			String error = ""; 
			if(hbirth != null && marriage != null && hbirth.after(marriage)){
				errors.add("Error US02: FAMILY: " + currentfam.getId() 
				+ ": Married " + dateFormat.format(marriage)
	        	+ " before husband's("+husband+") birth on " + dateFormat.format(hbirth)+"\n");
			}
			if(wbirth != null && marriage != null && wbirth.after(marriage)){
				errors.add("Error US02: FAMILY: " + currentfam.getId() 
				+ ": Married " + dateFormat.format(marriage)
	        	+ " before wife's("+wife+") birth on " + dateFormat.format(wbirth));
			}
		}
		
		return errors;
	}
	//Sprint 1 - Ruchika Sutariya - User Story 09 
	@SuppressWarnings("deprecation")
	public static ArrayList<String> checkBirthBeforeDeathofParents(ArrayList<indi> indArray, ArrayList<fam> famArray){
		ArrayList<String> errors = new ArrayList<String>();
		for(int i=0;i<famArray.size();i++){
			fam currentFamiliy = famArray.get(i);
			String fatherName = currentFamiliy.getHusband();
			String motherName = currentFamiliy.getWife();
			ArrayList<String> children = new ArrayList<String>();
			Date fatherDeath = getDeathDate(fatherName,indArray);
			Date deathMinusNineMonths =null;
			if(fatherDeath!=null){
				Calendar cal = Calendar.getInstance();
				cal.setTime(fatherDeath);
				cal.add(Calendar.MONTH,-9);
				deathMinusNineMonths = cal.getTime();
			}
			
			//fatherDeath.setMonth((fatherDeath.getMonth() - 1 -9) % 12 + 1);;
			Date motherDeath = getDeathDate(motherName, indArray);
			for(int j=0;j<children.size();j++){
				String currentChildId = children.get(j);
				Date currentChildBirth = getBirthDeath(currentChildId, indArray).get(0);
				if(fatherDeath!=null){
					if(currentChildBirth.after(deathMinusNineMonths)){
						errors.add("Error US 09 : FAMILY: Birthdate of child is "+currentChildBirth+" which is before nine months after death of father");
					}
				}else if(motherDeath!=null){
					if(currentChildBirth.after(motherDeath)){
						errors.add("Error US 09 : FAMILY: Birthdate of child is "+currentChildBirth+" which is after death of mother");
					}
				}else{
					continue;
				}
			}
		}
		return errors;
	}
	public static Date getDeathDate(String name, ArrayList<indi> indArray){
		for(int i=0;i<indArray.size();i++){
			indi currentIndi = indArray.get(i);
			if(currentIndi.getName().equals(name)){
				return currentIndi.getDeath();
			}
		}
		return null;
	}
	
	//Sprint 1 - Charmi Bhikadiya - User Story 16 : Male Members lastnames
	public static ArrayList<String> checkMaleLastNames(ArrayList<indi> indArray, ArrayList<fam> famArray){
		ArrayList<String> errors = new ArrayList<String>();
		for(int i=0;i<famArray.size();i++){
			String husbandId = famArray.get(i).getHusband();
			String lastName="";
			ArrayList<String> children = new ArrayList<String>();
			children = famArray.get(i).getChildren();
			for(int j=0;j<indArray.size();j++){
				if(indArray.get(j).getId().equals(husbandId)){
					lastName=indArray.get(j).getName().substring(indArray.get(j).getName().indexOf("/")+1, indArray.get(j).getName().length()-2);
					break;
					//System.out.println(lastName);
				}
			}
			for(int j=0;j<children.size();j++){
				for(int k=0;k<indArray.size();k++){
					if(children.get(j).equals(indArray.get(k).getId())){
						String sex = indArray.get(k).getSex();
						String indiName = indArray.get(k).getName();
						String indiLName = indiName.substring(indiName.indexOf("/")+1,indiName.length()-2);
						if(!lastName.equals(indiLName)
								&& indArray.get(k).getSex().equals("M")){
							errors.add("Error US16: FAMILY:"+famArray.get(i).getId()+" INDIVIDUAL:"+ indArray.get(k).getId()+"The last name of all male members should be "+lastName);
						}
					}
				}
			}
			
		}
		return errors;
	}
	public static int getIndexOfChild(String ChildID, ArrayList<indi> individualArray){
		for(int i = 0; i< individualArray.size();i++){
			indi child = individualArray.get(i);
			if(child.getId().equals(ChildID)){
				return i;
			}
		}
		return -1;
	}
	//Sprint 1 - Charmi Bhikadiya - User Story 13:Sibling Spacing
	private class dateAndId {            //class to help get 2 array of Id and birth date for children
		String Id;
		Date date;
		
		private dateAndId (String Id, Date date) {
			this.Id = Id;
			this.date = date;
		}
		
		private Date getDate() { return this.date; }
		
		private String getId() { return this.Id; }
	}
	
	public static ArrayList<String> compareSiblingSpacing(ArrayList<indi> indArray, ArrayList<fam> famArray){
		String error = "";
		fam famspot = null;
		indi currentspot = null;
		ArrayList<String> errors = new ArrayList<String>();
		ArrayList<dateAndId> siblingBirthDates = new ArrayList<>();
		for (fam famArray1 : famArray){
			famspot = famArray1;
			for (indi indArray1 : indArray){
				 currentspot = indArray1;
                 String spot = currentspot.getId();
                 ArrayList<String> children = famspot.getChildren();
                 if (children.contains(spot) && currentspot.getBirth() != null) {
            		siblingBirthDates.add(new gedcomParser().new dateAndId(currentspot.getId(), currentspot.getBirth()));	
                }
			}
		}
			
		for (dateAndId sibling1 : siblingBirthDates) {
			for (dateAndId sibling2 : siblingBirthDates) {
				long diff = Math.abs(sibling1.getDate().getTime() - sibling2.getDate().getTime());
				long Hours = diff / (60 * 60 * 1000);
		            long diffDays = Hours / 24;
		            long diffMonths = diffDays / 30;
		            
		            //System.out.println(sibling1.getId() + " " + sibling2.getId());
		            //System.out.println(sibling1.getDate() + " " + sibling2.getDate());
		            
		            if (diffDays > 2  && sibling1.getId() != sibling2.getId()){
		            	//errors.add("Error US13: Sibling " + sibling1.getId() + " and " + sibling2.getId() + " have birthdays more than 2 days apart");		            
		            	}
		            if (diffMonths < 9 && sibling1.getId() != sibling2.getId()){
		            	errors.add("Error US13: Sibling " + sibling1.getId() + " and " + sibling2.getId() + " have birthdays less than 9 months apart");		            }
			}
		}
		
		return errors;
	}

	
	public static ArrayList<Date> getBirthDeath(String id, ArrayList<indi> indArray){
		ArrayList<Date> BirthDeath = new ArrayList<Date>();
		Date yourdeath = null;
		Date yourbirth = null;
		for(int i=0; i<indArray.size(); i++){
			indi currentspot = indArray.get(i);
			String current_id = currentspot.getId();
			if(current_id.equals(id)){
				yourdeath= currentspot.getDeath(); 
				yourbirth = currentspot.getBirth(); 
			}
		}
		BirthDeath.add(yourbirth);
		BirthDeath.add(yourdeath);
		return BirthDeath; 
	}
	
	//Sprint 2 - Priya Parmar - US - 07 - Less then 150 years old
	public static ArrayList<String> checkIfAgeOver150(ArrayList<indi> indArray){
		
		ArrayList<String> errors = new ArrayList<String>();
		for (int j=0; j < indArray.size(); j++) {
            indi currentspot = indArray.get(j);
            int age = currentspot.getAge();
            //System.out.println(age);
            if (age > 150){
            	errors.add("Error US07: INDIVIDUAL: " + currentspot.getId() + " has an age " + age + " over 150!") ;
             }
		}
		return errors;
	}
	
	//Sprint 2 - Priya Parmar - US - 10 - Marriage after 14
	public static ArrayList<String> checkMarriageAfter14(ArrayList<fam> famArray, ArrayList<indi> indiArray){
		ArrayList<String> errors = new ArrayList<>();
		for(int i=0;i<famArray.size();i++){
			Date a = famArray.get(i).getMarriage();
			if(famArray.get(i).getMarriage() != null){
				indi husband = getIndividualById(famArray.get(i).getHusband(), indiArray);
				indi wife = getIndividualById(famArray.get(i).getWife(),indiArray);
				
				int wifeAge =0;
				if(wife!=null){
					wifeAge = getAgeWhenMarried(wife.getBirth(),famArray.get(i).getMarriage());
				}
				int husbandAge = getAgeWhenMarried(husband.getBirth(),famArray.get(i).getMarriage());
				
				if(husbandAge<14){
					errors.add("Error US10: INDIVIDUAL: " + husband.getId() + " was an age " + husbandAge + " when he got married which is less than 14!");
				}
				if(wifeAge<14 && wife!=null){ 
					errors.add("Error US10: INDIVIDUAL: " + wife.getId() + " was an age " + wifeAge + " when she got married which is less than 14!");
				}
			}
			
		}
		return errors;
	}
	
	//Sprint 2 - Ruchika Sutariya -US 18 - Siblings should not marry one another
	public static void SiblingShouldnotMarry(ArrayList<indi> indArray, ArrayList<fam> famArray){
		
		for(int i=0;i<famArray.size();i++){
			ArrayList<String> children = famArray.get(i).getChildren();
			
			if(famArray.get(i).getChildren()!=null)
			{
				String familyID=famArray.get(i).getId();
				
				if(children.size()>=2)
				{
					indi husband = getIndividualById(famArray.get(i).getHusband(), indArray);
					indi wife = getIndividualById(famArray.get(i).getWife(),indArray);
					String mothername=wife.getName();
					String fathername=husband.getName();
					
					
				System.out.println(children+" are Siblings who has same mother" + mothername + "and father" + fathername + "so they should not marry with each other.");
				
				}
				else
					System.out.println("There is only one child");
				
			}
		}
	}

	//Sprint 2 - Ruchika Sutariya -US 17 - Parents should not marry any of their descendants
public static void ChkNoMarriageDescendent(ArrayList<indi> indArray, ArrayList<fam> famArray){
	for(int i=0;i<famArray.size();i++)
	{
		
		Date marriagedate1=famArray.get(i).getMarriage();
		//System.out.println(marriagedate1);
		
		for(int j=i+1;j<famArray.size();j++)
		{
			Date marriagedate2=famArray.get(j).getMarriage();
			//System.out.println(marriagedate2);
			 if(marriagedate1!=null && marriagedate2!=null)
			 if(marriagedate1.before(marriagedate2))
			 {
				 String familyID1=famArray.get(j).getId();
				 String familyID=famArray.get(i).getId();
				 System.out.println(familyID+" should not marry to descendant"+familyID1);
			 }
			 
			
		}
		
	}
	
}
	// Sprint 2 - Charmi Bhikadiya - US 19 - First Cousins should not marry
	public static void CousinShouldNotMarry(ArrayList<indi> indArray, ArrayList<fam> famArray){
	String lastName1="";
	String lastName2="";
	System.out.println("These all are cousins of Same Family so should not be marry with each other.");
	for(int i=0;i<indArray.size();i++){
		String indiID=indArray.get(i).getId();
		//lastName1=indArray.get(i).getName().substring(indArray.get(i).getName().indexOf("/")+1, indArray.get(i).getName().length()-2);
		//System.out.println(indiID +" "+ lastName);	
		System.out.println(indArray.get(i).getId());
		for(i=0;i<indArray.size();i++)
		{
			lastName1=indArray.get(i).getName().substring(indArray.get(i).getName().indexOf("/")+1, indArray.get(i).getName().length()-2);
			
			for(int j=i+1;j<indArray.size();j++)
			{
				lastName2=indArray.get(j).getName().substring(indArray.get(j).getName().indexOf("/")+1, indArray.get(j).getName().length()-2);
				
				if(lastName2.equals(lastName1))
				{
					
					indiID=indArray.get(j).getId();
					System.out.println(indiID);
				}
				
					
			}
			break;
		}
		System.out.println("These all are cousins of Same Family so should not be marry with each other.");
		
				for(int j=i+1;j<indArray.size();j++)
				{
					//System.out.println(lastName1);
					lastName2=indArray.get(j).getName().substring(indArray.get(j).getName().indexOf("/")+1, indArray.get(j).getName().length()-2);
					//System.out.println(lastName2);
					if(!lastName1.equals(lastName2))
					{
						indiID=indArray.get(j).getId();
						System.out.println(indiID);
					}
					
				}
				break;
			
		}
	
		
}
	
	
	//Sprint 2 - Charmi Bhikadiya - US 28 - Order Siblings by age
	public static void orderSiblingsByAge(ArrayList<fam> famArray, ArrayList<indi> indArray){
		
		for(int i=0;i<famArray.size();i++){
			ArrayList<indi> orderedSiblings = new ArrayList<>();
			ArrayList<String> siblings = famArray.get(i).getChildren();
			for(int j =0 ;j<siblings.size();j++){
				orderedSiblings.add(getIndividualById(siblings.get(j), indArray));
			}
			gedcomParser ged = new gedcomParser();
			Collections.sort(orderedSiblings,ged.new AgeComparator());
			System.out.println("\nSiblings of family "+famArray.get(i).getId()+":");
			for(int j=0;j<orderedSiblings.size();j++){
				System.out.println(orderedSiblings.get(j).getName()+"has age "+orderedSiblings.get(j).getAge());
			}
		}
		
	}
	//US 27
	static int getIndiAge(String sDate) {
        SimpleDateFormat dt = new SimpleDateFormat("dd MMM yyyy"); 
	    Date date; 
	    try{
            date = dt.parse(sDate);
            //System.out.println(date);
            Date currentDate = new Date();
            //if (deathcheck=false) {
            long difference = currentDate.getTime() - date.getTime();
            long Hours = difference / (60 * 60 * 1000);
            long Days = Hours / 24; 
            double Year = Days / 365;
            int year = (int) Year;
            //  System.out.println(year);
            return year;
         }
         catch (Exception ex) {}
        return 0;
    }
	static int getAgeWhenMarried(Date first, Date last) {
		Calendar a = getCalendar(first);
	    Calendar b = getCalendar(last);
	    int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
	    if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || 
	        (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
	        diff--;
	    }
	    return diff;
	}
	public static Calendar getCalendar(Date date) {
	    Calendar cal = Calendar.getInstance(Locale.US);
	    cal.setTime(date);
	    return cal;
	}
	public static indi getIndividualById(String id, ArrayList<indi> indArray){
		indi individual=null;
		for(int i=0;i<indArray.size();i++){
			String indiId = indArray.get(i).getId();
			if(indiId.equals(id)){
				individual = indArray.get(i);
				break;
			}
		}
		return individual;
	}

	public static String getName(String id, ArrayList<indi> indArray){
		String yourname = "";
		for(int i=0; i<indArray.size(); i++){
			indi currentspot = indArray.get(i);
			String current_id = currentspot.getId();
			if(current_id.equals(id)){
				yourname= currentspot.getName(); 
			}
		}
		return yourname; 
	}
	
	public static void main(String[] args) throws IOException{
		ArrayList<indi> individualArray = new ArrayList<indi>();
		ArrayList<fam> familyArray = new ArrayList<fam>();
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the gedcom file path with '\\' you wish evaluate: ");
		//String file = in.nextLine();
		String file ="C:\\Users\\parmarp\\Downloads\\My-Family-17-Mar-2017-227.ged";
		in.close();
		parseFile(file, individualArray, familyArray);
		printIndiAndFamData(individualArray, familyArray);
	}
}

